package com.nirocca.ornithoalert.grid;

import java.io.IOException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.nirocca.ornithoalert.AllNotThisYearMain;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.SpeciesStatistic;
import com.nirocca.ornithoalert.statistics.StatisticsCalculator;
import com.nirocca.ornithoalert.util.SightingFilter;

/**
 * Ranks rare species (categories never/veryrare/rare, all Germany) by total sighting count over the
 * last years, filtered by already sighted species, sub species and indeterminate species. Also
 * reports in how many of the last calendar years each species was sighted. Run occasionally and
 * paste the result into CreateAllDeFile.
 */
public class RareSpeciesAnalysisMain {

    //One month max per query, slower queries fail server side. Categories never/veryrare/rare only.
    private static final String RARE_SPECIES_URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=display&p_cc=212&sp_tg=1&sp_DChoice=range&sp_DFrom=01.01.2025&sp_DTo=31.01.2025&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&plan-vertical=AUTO&sp_S=&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Family=1&sp_PChoice=all&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001010001100110110010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000&p_cc=212&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=09.11.2026&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final int YEARS_BACK = 5;
    private static final int LAST_CALENDAR_YEARS = 5;
    private static final int MIN_SIGHTING_COUNT = 10;
    private static final int PARALLEL_MONTH_QUERIES = 3;
    private static final DateTimeFormatter URL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final Pattern SPECIES_ID_PATTERN = Pattern.compile("sp_S=(\\d+)");

    private record RareSpecies(int speciesId, String germanName, long sightingCount, long yearsSeen) {
    }

    private record MonthStatistics(YearMonth month, List<SpeciesStatistic> statistics) {
    }


    public static void main(String[] args) throws IOException {
        List<RareSpecies> topSpecies = calcTopUnsightedRareSpecies();
        System.out.println();
        System.out.println(">= " + MIN_SIGHTING_COUNT + " sightings (main list for CreateAllDeFile):");
        topSpecies.stream().filter(s -> s.sightingCount() >= MIN_SIGHTING_COUNT).forEach(RareSpeciesAnalysisMain::printSpecies);
        System.out.println();
        System.out.println("< " + MIN_SIGHTING_COUNT + " sightings (comment section for CreateAllDeFile):");
        topSpecies.stream().filter(s -> s.sightingCount() < MIN_SIGHTING_COUNT).forEach(RareSpeciesAnalysisMain::printSpecies);
    }

    private static void printSpecies(RareSpecies species) {
        Species enumSpecies = Species.getById(species.speciesId());
        if (enumSpecies == Species.UNKNOWN) {
            System.out.println("// no enum constant for " + species);
            return;
        }
        System.out.println(enumSpecies.name() + ", // " + species.sightingCount() + " sightings (last "
            + YEARS_BACK + " years), " + species.yearsSeen() + "/" + LAST_CALENDAR_YEARS + " calendar years");
    }

    private static List<RareSpecies> calcTopUnsightedRareSpecies() throws IOException {
        List<MonthStatistics> monthStatistics = fetchRareSpeciesStatistics();
        List<SpeciesStatistic> statistics = monthStatistics.stream()
            .flatMap(m -> m.statistics().stream())
            .collect(Collectors.toList());
        System.out.println("total statistics: " + statistics.size());
        statistics = SightingFilter.filterOutNonRelevantStatistics(statistics);
        statistics = SightingFilter.filterOutSubSpecies(statistics);
        statistics = StatisticsCalculator.filterOutAlreadySightedSpecies(statistics);
        System.out.println("after filters: " + statistics.size());

        Set<Integer> filteredIds = statistics.stream()
            .map(RareSpeciesAnalysisMain::extractSpeciesId)
            .collect(Collectors.toSet());
        Set<Integer> lastYears = new HashSet<>();
        for (int year = YearMonth.now().getYear() - LAST_CALENDAR_YEARS + 1; year <= YearMonth.now().getYear(); year++) {
            lastYears.add(year);
        }

        Map<Integer, Long> counts = new HashMap<>();
        Map<Integer, Set<Integer>> yearsBySpecies = new HashMap<>();
        Map<Integer, String> names = new HashMap<>();
        for (MonthStatistics monthStatistic : monthStatistics) {
            for (SpeciesStatistic statistic : monthStatistic.statistics()) {
                int speciesId = extractSpeciesId(statistic);
                if (!filteredIds.contains(speciesId)) {
                    continue;
                }
                counts.merge(speciesId, (long) statistic.count(), Long::sum);
                yearsBySpecies.computeIfAbsent(speciesId, k -> new HashSet<>()).add(monthStatistic.month().getYear());
                names.putIfAbsent(speciesId, statistic.germanName());
            }
        }

        return counts.entrySet().stream()
            .map(e -> new RareSpecies(e.getKey(), names.get(e.getKey()), e.getValue(),
                yearsBySpecies.get(e.getKey()).stream().filter(lastYears::contains).count()))
            .sorted(Comparator.comparingLong(RareSpecies::sightingCount).reversed())
            .collect(Collectors.toList());
    }

    private static int extractSpeciesId(SpeciesStatistic statistic) {
        Matcher matcher = SPECIES_ID_PATTERN.matcher(statistic.observationsLink());
        if (!matcher.find()) {
            throw new RuntimeException("No species id in link: " + statistic.observationsLink());
        }
        return Integer.parseInt(matcher.group(1));
    }

    private static List<MonthStatistics> fetchRareSpeciesStatistics() throws IOException {
        YearMonth start = YearMonth.of(YearMonth.now().getYear() - YEARS_BACK, 1);
        YearMonth end = YearMonth.now().minusMonths(1);
        List<YearMonth> months = new ArrayList<>();
        for (YearMonth month = start; !month.isAfter(end); month = month.plusMonths(1)) {
            months.add(month);
        }

        AllNotThisYearMain statisticsReader = new AllNotThisYearMain();
        ExecutorService executor = Executors.newFixedThreadPool(PARALLEL_MONTH_QUERIES);
        List<Callable<MonthStatistics>> tasks = months.stream()
            .<Callable<MonthStatistics>>map(month -> () -> readRareSpeciesStatisticsForMonth(statisticsReader, month))
            .collect(Collectors.toList());
        try {
            return executor.invokeAll(tasks).stream()
                .map(RareSpeciesAnalysisMain::getFutureResult)
                .collect(Collectors.toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    private static MonthStatistics getFutureResult(Future<MonthStatistics> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static MonthStatistics readRareSpeciesStatisticsForMonth(AllNotThisYearMain statisticsReader,
                                                                    YearMonth month) throws IOException {
        String url = RARE_SPECIES_URL_TEMPLATE
            .replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + month.atDay(1).format(URL_DATE_FORMAT) + "&")
            .replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + month.atEndOfMonth().format(URL_DATE_FORMAT) + "&");
        System.out.println("Fetching rare species statistics for " + month);
        return new MonthStatistics(month, statisticsReader.readStatistics(url));
    }
}
