package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Day;
import com.nirocca.ornithoalert.model.Sighting;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatesExporter {
    private static SightingsPageParser parser = new SightingsPageParser();
    private static OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    
    private static final Map<String, String> LATIN_NAME_TO_COLOR = new HashMap<>();
    
    static {
        LATIN_NAME_TO_COLOR.put("Aythya marila", "lightgreen"); //Bergente
        LATIN_NAME_TO_COLOR.put("Fringilla montifringilla", "darkgreen"); //Berfink
        LATIN_NAME_TO_COLOR.put("Anthus spinoletta", "lightblue"); //Bergpieper
        LATIN_NAME_TO_COLOR.put("Carduelis cannabina", "darkblue"); //Bluthaenfling
        LATIN_NAME_TO_COLOR.put("Loxia curvirostra", "yellow"); //Fichtenkreuzschnabel
        LATIN_NAME_TO_COLOR.put("Columba oenas", "aqua"); //Hohltaube
        LATIN_NAME_TO_COLOR.put("Larus cachinnans", "aquamarine"); //Steppenmoewe
        LATIN_NAME_TO_COLOR.put("Rallus aquaticus", "darkgrey"); //Wasserralle
        LATIN_NAME_TO_COLOR.put("Anthus pratensis", "wheat"); //Wiesenpieper
        LATIN_NAME_TO_COLOR.put("Cygnus bewickii", "violet"); //Zwergschwan
        LATIN_NAME_TO_COLOR.put("Larus canus", "teal"); //Sturmmoewe
        LATIN_NAME_TO_COLOR.put("Alopochen aegyptiaca", "sienna"); //Nilgans
        LATIN_NAME_TO_COLOR.put("Buteo lagopus", "sandybrown"); //Raufussbussard
        LATIN_NAME_TO_COLOR.put("Anas acuta", "salmon"); //Spiessente
        LATIN_NAME_TO_COLOR.put("Certhia familiaris", "orange"); //Waldbaumlaeufer
        LATIN_NAME_TO_COLOR.put("Parus montanus", "coral"); //Weidenmeise
        LATIN_NAME_TO_COLOR.put("Branta leucopsis", "beige"); //Weisswangengans
    }

    public void printCoordinates(List<Sighting> sightings) throws IOException {
        System.out.println("name,desc,latitude,longitude,color,url");
        Set<Coordinates> coordinatesUsed = new HashSet<>();
        for (Sighting sighting : sightings) {
            Coordinates coordinates = getCoordinates(sighting.getUrl());
            while (coordinatesUsed.contains(coordinates)) {
                coordinates.shiftABit();
            }
            coordinatesUsed.add(coordinates);
            
            System.out.printf("%s,%s,%s,%s,%s,%s%n", sighting.getGermanNamePlural().replaceAll(",", ""),
                    sighting.getDate().replaceAll(",", ""), coordinates.getLatitude(),
                    coordinates.getLongitude(), getColor(sighting), sighting.getUrl());
        }
    }
    
    private String getColor(Sighting sighting) {
         if (LATIN_NAME_TO_COLOR.containsKey(sighting.getLatinName())) {
         return LATIN_NAME_TO_COLOR.get(sighting.getLatinName());
         }
         return "red";
    }
    

    private Coordinates getCoordinates(String detailsUrl) throws IOException {
        String html = ornithoPageReader.getHtmlForPage(detailsUrl);
        Document doc = Jsoup.parse(html);
        Element topElement = doc.getElementById("detail-summary-container");
        
        String locationPageUrl = topElement.selectFirst("a[href~=http://www.ornitho.de/index.php\\?m_id=52.*]").attr("href");
        
        html = ornithoPageReader.getHtmlForPage(locationPageUrl);
        doc = Jsoup.parse(html);
        String coordText = doc.getElementById("td-main-table").select("td[class='box']").get(1).ownText();
        Pattern pattern = Pattern.compile("(.* E) / (.*'' N).*");
        Matcher matcher = pattern.matcher(coordText);
        if (!matcher.matches()) {
            System.out.println("unexpected coord format: " + coordText);
        }
        
        return new Coordinates(matcher.group(2), matcher.group(1));
    }
    
    public static void main(String[] args) throws IOException {
        
        String html = ornithoPageReader.getHtmlForPage("http://www.ornitho.de/index.php?m_id=94&p_c=2&p_cc=206&sp_tg=1&sp_DFrom=03.01.2018&sp_DTo=03.01.2018&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=15&sp_SChoice=species&speciesFilter=spie%DF&sp_S=101&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010100010000001100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=426&sp_Commune=13197&sp_Info=&sp_P=0&sp_Coord%5BW%5D=9.4514074838706&sp_Coord%5BS%5D=51.30253038235&sp_Coord%5BE%5D=9.4693769358023&sp_Coord%5BN%5D=51.320499834282&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten");
        
        List<Day> structure = parser.parseSightingStructure(html);
        List<Sighting> sightings = Sighting.fromDays(structure);
        new CoordinatesExporter().printCoordinates(sightings);
    }

}
