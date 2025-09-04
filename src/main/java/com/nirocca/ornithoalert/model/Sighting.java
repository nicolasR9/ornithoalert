package com.nirocca.ornithoalert.model;

import java.util.regex.Pattern;

import com.nirocca.ornithoalert.model.ornitho.OrnithoData;
import com.nirocca.ornithoalert.model.ornitho.Species;

public record Sighting(String date, String germanName, String latinName, int speciesId, String url, String locationText,
                       String count, Coordinates coordinates) {

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

    @Override
    public String toString() {
        return "[" + germanName + ", " + count + ", " + date + ", " + locationText + "](" + url + ")";
    }
}
