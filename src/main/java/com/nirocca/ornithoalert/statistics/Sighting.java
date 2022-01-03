package com.nirocca.ornithoalert.statistics;

import java.util.Date;

public class Sighting {
    private final Species species;
    private final Date sightingDate;
    private final boolean isZeroCount;
    
    Sighting(Species species, Date sightingDate, String countString) {
        this.species = species;
        this.sightingDate = sightingDate;
        isZeroCount = "0".equals(countString);
    }

    Date getSightingDate() {
        return sightingDate;
    }

    public Species getSpecies() {
        return species;
    }

    public boolean isZeroCount() {
        return isZeroCount;
    }
}
