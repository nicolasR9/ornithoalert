package com.nirocca.ornithoalert.model;

import java.util.ArrayList;
import java.util.List;

public class Sighting {
    
    private String date;
    private String location;
    private String germanNamePlural;
    private String latinName;
    private String url;
    
    public Sighting(String date, String germanNamePlural, String latinName, String url, String location) {
        this.date = date;
        this.germanNamePlural = germanNamePlural;
        this.latinName = latinName;
        this.url = url;
        this.location = location;
    }
    
    public Sighting(Day day, Location location, Observation observation) {
        this(day.parseDay(), observation.parseGermanName(), observation.parseLatinName(), observation.parseUrl(), location.getLocationText());
    }
    
    public static List<Sighting> fromDays(List<Day> days) {
        List<Sighting> sightings = new ArrayList<>();
        for (Day day : days) {
            for (Location location : day.getLocations()) {
                for (Observation observation : location.getObservations()) {
                    sightings.add(new Sighting(day, location, observation));
                }
            }
        }
        return sightings;
    }
    
    public String getLatinName() {
        return latinName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getGermanNamePlural() {
        return germanNamePlural;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "[" + germanNamePlural + ", " +  date + ", " + location +"](" + url + ")";
    }
}
