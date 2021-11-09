package com.nirocca.ornithoalert.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

public class ColorReplacer {

    private static final String FILE = "/Users/nirocca/tmp/coord.txt";
    private static final Map<String, String> BIRDS_TO_COLORS = new HashMap<>();

    static {
        BIRDS_TO_COLORS.put("Strandpieper", "yellow");
        BIRDS_TO_COLORS.put("Ohrenlerche", "green");
        BIRDS_TO_COLORS.put("Ohrenlerchen", "green");
        BIRDS_TO_COLORS.put("Berghänfling", "brown");
        BIRDS_TO_COLORS.put("Berghänflinge", "brown");
        BIRDS_TO_COLORS.put("Schneeammer", "blue");
        BIRDS_TO_COLORS.put("Schneeammern", "blue");
        BIRDS_TO_COLORS.put("Spornammer", "white");
        BIRDS_TO_COLORS.put("Spornammern", "white");
    }

    public static void main(String[] args) throws IOException {

        List<String> lines = IOUtils.readLines(new FileInputStream(FILE));
        Pattern pattern = Pattern.compile("(([^,]+),.*,)red(,.*)");
        for (String line : lines) {
            Matcher m = pattern.matcher(line);
            if (m.matches() && BIRDS_TO_COLORS.containsKey(m.group(2))) {
                System.out.println(m.group(1) + BIRDS_TO_COLORS.get(m.group(2)) + m.group(3));
            } else {
                System.out.println(line);
            }
        }

    }

}
