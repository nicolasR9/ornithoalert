package com.nirocca.ornithoalert.location;

import static com.nirocca.ornithoalert.Species.ALPENBRAUNELLE;
import static com.nirocca.ornithoalert.Species.BARTGEIER;
import static com.nirocca.ornithoalert.Species.GAENSEGEIER;
import static com.nirocca.ornithoalert.Species.HASELHUHN;
import static com.nirocca.ornithoalert.Species.SAATGANS__SSP__FABALIS___WALDSAATGANS;
import static com.nirocca.ornithoalert.Species.SCHLANGENADLER;
import static com.nirocca.ornithoalert.Species.STEINHUHN;
import static com.nirocca.ornithoalert.Species.STEINROETEL;
import static com.nirocca.ornithoalert.Species.ZITRONENZEISIG;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=duration&p_cc=202&sp_tg=1&sp_DChoice=range&sp_DFrom=01.01.2024&sp_DTo=31.12.2024&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=&sp_S=53&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=202&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=04.11.2024&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int[] yearsToCheck = {2020, 2021, 2022, 2023, 2024, 2024};
    private static final String FROM_DATE = "01.12.";
    private static final String TO_DATE = "31.01.";

    private static final Species[] SPECIES = {
        /*PRACHTEIDERENTE,
        BERGHAENFLING,
        ZWERGSCHWAN,
        OHRENTAUCHER,
        PRACHTTAUCHER,
        WALDKAUZ,
        SAMTENTE,
        EISENTE,
        SEIDENSCHWANZ*/
        SAATGANS__SSP__FABALIS___WALDSAATGANS
        };

    private static final Species[] SPECIES1 = {
        HASELHUHN,
        ALPENBRAUNELLE,
        GAENSEGEIER,
        BARTGEIER,
        ZITRONENZEISIG,
        STEINHUHN,
        STEINROETEL,
        SCHLANGENADLER
    };

    private static final boolean separateFiles = false;

    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "specificSpecies/";

    public static void main(String[] args) throws IOException, ParseException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        ColorProvider colorProvider = new FixedColorProvider();

        List<Sighting> sightings = new ArrayList<>();


        for (Species species: SPECIES) {
            Stats stats = new Stats();
            if (separateFiles) {
                sightings = new ArrayList<>();
            }

            System.out.println("Species: " + species.name());
            for (int year : yearsToCheck) {
                String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
                url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + getToDate(year) + "&");
                url = url.replaceAll("sp_S=[^&]+&", "sp_S=" + species.getOrnithoSpeciesId() + "&");
                System.out.println(url);

                List<Sighting> newSightings = Main.calcSightings(url, SortBy.TIME, FilterMySightedSpecies.NO);
                System.out.printf("Added %d sightings.%n", newSightings.size());
                stats.add(newSightings.size(), year - yearsToCheck[0]);
                sightings.addAll(newSightings);
            }
            System.out.printf("Stats: avg: %s, med: %s%n", stats.calculateAverage(), stats.calculateMedian());

            if (separateFiles) {
                try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + species.name() + ".txt")) {
                    coordinatesExporter.printCoordinates(new PrintParameters(sightings, true, out, colorProvider, true));
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

    private static String getToDate(int year) throws ParseException {
        String toDate = TO_DATE + year;
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date td = dateFormat.parse(toDate);
        if (td.after(new Date())) {td = new Date();}
        var fd = dateFormat.parse(FROM_DATE + year);
        if (fd.after(td)) {  // over year end - add one year
            Calendar cal = Calendar.getInstance();
            cal.setTime(td);
            cal.add(Calendar.YEAR, 1);
            td = cal.getTime();
        }
        return dateFormat.format(td);
    }

    private static final class FixedColorProvider implements ColorProvider{
        private final Deque<String> COLORS = new ArrayDeque<>(Arrays.asList("darkgreen", "darkblue", "black", "white", "yellow", "violet",
            "lightgreen", "teal", "beige", "darkslategray", "aquamarine", "lightblue",  "sandybrown"));
        private final Map<String, String> latinToColor = new HashMap<>();

        @Override
        public String getColor(Sighting sighting) {
            if (!latinToColor.containsKey(sighting.latinName())) {
                if (COLORS.isEmpty()) {
                    COLORS.push("red");
                }
                latinToColor.put(sighting.latinName(), COLORS.pop());
            }
            return latinToColor.get(sighting.latinName());
        }
    }
}

class Stats {
    private final int[] counts = new int[5];

    public void add(int v, int index) {
        counts[index] = v;
    }

    public double calculateAverage() {
        int sum = 0;
        for (int count : counts) {
            sum += count;
        }
        return (double) sum / counts.length;
    }

    public double calculateMedian() {
        int[] sortedCounts = counts.clone();
        Arrays.sort(sortedCounts);
        int middle = sortedCounts.length / 2;
        if (sortedCounts.length % 2 == 0) {
            return (sortedCounts[middle - 1] + sortedCounts[middle]) / 2.0;
        } else {
            return sortedCounts[middle];
        }
    }


}
