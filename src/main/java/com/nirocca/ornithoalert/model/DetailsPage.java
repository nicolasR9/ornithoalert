package com.nirocca.ornithoalert.model;

import org.jsoup.nodes.Element;

public class DetailsPage {
    
    private Element pageElement;
    
    public DetailsPage(Element pageElement) {
        this.pageElement = pageElement;
    }

    public String parseLocationUrl() {
        return pageElement.selectFirst("a[href~=http://www.ornitho.de/index.php\\?m_id=52.*]").attr("href");
    }

}
