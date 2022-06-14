package com.nirocca.ornithoalert.grid;

import com.nirocca.ornithoalert.model.Sighting;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import com.spatial4j.core.shape.Rectangle;
import com.spatial4j.core.shape.SpatialRelation;
import com.spatial4j.core.shape.impl.PointImpl;
import com.spatial4j.core.shape.impl.RectangleImpl;
import java.io.IOException;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

public class GridMain {
    private static final double LATITUDE_DELTA_20_KM = 0.180237;
    private static final double LONGITUDE_DELTA_20_KM = 0.295272;
    private static final String SIGHTINGS_FILE ="/allDe.txt";

    public static void main(String[] args) throws IOException {
        List<Sighting> sightings = readSightings();
        findBestPlacesForEachMonth(sightings);
    }

    private static void findBestPlacesForEachMonth(List<Sighting> sightings) {
        System.out.println("Locations: lower left corner of 20x20km square.");
        List<Rectangle> germanyGrid = createGermanyGrid();
        for (Month month : Month.values()) {
            System.out.println("Month: " + month);
            List<Sighting> monthSightings = filterByMonth(sightings, month);
            List<Hotspot> hotspotRanking = getHotspotRanking(germanyGrid, monthSightings);
            hotspotRanking.subList(0, 5).stream().forEach(System.out::println);
            System.out.println();
        }
    }

    private static List<Hotspot> getHotspotRanking(List<Rectangle> germanyGrid, List<Sighting> monthSightings) {
        List<Hotspot> result = new ArrayList<>(germanyGrid.size());
        for (Rectangle rectangle : germanyGrid) {
            List<Sighting> sightings = filterByRectangle(monthSightings, rectangle);
            Hotspot hotspot = new Hotspot(rectangle, sightings);
            calcScore(hotspot);
            result.add(hotspot);
        }

        Collections.sort(result, Comparator.reverseOrder());
        return result;
    }

    private static void calcScore(Hotspot hotspot) {
        Map<String, Integer> sightingCountBySpecies = hotspot.getSightingCountBySpecies();
        double score = 0.0;
        for (int count : sightingCountBySpecies.values()) {
            for (int i = 1; i < count; i++) {
                score += 1.0 / count;
            }
        }

        hotspot.setScore(score);
    }

    private static List<Sighting> filterByRectangle(List<Sighting> monthSightings, Rectangle rectangle) {
        return monthSightings
            .stream()
            .filter(s -> rectangle.relate(toPoint(s.getLocation())) == SpatialRelation.CONTAINS)
            .collect(Collectors.toList());
    }

    private static Point toPoint(String location) {
        String[] split = location.split(",");
        return new PointImpl(Double.parseDouble(split[0]), Double.parseDouble(split[1]), SpatialContext.GEO);
    }

    private static List<Rectangle> createGermanyGrid() {
        List<Rectangle> result = new ArrayList<>();
        double minLati = 47.270320;
        double minLongi = 5.874760;
        double maxLati = 54.909713;
        double maxLongi = 15.037702;

        double lati = minLati;
        double longi = minLongi;
        while (lati < maxLati) {
            while (longi < maxLongi) {
                result.add(new RectangleImpl(lati, lati + LATITUDE_DELTA_20_KM, longi, longi + LONGITUDE_DELTA_20_KM, SpatialContext.GEO));
                longi += LONGITUDE_DELTA_20_KM;
            }
            longi = minLongi;
            lati += LATITUDE_DELTA_20_KM;
        }
        return result;
    }

    private static List<Sighting> filterByMonth(List<Sighting> sightings, Month month) {
        // Freitag 22. Januar 2021

        String monthName = month.getDisplayName(TextStyle.FULL, Locale.GERMAN);
        return sightings.stream().filter(s -> s.getDate().split(" ")[2].equals(monthName)).collect(Collectors.toList());
    }

    private static List<Sighting> readSightings() throws IOException {
        List<String> lines = IOUtils.readLines(GridMain.class.getResourceAsStream(SIGHTINGS_FILE), "utf-8");
        lines.remove(0);

        List<Sighting> result = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split(",");
            result.add(new Sighting(fields[1], fields[0], null, Integer.parseInt(fields[6]), fields[5], fields[2] + "," + fields[3], "-1"));
        }
        return result;
    }

}
