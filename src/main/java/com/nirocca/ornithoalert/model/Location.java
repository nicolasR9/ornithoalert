package com.nirocca.ornithoalert.model;

import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public final class Location {
    private final Element locationElement;
    private final List<Observation> observations = new ArrayList<>();
    
    public Location(Element locationElement) {
        this.locationElement = locationElement;
    }
    
    public String getLocationText() {
        return locationElement.selectFirst("a").text();
    }
    
    public List<Observation> getObservations() {
        return observations;
    }
}