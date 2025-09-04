package com.nirocca.ornithoalert.model.ornitho;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataWrapper {
    @JsonProperty("data")
    private List<OrnithoData> data;

    public List<OrnithoData> getData() {
        return data;
    }

    public void setData(List<OrnithoData> data) {
        this.data = data;
    }
}