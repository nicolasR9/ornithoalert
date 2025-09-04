package com.nirocca.ornithoalert.model.ornitho;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrnithoData {
    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("place_type")
    private String placeType;

    @JsonProperty("species_array")
    private Species speciesArray;

    @JsonProperty("birds_count")
    private String birdsCount;

    @JsonProperty("date")
    private String date;

    @JsonProperty("opt_observers")
    private List<Observer> optObservers;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public Species getSpeciesArray() {
        return speciesArray;
    }

    public void setSpeciesArray(Species speciesArray) {
        this.speciesArray = speciesArray;
    }

    public String getBirdsCount() {
        return birdsCount;
    }

    public void setBirdsCount(String birdsCount) {
        this.birdsCount = birdsCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Observer> getOptObservers() {
        return optObservers;
    }

    public void setOptObservers(List<Observer> optObservers) {
        this.optObservers = optObservers;
    }
}