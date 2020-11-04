package org.dpppt.additionalinfo.backend.ws.statistics;

import java.time.LocalDate;
import java.util.Random;
import org.dpppt.additionalinfo.backend.ws.model.statistics.History;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;

public class MockStatisticClient implements StatisticClient {

    private boolean mockImplausible = false;
    private Random rand = new Random();

    /**
     * returns an implausible total active users value on every second method call
     *
     * @return
     */
    @Override
    public Statistics getStatistics() {
        LocalDate today = LocalDate.now();
        Statistics statistics = new Statistics();
        int totalActiveUsers = 1600000 + rand.nextInt(250000);
        if (mockImplausible) {
            totalActiveUsers /= 4;
        }
        statistics.setTotalActiveUsers(totalActiveUsers);
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

        mockImplausible = !mockImplausible;
        return statistics;
    }
}
