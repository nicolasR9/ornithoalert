package com.nirocca.ornithoalert.frequencies;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.nirocca.ornithoalert.AllNotThisYearMain;
import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.model.SpeciesStatistic;

public class HelgolandBestWeekMain {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=duration&p_cc=217&sp_tg=1&sp_DChoice=range&sp_DFrom=01.09.2026&sp_DTo=07.09.2026&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&plan-vertical=AUTO&sp_S=&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=217&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=15.07.2017&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final LocalDate FIRST_WINDOW_START = LocalDate.of(2020, 8, 15);
    private static final LocalDate LAST_WINDOW_END = LocalDate.of(2020, 11, 15);
    private static final int[] YEARS_TO_CHECK = {2016, 2017, 2018, 2019, 2020, 2021, 2022, 2023, 2024, 2025};

    private static final List<String> SPECIES = List.of(
        "Spornammer", "Spornpieper", "Spatelraubmöwe", "Waldpieper", "Atlantiksturmtaucher",
        "Rosenstar", "Tienschan-Laubsänger", "Buschrohrsänger", "Goldhähnchen-Laubsänger",
        "Waldammer", "Papageitaucher");

    private static final int PARALLEL_QUERIES = 3;

    public static void main(String[] args) throws Exception {
        List<LocalDate> windowStarts = getWindowStarts();

        // counts per species: one int[] per window, one count per year
        Map<String, List<int[]>> countsBySpecies = new java.util.LinkedHashMap<>();
        for (String name : SPECIES) {
            List<int[]> windowCounts = new ArrayList<>();
            for (int i = 0; i < windowStarts.size(); i++) {
                windowCounts.add(new int[YEARS_TO_CHECK.length]);
            }
            countsBySpecies.put(name, windowCounts);
        }

        List<Callable<int[]>> tasks = new ArrayList<>();
        for (LocalDate windowStart : windowStarts) {
            for (int year : YEARS_TO_CHECK) {
                LocalDate start = windowStart.withYear(year);
                LocalDate end = start.plusDays(6);
                String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FORMATTER.format(start) + "&");
                url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + FORMATTER.format(end) + "&");
                final String taskUrl = url;
                tasks.add(() -> countSightingsPerSpecies(taskUrl));
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_QUERIES);
        try {
            List<Future<int[]>> futures = executor.invokeAll(tasks);
            int taskIndex = 0;
            for (int windowIndex = 0; windowIndex < windowStarts.size(); windowIndex++) {
                for (int yearIndex = 0; yearIndex < YEARS_TO_CHECK.length; yearIndex++) {
                    int[] windowCounts = futures.get(taskIndex++).get();
                    for (int speciesIndex = 0; speciesIndex < SPECIES.size(); speciesIndex++) {
                        countsBySpecies.get(SPECIES.get(speciesIndex)).get(windowIndex)[yearIndex] = windowCounts[speciesIndex];
                    }
                }
            }
        } finally {
            executor.shutdown();
        }

        writeCsv(countsBySpecies);
    }

    // aggregated species view (sp_FChoice=species): one request per window, no paging
    private static int[] countSightingsPerSpecies(String url) throws IOException {
        int[] counts = new int[SPECIES.size()];
        for (SpeciesStatistic stat : new AllNotThisYearMain().readStatistics(url)) {
            int index = SPECIES.indexOf(stat.germanName());
            if (index >= 0) {
                counts[index] += stat.count();
            }
        }
        return counts;
    }

    private static List<LocalDate> getWindowStarts() {
        List<LocalDate> windowStarts = new ArrayList<>();
        LocalDate date = FIRST_WINDOW_START;
        while (!date.plusDays(6).isAfter(LAST_WINDOW_END)) {
            windowStarts.add(date);
            date = date.plusDays(7);
        }
        return windowStarts;
    }

    private static double median(int[] counts) {
        int[] sorted = counts.clone();
        Arrays.sort(sorted);
        int middle = sorted.length / 2;
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        }
        return sorted[middle];
    }

    private static void writeCsv(Map<String, List<int[]>> countsBySpecies) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new FileOutputStream(Constants.OUTPUT_DIR + "helgolandBestWeek.csv"));
        out.print("Art");
        DateTimeFormatter headerFormat = DateTimeFormatter.ofPattern("dd.MM");
        for (LocalDate windowStart : getWindowStarts()) {
            out.print("," + windowStart.format(headerFormat) + "-" + windowStart.plusDays(6).format(headerFormat));
        }
        out.println();
        for (String species : SPECIES) {
            out.print(species);
            for (int[] windowCounts : countsBySpecies.get(species)) {
                double median = median(windowCounts);
                out.print("," + (median == Math.floor(median) ? String.valueOf((long) median) : String.valueOf(median)));
            }
            out.println();
        }
        out.close();
        System.out.println("Written to " + Constants.OUTPUT_DIR + "helgolandBestWeek.csv");
    }
}
