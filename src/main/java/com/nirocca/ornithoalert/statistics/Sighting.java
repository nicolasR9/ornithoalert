package com.nirocca.ornithoalert.statistics;

import java.util.Date;

class Sighting {
    private final Species species;
    private final Date sightingDate;
    
    
    Sighting(Species species, Date sightingDate) {
        this.species = species;
        this.sightingDate = sightingDate;
    }

    Date getSightingDate() {
        return sightingDate;
    }

    Species getSpecies() {
        return species;
    }
}
