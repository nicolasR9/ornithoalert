package com.nirocca.ornithoalert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nirocca.ornithoalert.model.SpeciesStatistic;
import com.nirocca.ornithoalert.util.SightingFilter;

public class AllNotThisYearMain {

    // last 5 days close BB area
    private static final String STATISTICS_URL = "https://www.ornitho.de/index.php?m_id=94&p_c=display&p_cc=206&sp_tg=1&sp_DFrom=25.02.2026&sp_DTo=25.02.2026&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=5&sp_SChoice=all&speciesFilter=&sp_S=1148&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001010001100110110010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000&p_cc=206&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=15.07.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";

    private static final Pattern SPECIES_PATTERN = Pattern.compile(
        "<b>([^<]+)</b>\\s*<span class=\"sci_name\">\\(([^)]+)\\)</span>",
        Pattern.DOTALL
    );

    private static final Pattern COUNT_PATTERN = Pattern.compile(
        ">(\\d+)&times;"
    );

    private static final Pattern LINK_PATTERN = Pattern.compile(
        "<a href=\"(https://www\\.ornitho\\.de/index\\.php\\?m_id=94[^\"]+)\"><em class=\"fas fa-sp-obs\""
    );

    private final OrnithoPageReader pageReader;
    private final MySightingsReader mySightingsReader;

    public AllNotThisYearMain() {
        this.pageReader = new OrnithoPageReader();
        this.mySightingsReader = new MySightingsReader();
    }

    public List<SpeciesStatistic> readFilteredStatistics() throws IOException {
        List<String> mySightedSpecies = mySightingsReader.readMySightedSpeciesLatinThisYear();
        String html = pageReader.getPageContent(STATISTICS_URL);
        List<SpeciesStatistic> allStatistics = parseStatistics(html);

        List<SpeciesStatistic> relevantStatistics = SightingFilter.filterOutNonRelevantStatistics(allStatistics);

        return relevantStatistics.stream()
            .filter(stat -> !mySightedSpecies.contains(stat.latinName()))
            .toList();
    }

    private List<SpeciesStatistic> parseStatistics(String html) {
        List<SpeciesStatistic> statistics = new ArrayList<>();
        Matcher speciesMatcher = SPECIES_PATTERN.matcher(html);

        while (speciesMatcher.find()) {
            String germanName = speciesMatcher.group(1).trim();
            String latinName = speciesMatcher.group(2).trim();

            int speciesStart = speciesMatcher.start();
            int searchStart = Math.max(0, speciesStart - 2000);
            String contextBefore = html.substring(searchStart, speciesStart);

            int count = extractCount(contextBefore);
            String link = extractLink(contextBefore);

            if (count > 0) {
                statistics.add(new SpeciesStatistic(count, germanName, latinName, link));
            }
        }

        return statistics;
    }

    private int extractCount(String context) {
        Matcher countMatcher = COUNT_PATTERN.matcher(context);
        String lastCount = "0";
        while (countMatcher.find()) {
            lastCount = countMatcher.group(1);
        }
        return Integer.parseInt(lastCount);
    }

    private String extractLink(String context) {
        Matcher linkMatcher = LINK_PATTERN.matcher(context);
        String lastLink = "";
        while (linkMatcher.find()) {
            lastLink = linkMatcher.group(1);
        }

        if (!lastLink.isEmpty()) {
            lastLink = lastLink.replace("&amp;", "&");
        }

        return lastLink;
    }

    public static void main(String[] args) throws IOException {
        AllNotThisYearMain reader = new AllNotThisYearMain();
        List<SpeciesStatistic> filteredSpecies = reader.readFilteredStatistics();

        System.out.println("Found " + filteredSpecies.size() + " species not yet sighted this year:\n");

        for (SpeciesStatistic s : filteredSpecies) {
            System.out.println(s.count() + "× " + s.germanName() + " (" + s.latinName() + ")");
            if (!s.observationsLink().isEmpty()) {
                System.out.println("   " + s.observationsLink());
            }
            System.out.println();
        }
    }
}
