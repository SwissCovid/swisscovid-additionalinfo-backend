package org.dpppt.additionalinfo.backend.ws.statistics;

import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;

public class StatisticHelper {

    public static void calculateRollingAverage(Statistics statistics) {
        // compute 7 day average
        int window = 7;
        // find first and last entry with values
        Integer first = null;
        Integer last = null;
        for (int i = 0; i < statistics.getHistory().size(); i++) {
            if (statistics.getHistory().get(i).getNewInfections() != null) {
                if (first == null || i < first) {
                    first = i;
                }
                if (last == null || i > last) {
                    last = i;
                }
            }
        }
        for (int i = first + 3; i <= last - 3; i++) {
            int sumInWindow = 0;
            int numElements = 0;
            for (int j = 0; j < window; j++) {
                Integer newInfectionsForDay =
                        statistics.getHistory().get(i - 3 + j).getNewInfections();
                if (newInfectionsForDay != null) {
                    sumInWindow += newInfectionsForDay;
                    numElements++;
                }
            }
            statistics
                    .getHistory()
                    .get(i)
                    .setNewInfectionsSevenDayAverage(sumInWindow / numElements);
        }
    }
}
