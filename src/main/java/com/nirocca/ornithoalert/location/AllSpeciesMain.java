package com.nirocca.ornithoalert.location;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.MaxNElementsCollector;
import com.nirocca.ornithoalert.PrintParameters;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;

public class AllSpeciesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=217&sp_tg=1&sp_DChoice=range&sp_DFrom=08.07.2026&sp_DTo=08.07.2026&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&plan-vertical=AUTO&sp_S=%28items%29+%3D%3E+items.length+%3E+0+%3F+items%5B0%5D.id+%3A+undefined&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000&p_cc=217&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=08.07.2026&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int[] yearsToCheck = {2020, 2021, 2022, 2023, 2024, 2025};
    private static final String FROM_DATE = "19.10.";
    private static final String TO_DATE = "10.11.";

    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "allSpecies/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        List<Sighting> sightings = new ArrayList<>();
        for (int year : yearsToCheck) {
            System.out.println("Year: " + year);
            LocalDate to = LocalDate.parse(TO_DATE + year, formatter);
            if (to.isAfter(LocalDate.now())) to = LocalDate.now();
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + to.format(formatter) + "&");

            System.out.println(url);

            sightings.addAll(com.nirocca.ornithoalert.Main.calcSightings(url, SortBy.SPECIES, FilterMySightedSpecies.YES));
        }

        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        for (Sighting sighting : sightings) {
            maxSpecies.add(new LatinComparedSpecies(sighting.germanName(), sighting.latinName()));
        }
        System.out.println("\nMax:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);

        System.out.println("Sightings read - printing coordinates.");
        try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + "coords.txt")) {
            coordinatesExporter.printCoordinates(new PrintParameters(sightings, true, out, null, true));
        }
        System.out.println("Done.");
    }
}
