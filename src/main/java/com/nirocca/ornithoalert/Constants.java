package com.nirocca.ornithoalert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constants {
    static final SortBy DEFAULT_SORT_ORDER = SortBy.SPECIES;
    static final OrnithoUrl DEFAULT_URL = OrnithoUrl.GROSSRAUM_LAST_8_DAYS;
   
    static final Set<String> SPECIES_TO_EXCLUDE = new HashSet<>(Arrays.asList(
            "Keine Art",
            "Saatgans (ssp. fabalis), Waldsaatgans",
            "Gimpel (ssp. pyrrhula), Trompetergimpel",
            "Bl\u00e4ss- / Saatg\u00e4nse",
            "Tundrasaatg\u00e4nse",
            "Waldsaatg\u00e4nse",
            "Hausgans",
            "Hausente",
            "Mittelmeer-_oder_Steppenm\u00f6we (Weißkopfm\u00f6we)",
            "Silber-oderMittelmeer-oderSteppenm\u00f6we",
            "Silber-_oder_Mittelmeer-_oder_Steppenm\u00f6we",
            "Silber- / Mittelmeer- / Steppenm\u00f6wen",
            "Mittelmeer- / Steppenm\u00f6wen",
            "Stockente, Bastard, fehlfarben",
            "Stockenten, Bastard, fehlfarben",
            "Taigabirkenzeisige",
            "Alpenbirkenzeisige",
            "Birkenzeisig (ssp. cabaret), Alpenbirkenzeisig",
            "Birkenzeisig (ssp. flammea), Taigabirkenzeisig",
            "Schwanzmeisen (ssp. caudatus)",
            "Schwanzmeise (ssp. caudatus)",
            "Stockenten Bastard fehlfarben",
            "Wasseramsel (ssp. cinclus)",
            "Raben-_oder_Nebelkr\u00e4he (Aaskr\u00e4he)",
            "Raben- / Nebelkr\u00e4hen"
            ));
    
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
}
