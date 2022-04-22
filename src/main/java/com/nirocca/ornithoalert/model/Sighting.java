package com.nirocca.ornithoalert.model;

import java.util.ArrayList;
import java.util.List;

public class Sighting {
    
    private final String date;
    private final String location;
    private final String germanNamePlural;
    private final String latinName;
    private final int speciesId;
    private final String url;
    private final String count;
    
    public Sighting(Day day, Location location, Observation observation) {
        this(day.parseDay(), observation.parseGermanName(), observation.parseLatinName(), observation.parseSpeciesId(), observation.parseUrl(),
            location.getLocationText(), observation.parseCount());
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

    private Sighting(String date, String germanNamePlural, String latinName, int speciesId, String url, String location, String count) {
        this.date = date;
        this.germanNamePlural = germanNamePlural;
        this.latinName = latinName;
        this.speciesId = speciesId;
        this.url = url;
        this.location = location;
        this.count = count;
    }

    public String getLatinName() {
        return latinName;
    }

    public int getSpeciesId() {
        return speciesId;
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

    public String getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "[" + germanNamePlural + ", " +  date + ", " + location +"](" + url + ")";
    }
}
