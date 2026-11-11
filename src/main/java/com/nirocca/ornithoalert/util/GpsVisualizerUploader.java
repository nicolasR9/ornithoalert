package com.nirocca.ornithoalert.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

public class GpsVisualizerUploader {

    private static final String MAP_FORM_URL = "https://www.gpsvisualizer.com/map?output_google";
    private static final Pattern MAP_URL_PATTERN = Pattern.compile("/display/map/[^\"']+\\.html");
    private static final String MAP_URL_PREFIX = "https://www.gpsvisualizer.com";
    // plain curl is blocked by cloudflare, a browser user agent is required
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36";

    public String upload(String gpsVisualizerTxt) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        ClassicHttpRequest request = ClassicRequestBuilder.post(MAP_FORM_URL)
                .setHeader("User-Agent", USER_AGENT)
                .setEntity(MultipartEntityBuilder.create()
                        .addTextBody("form", "google")
                        .addTextBody("submitted", "1")
                        .addTextBody("width", "800")
                        .addTextBody("height", "600")
                        .addTextBody("bg_map", "google")
                        .addTextBody("force_type", "waypoints")
                        .addTextBody("data", gpsVisualizerTxt, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8))
                        .build())
                .build();

        String response = client.execute(request,
                r -> EntityUtils.toString(r.getEntity(), StandardCharsets.UTF_8));
        return extractMapUrl(response);
    }

    private String extractMapUrl(String response) {
        Matcher matcher = MAP_URL_PATTERN.matcher(response);
        if (!matcher.find()) {
            throw new IllegalStateException("No map URL in gpsvisualizer response: " + response.substring(0, Math.min(500, response.length())));
        }
        return MAP_URL_PREFIX + matcher.group();
    }

}
