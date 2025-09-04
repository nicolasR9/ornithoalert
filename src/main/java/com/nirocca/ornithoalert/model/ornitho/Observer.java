package com.nirocca.ornithoalert.model.ornitho;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Observer {
    @JsonProperty("opt_observer_info")
    private List<ObserverInfo> optObserverInfo;

    public List<ObserverInfo> getOptObserverInfo() {
        return optObserverInfo;
    }

    public void setOptObserverInfo(List<ObserverInfo> optObserverInfo) {
        this.optObserverInfo = optObserverInfo;
    }
}