package com.nirocca.ornithoalert.location;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class AllSpeciesSeparateFilesMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=-1&sp_tg=1&sp_DChoice=range&sp_DFrom=20.03.2021&sp_DTo=20.06.2021&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&speciesFilter=&sp_S=1197&sp_SChoice=category&sp_Cat[never]=1&sp_Cat[veryrare]=1&sp_Cat[rare]=1&sp_Cat[unusual]=1&sp_Family=1&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010100011001101100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000010000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_PChoice=polygon&sp_Polygon=POLYGON%28%2812.917191257748268+52.09356413481289%2C13.135544528448392+52.1079049683115%2C13.167816867199667+52.05726967744495%2C12.992722263336361+51.98882088496147%2C12.917191257748268+52.09356413481289%29%29&OpenLayers_Control_LayerSwitcher_5_baseLayers=OpenStreetMap+Live&Z%C3%A4hlgebiete_/_Probefl%C3%A4chen=Z%C3%A4hlgebiete+%2F+Probefl%C3%A4chen&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=09.06.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1";
    private static final int[] yearsToCheck = {2019, 2020, 2021};
    private static final String FROM_DATE = "20.5.";
    private static final String TO_DATE = "20.8.";

    private static final String PATH_TO_COORDS_DIR = "/Users/nirocca/tmp/voegel/tmp/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();

        List<Sighting> sightings = new ArrayList<>();
        for (int year : yearsToCheck) {
            System.out.println("Year: " + year);
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + year + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + year + "&");

            sightings.addAll(com.nirocca.ornithoalert.Main.calcSightings(url, SortBy.SPECIES, FilterMySightedSpecies.ONLY_THIS_YEAR));
        }

        var map = sightings.stream().collect(Collectors.groupingBy(sighting -> Species.getById(sighting.getSpeciesId()).name()));
        System.out.println("Sightings read - printing coordinates.");

        for (Entry<String, List<Sighting>> entry : map.entrySet()) {
            try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + entry.getKey() + ".txt")) {
                coordinatesExporter.printCoordinates(entry.getValue(), false, out);
            }
        }
        System.out.println("Done.");
    }
}
