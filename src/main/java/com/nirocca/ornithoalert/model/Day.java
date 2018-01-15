package com.nirocca.ornithoalert.model;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public final class Day {
    private Element dayElement;
    private List<Location> locations = new ArrayList<>();
    
    public Day(Element dayElement) {
        this.dayElement = dayElement;
    }

    public String parseDay() {
        return dayElement.selectFirst("b").text();
        
    }
    
    public List<Location> getLocations() {
        return locations;
    }
}