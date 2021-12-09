package com.nirocca.ornithoalert.statistics;

import java.util.Objects;

public class Species {
    private final int speciesId;
    private final String speciesName;

    Species(int speciesId, String speciesName) {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
    }

    String getSpeciesName() {
        return speciesName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Species species = (Species) o;
        return speciesId == species.speciesId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(speciesId);
    }

    @Override
    public String toString() {
        return speciesName;
    }
}
