package org.dpppt.additionalinfo.backend.ws.statistics;

import java.time.LocalDate;

import org.dpppt.additionalinfo.backend.ws.model.statistics.History;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;

public class MockStatisticClient implements StatisticClient {
	@Override
	public Statistics getStatistics() {
		LocalDate today = LocalDate.now();
		Statistics statistics = new Statistics();
		statistics.setTotalActiveUsers(1623942);
		statistics.setLastUpdated(today);
		LocalDate dayDate = LocalDate.now().minusDays(21);
		for (int i = 0; dayDate.isBefore(today); i++) {
			History history = new History();
			history.setDate(dayDate);
			history.setCovidcodesEntered(50 + i * 2);
			history.setNewInfections((int) (210 + Math.pow(i, 2)));
			//history.setNewInfectionsSevenDayAverage((int) (250 + Math.pow(i, 2)));
			statistics.getHistory().add(history);

			dayDate = dayDate.plusDays(1);
		}
		
		int window = 7;
		for (int i = 3; i < statistics.getHistory().size() - 3; i++) {
			int sumInWindow = 0;
			for (int j = 0; j < window; j++) {
				Integer newInfectionsForDay = statistics.getHistory().get(i - 3 + j).getNewInfections();
				if (newInfectionsForDay != null) {
					sumInWindow += newInfectionsForDay;
				}
			}
			statistics.getHistory().get(i).setNewInfectionsSevenDayAverage(sumInWindow / window);
		}
		
		return statistics;
	}
}
