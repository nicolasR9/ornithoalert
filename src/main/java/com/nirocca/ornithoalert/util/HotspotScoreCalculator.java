package com.nirocca.ornithoalert.util;

import com.nirocca.ornithoalert.grid.Hotspot;
import java.util.Map;

public class HotspotScoreCalculator {
    public static void calcScore(Hotspot hotspot, int[] years) {
        double score = 0.0;
        for (int year : years) {
            Map<String, Integer> sightingCountBySpecies = hotspot.getSightingCountBySpecies(year);
            for (int count : sightingCountBySpecies.values()) {
                if (count > 10) {
                    score += 10;
                } else if (count >= 5) {
                    score += 5;
                } else {
                    score += 1;
                }
            }
        }
        hotspot.setScore(score);
    }
}
