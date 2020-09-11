package org.dpppt.additionalinfo.backend.ws.statistics;

import org.dpppt.additionalinfo.backend.ws.model.statistics.History;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.joda.time.LocalDate;

public class MockStatisticClient implements StatisticClient {
    @Override
    public Statistics getStatistics() {
        Statistics statistics = new Statistics();
        statistics.setTotalActiveUsers(1623942);
        LocalDate today = LocalDate.now();
        LocalDate dayDate = LocalDate.now().minusDays(7);
        for (int i = 0; dayDate.isBefore(today.minusDays(1)); i++) {
            History history = new History();
            history.setDate(today.minusDays(i));
            history.setCovidcodesEntered(50 + i * 2);
            history.setNewInfections((int) (210 + Math.pow(i, 2)));
            history.setNewInfectionsSevenDayAverage((int) (250 + Math.pow(i, 2)));
            statistics.getHistory().add(history);

            dayDate = dayDate.plusDays(1);
        }
        return statistics;
    }
}
