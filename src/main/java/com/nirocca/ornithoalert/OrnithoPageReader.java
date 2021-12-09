package com.nirocca.ornithoalert;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class OrnithoPageReader {

    private final HttpClient client;

    public OrnithoPageReader() {
        BasicCookieStore cookieStore;
        try {
            cookieStore = new CookiesReader().readCookies();
        } catch (IOException e) {
            throw new RuntimeException("Unable to read cookies.", e);
        }
        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

    }

    public String getHtmlForPage(String ornithoUrl) throws IOException {

        final HttpGet request = new HttpGet(ornithoUrl);

        HttpResponse response = client.execute(request);

        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

}
