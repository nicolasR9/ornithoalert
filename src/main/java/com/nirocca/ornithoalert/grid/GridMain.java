package com.nirocca.ornithoalert.grid;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.MySightingsReader;
import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.util.HotspotScoreCalculator;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Rectangle;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.locationtech.spatial4j.shape.impl.PointImpl;
import org.locationtech.spatial4j.shape.impl.RectangleImpl;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Builder;
import org.apache.commons.io.IOUtils;

public class GridMain {
    private static final double LATITUDE_DELTA_20_KM = 0.180237;
    private static final double LONGITUDE_DELTA_20_KM = 0.295272;
    private static final String SIGHTINGS_FILE ="/allDe.txt";

    private static final int[] YEARS_IN_SIGHTINGS_FILE = {2022, 2023, 2024};

    public static void main(String[] args) throws IOException {
        List<Sighting> sightings = readSightings();
        findBestPlacesForEachMonth(sightings);
    }

    private static void findBestPlacesForEachMonth(List<Sighting> sightings) throws IOException {
        System.out.println("Locations: lower left corner of 20x20km square.");

        List<Rectangle> germanyGrid = createGermanyGrid();
        for (Month month : Month.values()) {
            System.out.println("Month: " + month);
            List<Sighting> monthSightings = filterByMonth(sightings, month);
            List<Hotspot> hotspotRanking = getHotspotRanking(germanyGrid, monthSightings);
            List<Hotspot> hotspots = hotspotRanking.subList(0, 5);
            hotspots.forEach(System.out::println);
            System.out.println();
            printCoordinatesToFile(month, hotspots);
        }
    }

    private static void printCoordinatesToFile(Month month, List<Hotspot> hotspots) throws IOException{
        Builder gpxBuilder = GPX.builder();
        for (Hotspot hotspot : hotspots) {
            Rectangle location = hotspot.getLocation();
            gpxBuilder
                .addTrack(track -> track
                    .addSegment(segment -> segment
                        .addPoint(p -> p.lat(location.getMinX()).lon(location.getMinY()))
                        .addPoint(p -> p.lat(location.getMinX()).lon(location.getMaxY()))
                        .addPoint(p -> p.lat(location.getMaxX()).lon(location.getMaxY()))
                        .addPoint(p -> p.lat(location.getMaxX()).lon(location.getMinY()))
                        .addPoint(p -> p.lat(location.getMinX()).lon(location.getMinY())))
                    .desc(String.format("score: %s, species: %s", hotspot.getScore(), hotspot.getSightingCountBySpecies()))
                );
        }

        final GPX gpx = gpxBuilder.build();

        String filename = Constants.OUTPUT_DIR + "grid/" + month.getDisplayName(TextStyle.FULL, Locale.GERMAN) + ".gpx";
        GPX.write(gpx, Paths.get(filename));
    }

    private static List<Hotspot> getHotspotRanking(List<Rectangle> germanyGrid, List<Sighting> monthSightings) {
        List<Hotspot> result = new ArrayList<>(germanyGrid.size());
        for (Rectangle rectangle : germanyGrid) {
            List<Sighting> sightings = filterByRectangle(monthSightings, rectangle);
            Hotspot hotspot = new Hotspot(rectangle, sightings);
            HotspotScoreCalculator.calcScore(hotspot, YEARS_IN_SIGHTINGS_FILE);
            result.add(hotspot);
        }

        result.sort(Comparator.reverseOrder());
        return result;
    }

    private static List<Sighting> filterByRectangle(List<Sighting> monthSightings, Rectangle rectangle) {
        return monthSightings
            .stream()
            .filter(s -> rectangle.relate(toPoint(s.coordinates())) == SpatialRelation.CONTAINS)
            .collect(Collectors.toList());
    }

    private static Point toPoint(Coordinates location) {
        return new PointImpl(location.getLatitude(), location.getLongitude(), SpatialContext.GEO);
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
        return sightings.stream().filter(s -> s.date().split(" ")[2].equals(monthName)).collect(Collectors.toList());
    }

    static List<Sighting> readSightings() throws IOException {
        List<String> lines = IOUtils.readLines(GridMain.class.getResourceAsStream(SIGHTINGS_FILE), "utf-8");
        lines.remove(0);

        List<Sighting> result = new ArrayList<>();
        for (String line : lines) {
            String[] fields = line.split(",");
            Coordinates coordinates = new Coordinates(Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), true);
            result.add(new Sighting(fields[1], fields[0], null,  Integer.parseInt(fields[6]), fields[5], "", "-1", coordinates));
        }
        List<String> sightedSpeciesIds = new MySightingsReader().readMySightedSpeciesIds();
        Set<Integer> ids = sightedSpeciesIds.stream().map(Integer::valueOf).collect(Collectors.toSet());
        return result.stream().filter(s -> !ids.contains(s.speciesId())).collect(Collectors.toList());
    }

}
