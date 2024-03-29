package com.nirocca.ornithoalert.location;

import com.nirocca.ornithoalert.*;
import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AllSpeciesSeparateFilesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=2&p_cc=200&sp_tg=1&sp_DChoice=range&sp_DFrom=15.07.2021&sp_DTo=31.08.2021&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=all&speciesFilter=&sp_S=1197&sp_Cat[verycommon]=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000&sp_cCO=000000000000001000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=20.03.2021&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1";
    private static final int[] yearsToCheck = {2021};
    private static final String FROM_DATE = "15.7.";
    private static final String TO_DATE = "31.8.";

    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "/allSpeciesSeparateFiles/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();

        List<Sighting> sightings = new ArrayList<>();
        for (int year : yearsToCheck) {
            System.out.println("Year: " + year);
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + year + "&");

            sightings.addAll(com.nirocca.ornithoalert.Main.calcSightings(url, SortBy.SPECIES, FilterMySightedSpecies.ONLY_THIS_YEAR));
        }

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
