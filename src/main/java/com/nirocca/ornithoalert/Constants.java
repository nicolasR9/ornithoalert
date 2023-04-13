package com.nirocca.ornithoalert;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String OUTPUT_DIR = "/Users/nicolas.rocca/tmp/ornithoalert/";
    static final SortBy DEFAULT_SORT_ORDER = SortBy.SPECIES;
    static final OrnithoUrl DEFAULT_URL = OrnithoUrl.GROSSRAUM_LAST_8_DAYS;
    
    static final Map<String, String> LATIN_NAME_TO_COLOR = new HashMap<>();
    
    // see https://www.w3schools.com/colors/colors_names.asp
    static {
        LATIN_NAME_TO_COLOR.put("Anser brachyrhynchus", "darkgreen"); //Kurzschnabelgans

        LATIN_NAME_TO_COLOR.put("Cygnus bewickii", "lightgreen"); //Zwergschwan

        LATIN_NAME_TO_COLOR.put("Falco vespertinus", "teal"); //Rotfußfalke

        LATIN_NAME_TO_COLOR.put("Tyto alba", "black"); //Schleiereule

        LATIN_NAME_TO_COLOR.put("Hydroprogne caspia", "white"); //Raubseeschwalbe

        LATIN_NAME_TO_COLOR.put("Linaria flavirostris", "darkslategray"); //Berghaenfling

        LATIN_NAME_TO_COLOR.put("Sternula albifrons", "aquamarine"); //Zwergseeschwalbe
        LATIN_NAME_TO_COLOR.put("Himantopus himantopus", "lightblue"); //Stelzenlaeufer
        LATIN_NAME_TO_COLOR.put("Calidris falcinellus", "darkblue"); //Sumpflaeufer
        LATIN_NAME_TO_COLOR.put("Tadorna ferruginea", "violet"); //Rostgans
        //free: white, yellow, sandybrown, beige
    }
    
    public enum SortBy {
        TIME,
        REGION,
        SPECIES
    }

    public enum FilterMySightedSpecies {
        NO,
        YES,
        ONLY_THIS_YEAR
    }
}
