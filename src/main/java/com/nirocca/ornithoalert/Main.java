package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    
    private static final String URL_PAGING_END = "&mp_item_per_page=60&mp_current_page=1";
    private static final Set<String> SPECIES_TO_EXCLUDE = new HashSet<>(Arrays.asList(
            "Keine Art",
            "Hausente",
            "Bauml\u00e4ufer, unbestimmt",
            "Gimpel (ssp. pyrrhula), Trompetergimpel",
            "Bl\u00e4ss- / Saatg\u00e4nse",
            "Tundrasaatg\u00e4nse",
            "Waldsaatg\u00e4nse",
            "Anser-G\u00e4nse, unbestimmt",
            "M\u00f6we, unbestimmt, unbestimmt",
            "M\u00f6wen, unbestimmt",
            "Gro\u00dfm\u00f6wen, unbestimmt",
            "Gro\u00dfm\u00f6we, unbestimmt",
            "Silber-oderMittelmeer-oderSteppenm\u00f6we",
            "Silber-_oder_Mittelmeer-_oder_Steppenm\u00f6we",
            "Silber- / Mittelmeer- / Steppenm\u00f6wen",
            "Stockente, Bastard, fehlfarben",
            "Taigabirkenzeisige",
            "Alpenbirkenzeisige",
            "Birkenzeisig (ssp. cabaret), Alpenbirkenzeisig",
            "Birkenzeisig (ssp. flammea), Taigabirkenzeisig",
            "Schwanzmeisen (ssp. caudatus)",
            "Schwanzmeise (ssp. caudatus)",
            "Stockenten Bastard fehlfarben"
            ));
    private static String url;
    private static SortBy sortBy;
    
    
    public static void main(String[] args) throws IOException, ParseException {
        initParams(args);
        
        MySightingsReader mySightingsReader = new MySightingsReader();
        List<String> mySightedSpeciesLatin = mySightingsReader.readMySightedSpeciesLatin();
        
        RegionLastSightingsReader regionLastSightingsReader = new RegionLastSightingsReader();
        List<Sighting> lastSightings = regionLastSightingsReader.read(url);
        
        lastSightings = sort(lastSightings, sortBy);
        
        lastSightings = lastSightings.stream()
                .filter(a->!SPECIES_TO_EXCLUDE.contains(a.getGermanNamePlural()))
                .filter(a->!mySightedSpeciesLatin.contains(a.getLatinName()))
                .collect(Collectors.toList());

        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        System.out.println("Markdown list:");
        for (Sighting sighting : lastSightings) {
            System.out.println(sighting + "<br>");
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanNamePlural(), sighting.getLatinName()));
        }
        
        System.out.println("\nMax 10:");
        maxSpecies.getMaxElements(10).stream().forEach(e->System.out.println(e));
        
        System.out.println("\nCoordinates for GPS Visualizer:");
        
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        coordinatesExporter.printCoordinates(lastSightings);
    }

    private static void initParams(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("url", true, "url to use (" + Arrays.toString(OrnithoUrl.values()) + " or custom");
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
        
        url = commandLine.getOptionValue("url", OrnithoUrl.GROSSRAUM_LAST_15_DAYS.name());
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
        
        sortBy = SortBy.valueOf(commandLine.getOptionValue("sort", SortBy.TIME.name()));
        
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
    
    private static enum SortBy {
        TIME,
        REGION,
        SPECIES
    }
    

}
