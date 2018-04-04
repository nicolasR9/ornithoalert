package com.nirocca.ornithoalert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constants {
    public static final SortBy DEFAULT_SORT_ORDER = SortBy.SPECIES;
    public static final OrnithoUrl DEFAULT_URL = OrnithoUrl.GROSSRAUM_LAST_8_DAYS;
   
    public static final Set<String> SPECIES_TO_EXCLUDE = new HashSet<>(Arrays.asList(
            "Keine Art",
            "Hausente",
            "Greifvogel, unbestimmt",
            "Meisen, unbestimmt",
            "Enten, unbestimmt",
            "Pieper, unbestimmt",
            "Schw\u00e4ne, unbestimmt",
            "Raben-_x_Nebelkr\u00e4he",
            "G\u00e4nsehybrid, unbestimmt",
            "G\u00e4nse (Anser / Branta), unbestimmt",
            "Bauml\u00e4ufer, unbestimmt",
            "Gimpel (ssp. pyrrhula), Trompetergimpel",
            "Bl\u00e4ss- / Saatg\u00e4nse",
            "Tundrasaatg\u00e4nse",
            "Waldsaatg\u00e4nse",
            "Anser-G\u00e4nse, unbestimmt",
            "M\u00f6we, unbestimmt, unbestimmt",
            "M\u00f6wen, unbestimmt",
            "Gro\u00dfm\u00f6wen, unbestimmt",
            "Gro\u00dfm\u00f6we, unbestimmt",
            "Silber-oderMittelmeer-oderSteppenm\u00f6we",
            "Silber-_oder_Mittelmeer-_oder_Steppenm\u00f6we",
            "Silber- / Mittelmeer- / Steppenm\u00f6wen",
            "Stockente, Bastard, fehlfarben",
            "Stockenten, Bastard, fehlfarben",
            "Taigabirkenzeisige",
            "Alpenbirkenzeisige",
            "Birkenzeisig (ssp. cabaret), Alpenbirkenzeisig",
            "Birkenzeisig (ssp. flammea), Taigabirkenzeisig",
            "Schwanzmeisen (ssp. caudatus)",
            "Schwanzmeise (ssp. caudatus)",
            "Stockenten Bastard fehlfarben",
            "Goldh\u00e4hnchen, unbestimmt"
            ));
    
    public static final Map<String, String> LATIN_NAME_TO_COLOR = new HashMap<>();
    
    // see https://www.w3schools.com/colors/colors_names.asp
    static {
        LATIN_NAME_TO_COLOR.put("Gallinago gallinago", "darkblue"); //Bekassine
        LATIN_NAME_TO_COLOR.put("Aythya marila", "lightgreen"); //Bergente
        LATIN_NAME_TO_COLOR.put("Fringilla montifringilla", "darkgreen"); //Bergfink
        LATIN_NAME_TO_COLOR.put("Anthus spinoletta", "lightblue"); //Bergpieper
        LATIN_NAME_TO_COLOR.put("Lullula arborea", "salmon"); //Haubenlaerche
        LATIN_NAME_TO_COLOR.put("Columba oenas", "yellow"); //Hohltaube
        LATIN_NAME_TO_COLOR.put("Anas querquedula", "teal"); //Knaeckente
        LATIN_NAME_TO_COLOR.put("Buteo lagopus", "sandybrown"); //Raufussbussard
        LATIN_NAME_TO_COLOR.put("Podiceps nigricollis", "sienna"); //Schwarzhalstaucher
        LATIN_NAME_TO_COLOR.put("Larus cachinnans", "aquamarine"); //Steppenmoewe
        LATIN_NAME_TO_COLOR.put("Certhia familiaris", "orange"); //Waldbaumlaeufer
        LATIN_NAME_TO_COLOR.put("Scolopax rusticola", "beige"); //Waldschnaepfe
        LATIN_NAME_TO_COLOR.put("Cinclus cinclus cinclus", "black"); //Wasseramsel
        LATIN_NAME_TO_COLOR.put("Rallus aquaticus", "coral"); //Wasserralle
        LATIN_NAME_TO_COLOR.put("Anthus pratensis", "wheat"); //Wiesenpieper
        LATIN_NAME_TO_COLOR.put("Cygnus bewickii", "violet"); //Zwergschwan
        
        //free: darkblue, teal
    }
    
    public static enum SortBy {
        TIME,
        REGION,
        SPECIES
    }
}
