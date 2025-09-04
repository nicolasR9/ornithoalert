package com.nirocca.ornithoalert.model;

public class Coordinates {
    private double latitude;
    private final double longitude;
    private final boolean exact;
    
    public Coordinates(double latitude, double longitude, boolean exact) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.exact = exact;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void shiftABit() {
        latitude -= 0.0006;
    }

    public boolean isExact() {
        return exact;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        Coordinates other = (Coordinates) obj;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        return Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);
    }
}
