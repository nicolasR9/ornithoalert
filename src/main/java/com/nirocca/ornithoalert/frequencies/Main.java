package com.nirocca.ornithoalert.frequencies;

import com.nirocca.ornithoalert.OrnithoPageReader;
import com.nirocca.ornithoalert.Species;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculates frequencies of observations per 10 days over a year for one species to find out which time of the year is
 * best for spotting this species.
 */
public class Main {
    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=1&p_cc=206&sp_tg=1&sp_DChoice=range&sp_DFrom=%%DATE_FROM%%&sp_DTo=%%DATE_TO%%&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=&sp_S=%%SPECIES_ID%%&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=001100000000000000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_Coord%5BW%5D=13.20225563997&sp_Coord%5BS%5D=52.397693609735&sp_Coord%5BE%5D=13.220225091902&sp_Coord%5BN%5D=52.415663061667&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=12.03.2019&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter FORMATTER_WITHOUT_YEAR = DateTimeFormatter.ofPattern("dd.MM");

    public static void main(String[] args) throws IOException {
        OrnithoPageReader pageReader = new OrnithoPageReader();
        int speciesId= Species.BERGENTE.getOrnithoSpeciesId();
        int[] yearsToCheck = {2016, 2017, 2018};
        for (int month = 1; month <= 12; month++) {
            for (int monthPart = 0; monthPart < 3; monthPart++) {
                int overallCount = 0;
                for (int year : yearsToCheck) {
                    int startDay = 10 * monthPart + 1;
                    LocalDate startDate = LocalDate.of(year, month, startDay);
                    LocalDate endDate = startDate.withDayOfMonth(Math.min(startDay + 9, startDate.getMonth().length(startDate.isLeapYear())));

                    if (year == yearsToCheck[0]) {
                        System.out.print(FORMATTER_WITHOUT_YEAR.format(startDate) + "-" + FORMATTER_WITHOUT_YEAR.format(endDate));
                    }

                    overallCount += getCount(pageReader, speciesId, startDate, endDate);
                }
                System.out.println("\t" + overallCount);
            }
        }

    }

    private static int getCount(OrnithoPageReader pageReader, int speciesId, LocalDate startDate, LocalDate endDate)
        throws IOException {
        String url = URL_TEMPLATE.replaceAll("%%DATE_FROM%%", FORMATTER.format(startDate));
        url = url.replaceAll("%%DATE_TO%%", FORMATTER.format(endDate));
        url = url.replaceAll("%%SPECIES_ID%%", String.valueOf(speciesId));
        String html = pageReader.getHtmlForPage(url);

        Pattern pattern = Pattern.compile(".*>(\\d+)&times;</div>.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        int count = 0;
        if (matcher.matches()) {
            count = Integer.parseInt(matcher.group(1));
        }
        return count;
    }

}
