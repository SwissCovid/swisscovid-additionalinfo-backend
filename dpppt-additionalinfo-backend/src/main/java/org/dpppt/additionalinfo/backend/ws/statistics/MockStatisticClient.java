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
			if (i > 1 && i < 20) {
				history.setNewInfections((int) (210 + Math.pow(i, 2)));
			}
			// history.setNewInfectionsSevenDayAverage((int) (250 + Math.pow(i, 2)));
			statistics.getHistory().add(history);

			dayDate = dayDate.plusDays(1);
		}

		StatisticHelper.calculateRollingAverage(statistics);

		return statistics;
	}
}
