package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Sighting;
import org.apache.commons.io.output.TeeOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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

public class CoordinatesExporter {
    private static final String PATH_TO_COORDS_FILE = "C:/Users/Nicki/Documents/coord.txt";
    
    private static OrnithoPageReader ornithoPageReader = new OrnithoPageReader();

    public void printCoordinates(List<Sighting> sightings) throws IOException {
        OutputStream outStream = createOutputStream();
        try (PrintWriter out = new PrintWriter(outStream)) {
        
            out.println("name,desc,latitude,longitude,color,url");
            Set<Coordinates> coordinatesUsed = new HashSet<>();
            for (Sighting sighting : sightings) {
                Coordinates coordinates = getCoordinates(sighting.getUrl());
                while (coordinatesUsed.contains(coordinates)) {
                    coordinates.shiftABit();
                }
                coordinatesUsed.add(coordinates);
                
                out.printf("%s,%s,%s,%s,%s,%s%n", sighting.getGermanNamePlural().replaceAll(",", ""),
                        sighting.getDate().replaceAll(",", ""), coordinates.getLatitude(),
                        coordinates.getLongitude(), getColor(sighting), sighting.getUrl());
            }
        }
    }

    private OutputStream createOutputStream() throws FileNotFoundException {
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
         if (Constants.LATIN_NAME_TO_COLOR.containsKey(sighting.getLatinName())) {
         return Constants.LATIN_NAME_TO_COLOR.get(sighting.getLatinName());
         }
         return "red";
    }
    

    private Coordinates getCoordinates(String detailsUrl) throws IOException {
        String html = ornithoPageReader.getHtmlForPage(detailsUrl);
        // for sightings with exact locations, the coordinates are directly on this page
        Pattern pattern = Pattern.compile(".*openlayerMap.addMovingMarker\\((\\d+\\.\\d+),(\\d+\\.\\d+).*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if (matcher.matches()) {
            return new Coordinates(matcher.group(1), matcher.group(2));
        }
        
        // for sightings without exact locations, call the subpage for the location
        Document doc = Jsoup.parse(html);
        Element topElement = doc.getElementById("detail-summary-container");
        
        String locationPageUrl = topElement.selectFirst("a[href~=http://www.ornitho.de/index.php\\?m_id=52.*]").attr("href");
        
        html = ornithoPageReader.getHtmlForPage(locationPageUrl);
        doc = Jsoup.parse(html);
        String coordText = doc.getElementById("td-main-table").select("td[class='box']").get(1).ownText();
        pattern = Pattern.compile("(.* E) / (.*'' N).*");
        matcher = pattern.matcher(coordText);
        if (!matcher.matches()) {
            System.err.println("unexpected coord format: " + coordText);
        }
        
        return new Coordinates(matcher.group(2), matcher.group(1));
    }
    
    public static void main(String[] args) throws IOException {
        String html = ornithoPageReader.getHtmlForPage("http://www.ornitho.de/index.php?m_id=94&p_c=2&p_cc=206&sp_tg=1&sp_DFrom=03.01.2018&sp_DTo=03.01.2018&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=15&sp_SChoice=species&speciesFilter=spie%DF&sp_S=101&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010100010000001100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=426&sp_Commune=13197&sp_Info=&sp_P=0&sp_Coord%5BW%5D=9.4514074838706&sp_Coord%5BS%5D=51.30253038235&sp_Coord%5BE%5D=9.4693769358023&sp_Coord%5BN%5D=51.320499834282&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten");
        
        SightingsPageParser parser = new SightingsPageParser();
        List<Day> structure = parser.parseSightingStructure(html);
        List<Sighting> sightings = Sighting.fromDays(structure);
        new CoordinatesExporter().printCoordinates(sightings);
    }

}
