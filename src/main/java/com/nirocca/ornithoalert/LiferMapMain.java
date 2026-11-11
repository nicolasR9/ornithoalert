package com.nirocca.ornithoalert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.util.GpsVisualizerUploader;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

public class LiferMapMain {

    public static void main(String[] args) throws IOException {
        List<Sighting> sightings = Main.calcSightings(OrnithoUrl.RARE_LIFERS_LAST_3_DAYS.getUrl(), Constants.SortBy.SPECIES);
        System.out.println("Lifer sightings: " + sightings.size());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new CoordinatesExporter().printCoordinates(new PrintParameters(sightings, false, output, null, true));

        String mapUrl = new GpsVisualizerUploader().upload(output.toString(StandardCharsets.UTF_8));
        System.out.println("Map URL: " + mapUrl);

        if (!Constants.LIFER_MAP_UPDATE_URL.isEmpty() && !authToken().isEmpty()) {
            pushMapUrl(mapUrl, authToken());
        } else {
            System.out.println("Constants.LIFER_MAP_UPDATE_URL or lifermap.properties token not set, skipping push to ornitho-service.");
        }
    }

    // token lives in the gitignored classpath file lifermap.properties (like cookies.txt)
    private static String authToken() throws IOException {
        try (InputStream in = LiferMapMain.class.getResourceAsStream("/lifermap.properties")) {
            if (in == null) {
                return "";
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("lifermap.auth.token", "");
        }
    }

    private static void pushMapUrl(String mapUrl, String authToken) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        ClassicHttpRequest request = ClassicRequestBuilder.post(Constants.LIFER_MAP_UPDATE_URL)
                .setHeader("X-Auth-Token", authToken)
                .setEntity(new StringEntity(mapUrl, ContentType.TEXT_PLAIN))
                .build();
        String response = client.execute(request, r -> EntityUtils.toString(r.getEntity(), StandardCharsets.UTF_8));
        System.out.println("ornitho-service response: " + response);
    }

}
