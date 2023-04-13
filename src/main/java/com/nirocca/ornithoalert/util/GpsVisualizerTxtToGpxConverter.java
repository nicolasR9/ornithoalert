package com.nirocca.ornithoalert.util;

import com.nirocca.ornithoalert.Constants;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Builder;
import io.jenetics.jpx.WayPoint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.commons.io.IOUtils;

public class GpsVisualizerTxtToGpxConverter {
    private static final String TXT_FILES_DIR = Constants.OUTPUT_DIR + "toGpxConverter";

    public static void main(String[] args) throws IOException {
        File sourceDir = new File(TXT_FILES_DIR);
        for (File file : Objects.requireNonNull(sourceDir.listFiles((dir, name) -> name.endsWith(".txt")))) {
            GPX gpx = createGpx(new FileInputStream(file));
            GPX.write(gpx, Paths.get(new File(sourceDir, file.getName() + ".gpx").getAbsolutePath()));
        }
    }

    private static GPX createGpx(InputStream inputStream) throws IOException {
        final Builder gpxBuilder = GPX.builder();
        java.util.List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        lines.remove(0);
        for (String line : lines) {
            String[] elements = line.split(",");
            gpxBuilder.addWayPoint(WayPoint.builder()
                .name(elements[0])
                .desc(elements[1])
                .addLink(elements[5])
                .build(Double.parseDouble(elements[2]), Double.parseDouble(elements[3])));
        }
        return gpxBuilder.build();
    }

    public static String toGpx(String txtCoords) throws IOException {
        GPX gpx = createGpx(new ByteArrayInputStream(txtCoords.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        GPX.write(gpx, output);
        return output.toString();
    }
}
