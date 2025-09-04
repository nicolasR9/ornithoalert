package com.nirocca.ornithoalert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.util.SightingFilter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {
    
    private static final String URL_PAGING_END = "&mp_item_per_page=60&mp_current_page=1";

    private static String url;
    private static SortBy sortBy;
    private static boolean onlyExactCoords;
    private static FilterMySightedSpecies filterMySightedSpecies;

    public static void main(String[] args) throws IOException, ParseException {
        initParams(args);

        List<Sighting> lastSightings = calcSightings(url, sortBy, filterMySightedSpecies);
        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        System.out.println("Markdown list:");
        for (Sighting sighting : lastSightings) {
            System.out.println(sighting + "<br>");
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanName(), sighting.getLatinName()));
        }
        
        System.out.println("\nMax:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);
        
        System.out.println("\nCoordinates for GPS Visualizer:");
        
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        coordinatesExporter.printCoordinates(new PrintParameters(lastSightings, onlyExactCoords, null, null, true));

    }

    public static List<Sighting> calcSightings(String url, SortBy sortBy) throws IOException {
        return calcSightings(url, sortBy, FilterMySightedSpecies.YES);
    }

    //must remain public (accessed by ornitho-service)
    public static List<Sighting> calcSightings(String url, SortBy sortBy, FilterMySightedSpecies filterOnlyThisYearParam) throws IOException {
        MySightingsReader mySightingsReader = new MySightingsReader();
        List<String> mySightedSpeciesLatin = readMySightings(filterOnlyThisYearParam, mySightingsReader);

        RegionLastSightingsReader regionLastSightingsReader = new RegionLastSightingsReader();
        url = sanitizeUrl(url);
        List<Sighting> lastSightings = regionLastSightingsReader.read(url);

        lastSightings = SightingFilter.filterOutNonRelevantSightings(lastSightings);
        lastSightings = lastSightings.stream()
            .filter(a -> !mySightedSpeciesLatin.contains(a.getLatinName()))
            .collect(Collectors.toList());
        lastSightings = sort(lastSightings, sortBy);

        return lastSightings;
    }

    private static String sanitizeUrl(String url) {
        if (!url.endsWith(URL_PAGING_END)) {
            url += URL_PAGING_END;
        }
        return url;
    }

    private static List<String> readMySightings(FilterMySightedSpecies filterOnlyThisYearParam, MySightingsReader mySightingsReader)
        throws IOException {
        return switch (filterOnlyThisYearParam) {
            case YES -> mySightingsReader.readMySightedSpeciesLatin();
            case NO -> Collections.emptyList();
            case ONLY_THIS_YEAR -> mySightingsReader.readMySightedSpeciesLatinThisYear();
            case ONLY_THIS_WEEKEND -> mySightingsReader.readMySightedSpeciesLatinThisWeekend();
        };
    }

    private static void initParams(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("url", true, "url to use (" + Arrays.toString(OrnithoUrl.values()) + " or custom");
        options.addOption("exact", true, "prints coords only if exact");
        options.addOption("cy", true, "show also observations if species was sighted in previous years (but not this year)");
        options.addOption("h", false, "print help");
        
        Option sortOption = new Option("sort", true, "sort (TIME, REGION, SPECIES)");
        sortOption.setType(SortBy.class);
        options.addOption(sortOption);
        
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        
        if (commandLine.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Main", options );
            System.exit(0);
        }
        
        url = commandLine.getOptionValue("url", Constants.DEFAULT_URL.name());
        try {
            OrnithoUrl urlEnum = OrnithoUrl.valueOf(url);
            url = urlEnum.getUrl();
            System.out.println("using URL: " + urlEnum);
        } catch (Exception e) {
            //use url as it was passed
            System.out.println("using URL: " + url);
        }
        
        sortBy = SortBy.valueOf(commandLine.getOptionValue("sort", Constants.DEFAULT_SORT_ORDER.name()));

        onlyExactCoords = Boolean.parseBoolean(commandLine.getOptionValue("exact", "false"));
        boolean filterOnlyThisYear = Boolean.parseBoolean(commandLine.getOptionValue("cy", "false"));
        filterMySightedSpecies = filterOnlyThisYear ? FilterMySightedSpecies.ONLY_THIS_YEAR : FilterMySightedSpecies.YES;

        System.out.println("filter: " + filterMySightedSpecies);
        System.out.println("using SORT: " + sortBy);
    }


    private static List<Sighting> sort(List<Sighting> lastSightings, SortBy sortBy) {
        Comparator<? super Sighting> comparator = null;
        switch (sortBy) {
            case TIME -> {
                return lastSightings;
            }
            case REGION -> comparator = Comparator.comparing(Sighting::getLocationText);
            case SPECIES -> comparator = Comparator.comparing(Sighting::getGermanName);
        }
        return lastSightings.stream().sorted(comparator).collect(Collectors.toList());
    }
}
