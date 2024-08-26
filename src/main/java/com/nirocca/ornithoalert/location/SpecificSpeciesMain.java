package com.nirocca.ornithoalert.location;

import static com.nirocca.ornithoalert.Species.ALPENBRAUNELLE;
import static com.nirocca.ornithoalert.Species.DREIZEHENSPECHT;
import static com.nirocca.ornithoalert.Species.GRAUSPECHT;
import static com.nirocca.ornithoalert.Species.HASELHUHN;
import static com.nirocca.ornithoalert.Species.KLEINES_SUMPFHUHN;
import static com.nirocca.ornithoalert.Species.REBHUHN;
import static com.nirocca.ornithoalert.Species.SANDERLING;
import static com.nirocca.ornithoalert.Species.SCHWARZKOPFMOEWE;
import static com.nirocca.ornithoalert.Species.SICHELSTRANDLAEUFER;
import static com.nirocca.ornithoalert.Species.TANNENHAEHER;
import static com.nirocca.ornithoalert.Species.TEMMINCKSTRANDLAEUFER;
import static com.nirocca.ornithoalert.Species.TUEPFELSUMPFHUHN;
import static com.nirocca.ornithoalert.Species.WEISSRUECKENSPECHT;
import static com.nirocca.ornithoalert.Species.ZWERGDOMMEL;
import static com.nirocca.ornithoalert.Species.ZWERGSTRANDLAEUFER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nirocca.ornithoalert.ColorProvider;
import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.PrintParameters;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;

public class SpecificSpeciesMain {

    //private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=duration&p_cc=206&sp_tg=1&sp_DFrom=02.07.2024&sp_DTo=02.07.2024&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=30&sp_SChoice=species&speciesFilter=&sp_S=341&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=206&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=02.07.2024&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=213&sp_tg=1&sp_DChoice=range&sp_DFrom=26.05.2024&sp_DTo=26.05.2024&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=&sp_S=326&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000010000000000000010000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=213&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=10.11.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int[] yearsToCheck = {2021, 2022, 2023, 2024};
    private static final String FROM_DATE = "01.01.";
    private static final String TO_DATE = "31.12.";

    private static final Species[] SPECIES1 = {
        DREIZEHENSPECHT,
        SCHWARZKOPFMOEWE,
        WEISSRUECKENSPECHT,
        TANNENHAEHER,
        TEMMINCKSTRANDLAEUFER,
        GRAUSPECHT,
        HASELHUHN,
        SANDERLING,
        SICHELSTRANDLAEUFER,
        TUEPFELSUMPFHUHN,
        ZWERGSTRANDLAEUFER,
        REBHUHN,
        ZWERGDOMMEL,
        KLEINES_SUMPFHUHN};

    private static final Species[] SPECIES = {
        DREIZEHENSPECHT,
        WEISSRUECKENSPECHT,
        TANNENHAEHER,
        GRAUSPECHT,
        HASELHUHN,
        ALPENBRAUNELLE};

    private static final boolean separateFiles = true;

    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "specificSpecies/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        ColorProvider colorProvider = new FixedColorProvider();

        List<Sighting> sightings = new ArrayList<>();

        for (Species species: SPECIES) {
            if (separateFiles) {
                sightings = new ArrayList<>();
            }

            System.out.println("Species: " + species.name());
            for (int year : yearsToCheck) {
                String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
                url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + year + "&");
                url = url.replaceAll("sp_S=[^&]+&", "sp_S=" + species.getOrnithoSpeciesId() + "&");
                System.out.println(url);

                List<Sighting> newSightings = Main.calcSightings(url, SortBy.TIME, FilterMySightedSpecies.NO);
                System.out.printf("Added %d sightings.%n", newSightings.size());
                sightings.addAll(newSightings);
            }

            if (separateFiles) {
                try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + species.name() + ".txt")) {
                    coordinatesExporter.printCoordinates(new PrintParameters(sightings, false, out, colorProvider, true));
                }
            }
        }

        System.out.printf("Printing %d sightings.%n", sightings.size());

        if (!separateFiles){
            try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + "specificSpeciesCoords.txt")) {
                coordinatesExporter.printCoordinates(new PrintParameters(sightings, false, out, colorProvider, true));
            }
        }
    }

    private static final class FixedColorProvider implements ColorProvider{
        private final Deque<String> COLORS = new ArrayDeque<>(Arrays.asList("darkgreen", "darkblue", "black", "white", "yellow", "violet",
            "lightgreen", "teal", "beige", "darkslategray", "aquamarine", "lightblue",  "sandybrown"));
        private final Map<String, String> latinToColor = new HashMap<>();

        @Override
        public String getColor(Sighting sighting) {
            if (!latinToColor.containsKey(sighting.getLatinName())) {
                if (COLORS.isEmpty()) {
                    COLORS.push("red");
                }
                latinToColor.put(sighting.getLatinName(), COLORS.pop());
            }
            return latinToColor.get(sighting.getLatinName());
        }
    }
}
