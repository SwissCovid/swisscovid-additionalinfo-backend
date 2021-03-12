package org.dpppt.additionalinfo.backend.ws.statistics;

import java.time.LocalDate;
import java.util.Random;
import org.dpppt.additionalinfo.backend.ws.data.HistoryDataService;
import org.dpppt.additionalinfo.backend.ws.model.statistics.History;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockStatisticClient implements StatisticClient {

    private static final Logger logger = LoggerFactory.getLogger(MockStatisticClient.class);

    private final HistoryDataService historyDataService;
    private boolean mockImplausible = false;
    private Random rand = new Random();

    public MockStatisticClient(HistoryDataService historyDataService) {
        this.historyDataService = historyDataService;
    }

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

        Integer latestSevenDayAverage = null;
        Integer prevWeekSevenDayAverage = null;
        for (int i = statistics.getHistory().size() - 1; i > 0; i--) {
            latestSevenDayAverage =
                    statistics.getHistory().get(i).getNewInfectionsSevenDayAverage();
            if (latestSevenDayAverage != null) {
                LocalDate day = statistics.getHistory().get(i).getDate();
                historyDataService.upsertLatestSevenDayAvgForDay(latestSevenDayAverage, day);
                prevWeekSevenDayAverage =
                        historyDataService.findLatestSevenDayAvgForDay(day.minusDays(7));
                if (prevWeekSevenDayAverage == null) {
                    logger.warn(
                            "no seven day avg history for {}. using current data as fallback", day);
                    prevWeekSevenDayAverage =
                            statistics.getHistory().get(i - 7).getNewInfectionsSevenDayAverage();
                }
                break;
            }
        }
        statistics.setNewInfectionsSevenDayAvg(latestSevenDayAverage);
        statistics.setNewInfectionsSevenDayAvgRelPrevWeek(
                (latestSevenDayAverage / (double) prevWeekSevenDayAverage) - 1);

        mockImplausible = !mockImplausible;
        return statistics;
    }
}
