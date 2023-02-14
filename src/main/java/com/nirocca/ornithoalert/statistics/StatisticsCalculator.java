package com.nirocca.ornithoalert.statistics;

import com.nirocca.ornithoalert.util.SightingFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;

public class StatisticsCalculator {

    private static final int MY_FIRST_SIGHTING_YEAR = 2017;

    public static List<Sighting> readMySightings() throws IOException {
        List<String> lines = IOUtils.readLines(
                StatisticsCalculator.class.getResourceAsStream("/ornitho_export_meine.txt"));
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        List<Sighting> allSightings = lines.subList(2, lines.size()).stream().map(line -> parseSighting(df, line))
            .collect(Collectors.toList());

        return SightingFilter.filterOutNonRelevantSightingsStats(allSightings);
    }

    private static Sighting parseSighting(DateFormat df, String line) {
        String[] parts = line.split("\\t");
        try {
            return new Sighting(new Species(Integer.parseInt(parts[1]), parts[2], parts[3]), df.parse(parts[7]), parts[24]);
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
        return calcSpeciesFirstSighting(year, sightings).size();
    }

    private Set<Species> calcSpeciesFirstSighting(int year, List<Sighting> sightings) {
        Set<Species> speciesSightedInPreviousYears = getSpeciesUpToYear(sightings, year);
        Set<Species> speciesForYear = getSpeciesForYear(sightings, year);
        speciesForYear.removeAll(speciesSightedInPreviousYears);
        return speciesForYear;
    }

    private long calcSpeciesCountUntilDate(int year, List<Sighting> sightings) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        //cal.add(Calendar.DAY_OF_MONTH, 3);
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

    private static Set<Species> getSpeciesForYear(List<Sighting> sightings, final int year) {
        return sightings
        .stream()
        .filter(sighting -> yearMatches(year, sighting))
        .map(Sighting::getSpecies).collect(Collectors.toSet());
    }
    

    private static boolean yearMatches(int year, Sighting sighting) {
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

    private  static Set<Species> calcSpeciesSightedEveryYearButNoInTheCurrentYear(List<Sighting> sightings) {
        Set<Species> everyYear = getSpeciesForYear(sightings, MY_FIRST_SIGHTING_YEAR);
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = MY_FIRST_SIGHTING_YEAR + 1; year < thisYear; year++) {
            everyYear = everyYear.stream().filter(getSpeciesForYear(sightings, year)::contains).collect(Collectors.toSet());
        }

        Set<Species> speciesThisYear = getSpeciesForYear(sightings, thisYear);

        everyYear.removeAll(speciesThisYear);
        return everyYear;
    }

    private static Set<Species> calcSpeciesSightedAlmostEveryYearButNoInTheCurrentYear(List<Sighting> sightings, int exceptCount) {

        Set<Species> almostAllYearsSpecies = calcSpeciesSightedAlmostEveryYear(sightings, exceptCount);

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        Set<Species> speciesThisYear = getSpeciesForYear(sightings, thisYear);
        almostAllYearsSpecies.removeAll(speciesThisYear);
        return almostAllYearsSpecies;
    }

    public static Set<Species> calcSpeciesSightedAlmostEveryYear(List<Sighting> sightings, int exceptCount) {
        Map<Species, Integer> count = new HashMap<>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int year = MY_FIRST_SIGHTING_YEAR; year < thisYear; year++) {
            getSpeciesForYear(sightings, year).forEach(s -> {if (!count.containsKey(s)) count.put(s, 0); count.put(s, count.get(s) + 1);} );
        }

        return count.entrySet().stream().filter(e -> e.getValue() >= (thisYear - MY_FIRST_SIGHTING_YEAR - exceptCount))
            .map(Entry::getKey).collect(Collectors.toSet());
    }


    public static void main(String[] args) throws IOException {
        StatisticsCalculator calculator = new StatisticsCalculator();
        List<Sighting> sightings = readMySightings();

        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = MY_FIRST_SIGHTING_YEAR; year <= thisYear; year++) {
            System.out.println(year);
            System.out.println("\ttotal species: " + calculator.calcSpeciesCount(year, sightings));
            System.out.println("\tfirst time s.: " + calculator.calcSpeciesCountFirstSighting(year, sightings));
            System.out.println("\tspecies until date: " + calculator.calcSpeciesCountUntilDate(year, sightings));
        }

        Set<Species> s = calculator.calcPreviouslySightedSpeciesNotSightedInTheCurrentYear(sightings);
        System.out.printf("\nPreviously sighted species not sighted this year: %d%n", s.size());

        s = calcSpeciesSightedAlmostEveryYearButNoInTheCurrentYear(sightings, 2);
        System.out.printf("\nSighted every previous year except at most 2, but not this year: %d%n", s.size());
        if (s.size() < 50)
            s.forEach(x->System.out.println(x.getSpeciesName()));

        s = calcSpeciesSightedAlmostEveryYearButNoInTheCurrentYear(sightings, 1);
        System.out.printf("\nSighted every previous year except at most 1, but not this year: %d%n", s.size());
        if (s.size() < 50)
            s.forEach(x->System.out.println(x.getSpeciesName()));

        s = calcSpeciesSightedEveryYearButNoInTheCurrentYear(sightings);
        System.out.printf("\nSighted every previous year, but not this year (%d):%n", s.size());
        if (s.size() < 50)
            s.forEach(x->System.out.println(x.getSpeciesName()));

        System.out.println("Sighted first this year: " + calculator.calcSpeciesFirstSighting(thisYear, sightings));
    }


}
