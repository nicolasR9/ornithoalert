package com.nirocca.ornithoalert.statistics;

import java.util.Date;

public class Sighting {
    private int speciesId;
    private String speciesName;
    private Date sightingDate;
    
    
    public Sighting(int speciesId, String speciesName, Date sightingDate) {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.sightingDate = sightingDate;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public Date getSightingDate() {
        return sightingDate;
    }
}
