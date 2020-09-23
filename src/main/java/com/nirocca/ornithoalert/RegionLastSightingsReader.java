package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Sighting;

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
import java.util.stream.Collectors;

public class RegionLastSightingsReader {

    private static final int CHUNK_SIZE = 5;
    
    private SightingsPageParser parser = new SightingsPageParser();
    
    private OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    

    public List<Sighting> read(String url) {
        List<Sighting> result = new ArrayList<>();
        List<Sighting> currentSightings;

        int fromPage = 1;
        do {
            int toPage = fromPage + CHUNK_SIZE - 1;
            System.out.println("reading pages " + fromPage + "-" + toPage);
            currentSightings = readPagesChunkParallel(url, fromPage, toPage);
            result.addAll(currentSightings);
            fromPage += CHUNK_SIZE;
        } while (!currentSightings.isEmpty());
        return result;
    }

    private List<Sighting> readPagesChunkParallel(String url, int fromPage, int toPage) {
        ExecutorService executor = Executors.newFixedThreadPool(toPage - fromPage + 1);
        List<Callable<List<Sighting>>> tasks = new ArrayList<>();
        for (int i = fromPage; i <= toPage; i++) {
            int finalI = i;
            tasks.add(() -> readPage(url.replace("current_page=1", "current_page=" + finalI)));
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
        String html = ornithoPageReader.getHtmlForPage(url);
        
        return parseSightings(html);
    }

    private List<Sighting> parseSightings(String html) {
        List<Day> days = parser.parseSightingStructure(html);
        return Sighting.fromDays(days);
    }
}
