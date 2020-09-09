package org.dpppt.additionalinfo.backend.ws.model.statistics;

public class History {

    private String date;
    private Integer newInfections;
    private Integer newInfectionsSevenDayAverage;
    private Integer covidcodesEntered;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNewInfections() {
        return newInfections;
    }

    public void setNewInfections(Integer newInfections) {
        this.newInfections = newInfections;
    }

    public Integer getNewInfectionsSevenDayAverage() {
        return newInfectionsSevenDayAverage;
    }

    public void setNewInfectionsSevenDayAverage(Integer newInfectionsSevenDayAverage) {
        this.newInfectionsSevenDayAverage = newInfectionsSevenDayAverage;
    }

    public Integer getCovidcodesEntered() {
        return covidcodesEntered;
    }

    public void setCovidcodesEntered(Integer covidcodesEntered) {
        this.covidcodesEntered = covidcodesEntered;
    }
}
