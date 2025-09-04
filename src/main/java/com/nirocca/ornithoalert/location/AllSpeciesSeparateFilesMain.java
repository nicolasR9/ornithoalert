package com.nirocca.ornithoalert.location;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.MaxNElementsCollector;
import com.nirocca.ornithoalert.MostFrequentColorProvider;
import com.nirocca.ornithoalert.PrintParameters;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;

public class AllSpeciesSeparateFilesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=200&sp_tg=1&sp_DChoice=range&sp_DFrom=01.08.2025&sp_DTo=01.08.2025&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=all&speciesFilter=&sp_S=1148&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100010000000000000000000000000000000000000000000&p_cc=200&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=02.06.2025&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int[] yearsToCheck = {2022, 2023, 2024};
    private static final String FROM_DATE = "20.10.";
    //private static final String FROM_DATE = "01.08.";
    private static final String TO_DATE = "02.11.";

    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "/allSpeciesSeparateFiles/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();

        Set<Species> exclude = Set.of(
            Species.HERINGSMOEWE,
            Species.SILBERMOEWE,
            Species.ALPENSTRANDLAEUFER,
            Species.AUSTERNFISCHER,
            Species.ROTSCHENKEL,
            Species.PFUHLSCHNEPFE
        );

        List<Sighting> sightings = new ArrayList<>();
        for (int year : yearsToCheck) {
            System.out.println("Year: " + year);
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + year + "&");

            List<Sighting> forYear = Main.calcSightings(url, SortBy.SPECIES, FilterMySightedSpecies.ONLY_THIS_YEAR);
            forYear = forYear.stream().filter(s -> !exclude.contains(Species.getById(s.getSpeciesId()))).toList();
            sightings.addAll(forYear);
        }

        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        System.out.println("Markdown list:");
        for (Sighting sighting : sightings) {
            System.out.println(sighting + "<br>");
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanName(), sighting.getLatinName()));
        }

        System.out.println("\nMax:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);

        Map<String, List<Sighting>>
            map = sightings.stream().collect(Collectors.groupingBy(sighting -> Species.getById(sighting.getSpeciesId()).name()));
        System.out.println("Sightings read - printing coordinates.");

        for (Entry<String, List<Sighting>> entry : map.entrySet()) {
            try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + entry.getKey() + ".txt")) {
                coordinatesExporter.printCoordinates(new PrintParameters(entry.getValue(), false, out, new MostFrequentColorProvider(entry.getValue()), true));
            }
        }
        System.out.println("Done.");
    }
}
