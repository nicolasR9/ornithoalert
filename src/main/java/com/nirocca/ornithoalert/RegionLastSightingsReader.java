package com.nirocca.ornithoalert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.model.ornitho.DataWrapper;

public class RegionLastSightingsReader {

    private static final int CHUNK_SIZE = 5;

    private final OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    private final String baseUrl;

    public RegionLastSightingsReader() {
        this.baseUrl = "https://www.ornitho.de";
    }

    public RegionLastSightingsReader(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<Sighting> read(String url) throws IOException {
        String html = ornithoPageReader.getPageContent(url);
        String dataUrl = extractDataUrl(html, baseUrl);


        List<Sighting> result = new ArrayList<>();
        List<Sighting> currentSightings;

        int fromPage = 1;
        do {
            int toPage = fromPage + CHUNK_SIZE - 1;
            System.out.println("reading pages " + fromPage + "-" + toPage);
            currentSightings = readPagesChunkParallel(dataUrl, fromPage, toPage);
            result.addAll(currentSightings);
            fromPage += CHUNK_SIZE;
        } while (!currentSightings.isEmpty());
        return result;
    }

    public static String extractDataUrl(String html, String baseUrl) {
        String regex = "\"(index\\.php\\?m_id=\\d+&content=observations_by_page[^\"]+)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return baseUrl + "/" + matcher.group(1);
        }
        throw new RuntimeException("Could not extract data url from html.");
    }

    private List<Sighting> readPagesChunkParallel(String url, int fromPage, int toPage) {
        ExecutorService executor = Executors.newFixedThreadPool(toPage - fromPage + 1);
        List<Callable<List<Sighting>>> tasks = new ArrayList<>();
        for (int i = fromPage; i <= toPage; i++) {
            final String finalUrl = url.contains("current_page=1") ?
                url.replace("current_page=1", "current_page=" + i)
                : url + "&mp_current_page=" + i;
            tasks.add(() -> readPage(finalUrl));
        }

        try {
            List<Future<List<Sighting>>> futures = executor.invokeAll(tasks);
            executor.shutdown();
            executor.awaitTermination(200, TimeUnit.SECONDS);
            return futures.stream().map(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                }
            }).flatMap(Collection::stream).collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    private List<Sighting> readPage(String url) throws IOException {
        String dataJson = ornithoPageReader.getPageContent(url);

        return parseSightings(dataJson);
    }

    private List<Sighting> parseSightings(String pageJson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(pageJson, DataWrapper.class).
            getData()
            .stream()
            .map(Sighting::of)
            .toList();

        // List<Day> days = parser.parseSightingStructure(pageJson);
        // return Sighting.fromDays(days);
    }
}
