package org.dpppt.additionalinfo.backend.ws.splunk;

import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;

public interface SplunkClient {
    public Statistics getStatistics();
}
