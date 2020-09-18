package org.dpppt.additionalinfo.backend.ws.model.statistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.dpppt.additionalinfo.backend.ws.json.CustomLocalDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class Statistics {

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate lastUpdated;
	
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

	public LocalDate getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDate lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

}
