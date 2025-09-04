package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.MaxNElementsCollector.ElementWithCount;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
import java.util.List;
import java.util.stream.Collectors;

public class MostFrequentColorProvider implements ColorProvider {

    private final List<String> latinNamesByFrequency;
    private static final String[] COLORS = {"darkgreen", "darkblue", "black", "white", "yellow", "violet",
        "lightgreen", "teal", "beige", "darkslategray", "aquamarine", "lightblue",  "sandybrown",
    };
    private static final String FALLBACK_COLOR = "red";

    public MostFrequentColorProvider(List<Sighting> sightings) {
        latinNamesByFrequency = createLatinNamesByFrequency(sightings);
    }

    private List<String> createLatinNamesByFrequency(List<Sighting> sightings) {
        final List<String> latinNamesByFrequency;
        MaxNElementsCollector<LatinComparedSpecies> frequencies = new MaxNElementsCollector<>();
        sightings.forEach(s -> frequencies.add(new LatinComparedSpecies(s.getGermanName(), s.getLatinName())));
        List<ElementWithCount<LatinComparedSpecies>> maxElements = frequencies.getMaxElements(100);
        latinNamesByFrequency = maxElements.stream().map(s -> s.getElement().getLatinName()).collect(Collectors.toList());
        return latinNamesByFrequency;
    }

    @Override
    public String getColor(Sighting sighting) {
        int pos = latinNamesByFrequency.indexOf(sighting.getLatinName());
        return (pos >= 0 && pos < COLORS.length) ? COLORS[pos] : FALLBACK_COLOR;
    }
}
