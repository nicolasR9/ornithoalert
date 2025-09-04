package com.nirocca.ornithoalert.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.nirocca.ornithoalert.model.Sighting;

public class SightingFilter {
    static final Set<String> SPECIES_TO_EXCLUDE = new HashSet<>(Arrays.asList(
        "Keine Art",
        "Gimpel (ssp. pyrrhula), Trompetergimpel",
        "Bläss- / Saatgänse",
        "Bläss-_oder_Saatgans",
        "Hausgans",
        "Hausente",
        "Mittelmeer-_oder_Steppenmöwe (Weißkopfmöwe)",
        "Silber-oderMittelmeer-oderSteppenmöwe",
        "Silber-_oder_Mittelmeer-_oder_Steppenmöwe",
        "Silber- / Mittelmeer- / Steppenmöwen",
        "Mittelmeer- / Steppenmöwen",
        "Stockente, Bastard, fehlfarben",
        "Stockenten, Bastard, fehlfarben",
        "Taigabirkenzeisige",
        "Birkenzeisig (ssp. flammea), Taigabirkenzeisig",
        "Buch- / Bergfinken",
        "Schwanzmeisen (ssp. caudatus)",
        "Schwanzmeise (ssp. caudatus)",
        "Stockenten Bastard fehlfarben",
        "Wasseramsel (ssp. cinclus)",
        "Raben-_oder_Nebelkrähe (Aaskrähe)",
        "Raben- / Nebelkrähen",
        "Tafel-_x_Reiherente",
        "Ringdrosseln (ssp. alpestris)",
        "Ringdrossel (ssp. alpestris)",
        "Weidenmeise (ssp. montanus), Alpenmeise",
        "Weidenmeisen (ssp. montanus), Alpenmeisen",
        "Alpenmeise",
        "Alpenmeisen",
        "Bachstelze (ssp. yarrellii), Trauerbachstelze",
        "Isländische Uferschnepfen",
        "Trottellummen / Tordalken",
        "Hellbäuchige Ringelgänse",
        "Schildrabe",
        "Waldrapp"
    ));

    public static List<Sighting> filterOutNonRelevantSightings(List<Sighting> sightings) {
        return sightings.stream()
        .filter(a -> !"0".equals(a.getCount()))
        .filter(a -> !isWithCommonFilterPattern(a.getGermanName()))
        .filter(a -> !SPECIES_TO_EXCLUDE.contains(a.getGermanName()))
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
