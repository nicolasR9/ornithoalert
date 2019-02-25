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
            "Hausente",
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
            "Wasseramsel (ssp. cinclus)"
            ));
    
    static final Map<String, String> LATIN_NAME_TO_COLOR = new HashMap<>();
    
    // see https://www.w3schools.com/colors/colors_names.asp
    static {
        LATIN_NAME_TO_COLOR.put("Remiz pendulinus", "sienna"); //Beutelmeise
        LATIN_NAME_TO_COLOR.put("Locustella naevia", "yellow"); //Feldschwirl
        LATIN_NAME_TO_COLOR.put("Pluvialis apricaria", "orange"); //Goldregenpfeifer
        LATIN_NAME_TO_COLOR.put("Anser brachyrhynchus", "darkgreen"); //Kurzschnabelgans
        LATIN_NAME_TO_COLOR.put("Falco columbarius", "black"); //Merlin
        LATIN_NAME_TO_COLOR.put("Emberiza hortulana", "sandybrown"); //Ortolan
        LATIN_NAME_TO_COLOR.put("Botaurus stellaris", "darkblue"); //Rohrdommel
        LATIN_NAME_TO_COLOR.put("Branta ruficollis", "salmon"); //Rothalsgans
        LATIN_NAME_TO_COLOR.put("Sylvia nisoria", "teal"); //Sperbergrasmuecke
        LATIN_NAME_TO_COLOR.put("Coturnix coturnix", "violet"); //Wachtel
        LATIN_NAME_TO_COLOR.put("Anthus pratensis", "lightgreen"); //Wiesenpieper

        //Limikolen
        LATIN_NAME_TO_COLOR.put("Numenius arquata", "beige"); //Brachvogel
        LATIN_NAME_TO_COLOR.put("Tringa erythropus", "beige"); //Dunkler Wasserlaeufer
        LATIN_NAME_TO_COLOR.put("Actitis hypoleucos", "beige"); //Flussuferläufer
        LATIN_NAME_TO_COLOR.put("Pluvialis squatarola", "beige"); //Kiebitzregenpfeifer
        LATIN_NAME_TO_COLOR.put("Charadrius hiaticula", "beige"); //Sandregenpfeifer
        LATIN_NAME_TO_COLOR.put("Scolopax rusticola", "beige"); //Waldschnepfe
        LATIN_NAME_TO_COLOR.put("Tringa ochropus", "beige"); //Waldwasserlaeufer
        LATIN_NAME_TO_COLOR.put("Calidris minuta", "beige"); //Zwergstrandläufer

        //free: aquamarine, lightblue, salmon
    }
    
    public enum SortBy {
        TIME,
        REGION,
        SPECIES
    }
}
