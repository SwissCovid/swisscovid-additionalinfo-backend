package org.dpppt.additionalinfo.backend.ws.model.statistics;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.LocalDate;
import org.dpppt.additionalinfo.backend.ws.json.CustomLocalDateSerializer;

public class History {

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate date;

    private Integer newInfections;
    private Integer newInfectionsSevenDayAverage;
    private Integer covidcodesEntered;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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
