package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Location;
import com.nirocca.ornithoalert.model.Observation;
import java.util.Collections;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class SightingsPageParser {
    
    public List<Day> parseSightingStructure(String html) {
        Document doc = Jsoup.parse(html);
        Element topElement = doc.selectFirst("div.listContainer");
        if (topElement == null) {
            return Collections.emptyList();
        }
        
        List<Day> days = new ArrayList<>();
        Day currentDay = null;
        Location currentLocation = null;
        for (Element sub : topElement.children()) {
            switch (sub.className()) {
                case "listTop":
                    currentDay = new Day(sub);
                    days.add(currentDay);
                    break;
                case "listSubmenu":
                    currentLocation = new Location(sub);
                    currentDay.getLocations().add(currentLocation);
                    break;
                case "listObservation":
                    Elements observationTables = sub.children();
                    for (Element observationTable : observationTables) {
                        currentLocation.getObservations().add(new Observation(observationTable));
                    }
                    break;
            }
        }
        return days;
    }

}
