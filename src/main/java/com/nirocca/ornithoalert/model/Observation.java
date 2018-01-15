package com.nirocca.ornithoalert.model;

import org.jsoup.nodes.Element;

public final class Observation {
    private Element observationElement;
    
    public Observation(Element observationElement) {
        this.observationElement = observationElement;
    }

    public String parseGermanName() {
        return observationElement.selectFirst("b").text();
    }

    public String parseLatinName() {
        return observationElement.selectFirst("i").text();
    }
    
    public String parseUrl() {
        //<a href="http://www.ornitho.de/index.php?m_id=54&id=28889469">
        return observationElement.selectFirst("a[href~=http://www.ornitho.de/index.php\\?m_id=54.*]").attr("href");
    }
}