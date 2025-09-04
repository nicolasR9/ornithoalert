package com.nirocca.ornithoalert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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

        HttpResponse response = client.execute(request);

        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

}
