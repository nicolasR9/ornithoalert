package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Sighting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegionLastSightingsReader {
    
    private SightingsPageParser parser = new SightingsPageParser();
    
    private OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    

    public List<Sighting> read(String url) throws IOException {
        List<Sighting> result = new ArrayList<>();
        List<Sighting> currentPageSightings = new ArrayList<>();
        
        int page = 1;
        
        do {
            System.out.println("reading page " + page);
            currentPageSightings = readPage(url.replace("current_page=1", "current_page=" + (page++)));
            result.addAll(currentPageSightings);
        } while (!currentPageSightings.isEmpty());
        return result;
    }
    
    private List<Sighting> readPage(String url) throws IOException {
        String html = ornithoPageReader.getHtmlForPage(url);
        
        return parseSightings(html);
    }

    private List<Sighting> parseSightings(String html) throws IOException {
        List<Day> days = parser.parseSightingStructure(html);
        return Sighting.fromDays(days);
    }
}
