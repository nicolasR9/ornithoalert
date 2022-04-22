package com.nirocca.ornithoalert.util;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Builder;
import io.jenetics.jpx.WayPoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class TxtToGpxConverter {
    private static final String TXT_FILES_DIR = "/Users/nirocca/tmp/convert";

    public static void main(String[] args) throws IOException {
        File sourceDir = new File(TXT_FILES_DIR);
        for (File file : Objects.requireNonNull(sourceDir.listFiles())) {
            final Builder gpxBuilder = GPX.builder();
            var lines = IOUtils.readLines(new FileInputStream(file), StandardCharsets.UTF_8);
            lines.remove(0);
            for (String line : lines) {
                String[] elements = line.split(",");
                gpxBuilder.addWayPoint(WayPoint.builder()
                    .name(elements[0])
                    .desc(elements[1])
                    .addLink(elements[5])
                    .build(Double.parseDouble(elements[2]), Double.parseDouble(elements[3])));
            }
            GPX.write(gpxBuilder.build(), Path.of(new File(sourceDir, file.getName() + ".gpx").getAbsolutePath()));
        }
    }
}
