package com.nirocca.ornithoalert.location;

import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=1&p_cc=206&sp_tg=1&sp_DChoice=range&sp_DFrom=%%DATE_FROM%%&sp_DTo=%%DATE_TO%%&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=3&sp_SChoice=species&speciesFilter=merlin&sp_S=%%SPECIES_ID%%&sp_Cat[never]=1&sp_Cat[veryrare]=1&sp_Cat[rare]=1&sp_Cat[unusual]=1&sp_Cat[escaped]=1&sp_Cat[common]=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010100011000101100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Coord[W]=13.20225563997&sp_Coord[S]=52.397693609735&sp_Coord[E]=13.220225091902&sp_Coord[N]=52.415663061667&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1";
    private static final int[] YEARS = {2014, 2015, 2016, 2017, 2018, 2019};
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Species[] SPECIES = {Species.WALDOHREULE};
    private static final String PATH_TO_COORDS_DIR = "/Users/nirocca/tmp/";

    public static void main(String[] args) throws IOException {
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();

        for (Species species: SPECIES) {
            List<Sighting> sightings = new ArrayList<>();
            System.out.println("Species: " + species.name());

            for (int year : YEARS) {
                System.out.println("  year: " + year);
                String url = URL_TEMPLATE.replaceAll("%%DATE_FROM%%", FORMATTER.format(LocalDate.of(year, 1, 1)));
                url = url.replaceAll("%%DATE_TO%%", FORMATTER.format(LocalDate.of(year, 12, 31)));
                url = url.replaceAll("%%SPECIES_ID%%", String.valueOf(species.getOrnithoSpeciesId()));

                sightings.addAll(com.nirocca.ornithoalert.Main.calcSightings(url, SortBy.TIME));
            }
            try (FileOutputStream out = new FileOutputStream(PATH_TO_COORDS_DIR  + species.name() + ".txt")) {
                coordinatesExporter.printCoordinates(sightings, false, out);
            }
        }


    }

}
