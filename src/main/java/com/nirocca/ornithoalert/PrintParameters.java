package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Sighting;

import java.io.OutputStream;
import java.util.List;

public record PrintParameters(List<Sighting> sightings, boolean onlyExactCoords, OutputStream outStream,
                              ColorProvider colorProvider, boolean printHeader, Integer speciesIdToAppend) {

    public PrintParameters(List<Sighting> sightings, boolean onlyExactCoords, OutputStream outStream,
                           ColorProvider colorProvider, boolean printHeader) {
        this(sightings, onlyExactCoords, outStream, colorProvider, printHeader, null);
    }
}