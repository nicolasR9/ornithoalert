package com.nirocca.ornithoalert.location;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.MaxNElementsCollector;
import com.nirocca.ornithoalert.model.LatinComparedSpecies;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllSpeciesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=217&sp_tg=1&sp_DChoice=range&sp_DFrom=18.10.2022&sp_DTo=18.10.2022&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=21&speciesFilter=&sp_S=1197&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000110000000000&sp_cCO=000000000000000010000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Polygon=POLYGON%28%2813.257316044376903+54.17943821122962%2C13.336966922997073+54.053868234850704%2C13.538154056063538+54.082879690060835%2C13.42073767464932+54.200329568449334%2C13.257316044376903+54.17943821122962%29%29&OpenLayers_Control_LayerSwitcher_5_baseLayers=OpenStreetMap+Live&Z%C3%A4hlgebiete+%2F+Probefl%C3%A4chen=Z%C3%A4hlgebiete+%2F+Probefl%C3%A4chen&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=24.08.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int[] yearsToCheck = {2018, 2019, 2021};
    private static final String FROM_DATE = "21.10.";
    private static final String TO_DATE = "30.10.";

    private static final String PATH_TO_COORDS_DIR = "/Users/nirocca/tmp/voegel/tmp/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();


        List<Sighting> sightings = new ArrayList<>();
        for (int year : yearsToCheck) {
            System.out.println("Year: " + year);
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + year + "&");

            sightings.addAll(com.nirocca.ornithoalert.Main.calcSightings(url, SortBy.SPECIES, FilterMySightedSpecies.YES));
        }

        MaxNElementsCollector<LatinComparedSpecies> maxSpecies = new MaxNElementsCollector<>();
        for (Sighting sighting : sightings) {
            maxSpecies.add(new LatinComparedSpecies(sighting.getGermanNamePlural(), sighting.getLatinName()));
        }
        System.out.println("\nMax:");
        maxSpecies.getMaxElements(30).forEach(System.out::println);

        System.out.println("Sightings read - printing coordinates.");
        try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + "coords.txt")) {
            coordinatesExporter.printCoordinates(sightings, false, out);
        }
        System.out.println("Done.");
    }
}
