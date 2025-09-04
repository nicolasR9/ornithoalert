package com.nirocca.ornithoalert.model;

import java.util.regex.Pattern;

import com.nirocca.ornithoalert.model.ornitho.OrnithoData;
import com.nirocca.ornithoalert.model.ornitho.Species;

public class Sighting {
    
    private final String date;
    private final String locationText;
    private final String germanNamePlural;
    private final String latinName;
    private final int speciesId;
    private final String url;
    private final String count;
    private final Coordinates coordinates;

    public static Sighting of(OrnithoData ornithoData) {
        Species speciesArray = ornithoData.getSpeciesArray();
        long idSighting = ornithoData.getOptObservers().get(0).getOptObserverInfo().get(0).getIdSighting();
        Coordinates coords = new Coordinates(
            ornithoData.getLat(),
            ornithoData.getLon(), "precise".equalsIgnoreCase(ornithoData.getPlaceType()));
        return new Sighting(
            extractDate(ornithoData.getDate()),
            speciesArray.getName().replaceAll("\\|", ""),
            speciesArray.getLatinName(),
            speciesArray.getId(),
            String.format("https://www.ornitho.de/index.php?m_id=54&id=%s", idSighting),
            ornithoData.getListSubmenu().getTitle(),
            ornithoData.getBirdsCount(),
            coords);
    }

    private static String extractDate(String date) {
        Pattern p = Pattern.compile("<span title=\"([^\"]+)\">.*");
        var m = p.matcher(date);
        if (m.matches()) {
            return m.group(1);
        }
        return date;
    }

    public Sighting(String date,
                    String germanNamePlural,
                    String latinName,
                    int speciesId,
                    String url,
                    String locationText,
                    String count,
                    Coordinates coordinates) {
        this.date = date;
        this.germanNamePlural = germanNamePlural;
        this.latinName = latinName;
        this.speciesId = speciesId;
        this.url = url;
        this.locationText = locationText;
        this.count = count;
        this.coordinates = coordinates;
    }

    public String getLatinName() {
        return latinName;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public String getLocationText() {
        return locationText;
    }
    
    public String getGermanNamePlural() {
        return germanNamePlural;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getDate() {
        return date;
    }

    public String getCount() {
        return count;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "[" + germanNamePlural + ", " +  date + ", " + locationText +"](" + url + ")";
    }
}
