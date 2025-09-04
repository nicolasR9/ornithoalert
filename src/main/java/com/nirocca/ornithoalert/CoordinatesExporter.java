package com.nirocca.ornithoalert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import org.apache.commons.io.output.TeeOutputStream;

public class CoordinatesExporter {
    private static final String PATH_TO_COORDS_FILE = Constants.OUTPUT_DIR + "coord.txt";
    
    private static final OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    private ColorProvider colorProvider;


    public void printCoordinates(PrintParameters printParameters) throws IOException {
        OutputStream outputStream = printParameters.outStream() != null ? printParameters.outStream() : createOutputStream();

        this.colorProvider = printParameters.colorProvider() != null ? printParameters.colorProvider() : new MostFrequentColorProvider(printParameters.sightings());
        try (PrintWriter out = new PrintWriter(outputStream)) {
            if (printParameters.printHeader())
                out.println("name,desc,latitude,longitude,color,url");
            Set<Coordinates> coordinatesUsed = new HashSet<>();
            for (Sighting sighting : printParameters.sightings()) {
                Coordinates coordinates = sighting.getCoordinates();
                if (coordinates == null) {
                    continue;
                }
                while (coordinatesUsed.contains(coordinates)) {
                    coordinates.shiftABit();
                }
                coordinatesUsed.add(coordinates);

                if (printParameters.onlyExactCoords() && !coordinates.isExact()) {
                    continue;
                }

                String latitude = String.valueOf(coordinates.getLatitude());
                String longitude = String.valueOf(coordinates.getLongitude());
                out.printf("%s,%s,%s,%s,%s,%s", sighting.getGermanName().replaceAll(",", ""),
                    sighting.getDate().replaceAll(",", ""), latitude, longitude, getColor(sighting), sighting.getUrl());
                if (printParameters.speciesIdToAppend() == null) {
                    out.printf("%n");
                } else {
                    out.printf(",%s%n", printParameters.speciesIdToAppend());
                }
            }
        }
    }

    private static OutputStream createOutputStream() throws FileNotFoundException {
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
        return colorProvider.getColor(sighting);
//         if (Constants.LATIN_NAME_TO_COLOR.containsKey(sighting.getLatinName())) {
//            return Constants.LATIN_NAME_TO_COLOR.get(sighting.getLatinName());
//         }
//         return "red";
    }

}
