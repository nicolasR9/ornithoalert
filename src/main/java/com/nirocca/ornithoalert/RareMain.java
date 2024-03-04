package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.statistics.Species;
import com.nirocca.ornithoalert.statistics.StatisticsCalculator;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RareMain {
    public static void main(String[] args) throws IOException {
        List<com.nirocca.ornithoalert.statistics.Sighting> mySightings = StatisticsCalculator.readMySightings();
        Set<com.nirocca.ornithoalert.statistics.Species> frequentSpecies =
                StatisticsCalculator.calcSpeciesSightedAlmostEveryYear(mySightings, 3);
        Set<String> frequentLatin = frequentSpecies.stream().map(Species::getLatinName).collect(Collectors.toSet());
        List<Sighting> lastSightings = Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_8_DAYS.getUrl(), Constants.SortBy.SPECIES, Constants.FilterMySightedSpecies.ONLY_THIS_YEAR);
        List<Sighting> sightings = lastSightings.stream().filter(s -> !frequentLatin.contains(s.getLatinName())).collect(Collectors.toList());

        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        System.out.println("Markdown list:");
        for (Sighting sighting : sightings) {
            System.out.println(sighting + "<br>");
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanNamePlural(), sighting.getLatinName()));
        }

        System.out.println("\nMax:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);

        System.out.println("\nCoordinates for GPS Visualizer:");

        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        coordinatesExporter.printCoordinates(new PrintParameters(sightings, false, null, null, true));

    }
}
