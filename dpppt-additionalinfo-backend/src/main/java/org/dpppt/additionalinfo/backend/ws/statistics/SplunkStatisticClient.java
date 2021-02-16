package org.dpppt.additionalinfo.backend.ws.statistics;

import java.net.URI;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.dpppt.additionalinfo.backend.ws.model.statistics.History;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SplunkStatisticClient implements StatisticClient {

  private final String url;
  private final String username;
  private final String password;
  private final String activeAppsQuery;
  private final String usedAuthCodeCountQuery;
  private final String positiveTestCountQuery;
  private final LocalDate queryStartDate;
  private final Integer queryEndDaysBack;
  private final Integer overrideActiveAppsCount;

  private final RestTemplate rt;

  private static final int CONNECT_TIMEOUT = 30_000;
  private static final int SOCKET_TIMEOUT = 30_000;

  private static final Logger logger = LoggerFactory.getLogger(SplunkStatisticClient.class);

  public SplunkStatisticClient(
      String splunkUrl,
      String splunkUsername,
      String splunkpassword,
      String activeAppsQuery,
      String usedAuthCodeCountQuery,
      String positiveTestCountQuery,
      LocalDate queryStartDate,
      Integer queryEndDaysBack, 
      Integer overrideActiveAppsCount) {
    this.url = splunkUrl;
    this.username = splunkUsername;
    this.password = splunkpassword;
    this.activeAppsQuery = activeAppsQuery;
    this.usedAuthCodeCountQuery = usedAuthCodeCountQuery;
    this.positiveTestCountQuery = positiveTestCountQuery;
    this.queryStartDate = queryStartDate;
    this.queryEndDaysBack = queryEndDaysBack;
    this.overrideActiveAppsCount = overrideActiveAppsCount;

    // Setup rest template for making http requests to Splunk. This configures a
    // custom HTTP client with some good defaults and a custom user agent.
    HttpClientBuilder builder =
        HttpClients.custom().useSystemProperties().setUserAgent("dp3t-additional-info-backend");
    builder
        .disableCookieManagement()
        .setDefaultRequestConfig(
            RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build());

    CloseableHttpClient httpClient = builder.build();
    this.rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
  }

  @Override
  public Statistics getStatistics() {
    long start = System.currentTimeMillis();
    logger.info("Loading statistics from Splunk: " + this.url);

    Statistics result = new Statistics();
    LocalDate today = LocalDate.now();
    result.setLastUpdated(today);
    fillDays(today, result);

    try {
      loadActiveApps(result);
      loadUsedAuthCodeCount(result);
      loadPositiveTestCount(result);
    } catch (Exception e) {
      logger.error("Could not load statistics from Splunk: " + e);
      throw new RuntimeException(e);
    }

    long end = System.currentTimeMillis();
    logger.info("Statistics loaded from Spunk in: " + (end - start) + " [ms]");
    return result;
  }

  private void fillDays(LocalDate today, Statistics statistics) {
    LocalDate dayDate = queryStartDate;
    LocalDate endDate = today.minusDays(queryEndDaysBack);
    logger.info("Setup statistics result history. Start: " + dayDate + " End: " + endDate);
    while (dayDate.isBefore(endDate)) {
      History history = new History();
      history.setDate(dayDate);
      statistics.getHistory().add(history);
      dayDate = dayDate.plusDays(1);
    }
  }

  private void loadActiveApps(Statistics statistics) throws Exception {
    logger.info("Loading active apps");
    RequestEntity<MultiValueMap<String, String>> request =
        RequestEntity.post(new URI(url))
            .accept(MediaType.APPLICATION_JSON)
            .headers(createHeaders())
            .body(createRequestParamsForActiveApps(activeAppsQuery));
    logger.debug("Request entity: " + request.toString());
    ResponseEntity<String> response = rt.exchange(request, String.class);
    logger.info("Result: Status: " + response.getStatusCode() + " Body: " + response.getBody());
    if (response.getStatusCode() == HttpStatus.OK) {
      List<SplunkResult> resultList = extractResultFromSplunkApiString(response.getBody());
      if (!resultList.isEmpty()) {
        // get latest result
        Optional<SplunkResult> latestCount =
            resultList.stream().filter(r -> r.getActiveApps() != null).findFirst();
        if (latestCount.isPresent()) {
          statistics.setTotalActiveUsers(latestCount.get().getActiveApps());
        } else {
          statistics.setTotalActiveUsers(null);
        }
      }
    }
    if (overrideActiveAppsCount != null) {
    	logger.info("Override active app count. From query: " + statistics.getTotalActiveUsers() + " override with: " + overrideActiveAppsCount);
    	statistics.setTotalActiveUsers(overrideActiveAppsCount);
    }
    logger.info("Active apps loaded");
  }

  private void loadUsedAuthCodeCount(Statistics statistics) throws Exception {
    logger.info("Loading used auth code count");
    RequestEntity<MultiValueMap<String, String>> request =
        RequestEntity.post(new URI(url))
            .accept(MediaType.APPLICATION_JSON)
            .headers(createHeaders())
            .body(createRequestParams(usedAuthCodeCountQuery));
    logger.debug("Request entity: " + request.toString());
    ResponseEntity<String> response = rt.exchange(request, String.class);
    logger.info("Result: Status: " + response.getStatusCode() + " Body: " + response.getBody());
    if (response.getStatusCode() == HttpStatus.OK) {
      List<SplunkResult> resultList = extractResultFromSplunkApiString(response.getBody());
      for (SplunkResult r : resultList) {
        for (History h : statistics.getHistory()) {
          if (h.getDate().isEqual(r.getTime().toLocalDate())) {
            h.setCovidcodesEntered(r.getUsedAuthorizationCodesCount());
          }
        }
      }
    }
    logger.info("Used auth code count loaded");
  }

  private void loadPositiveTestCount(Statistics statistics) throws Exception {
    logger.info("Loading positive test count");
    RequestEntity<MultiValueMap<String, String>> request =
        RequestEntity.post(new URI(url))
            .accept(MediaType.APPLICATION_JSON)
            .headers(createHeaders())
            .body(createRequestParams(positiveTestCountQuery));
    logger.debug("Request entity: " + request.toString());
    ResponseEntity<String> response = rt.exchange(request, String.class);
    logger.info("Result: Status: " + response.getStatusCode() + " Body: " + response.getBody());
    if (response.getStatusCode() == HttpStatus.OK) {
      List<SplunkResult> resultList = extractResultFromSplunkApiString(response.getBody());
      for (SplunkResult r : resultList) {
        for (History h : statistics.getHistory()) {
          if (h.getDate().isEqual(r.getTime().toLocalDate())) {
            h.setNewInfections(r.getPositiveTestCount());
          }
        }
      }
    }
    StatisticHelper.calculateRollingAverage(statistics);

    statistics.setTotalCovidcodesEntered(59532); // TODO replace mock data with splunk data
    statistics.setTotalCovidcodesEntered0to2d(0.59576); // TODO replace mock data with splunk data

    Integer lastSevenDayAverage = null;
    Integer prevWeekSevenDayAverage = null;
    for (int i = statistics.getHistory().size() - 1; i > 0; i--) {
      lastSevenDayAverage = statistics.getHistory().get(i).getNewInfectionsSevenDayAverage();
      if (lastSevenDayAverage != null) {
        prevWeekSevenDayAverage =
                statistics.getHistory().get(i - 7).getNewInfectionsSevenDayAverage();
        break;
      }
    }
    statistics.setNewInfectionsSevenDayAvg(lastSevenDayAverage);
    statistics.setNewInfectionsSevenDayAvgRelPrevWeek(
            (lastSevenDayAverage / (double) prevWeekSevenDayAverage) - 1);

    logger.info("Positive test count loaded");
  }

  private MultiValueMap<String, String> createRequestParams(String query) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("search", query);
    long daysBack = ChronoUnit.DAYS.between(queryStartDate, LocalDate.now());
    params.add("earliest_time", "-" + daysBack + "d@d");
    params.add("latest_time", "-" + queryEndDaysBack + "d@d");
    params.add("output_mode", "json");
    return params;
  }

  /**
   * For the active apps, we always get the last 10 days, as we just need the most current value and
   * no history.
   *
   * @param query
   * @return
   */
  private MultiValueMap<String, String> createRequestParamsForActiveApps(String query) {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("search", query);
    int daysBack = 10;
    params.add("earliest_time", "-" + daysBack + "d@d");
    params.add("latest_time", "now");
    params.add("output_mode", "json");
    return params;
  }

  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(username, password);
    return headers;
  }

  /**
   * Reads the response from the splunk api (not fully valid json, but single json objects, line by
   * line) and returns a list of {@link SplunkResult} in descending order by time.
   *
   * @param splunkApiResponse
   * @return
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  private List<SplunkResult> extractResultFromSplunkApiString(String splunkApiResponse)
      throws JsonMappingException, JsonProcessingException {
    String sanitizedSplunkApiStrint = splunkApiResponse.replaceAll("\"NO_DATA\"", "null");
    ObjectMapper om = new ObjectMapper();
    List<SplunkResult> result = new ArrayList<>();
    String[] lines = sanitizedSplunkApiStrint.split("\\n");
    for (String line : lines) {
      SplunkResponse response = om.readValue(line, SplunkResponse.class);
      result.add(response.getResult());
    }
    Collections.sort(result, Collections.reverseOrder(Comparator.comparing(SplunkResult::getTime)));
    return result;
  }
}
