package com.nirocca.ornithoalert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class OrnithoPageReader {

    private final HttpClient client;

    public OrnithoPageReader() {
        client = HttpClientBuilder.create().build();
    }

    public String getPageContent(String ornithoUrl) throws IOException {

        final HttpGet request = new HttpGet(ornithoUrl);
        String cookies; // from chrome devtools (network, export request as curl)
        try {
            cookies = IOUtils.toString(OrnithoPageReader.class.getResourceAsStream("/cookies.txt"),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        request.setHeader("Cookie", cookies);

        return client.execute(request, response -> EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
    }

}
