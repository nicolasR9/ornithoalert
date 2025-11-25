package com.nirocca.ornithoalert;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MainIntegrationTest {

    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        System.out.println("WireMock server started at: http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void testMainCalcSightingsDirect() throws Exception {
        // Load mock responses from external files
        String mockOrnithoResponse = Files.readString(
            Paths.get("src/test/resources/mock-ornitho-main-page.html"),
            StandardCharsets.UTF_8);

        String mockJsonResponse = Files.readString(
            Paths.get("src/test/resources/mock-ornitho-data-page1.json"),
            StandardCharsets.UTF_8);

        // Mock data page requests - first page with data (higher priority)
        wireMockServer.stubFor(get(urlPathEqualTo("/index.php"))
            .withQueryParam("m_id", equalTo("54"))
            .withQueryParam("content", equalTo("observations_by_page"))
            .withQueryParam("mp_current_page", equalTo("1"))
            .atPriority(1) // Higher priority than main page
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(mockJsonResponse)));

        // Mock subsequent pages to return empty data to stop pagination
        String emptyJsonResponse = Files.readString(
            Paths.get("src/test/resources/mock-ornitho-data-empty.json"),
            StandardCharsets.UTF_8);

        wireMockServer.stubFor(get(urlPathEqualTo("/index.php"))
            .withQueryParam("m_id", equalTo("54"))
            .withQueryParam("content", equalTo("observations_by_page"))
            .withQueryParam("mp_current_page", matching("[2-9]|1[0-9]"))
            .atPriority(1) // Higher priority than main page
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(emptyJsonResponse)));

        // Setup WireMock stubs for HTTP calls - main page (lower priority)
        wireMockServer.stubFor(get(urlPathEqualTo("/index.php"))
            .withQueryParam("m_id", equalTo("54"))
            .withQueryParam("region_id", equalTo("123"))
            .atPriority(5) // Lower priority so data page stubs match first
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/html")
                .withBody(mockOrnithoResponse)));

        // Mock MySightingsReader response (return HTML with one already sighted species)
        String mySightingsResponse = Files.readString(
            Paths.get("src/test/resources/mock-my-sightings.html"),
            StandardCharsets.UTF_8);

        wireMockServer.stubFor(get(urlPathEqualTo("/index.php"))
            .withQueryParam("m_id", equalTo("94"))
            .atPriority(1) // Higher priority than main page
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "text/html")
                .withBody(mySightingsResponse)));


        // Create testable versions of the readers that use our mock server
        String testUrl = "http://localhost:" + wireMockServer.port() + "/index.php?m_id=54&region_id=123";

        // Test Main.calcSightings directly with dependency injection
        String mockBaseUrl = "http://localhost:" + wireMockServer.port();
        RegionLastSightingsReader regionReader = new RegionLastSightingsReader(mockBaseUrl);
        TestableMySightingsReader myReader = new TestableMySightingsReader(wireMockServer.port());

        List<Sighting> results = Main.calcSightings(testUrl, Constants.SortBy.SPECIES,
            Constants.FilterMySightedSpecies.YES, myReader, regionReader);

        // Verify results
        assertNotNull(results);
        assertEquals(2, results.size()); // "Keine Art" and zero count should be filtered out

        // Verify first sighting (sorted by species name)
        Sighting firstSighting = results.get(0);
        assertEquals("Amsel", firstSighting.germanName());
        assertEquals("Turdus merula", firstSighting.latinName());
        assertEquals("Berlin Mitte", firstSighting.locationText());
        assertTrue(firstSighting.coordinates().isExact());

        // Verify second sighting
        Sighting secondSighting = results.get(1);
        assertEquals("Rotkehlchen", secondSighting.germanName());
        assertEquals("Erithacus rubecula", secondSighting.latinName());
        assertEquals("Berlin Tiergarten", secondSighting.locationText());
        assertFalse(secondSighting.coordinates().isExact());
    }

    @Test
    void testCoordinatesExporterIntegration() throws Exception {
        List<Sighting> testSightings = Arrays.asList(
            new Sighting("2024-11-24 08:30", "Amsel", "Turdus merula", 12345,
                "https://www.ornitho.de/index.php?m_id=54&id=987654", "Berlin Mitte", "2",
                new Coordinates(52.5200, 13.4050, true)),
            new Sighting("2024-11-24 09:15", "Rotkehlchen", "Erithacus rubecula", 12346,
                "https://www.ornitho.de/index.php?m_id=54&id=987655", "Berlin Tiergarten", "1",
                new Coordinates(52.5100, 13.3950, false))
        );

        CoordinatesExporter exporter = new CoordinatesExporter();

        ByteArrayOutputStream testOutputStream = new ByteArrayOutputStream();
        PrintParameters params = new PrintParameters(testSightings, false, testOutputStream, null, true);
        exporter.printCoordinates(params);

        String output = testOutputStream.toString(StandardCharsets.UTF_8);

        // Verify GPS Visualizer format output (based on actual output format)
        assertTrue(output.contains("Amsel,2024-11-24 08:30,52.52,13.405"), "Output should contain Amsel coordinates");
        assertTrue(output.contains("Rotkehlchen,2024-11-24 09:15,52.51,13.395"), "Output should contain Rotkehlchen coordinates");
        assertTrue(output.contains("https://www.ornitho.de/index.php?m_id=54&id=987654"), "Output should contain first URL");
        assertTrue(output.contains("https://www.ornitho.de/index.php?m_id=54&id=987655"), "Output should contain second URL");
    }

    // Test implementations that redirect to mock server

    private static class TestableMySightingsReader extends MySightingsReader {
        private final int mockServerPort;

        public TestableMySightingsReader(int mockServerPort) {
            this.mockServerPort = mockServerPort;
        }

        @Override
        public List<String> readMySightedSpeciesLatin() throws IOException {
            // Create a testable URL that redirects to our mock server
            String testUrl = "http://localhost:" + mockServerPort + "/index.php?m_id=94&test=true";

            OrnithoPageReader pageReader = new OrnithoPageReader();
            String html = pageReader.getPageContent(testUrl);

            return extractBirdNames(html);
        }

    }
}