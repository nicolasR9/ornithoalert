package com.nirocca.ornithoalert.grid;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Species;
import org.apache.commons.io.IOUtils;

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

import static com.nirocca.ornithoalert.Species.ALPENBRAUNELLE;
import static com.nirocca.ornithoalert.Species.ATLANTIKSTURMTAUCHER;
import static com.nirocca.ornithoalert.Species.BARTGEIER;
import static com.nirocca.ornithoalert.Species.BLAUSCHWANZ;
import static com.nirocca.ornithoalert.Species.DREIZEHENSPECHT;
import static com.nirocca.ornithoalert.Species.DUNKLER_STURMTAUCHER;
import static com.nirocca.ornithoalert.Species.EISMOEWE;
import static com.nirocca.ornithoalert.Species.GAENSEGEIER;
import static com.nirocca.ornithoalert.Species.GELBBRAUEN_LAUBSAENGER;
import static com.nirocca.ornithoalert.Species.GOLDHAEHNCHEN_LAUBSAENGER;
import static com.nirocca.ornithoalert.Species.HASELHUHN;
import static com.nirocca.ornithoalert.Species.POLARMOEWE;
import static com.nirocca.ornithoalert.Species.SEEREGENPFEIFER;
import static com.nirocca.ornithoalert.Species.SPORNAMMER;
import static com.nirocca.ornithoalert.Species.STEINADLER;
import static com.nirocca.ornithoalert.Species.TANNENHAEHER;
import static com.nirocca.ornithoalert.Species.WEISSRUECKENSPECHT;
import static com.nirocca.ornithoalert.Species.ZITRONENSTELZE;
import static com.nirocca.ornithoalert.Species.ZITRONENZEISIG;
import static com.nirocca.ornithoalert.Species.ZWERGGANS;
import static com.nirocca.ornithoalert.Species.ZWERGKANADAGANS;

public class RenderAllDe {

    private static final String SIGHTINGS_FILE ="/allDe.txt";
    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "m/";

    public static void main(String[] args) throws IOException {
        FixedColorProvider cp = new FixedColorProvider();
        List<String> lines = IOUtils.readLines(RenderAllDe.class.getResourceAsStream(SIGHTINGS_FILE), "utf-8");
        List<String> result = new ArrayList<>();
        result.add(lines.remove(0));


        Set<Species> excludedSpecies = Set.of(ALPENBRAUNELLE, ATLANTIKSTURMTAUCHER, BLAUSCHWANZ, DUNKLER_STURMTAUCHER,
                EISMOEWE, GAENSEGEIER, GELBBRAUEN_LAUBSAENGER, GOLDHAEHNCHEN_LAUBSAENGER, HASELHUHN, POLARMOEWE, SEEREGENPFEIFER,
                SPORNAMMER, TANNENHAEHER, ZITRONENSTELZE, ZWERGGANS, ZWERGKANADAGANS, STEINADLER,
                DREIZEHENSPECHT, WEISSRUECKENSPECHT, BARTGEIER, ZITRONENZEISIG);
        for (String line : lines) {
            String[] fields = line.split(",");
            int speciesId = Integer.parseInt(fields[6]);
            if (!excludedSpecies.contains(Species.getById(speciesId))) {
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
