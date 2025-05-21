package com.nirocca.ornithoalert.grid;

import static com.nirocca.ornithoalert.Species.DOPPELSCHNEPFE;
import static com.nirocca.ornithoalert.Species.DUNKELLAUBSAENGER;
import static com.nirocca.ornithoalert.Species.GROSSER_SCHLAMMLAEUFER;
import static com.nirocca.ornithoalert.Species.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.nirocca.ornithoalert.Constants;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.PrintParameters;
import com.nirocca.ornithoalert.Species;
import com.nirocca.ornithoalert.model.Sighting;
import org.apache.commons.io.FileUtils;

public class CreateAllDeFile {

    private static final String URL_TEMPLATE = "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=216&sp_tg=1&sp_DChoice=range&sp_DFrom=18.10.2022&sp_DTo=18.10.2022&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DOffset=5&sp_SChoice=species&speciesFilter=Fichtenkr&sp_S=515&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111110000000000&p_cc=216&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=24.08.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten";
    private static final String FROM_DATE = "01.01.2022";
    private static final String TO_DATE = "31.12.2024";
    private static final File OUT_FILE = new File("src/main/resources/allDe.txt");

    private static final Species[] SPECIES = {
            ADLERBUSSARD,
            ALPENBRAUNELLE,
            ATLANTIKSTURMTAUCHER,
            BARTGEIER,
            BINDENKREUZSCHNABEL,
            BLAURACKE,
            BLAUSCHWANZ,
            BRILLENGRASMUECKE,
            BUSCHROHRSAENGER,
            DOPPELSCHNEPFE,
            DUNKELLAUBSAENGER,
            DUNKLER_STURMTAUCHER,
            EISMOEWE,
            GAENSEGEIER,
            GELBSCHNABELTAUCHER,
            GOLDHAEHNCHEN_LAUBSAENGER,
            GROSSER_SCHLAMMLAEUFER,
            GRUENLAUBSAENGER,
            HASELHUHN,
            ISABELLSTEINSCHMAETZER,
            KIEFERNKREUZSCHNABEL,
            KRABBENTAUCHER,
            PAPAGEITAUCHER,
            POLARMOEWE,
            RALLENREIHER,
            ROETELSCHWALBE,
            ROSAFLAMINGO,
            ROSENSTAR,
            ROTFLUEGEL_BRACHSCHWALBE,
            ROTKOPFWUERGER,
            SCHLANGENADLER,
            SCHNEEGANS,
            SCHWALBENMOEWE,
            SCHWARZKOPF_RUDERENTE,
            SEEREGENPFEIFER,
            SEGGENROHRSAENGER,
            SKUA,
            SPATELRAUBMOEWE,
            SPERBEREULE,
            SPORNAMMER,
            SPORNPIEPER,
            STEINHUHN,
            STEINROETEL,
            TIENSCHAN_LAUBSAENGER,
            TRIEL,
            WALDPIEPER,
            WEISSKOPF_RUDERENTE,
            WELLENLAEUFER,
            ZISTENSAENGER,
            ZITRONENSTELZE,
            ZITRONENZEISIG,
            ZWERGADLER,
            ZWERGKANADAGANS,
            ZWERGOHREULE,
            ZWERGSUMPFHUHN,
    };



    public static void main(String[] args) throws IOException {
        FileUtils.write(new File(OUT_FILE.toURI()), "", Charset.defaultCharset());

        CoordinatesExporter coordinatesExporter = new CoordinatesExporter();
        boolean first = true;
        for (Species species : SPECIES) {
            FileOutputStream outStream = new FileOutputStream(OUT_FILE, true);
            System.out.println("Processing species: " + species.name());
            String url = URL_TEMPLATE.replaceAll("sp_DFrom=[^&]+&", "sp_DFrom=" + FROM_DATE + "&");
            url = url.replaceAll("sp_DTo=[^&]+&", "sp_DTo=" + TO_DATE + "&");
            url = url.replaceAll("sp_S=[^&]+&", "sp_S=" + species.getOrnithoSpeciesId() + "&");

            List<Sighting> sightings = Main.calcSightings(url, Constants.SortBy.TIME);
            PrintParameters p = new PrintParameters(sightings, false, outStream, null, first, species.getOrnithoSpeciesId());
            coordinatesExporter.printCoordinates(p);
            first = false;
        }

        System.out.println("Done. Written result to: " + OUT_FILE.getAbsolutePath());
    }
}
