package com.nirocca.ornithoalert.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.grid.Hotspot;
import com.nirocca.ornithoalert.model.Sighting;

public class HotspotScoreCalculator {
    public static void calcScore(Hotspot hotspot, int[] years) {
        Map<String, int[]> sightingCountBySpeciesPerYear = getBySpeciesPerYear(hotspot.getSightings(), years);
        double score = 0.0;
        for (int[] perYear : sightingCountBySpeciesPerYear.values()) {
            score += calcScorePerSpecies(perYear);
        }
        hotspot.setScore(score);
    }

    private static double calcScorePerSpecies(int[] perYearCount) {
        double score = 0.0;
        int yearsSighted = 0;
        for (int count : perYearCount) {
                if (count == 0) continue;
                if (count > 10) {
                    score += 10;
                } else if (count >= 5) {
                    score += 5;
                } else {
                    score += 1;
                }
                yearsSighted++;
        }
        if (yearsSighted == 1) {
            score = Math.min(score, 1);
        }
        return score;
    }

    private static Map<String,int[]> getBySpeciesPerYear(List<Sighting> sightings, int[] years) {
        Map<String, int[]> map = new HashMap<>();
        sightings.forEach(s -> addToMap(map, calcYearIndex(s.getDate(), years[0]), calcName(s), years.length));
        return map;
    }

    private static void addToMap(Map<String,int[]> map, int yearIndex, String speciesName, int yearsLength) {
        int[] counts = map.getOrDefault(speciesName, new int[yearsLength]);
        counts[yearIndex]++;
        map.put(speciesName, counts);
    }

    private static int calcYearIndex(String date, int startYear) {
        return Integer.parseInt(date.substring(date.length() - 4)) - startYear;
    }

    private static String calcName(Sighting sighting) {
        return sighting.getSpeciesId() > 0 ? Species.getById(sighting.getSpeciesId()).name() : sighting.getLatinName();
    }
}
