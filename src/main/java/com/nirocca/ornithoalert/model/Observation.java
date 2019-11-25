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
        //<a href="https://www.ornitho.de/index.php?m_id=54&id=28889469">
        //return observationElement.selectFirst("a[href~=https://www.ornitho.de/index.php\\?m_id=54.*]").attr("href");
        String obervationId = observationElement.selectFirst("form[id~=btn-edit-*]").selectFirst("input[name=id]").val();
        return String.format("https://www.ornitho.de/index.php?m_id=54&id=%s", obervationId);
    }

    public String parseCount() {
        return observationElement.selectFirst("span[style=\"vertical-align:top\"]").text();

    }
}