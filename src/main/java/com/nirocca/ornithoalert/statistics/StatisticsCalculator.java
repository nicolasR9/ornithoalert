package com.nirocca.ornithoalert.statistics;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StatisticsCalculator {

    private static final int MY_FIRST_SIGHTING_YEAR = 2017;

    private List<Sighting> readMySightings() throws IOException {
        List<String> lines = IOUtils.readLines(
                StatisticsCalculator.class.getResourceAsStream("/ornitho_export_meine.txt"));
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return lines.subList(2, lines.size()).stream().map(line -> parseSighting(df, line))
                .collect(Collectors.toList());
    }

    private Sighting parseSighting(DateFormat df, String line) {
        String[] parts = line.split("\\t");
        try {
            return new Sighting(Integer.parseInt(parts[1]), parts[2], df.parse(parts[8]));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unparsable line: " + line);
        } catch (ParseException e) {
            throw new RuntimeException("Unparsable line: " + line);
        }
    }

    private long calcSpeciesCount(int year, List<Sighting> sightings) {
        return getSpeciesIdsForYear(sightings, year).size();
    }
    
    private long calcSpeciesCountFirstSighting(int year, List<Sighting> sightings) {
        Set<Integer> speciesSightedInPreviousYears = new HashSet<Integer>();
        for (int i = MY_FIRST_SIGHTING_YEAR; i < year; i++) {
            speciesSightedInPreviousYears.addAll(getSpeciesIdsForYear(sightings, i));
        }
        Set<Integer> speciesIdsForYear = getSpeciesIdsForYear(sightings, year);
        speciesIdsForYear.removeAll(speciesSightedInPreviousYears);
        return speciesIdsForYear.size();
    }

    private Set<Integer> getSpeciesIdsForYear(List<Sighting> sightings, final int year) {
        return sightings
        .stream()
        .filter(sighting -> yearMatches(year, sighting))
        .map(sighting -> sighting.getSpeciesId()).collect(Collectors.toSet());
    }
    

    private boolean yearMatches(int year, Sighting sighting) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sighting.getSightingDate());
        return cal.get(Calendar.YEAR) == year;
    }

    public static void main(String[] args) throws IOException {
        StatisticsCalculator calculator = new StatisticsCalculator();
        List<Sighting> sightings = calculator.readMySightings();
        
        for (int year = MY_FIRST_SIGHTING_YEAR; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
            System.out.println(year);
            System.out.println("\ttotal species: " + calculator.calcSpeciesCount(year, sightings));
            System.out.println("\tfirst time s.: " + calculator.calcSpeciesCountFirstSighting(year, sightings));
        }
    }

}
