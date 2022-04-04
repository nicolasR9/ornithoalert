package com.nirocca.ornithoalert;

import com.nirocca.ornithoalert.model.Sighting;

public interface ColorProvider {

    String getColor(Sighting sighting);
}
