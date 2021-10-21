package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
import java.util.function.Predicate;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    
    private static final String URL_PAGING_END = "&mp_item_per_page=60&mp_current_page=1";

    private static String url;
    private static SortBy sortBy;
    private static boolean onlyExactCoords;
    private static boolean filterOnlyThisYear;

    
    public static void main(String[] args) throws IOException, ParseException {
        initParams(args);

        List<Sighting> lastSightings = calcSightings(url, sortBy, filterOnlyThisYear);
        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        System.out.println("Markdown list:");
        for (Sighting sighting : lastSightings) {
            System.out.println(sighting + "<br>");
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanNamePlural(), sighting.getLatinName()));
        }
        
        System.out.println("\nMax 10:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);
        
        System.out.println("\nCoordinates for GPS Visualizer:");
        
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        coordinatesExporter.printCoordinates(lastSightings, onlyExactCoords);
    }

    public static List<Sighting> calcSightings(String url, SortBy sortBy) throws IOException {
        return calcSightings(url, sortBy, false);
    }

    //must remain public (accessed by ornitho-service)
    public static List<Sighting> calcSightings(String url, SortBy sortBy, boolean filterOnlyThisYearParam) throws IOException {
        filterOnlyThisYear = filterOnlyThisYearParam;
        MySightingsReader mySightingsReader = new MySightingsReader();
        List<String> mySightedSpeciesLatin = filterOnlyThisYear ?
            mySightingsReader.readMySightedSpeciesLatinThisYear() :
            mySightingsReader.readMySightedSpeciesLatin();

        RegionLastSightingsReader regionLastSightingsReader = new RegionLastSightingsReader();
        List<Sighting> lastSightings = regionLastSightingsReader.read(url);

        lastSightings = sort(lastSightings, sortBy);

        lastSightings = lastSightings.stream()
                .filter(a->!"0".equals(a.getCount()))
                .filter(isWithoutCommonFilterPattern())
                .filter(a->!Constants.SPECIES_TO_EXCLUDE.contains(a.getGermanNamePlural()))
                .filter(a->!mySightedSpeciesLatin.contains(a.getLatinName()))
                .collect(Collectors.toList());


        return lastSightings;
    }

    private static Predicate<Sighting> isWithoutCommonFilterPattern() {
        return a->!a.getGermanNamePlural().contains("unbestimmt")
            && !a.getGermanNamePlural().contains("_x_")
            && !a.getGermanNamePlural().contains(" x ");
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
            if (!url.endsWith(URL_PAGING_END)) {
                url += URL_PAGING_END;
            }
            System.out.println("using URL: " + url);
        }
        
        sortBy = SortBy.valueOf(commandLine.getOptionValue("sort", Constants.DEFAULT_SORT_ORDER.name()));

        onlyExactCoords = Boolean.parseBoolean(commandLine.getOptionValue("exact", "false"));
        filterOnlyThisYear = Boolean.parseBoolean(commandLine.getOptionValue("cy", "false"));

        System.out.println("using SORT: " + sortBy);
    }


    private static List<Sighting> sort(List<Sighting> lastSightings, SortBy sortBy) {
        Comparator<? super Sighting> comparator = null;
        switch (sortBy) {
            case TIME:
                return lastSightings;
            case REGION:
                comparator = (a, b)->a.getLocation().compareTo(b.getLocation());
                break;
            case SPECIES:
                comparator = (a, b)->a.getGermanNamePlural().compareTo(b.getGermanNamePlural());
                break;
        }
        return lastSightings.stream().sorted(comparator).collect(Collectors.toList());
    }
    

    

}
