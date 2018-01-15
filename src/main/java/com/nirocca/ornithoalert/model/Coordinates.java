package com.nirocca.ornithoalert.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinates {
    private String latitude;
    private String longitude;
    
    public Coordinates(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public String getLatitude() {
        return latitude;
    }
    
    public String getLongitude() {
        return longitude;
    }
    
    public void shiftABit() {
        Pattern p = Pattern.compile("(\\d+)\u00B0(\\d+)'(\\d+)(.\\d+'' N)");
        Matcher m = p.matcher(latitude);
        if (!m.matches()) {
            throw new RuntimeException("unexpected coordinate format");
        }
        
        int degrees = Integer.parseInt(m.group(1));
        int minutes = Integer.parseInt(m.group(2));
        int seconds = Integer.parseInt(m.group(3));
        
        int offset = 5;
        seconds += offset;
        if (seconds > 59) {
            ++minutes;
            seconds -= 60;
        }
        
        if (minutes > 59) {
            ++degrees;
            minutes -= 60;
        }
        
        latitude = String.format("%d\u00B0%02d'%02d%s", degrees, minutes, seconds, m.group(4));
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
        result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
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
        if (latitude == null) {
            if (other.latitude != null)
                return false;
        } else if (!latitude.equals(other.latitude))
            return false;
        if (longitude == null) {
            if (other.longitude != null)
                return false;
        } else if (!longitude.equals(other.longitude))
            return false;
        return true;
    }
    
    
}
