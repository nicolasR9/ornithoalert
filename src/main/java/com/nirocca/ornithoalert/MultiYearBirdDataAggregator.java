package com.nirocca.ornithoalert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiYearBirdDataAggregator {

    private final OrnithoPageReader ornithoPageReader = new OrnithoPageReader();
    private static final String BASE_URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=duration&p_cc=206&sp_tg=1&sp_DChoice=range&sp_DFrom=START_DATE&sp_DTo=END_DATE&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&speciesFilter=&sp_S=1148&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=206&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FChoice=species&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=08.02.2026&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";

    // Month data: [days in month, month name]
    private static final int[][] MONTH_DAYS = {
        {31, 1}, {28, 2}, {31, 3}, {30, 4}, {31, 5}, {30, 6},
        {31, 7}, {31, 8}, {30, 9}, {31, 10}, {30, 11}, {31, 12}
    };

    private static final int[][] LEAP_YEAR_MONTH_DAYS = {
        {31, 1}, {29, 2}, {31, 3}, {30, 4}, {31, 5}, {30, 6},
        {31, 7}, {31, 8}, {30, 9}, {31, 10}, {30, 11}, {31, 12}
    };

    private static final String[] MONTH_NAMES = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public static class YearlyData {
        private final int year;
        private final Map<String, Integer> speciesCounts;
        private final int totalSpecies;
        private final int totalSightings;

        public YearlyData(int year, Map<String, Integer> speciesCounts) {
            this.year = year;
            this.speciesCounts = speciesCounts;
            this.totalSpecies = speciesCounts.size();
            this.totalSightings = speciesCounts.values().stream().mapToInt(Integer::intValue).sum();
        }

        public int getYear() { return year; }
        public Map<String, Integer> getSpeciesCounts() { return speciesCounts; }
        public int getTotalSpecies() { return totalSpecies; }
        public int getTotalSightings() { return totalSightings; }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public Map<Integer, YearlyData> aggregateMultiYearData() throws IOException {
        Map<Integer, YearlyData> yearlyResults = new HashMap<>();
        int[] years = {2023, 2024, 2025};

        System.out.println("Starting multi-year bird data aggregation (2023-2025)...");
        System.out.println("=".repeat(80));

        for (int year : years) {
            System.out.println("\nProcessing year " + year + "...");
            Map<String, Integer> yearlyAggregation = aggregateYearData(year);
            yearlyResults.put(year, new YearlyData(year, yearlyAggregation));

            System.out.println("Completed " + year + ": " + yearlyAggregation.size() + " species, " +
                             yearlyAggregation.values().stream().mapToInt(Integer::intValue).sum() + " total sightings");
        }

        return yearlyResults;
    }

    private Map<String, Integer> aggregateYearData(int year) {
        Map<String, Integer> yearlyAggregation = new HashMap<>();
        int[][] monthDays = isLeapYear(year) ? LEAP_YEAR_MONTH_DAYS : MONTH_DAYS;

        for (int month = 1; month <= 12; month++) {
            System.out.print("  " + MONTH_NAMES[month - 1] + "...");

            String startDate = String.format("%02d.%02d.%d", 1, month, year);
            String endDate = String.format("%02d.%02d.%d", monthDays[month - 1][0], month, year);

            String monthlyUrl = BASE_URL_TEMPLATE
                .replace("START_DATE", startDate)
                .replace("END_DATE", endDate);

            Map<String, Integer> monthlyData = extractSpeciesCounts(monthlyUrl);

            System.out.println(" (" + monthlyData.size() + " species)");

            // Aggregate monthly data into yearly totals
            for (Map.Entry<String, Integer> entry : monthlyData.entrySet()) {
                String species = entry.getKey();
                Integer count = entry.getValue();
                yearlyAggregation.put(species, yearlyAggregation.getOrDefault(species, 0) + count);
            }
        }

        return yearlyAggregation;
    }

    private Map<String, Integer> extractSpeciesCounts(String url) {
        Map<String, Integer> speciesCounts = new HashMap<>();

        try {
            String htmlContent = ornithoPageReader.getPageContent(url);
            speciesCounts = parseSpeciesCounts(htmlContent);
        } catch (Exception e) {
            System.err.println("\nError processing URL: " + url);
            System.err.println("Error: " + e.getMessage());
            // Continue processing other months even if one fails
        }

        return speciesCounts;
    }

    private Map<String, Integer> parseSpeciesCounts(String htmlContent) {
        Map<String, Integer> speciesCounts = new HashMap<>();

        // Pattern based on the observed structure from previous testing
        Pattern speciesPattern = Pattern.compile("<b>([^<]+)</b>\\s*<span class=\"sci_name\">\\([^)]+\\)</span>[^>]*?title=\"(\\d+)\"", Pattern.DOTALL);
        Matcher matcher = speciesPattern.matcher(htmlContent);

        while (matcher.find()) {
            try {
                String species = matcher.group(1).trim();
                int count = Integer.parseInt(matcher.group(2));

                if (!species.isEmpty() && count > 0) {
                    speciesCounts.put(species, speciesCounts.getOrDefault(species, 0) + count);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        // Alternative pattern if the first one doesn't work
        if (speciesCounts.isEmpty()) {
            Pattern alternatePattern = Pattern.compile("<b>([A-ZÄÖÜ][^<]+)</b>.*?title=\"(\\d+)\"", Pattern.DOTALL);
            Matcher alternateMatcher = alternatePattern.matcher(htmlContent);

            while (alternateMatcher.find()) {
                try {
                    String species = alternateMatcher.group(1).trim();
                    int count = Integer.parseInt(alternateMatcher.group(2));

                    if (!species.isEmpty() && count > 0) {
                        // Filter out metadata lines
                        if (!species.contains("Taxa (Arten") && !species.contains("unbestimmt")) {
                            speciesCounts.put(species, speciesCounts.getOrDefault(species, 0) + count);
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }

        // Fallback pattern for broader matching
        if (speciesCounts.isEmpty()) {
            Pattern imgPattern = Pattern.compile("title=\"(\\d+)\"[^>]*>");
            Matcher imgMatcher = imgPattern.matcher(htmlContent);

            while (imgMatcher.find()) {
                int count = Integer.parseInt(imgMatcher.group(1));
                int imgStart = imgMatcher.start();

                int searchStart = Math.max(0, imgStart - 500);
                String precedingText = htmlContent.substring(searchStart, imgStart);

                Pattern speciesBackPattern = Pattern.compile("<b>([A-ZÄÖÜ][^<]+)</b>");
                Matcher speciesBackMatcher = speciesBackPattern.matcher(precedingText);

                String lastSpecies = null;
                while (speciesBackMatcher.find()) {
                    lastSpecies = speciesBackMatcher.group(1).trim();
                }

                if (lastSpecies != null && !lastSpecies.isEmpty() && count > 0) {
                    // Filter out metadata lines
                    if (!lastSpecies.contains("Taxa (Arten") && !lastSpecies.contains("unbestimmt")) {
                        speciesCounts.put(lastSpecies, speciesCounts.getOrDefault(lastSpecies, 0) + count);
                    }
                }
            }
        }

        return speciesCounts;
    }

    public void printResults(Map<Integer, YearlyData> yearlyResults) {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("MULTI-YEAR BIRD SPECIES COUNT AGGREGATION (2023-2025)");
        System.out.println("=".repeat(100));

        if (yearlyResults.isEmpty()) {
            System.out.println("No data found across any years.");
            return;
        }

        // Print yearly summaries
        System.out.println("\nYEARLY SUMMARIES:");
        System.out.println("-".repeat(60));
        for (int year = 2023; year <= 2025; year++) {
            YearlyData data = yearlyResults.get(year);
            if (data != null) {
                System.out.printf("%d: %,d species, %,d total sightings%n",
                    year, data.getTotalSpecies(), data.getTotalSightings());
            }
        }

        // Aggregate all years
        Map<String, Integer> totalAggregation = new HashMap<>();
        Map<String, Map<Integer, Integer>> speciesByYear = new HashMap<>();

        for (YearlyData yearData : yearlyResults.values()) {
            for (Map.Entry<String, Integer> entry : yearData.getSpeciesCounts().entrySet()) {
                String species = entry.getKey();
                Integer count = entry.getValue();

                totalAggregation.put(species, totalAggregation.getOrDefault(species, 0) + count);

                speciesByYear.computeIfAbsent(species, k -> new HashMap<>())
                    .put(yearData.getYear(), count);
            }
        }

        // Sort species by total count
        List<Map.Entry<String, Integer>> sortedEntries = totalAggregation.entrySet()
            .stream()
            .sorted((a, b) -> {
                int countComparison = b.getValue().compareTo(a.getValue());
                return countComparison != 0 ? countComparison : a.getKey().compareTo(b.getKey());
            })
            .toList();

        // Print detailed results - ALL SPECIES
        System.out.println("\nDETAILED RESULTS (ALL SPECIES sorted by total count):");
        System.out.println("-".repeat(100));
        System.out.printf("%-50s %8s %8s %8s %8s %10s%n", "Species", "2023", "2024", "2025", "Total", "Avg/Year");
        System.out.println("-".repeat(100));

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String species = entry.getKey();
            int total = entry.getValue();

            Map<Integer, Integer> yearCounts = speciesByYear.get(species);
            int count2023 = yearCounts.getOrDefault(2023, 0);
            int count2024 = yearCounts.getOrDefault(2024, 0);
            int count2025 = yearCounts.getOrDefault(2025, 0);
            double average = total / 3.0;

            System.out.printf("%-50s %8d %8d %8d %8d %10.1f%n",
                species.length() > 50 ? species.substring(0, 47) + "..." : species,
                count2023, count2024, count2025, total, average);
        }

        // Summary statistics
        System.out.println("-".repeat(100));
        int totalSpecies = totalAggregation.size();
        int totalSightings = totalAggregation.values().stream().mapToInt(Integer::intValue).sum();

        System.out.printf("%-50s %8s %8s %8s %8d %10s%n", "GRAND TOTALS:", "", "", "", totalSightings, "");
        System.out.printf("%-50s %8s %8s %8s %8d %10.1f%n", "UNIQUE SPECIES:", "", "", "", totalSpecies, totalSpecies / 3.0);

        // Species appearing in all years
        long speciesInAllYears = speciesByYear.values().stream()
            .filter(yearMap -> yearMap.size() == 3)
            .count();

        System.out.println();
        System.out.println("ADDITIONAL STATISTICS:");
        System.out.println("-".repeat(50));
        System.out.printf("Species appearing in all 3 years: %d%n", speciesInAllYears);
        System.out.printf("Species appearing only once: %d%n",
            speciesByYear.values().stream().mapToInt(Map::size).filter(size -> size == 1).count());
        System.out.printf("Average sightings per year: %,.1f%n", totalSightings / 3.0);
        System.out.println("=".repeat(100));
    }

    public static void main(String[] args) {
        MultiYearBirdDataAggregator aggregator = new MultiYearBirdDataAggregator();

        try {
            Map<Integer, YearlyData> yearlyResults = aggregator.aggregateMultiYearData();
            aggregator.printResults(yearlyResults);
        } catch (IOException e) {
            System.err.println("Error during aggregation: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}