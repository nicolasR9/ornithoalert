package com.nirocca.ornithoalert.statistics;

import java.util.Objects;

public class Species {
    private final int speciesId;
    private final String speciesName;

    private final String latinName;

    Species(int speciesId, String speciesName, String latinName) {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.latinName = latinName;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public String getLatinName() {
        return latinName;
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
