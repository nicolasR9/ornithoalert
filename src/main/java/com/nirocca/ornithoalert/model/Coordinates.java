package com.nirocca.ornithoalert.model;

import gov.nasa.worldwind.geom.Angle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinates {
    private double latitude;
    private double longitude;
    
    public Coordinates(String latitude, String longitude) {
        this.latitude = toDecimal(latitude);
        this.longitude = toDecimal(longitude);
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    private double toDecimal(String coordinate) {
        try {
            return Double.parseDouble(coordinate);
        } catch (NumberFormatException ex) {
            Pattern p = Pattern.compile("(\\d+)\u00B0(\\d+)'(\\d+)\\.\\d+'' [EN]");
            Matcher m = p.matcher(coordinate);
            if (!m.matches()) {
                throw new RuntimeException("unexpected coordinate format: " + coordinate);
            }
            int degrees = Integer.parseInt(m.group(1));
            int minutes = Integer.parseInt(m.group(2));
            int seconds = Integer.parseInt(m.group(3));

            return Angle.fromDMS(degrees, minutes, seconds).getDegrees();
        }
    }
    
    public void shiftABit() {
        latitude -= 0.0004;
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
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        return true;
    }
}
