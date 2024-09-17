package com.nirocca.ornithoalert.frequencies;

import static com.nirocca.ornithoalert.Species.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.Species;

public class BestWeekMultipleSpecies {
    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=203&sp_tg=1&sp_DChoice=range&sp_DFrom=01.01.2022&sp_DTo=31.12.2022&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=&sp_S=154&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000001100010000110000000000000000000000000000010001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&p_cc=203&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=05.01.2023&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String PATH_TO_COORDS_DIR = Constants.OUTPUT_DIR + "bestWeek/";
    private static final PrintWriter OUT;

    private static final String START_DATE = "15.09.";
    private static final String END_DATE = "15.11.";

    static {
      try {
        OUT = new PrintWriter(new FileOutputStream(PATH_TO_COORDS_DIR + "/stats.txt"));
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

  private static final int[] yearsToCheck = {2020, 2021, 2022, 2023};

    private static final List<Species> SPECIES = List.of(
        DUNKLER_STURMTAUCHER, SPORNAMMER, GELBBRAUEN_LAUBSAENGER, WELLENLAEUFER, SPATELRAUBMOEWE, ATLANTIKSTURMTAUCHER,
        SKUA, KRABBENTAUCHER, SCHWALBENMOEWE);

  public static void main(String[] args) throws IOException {
        printHeader();

        for (Species species : SPECIES) {
          System.out.println("Species: " + species.name());
            OUT.print(species.name());
            LocalDate date = getStartDate();
            LocalDate finalDate = getEndDate();
            do {
                int count = 0;
                for (int year : yearsToCheck) {
                    LocalDate startDate = date.withYear(year);
                    LocalDate endDate = startDate.plusDays(6);

                    String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FORMATTER.format(startDate) + "&");
                    url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + FORMATTER.format(endDate) + "&");
                    url = url.replaceAll("sp_S=[^&]+&", "sp_S=" + species.getOrnithoSpeciesId() + "&");

                    count += Main.calcSightings(url, Constants.SortBy.SPECIES).size();
                }
                OUT.print("\t" + count);
                date = date.plusDays(7);
            } while (date.plusDays(6).isBefore(finalDate));
            OUT.println();
            OUT.flush();
        }
    }

  private static LocalDate getEndDate() {
    return LocalDate.parse(END_DATE + "2020", FORMATTER);
  }

  private static LocalDate getStartDate() {
    return LocalDate.parse(START_DATE + "2020", FORMATTER);
  }

  private static void printHeader() {
        LocalDate date = getStartDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        do {
            OUT.print("\t" + date.format(formatter) + "-" + date.plusDays(6).format(formatter));
            date = date.plusDays(7);
        } while (date.plusDays(6).isBefore(getEndDate()));
        OUT.println();
        OUT.flush();
    }
}
