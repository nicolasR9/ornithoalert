package com.nirocca.ornithoalert.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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
            return new Sighting(new Species(Integer.parseInt(parts[1]), parts[2]), df.parse(parts[7]));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unparsable number: " + parts[1]);
        } catch (ParseException e) {
            throw new RuntimeException("Unparsable line: " + line);
        }
    }

    private long calcSpeciesCount(int year, List<Sighting> sightings) {
        return getSpeciesForYear(sightings, year).size();
    }
    
    private long calcSpeciesCountFirstSighting(int year, List<Sighting> sightings) {
        Set<Species> speciesSightedInPreviousYears = getSpeciesUpToYear(sightings, year);
        Set<Species> speciesForYear = getSpeciesForYear(sightings, year);
        speciesForYear.removeAll(speciesSightedInPreviousYears);
        return speciesForYear.size();
    }

    private long calcSpeciesCountUntilDate(int year, List<Sighting> sightings) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        List<Sighting> filteredSightings = sightings.stream().filter(s -> s.getSightingDate().before(cal.getTime()))
            .collect(Collectors.toList());
        return calcSpeciesCount(year, filteredSightings);
    }

    private Set<Species> getSpeciesUpToYear(List<Sighting> sightings, int lastYearExclusive) {
        Set<Species> speciesSightedInPreviousYears = new HashSet<>();
        for (int i = MY_FIRST_SIGHTING_YEAR; i < lastYearExclusive; i++) {
            speciesSightedInPreviousYears.addAll(getSpeciesForYear(sightings, i));
        }
        return speciesSightedInPreviousYears;
    }

    private Set<Species> getSpeciesForYear(List<Sighting> sightings, final int year) {
        return sightings
        .stream()
        .filter(sighting -> yearMatches(year, sighting))
        .map(Sighting::getSpecies).collect(Collectors.toSet());
    }
    

    private boolean yearMatches(int year, Sighting sighting) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(sighting.getSightingDate());
        return cal.get(Calendar.YEAR) == year;
    }

    private Set<Species> calcPreviouslySightedSpeciesNotSightedInTheCurrentYear(List<Sighting> sightings) {

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        Set<Species> previousSpecies = getSpeciesUpToYear(sightings, thisYear);
        Set<Species> speciesThisYear = getSpeciesForYear(sightings, thisYear);

        previousSpecies.removeAll(speciesThisYear);
        return previousSpecies;
    }

    private Set<Species> calcSpeciesSightedEveryYearButNoInTheCurrentYear(List<Sighting> sightings) {
        Set<Species> everyYear = getSpeciesForYear(sightings, MY_FIRST_SIGHTING_YEAR);
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = MY_FIRST_SIGHTING_YEAR + 1; year < thisYear; year++) {
            everyYear = everyYear.stream().filter(getSpeciesForYear(sightings, year)::contains).collect(Collectors.toSet());
        }

        Set<Species> speciesThisYear = getSpeciesForYear(sightings, thisYear);

        everyYear.removeAll(speciesThisYear);
        return everyYear;
    }

    private Set<Species> calcSpeciesSightedAlmostEveryYearButNoInTheCurrentYear(List<Sighting> sightings) {
        Map<Species, Integer> count = new HashMap<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int year = MY_FIRST_SIGHTING_YEAR; year < thisYear; year++) {
            getSpeciesForYear(sightings, year).forEach(s -> {if (!count.containsKey(s)) count.put(s, 0); count.put(s, count.get(s) + 1);} );
        }

        Set<Species> almostAllYearsSpecies = count.entrySet().stream().filter(e -> e.getValue() >= (thisYear - MY_FIRST_SIGHTING_YEAR -1))
            .map(Entry::getKey).collect(Collectors.toSet());

        Set<Species> speciesThisYear = getSpeciesForYear(sightings, thisYear);

        almostAllYearsSpecies.removeAll(speciesThisYear);
        return almostAllYearsSpecies;
    }

    public static void main(String[] args) throws IOException {
        StatisticsCalculator calculator = new StatisticsCalculator();
        List<Sighting> sightings = calculator.readMySightings();
        
        for (int year = MY_FIRST_SIGHTING_YEAR; year <= Calendar.getInstance().get(Calendar.YEAR); year++) {
            System.out.println(year);
            System.out.println("\ttotal species: " + calculator.calcSpeciesCount(year, sightings));
            System.out.println("\tfirst time s.: " + calculator.calcSpeciesCountFirstSighting(year, sightings));
            System.out.println("\tspecies until date: " + calculator.calcSpeciesCountUntilDate(year, sightings));
        }

        Set<Species> s = calculator.calcPreviouslySightedSpeciesNotSightedInTheCurrentYear(sightings);
        System.out.printf("\nPreviously sighted species not sighted this year (%d):%n", s.size());
        s.forEach(x->System.out.println(x.getSpeciesName()));

        s = calculator.calcSpeciesSightedAlmostEveryYearButNoInTheCurrentYear(sightings);
        System.out.printf("\nSighted every previous year except at most one, but not this year (%d):%n", s.size());
        s.forEach(x->System.out.println(x.getSpeciesName()));

        s = calculator.calcSpeciesSightedEveryYearButNoInTheCurrentYear(sightings);
        System.out.printf("\nSighted every previous year, but not this year (%d):%n", s.size());
        s.forEach(x->System.out.println(x.getSpeciesName()));


    }


}
