package com.nirocca.ornithoalert.grid;

import static com.nirocca.ornithoalert.Species.ALPENBIRKENZEISIG;
import static com.nirocca.ornithoalert.Species.TANNENHAEHER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Species;
import org.apache.commons.io.IOUtils;

public class RenderAllDe {

    private static final String SIGHTINGS_FILE ="/allDe.txt";
    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "m/";

    public static void main(String[] args) throws IOException {
        FixedColorProvider cp = new FixedColorProvider();
        List<String> lines = IOUtils.readLines(RenderAllDe.class.getResourceAsStream(SIGHTINGS_FILE), "utf-8");
        List<String> result = new ArrayList<>();
        result.add(lines.remove(0));


        Set<Species> excludedSpecies = Set.of(TANNENHAEHER, ALPENBIRKENZEISIG);
        for (String line : lines) {
            String[] fields = line.split(",");
            int speciesId = Integer.parseInt(fields[6]);
            if (!fields[1].endsWith("2021") && !excludedSpecies.contains(Species.getById(speciesId))) { // too big result file otherwise
                result.add(line.replace("darkgreen", cp.getColor(speciesId)));
            }
        }

        FileOutputStream os = new FileOutputStream(PATH_TO_COORDS_DIR + "coords.txt");
        IOUtils.writeLines(result, null, os, Charset.defaultCharset());
    }

    private  static final class FixedColorProvider {
        private final Deque<String> COLORS = new ArrayDeque<>(Arrays.asList("darkgreen", "darkblue", "black", "white", "yellow", "violet",
                "lightgreen", "teal", "beige", "darkslategray", "aquamarine", "lightblue",  "sandybrown"));
        private final Map<Integer, String> idToColor = new HashMap<>();

        public String getColor(int speciesId) {
            if (!idToColor.containsKey(speciesId)) {
                if (COLORS.isEmpty()) {
                    COLORS.push("red");
                }
                idToColor.put(speciesId, COLORS.pop());
            }
            return idToColor.get(speciesId);
        }
    }

}
