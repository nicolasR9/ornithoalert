package com.nirocca.ornithoalert.model;

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
        String result = observationElement.select("i").not("[class]").text();
        return result.replaceAll("\\(", "").replaceAll("\\)", "");
    }
    
    public String parseUrl() {
        return observationElement.selectFirst("a[href~=https://www.ornitho.de/index.php\\?m_id=54.*]").attr("href");
    }

    public String parseCount() {
        return observationElement.selectFirst("span[style=\"vertical-align:top\"]").text();

    }
}