package com.nirocca.ornithoalert.model;

public record SpeciesStatistic(
    int count,
    String germanName,
    String latinName,
    String observationsLink
) {}
