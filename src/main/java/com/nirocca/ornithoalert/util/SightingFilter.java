package com.nirocca.ornithoalert.util;

import com.nirocca.ornithoalert.model.Sighting;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SightingFilter {
    static final Set<String> SPECIES_TO_EXCLUDE = new HashSet<>(Arrays.asList(
        "Keine Art",
        "Gimpel (ssp. pyrrhula), Trompetergimpel",
        "Bl\u00e4ss- / Saatg\u00e4nse",
        "Tundrasaatg\u00e4nse",
        "Bl\u00e4ss-_oder_Saatgans",
        "Saatgans (ssp. rossicus) Tundrasaatgans",
        "Tundrasaatgans",
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
        "Birkenzeisig (ssp. flammea), Taigabirkenzeisig",
        "Buch- / Bergfinken",
        "Schwanzmeisen (ssp. caudatus)",
        "Schwanzmeise (ssp. caudatus)",
        "Stockenten Bastard fehlfarben",
        "Wasseramsel (ssp. cinclus)",
        "Raben-_oder_Nebelkr\u00e4he (Aaskr\u00e4he)",
        "Raben- / Nebelkr\u00e4hen",
        "Tafel-_x_Reiherente",
        "Ringdrosseln (ssp. alpestris)",
        "Ringdrossel (ssp. alpestris)",
        "Weidenmeise (ssp. montanus), Alpenmeise",
        "Weidenmeisen (ssp. montanus), Alpenmeisen",
        "Alpenmeise",
        "Alpenmeisen",
        "Bachstelze (ssp. yarrellii), Trauerbachstelze"
    ));

    public static List<Sighting> filterOutNonRelevantSightings(List<Sighting> sightings) {
        return sightings.stream()
        .filter(a -> !"0".equals(a.getCount()))
        .filter(a -> !isWithCommonFilterPattern(a.getGermanNamePlural()))
        .filter(a -> !SPECIES_TO_EXCLUDE.contains(a.getGermanNamePlural()))
        .collect(Collectors.toList());
    }

    public static List<com.nirocca.ornithoalert.statistics.Sighting> filterOutNonRelevantSightingsStats(List<com.nirocca.ornithoalert.statistics.Sighting> sightings) {
        return sightings.stream()
            .filter(a -> !a.isZeroCount())
            .filter(a -> !isWithCommonFilterPattern(a.getSpecies().getSpeciesName()))
            .filter(a -> !SPECIES_TO_EXCLUDE.contains(a.getSpecies().getSpeciesName()))
            .collect(Collectors.toList());
    }


    private static boolean isWithCommonFilterPattern(String name) {
        return name.contains("unbestimmt")
            || name.contains("- / ")
            || name.contains("_oder_")
            || name.contains("_x_")
            || name.contains(" x ");
    }

}
