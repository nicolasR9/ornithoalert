package com.nirocca.ornithoalert.util;

import java.util.Map;

import com.nirocca.ornithoalert.grid.Hotspot;

public class HotspotScoreCalculator {
    public static void calcScore(Hotspot hotspot, int[] years) {
        double score = 0.0;
        int yearsSighted = 0;
        for (int year : years) {
            Map<String, Integer> sightingCountBySpecies = hotspot.getSightingCountBySpecies(year);
            for (int count : sightingCountBySpecies.values()) {
                if (count == 0) break;
                if (count > 10) {
                    score += 10;
                } else if (count >= 5) {
                    score += 5;
                } else {
                    score += 1;
                }
                yearsSighted++;
            }
        }
        if (yearsSighted == 1) {
            score = Math.max(score, 1);
        }
        hotspot.setScore(score);
    }
}
