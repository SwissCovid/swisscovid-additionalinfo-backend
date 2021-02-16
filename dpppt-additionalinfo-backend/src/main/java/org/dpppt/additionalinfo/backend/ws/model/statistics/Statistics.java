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
    private Double totalCovidcodesEntered0to2d;
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

    public Double getTotalCovidcodesEntered0to2d() {
        return totalCovidcodesEntered0to2d;
    }

    public void setTotalCovidcodesEntered0to2d(Double totalCovidcodesEntered0to2d) {
        this.totalCovidcodesEntered0to2d = totalCovidcodesEntered0to2d;
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
