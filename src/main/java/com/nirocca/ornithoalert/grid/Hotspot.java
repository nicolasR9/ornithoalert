package com.nirocca.ornithoalert.grid;

import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;
import com.spatial4j.core.shape.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Hotspot implements Comparable<Hotspot> {

    private Rectangle location;
    private double score;
    private List<Sighting> sightings;

    public Hotspot(Rectangle location, List<Sighting> sightings) {
        this.location = location;
        this.sightings = sightings;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Rectangle getLocation() {
        return location;
    }

    public double getScore() {
        return score;
    }

    public List<Sighting> getSightings() {
        return sightings;
    }

    public Map<String, Integer> getSightingCountBySpecies(int year) {
        Map<String, List<Sighting>> map = sightings.stream().collect(
            Collectors.groupingBy(sighting -> sighting.getSpeciesId() > 0 ? Species.getById(sighting.getSpeciesId()).name() : sighting.getLatinName()));
        Map<String, Integer> result = new HashMap<>(map.size());
        for (Entry<String, List<Sighting>> entry : map.entrySet()) {
            List<Sighting> sightingsInYear =
                entry.getValue().stream().filter(s -> s.getDate().endsWith(String.valueOf(year))).toList();
            result.put(entry.getKey(), sightingsInYear.size());
        }
        return result;
    }
    Map<String, Integer> getSightingCountBySpecies() {
        Map<String, List<Sighting>> map = sightings.stream().collect(
            Collectors.groupingBy(sighting -> Species.getById(sighting.getSpeciesId()).name()));
        Map<String, Integer> result = new HashMap<>(map.size());
        for (Entry<String, List<Sighting>> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    @Override
    public int compareTo(Hotspot o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {


        return "" + location.getMinX() + ", " + location.getMinY() +
            ", score=" + score +
            ", sightings=" + getSightingCountBySpecies();
    }
}
