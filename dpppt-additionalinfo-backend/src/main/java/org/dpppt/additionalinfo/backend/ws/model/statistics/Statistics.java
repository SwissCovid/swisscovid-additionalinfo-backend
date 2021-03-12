package org.dpppt.additionalinfo.backend.ws.model.statistics;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.dpppt.additionalinfo.backend.ws.json.CustomLocalDateSerializer;

public class Statistics {

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate lastUpdated;

    private Integer totalActiveUsers;

    private Integer totalCovidcodesEntered;
    private Double covidcodesEntered0to2dPrevWeek;
    private Integer newInfectionsSevenDayAvg;
    private Double newInfectionsSevenDayAvgRelPrevWeek;

    private List<History> history = new ArrayList<History>();

    public Integer getTotalActiveUsers() {
        return totalActiveUsers;
    }

    public void setTotalActiveUsers(Integer totalActiveUsers) {
        this.totalActiveUsers = totalActiveUsers;
    }

    public Integer getTotalCovidcodesEntered() {
        return totalCovidcodesEntered;
    }

    public void setTotalCovidcodesEntered(Integer totalCovidcodesEntered) {
        this.totalCovidcodesEntered = totalCovidcodesEntered;
    }

    public Double getCovidcodesEntered0to2dPrevWeek() {
        return covidcodesEntered0to2dPrevWeek;
    }

    public void setCovidcodesEntered0to2dPrevWeek(Double covidcodesEntered0to2dPrevWeek) {
        this.covidcodesEntered0to2dPrevWeek = covidcodesEntered0to2dPrevWeek;
    }

    public Integer getNewInfectionsSevenDayAvg() {
        return newInfectionsSevenDayAvg;
    }

    public void setNewInfectionsSevenDayAvg(Integer newInfectionsSevenDayAvg) {
        this.newInfectionsSevenDayAvg = newInfectionsSevenDayAvg;
    }

    public Double getNewInfectionsSevenDayAvgRelPrevWeek() {
        return newInfectionsSevenDayAvgRelPrevWeek;
    }

    public void setNewInfectionsSevenDayAvgRelPrevWeek(Double newInfectionsSevenDayAvgRelPrevWeek) {
        this.newInfectionsSevenDayAvgRelPrevWeek = newInfectionsSevenDayAvgRelPrevWeek;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
