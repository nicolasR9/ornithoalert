package com.nirocca.ornithoalert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Sighting;
import org.apache.commons.io.output.TeeOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class CoordinatesExporter {
    private static final String PATH_TO_COORDS_FILE = Constants.OUTPUT_DIR + "coord.txt";
    
    private static final OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    private ColorProvider colorProvider;


    public void printCoordinates(PrintParameters printParameters) throws IOException {
        OutputStream outputStream = printParameters.outStream() != null ? printParameters.outStream() : createOutputStream();

        this.colorProvider = printParameters.colorProvider() != null ? printParameters.colorProvider() : new MostFrequentColorProvider(printParameters.sightings());
        try (PrintWriter out = new PrintWriter(outputStream)) {
            if (printParameters.printHeader())
                out.println("name,desc,latitude,longitude,color,url");
            Set<Coordinates> coordinatesUsed = new HashSet<>();
            for (Sighting sighting : printParameters.sightings()) {
                Coordinates coordinates = getCoordinates(sighting.getUrl());
                while (coordinatesUsed.contains(coordinates)) {
                    coordinates.shiftABit();
                }
                coordinatesUsed.add(coordinates);

                if (printParameters.onlyExactCoords() && !coordinates.isExact()) {
                    continue;
                }

                String latitude = String.valueOf(coordinates.getLatitude());
                String longitude = String.valueOf(coordinates.getLongitude());
                out.printf("%s,%s,%s,%s,%s,%s", sighting.getGermanNamePlural().replaceAll(",", ""),
                    sighting.getDate().replaceAll(",", ""), latitude, longitude, getColor(sighting), sighting.getUrl());
                if (printParameters.speciesIdToAppend() == null) {
                    out.printf("%n");
                } else {
                    out.printf(",%s%n", printParameters.speciesIdToAppend());
                }
            }
        }
    }

    private static OutputStream createOutputStream() throws FileNotFoundException {
        File file = new File(PATH_TO_COORDS_FILE);
        if (file.getParentFile().exists()) {
            FileOutputStream fileOut = new FileOutputStream(file);
            return new TeeOutputStream(System.out, fileOut);
        } else {
            System.err.println("coords path not found " + file.getParent());
            return System.out;
        }
    }
    
    private String getColor(Sighting sighting) {
        return colorProvider.getColor(sighting);
//         if (Constants.LATIN_NAME_TO_COLOR.containsKey(sighting.getLatinName())) {
//            return Constants.LATIN_NAME_TO_COLOR.get(sighting.getLatinName());
//         }
//         return "red";
    }
    

    public static Coordinates getCoordinates(String detailsUrl) throws IOException {
        String html = ornithoPageReader.getHtmlForPage(detailsUrl);
        // for sightings with exact locations, the coordinates are directly on this page
        Pattern pattern = Pattern.compile(".*openlayerMap.addMovingMarker\\((\\d+\\.\\d+),(\\d+\\.\\d+).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.matches()) {
            return new Coordinates(matcher.group(1), matcher.group(2), true);
        }
        
        // for sightings without exact locations, call the subpage for the location
        Document doc = Jsoup.parse(html);
        Element topElement = doc.getElementById("detail-summary-container");
        
        String locationPageUrl = topElement.selectFirst("a[href~=https://www.ornitho.de/index.php\\?m_id=52.*]").attr("href");
        
        html = ornithoPageReader.getHtmlForPage(locationPageUrl);
        doc = Jsoup.parse(html);
        String coordText = doc.getElementById("td-main-table").select("div[class='col-sm-80']").get(0).ownText();
        pattern = Pattern.compile("(.* E) / (.*'' N).*");
        matcher = pattern.matcher(coordText);
        if (!matcher.matches()) {
            System.err.println("unexpected coord format: " + coordText);
        }
        
        return new Coordinates(matcher.group(2), matcher.group(1), false);
    }
    
    public static void main(String[] args) throws IOException {
        String html = ornithoPageReader.getHtmlForPage("http://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=206&sp_tg=1&sp_DFrom=23.02.2018&sp_DTo=23.02.2018&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=15&speciesFilter=&sp_S=1197&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Cat%5Bverycommon%5D=1&sp_Family=1&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000010000001100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=426&sp_Commune=13197&sp_Info=&sp_P=0&sp_PChoice=coord&sp_Coord%5BW%5D=13.033103&sp_Coord%5BS%5D=52.172652&sp_Coord%5BE%5D=13.176096&sp_Coord%5BN%5D=52.266145&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1");
        
        SightingsPageParser parser = new SightingsPageParser();
        List<Day> structure = parser.parseSightingStructure(html);
        List<Sighting> sightings = Sighting.fromDays(structure);
        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        coordinatesExporter.printCoordinates(new PrintParameters(sightings, false, null, null, true));

    }

}
