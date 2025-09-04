package com.nirocca.ornithoalert.model.ornitho;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Species {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name_plur")
    private String namePlur;

    @JsonProperty("latin_name")
    private String latinName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamePlur() {
        return namePlur;
    }

    public void setNamePlur(String namePlur) {
        this.namePlur = namePlur;
    }

    public String getLatinName() {
        return latinName;
    }

    public void setLatinName(String latinName) {
        this.latinName = latinName;
    }
}