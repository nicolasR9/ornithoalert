package com.nirocca.ornithoalert.model;

public final class LatinComparedSpecies {
    private String germanName;
    private String latinName;
    
    public LatinComparedSpecies(String germanName, String latinName) {
        this.germanName = germanName;
        this.latinName = latinName;
    }
    @Override
    public String toString() {
        return germanName;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((latinName == null) ? 0 : latinName.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LatinComparedSpecies other = (LatinComparedSpecies) obj;
        if (latinName == null) {
            if (other.latinName != null)
                return false;
        } else if (!latinName.equals(other.latinName))
            return false;
        return true;
    }
    
    
    
}