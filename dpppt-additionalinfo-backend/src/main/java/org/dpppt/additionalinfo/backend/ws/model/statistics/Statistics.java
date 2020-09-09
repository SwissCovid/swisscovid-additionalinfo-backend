package org.dpppt.additionalinfo.backend.ws.model.statistics;

import java.util.ArrayList;
import java.util.List;

public class Statistics {

    private Integer totalActiveUsers;
    private List<History> history = new ArrayList<History>();

    public Integer getTotalActiveUsers() {
        return totalActiveUsers;
    }

    public void setTotalActiveUsers(Integer totalActiveUsers) {
        this.totalActiveUsers = totalActiveUsers;
    }

    public List<History> getHistory() {
        return history;
    }

    public void setHistory(List<History> history) {
        this.history = history;
    }
}
