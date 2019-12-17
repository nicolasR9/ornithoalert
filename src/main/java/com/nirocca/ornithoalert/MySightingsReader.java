package com.nirocca.ornithoalert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MySightingsReader {
    
    private static final String MY_SIGHTINGS_URL = "https://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=206&sp_tg=1&sp_DChoice=all&sp_DFrom=04.08.2017&sp_DTo=04.08.2017&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=all&speciesFilter=&sp_S=1197&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Cat%5Bverycommon%5D=1&sp_Family=1&sp_PChoice=all&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000010000001100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=349&sp_Commune=12226&sp_Info=&sp_P=0&sp_Coord%5BW%5D=9.4514074838706&sp_Coord%5BS%5D=51.30253038235&sp_Coord%5BE%5D=9.4693769358023&sp_Coord%5BN%5D=51.320499834282&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyMyData=1&sp_OnlyAH=0&sp_Ats=-00000&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final String MY_SIGHTINGS_URL_THIS_YEAR = "https://www.ornitho.de/index.php?m_id=94&p_c=1&p_cc=206&sp_tg=1&sp_DChoice=range&sp_DFrom=%%DATE_FROM%%&sp_DTo=%%DATE_TO%%&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=all&speciesFilter=&sp_S=1197&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=all&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000010000001100100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Coord%5BW%5D=13.20225563997&sp_Coord%5BS%5D=52.397693609735&sp_Coord%5BE%5D=13.220225091902&sp_Coord%5BN%5D=52.415663061667&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyMyData=1&sp_OnlyAH=0&sp_Ats=-00000&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=16.06.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<String> readMySightedSpeciesLatinThisYear() throws IOException {
        LocalDate today = LocalDate.now();
        LocalDate beginningOfYear = LocalDate.now().withDayOfYear(1);
        String url = MY_SIGHTINGS_URL_THIS_YEAR
            .replaceAll("%%DATE_FROM%%", FORMATTER.format(beginningOfYear))
            .replaceAll("%%DATE_TO%%", FORMATTER.format(today));
        return readSightings(url);
    }


    public List<String> readMySightedSpeciesLatin() throws IOException {
        return readSightings(MY_SIGHTINGS_URL);
    }

    private List<String> readSightings(String url) throws IOException {
        BasicCookieStore cookieStore = new CookiesReader().readCookies();
        HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

        final HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        String html = EntityUtils.toString(response.getEntity(), "UTF-8");

        return extractBirdNames(html);
    }

    private List<String> extractBirdNames(String html) {
        Pattern p = Pattern.compile("<span style=\"color:#[0-9A-H]+\"><b>[^<]+</b> \\(<i>([^<]+)</i>\\)</span>");
        ArrayList<String> result = new ArrayList<>();
        Matcher m = p.matcher(html);
        while (m.find()) {
            result.add(m.group(1));
        }
        
        return result;
    }

    
    public static void main(String[] args) throws IOException {
        System.out.println(new MySightingsReader().readMySightedSpeciesLatin());
    }


}
