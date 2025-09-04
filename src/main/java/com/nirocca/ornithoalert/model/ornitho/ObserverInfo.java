package com.nirocca.ornithoalert.model.ornitho;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ObserverInfo {
    @JsonProperty("id_sighting")
    private long idSighting;

    public long getIdSighting() {
        return idSighting;
    }

    public void setIdSighting(long idSighting) {
        this.idSighting = idSighting;
    }
}