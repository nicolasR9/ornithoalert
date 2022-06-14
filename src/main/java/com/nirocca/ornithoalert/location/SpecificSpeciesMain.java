package com.nirocca.ornithoalert.location;

import static com.nirocca.ornithoalert.Species.ALEXANDERSITTICH;
import static com.nirocca.ornithoalert.Species.ALPENBRAUNELLE;
import static com.nirocca.ornithoalert.Species.ALPENSEGLER;
import static com.nirocca.ornithoalert.Species.BAUMFALKE;
import static com.nirocca.ornithoalert.Species.CHILEFLAMINGO;
import static com.nirocca.ornithoalert.Species.DREIZEHENSPECHT;
import static com.nirocca.ornithoalert.Species.FLUSSREGENPFEIFER;
import static com.nirocca.ornithoalert.Species.GELBBRAUEN_LAUBSAENGER;
import static com.nirocca.ornithoalert.Species.HALSBANDSCHNAEPPER;
import static com.nirocca.ornithoalert.Species.HASELHUHN;
import static com.nirocca.ornithoalert.Species.KLEINES_SUMPFHUHN;
import static com.nirocca.ornithoalert.Species.LACHSEESCHWALBE;
import static com.nirocca.ornithoalert.Species.NACHTREIHER;
import static com.nirocca.ornithoalert.Species.ORPHEUSSPOETTER;
import static com.nirocca.ornithoalert.Species.REBHUHN;
import static com.nirocca.ornithoalert.Species.ROSAFLAMINGO;
import static com.nirocca.ornithoalert.Species.ROTFUSSFALKE;
import static com.nirocca.ornithoalert.Species.ROTKEHLPIEPER;
import static com.nirocca.ornithoalert.Species.ROTKOPFWUERGER;
import static com.nirocca.ornithoalert.Species.SANDREGENPFEIFER;
import static com.nirocca.ornithoalert.Species.SCHLAGSCHWIRL;
import static com.nirocca.ornithoalert.Species.SCHWARZKOPF_RUDERENTE;
import static com.nirocca.ornithoalert.Species.SEEREGENPFEIFER;
import static com.nirocca.ornithoalert.Species.SEIDENREIHER;
import static com.nirocca.ornithoalert.Species.SEIDENSAENGER;
import static com.nirocca.ornithoalert.Species.SPERBERGRASMUECKE;
import static com.nirocca.ornithoalert.Species.SPORNAMMER;
import static com.nirocca.ornithoalert.Species.STEINADLER;
import static com.nirocca.ornithoalert.Species.TANNENHAEHER;
import static com.nirocca.ornithoalert.Species.TEICHWASSERLAEUFER;
import static com.nirocca.ornithoalert.Species.TORDALK;
import static com.nirocca.ornithoalert.Species.TURTELTAUBE;
import static com.nirocca.ornithoalert.Species.WACHTEL;
import static com.nirocca.ornithoalert.Species.WEISSKOPF_RUDERENTE;
import static com.nirocca.ornithoalert.Species.WEISSRUECKENSPECHT;
import static com.nirocca.ornithoalert.Species.WEISSSTORCH;
import static com.nirocca.ornithoalert.Species.WESPENBUSSARD;
import static com.nirocca.ornithoalert.Species.ZAUNAMMER;
import static com.nirocca.ornithoalert.Species.ZIPPAMMER;
import static com.nirocca.ornithoalert.Species.ZITRONENZEISIG;
import static com.nirocca.ornithoalert.Species.ZWERGGANS;
import static com.nirocca.ornithoalert.Species.ZWERGKANADAGANS;
import static com.nirocca.ornithoalert.Species.ZWERGOHREULE;
import static com.nirocca.ornithoalert.Species.ZWERGSCHNAEPPER;
import static com.nirocca.ornithoalert.Species.ZWERGSCHNEPFE;

import com.nirocca.ornithoalert.ColorProvider;
import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecificSpeciesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=206&sp_tg=1&sp_DChoice=range&sp_DFrom=01.01.2020&sp_DTo=31.12.2020&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=&sp_S=145&sp_Cat[never]=1&sp_Cat[veryrare]=1&sp_Cat[rare]=1&sp_Cat[unusual]=1&sp_Family=1&sp_PChoice=all&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010100011001101100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000010000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=01.01.2020&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1";
    private static final int[] yearsToCheck = {2021};
    private static final String FROM_DATE = "01.01.";
    private static final String TO_DATE = "31.12.";

    private static final Species[] SPECIES = {ORPHEUSSPOETTER, TORDALK, ALEXANDERSITTICH,
    WEISSRUECKENSPECHT, LACHSEESCHWALBE, SPORNAMMER, ROTFUSSFALKE, GELBBRAUEN_LAUBSAENGER, SEEREGENPFEIFER, ROTKEHLPIEPER,
    ZWERGGANS, KLEINES_SUMPFHUHN, ZIPPAMMER, ZWERGKANADAGANS, DREIZEHENSPECHT, ZITRONENZEISIG, SEIDENSAENGER, ROSAFLAMINGO,
    CHILEFLAMINGO, SCHWARZKOPF_RUDERENTE, WEISSKOPF_RUDERENTE, HASELHUHN, ALPENBRAUNELLE, ROTKOPFWUERGER, TEICHWASSERLAEUFER,
    STEINADLER};

    private static final boolean separateFiles = false;

    private static final String PATH_TO_COORDS_DIR = "/Users/nirocca/tmp/voegel/tmp/";

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

                List<Sighting> newSightings = Main.calcSightings(url, SortBy.TIME, FilterMySightedSpecies.NO);
                System.out.printf("Added %d sightings.%n", newSightings.size());
                sightings.addAll(newSightings);
            }

            if (separateFiles) {
                try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + species.name() + ".txt")) {
                    coordinatesExporter.printCoordinates(sightings, false, out, colorProvider);
                }
            }
        }

        System.out.printf("Printing %d sightings.%n", sightings.size());

        if (!separateFiles){
            try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + "specificSpeciesCoords.txt")) {
                coordinatesExporter.printCoordinates(sightings, false, out, colorProvider);
            }
        }
    }

    private static final class FixedColorProvider implements ColorProvider{
        private Deque<String> COLORS = new ArrayDeque<>(Arrays.asList("darkgreen", "darkblue", "black", "white", "yellow", "violet",
            "lightgreen", "teal", "beige", "darkslategray", "aquamarine", "lightblue",  "sandybrown"));
        private Map<String, String> latinToColor = new HashMap<>();

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
