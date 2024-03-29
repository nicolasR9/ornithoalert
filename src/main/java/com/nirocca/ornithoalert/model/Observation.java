package com.nirocca.ornithoalert.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

public final class Observation {
    private final Element observationElement;
    
    public Observation(Element observationElement) {
        this.observationElement = observationElement;
    }

    public String parseGermanName() {
        return observationElement.selectFirst("b").text();
    }

    public String parseLatinName() {
        String result = observationElement.select("span[class=sci_name]").text();
        return result.replaceAll("\\(", "").replaceAll("\\)", "");
    }
    
    public String parseUrl() {
        return observationElement.selectFirst("a[href~=https://www.ornitho.de/index.php\\?m_id=54.*]").attr("href");
    }

    public int parseSpeciesId() {
        Element l = observationElement.selectFirst("a[href~=https://www.ornitho.de/index.php\\?m_id=94.*]");
        if (l == null) {
            System.err.println("Unable to find species id");
            return -1;
        }
        String link = l.attr("href");
        Matcher m = Pattern.compile(".*sp_S=(\\d+)&.*").matcher(link);
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        }
        throw new RuntimeException("No matching link: " + link);
    }

    public String parseCount() {
        return observationElement.selectFirst("span[style=\"vertical-align:top\"]").text();

    }
}