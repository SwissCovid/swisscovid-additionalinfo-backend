package org.dpppt.additionalinfo.backend.ws.statistics;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
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
	private final Integer queryStartDaysBack;
	private final Integer queryEndDaysBack;

	private final RestTemplate rt;

	private static final int CONNECT_TIMEOUT = 30_000;
	private static final int SOCKET_TIMEOUT = 30_000;

	private static final Logger logger = LoggerFactory.getLogger(SplunkStatisticClient.class);

	public SplunkStatisticClient(String splunkUrl, String splunkUsername, String splunkpassword, String activeAppsQuery,
			String usedAuthCodeCountQuery, String positiveTestCountQuery, Integer queryStartDaysBack, Integer queryEndDaysBack) {
		this.url = splunkUrl;
		this.username = splunkUsername;
		this.password = splunkpassword;
		this.activeAppsQuery = activeAppsQuery;
		this.usedAuthCodeCountQuery = usedAuthCodeCountQuery;
		this.positiveTestCountQuery = positiveTestCountQuery;
		this.queryStartDaysBack = queryStartDaysBack;
		this.queryEndDaysBack = queryEndDaysBack;

		// Setup rest template for making http requests to Splunk. This configures a
		// custom HTTP client with some good defaults and a custom user agent.
		HttpClientBuilder builder = HttpClients.custom().setUserAgent("dp3t-additional-info-backend");
		builder.disableCookieManagement().setDefaultRequestConfig(
				RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build());
		try {
			TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
			javax.net.ssl.SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
					.loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
			builder.setSSLSocketFactory(csf);

		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			logger.warn("Could not seup ssl context.", e);
		}

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
		LocalDate dayDate = LocalDate.now().minusDays(queryStartDaysBack);
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
		RequestEntity<MultiValueMap<String, String>> request = RequestEntity.post(new URI(url))
				.accept(MediaType.APPLICATION_JSON).headers(createHeaders()).body(createRequestParams(activeAppsQuery));
		logger.debug("Request entity: " + request.toString());
		ResponseEntity<String> response = rt.exchange(request, String.class);
		logger.info("Result: Status: " + response.getStatusCode() + " Body: " + response.getBody());
		if (response.getStatusCode() == HttpStatus.OK) {
			List<SplunkResult> resultList = extractResultFromSplunkApiString(response.getBody());
			if (!resultList.isEmpty()) {
				// get latest result
				Optional<SplunkResult> latestCount = resultList.stream().filter(r -> r.getActiveApps() != null)
						.findFirst();
				if (latestCount.isPresent()) {
					statistics.setTotalActiveUsers(latestCount.get().getActiveApps());
				} else {
					statistics.setTotalActiveUsers(null);
				}
			}
		}
		logger.info("Active apps loaded");
	}

	private void loadUsedAuthCodeCount(Statistics statistics) throws Exception {
		logger.info("Loading used auth code count");
		RequestEntity<MultiValueMap<String, String>> request = RequestEntity.post(new URI(url))
				.accept(MediaType.APPLICATION_JSON).headers(createHeaders())
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
		RequestEntity<MultiValueMap<String, String>> request = RequestEntity.post(new URI(url))
				.accept(MediaType.APPLICATION_JSON).headers(createHeaders())
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

		logger.info("Positive test count loaded");
	}

	private MultiValueMap<String, String> createRequestParams(String query) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("search", query);
		params.add("earliest_time", "-" + queryStartDaysBack + "d@d");
		params.add("latest_time", "-" + queryEndDaysBack + "d@d");
		params.add("output_mode", "json");
		return params;
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		return headers;
	}

	/**
	 * Reads the response from the splunk api (not fully valid json, but single json
	 * objects, line by line) and returns a list of {@link SplunkResult} in
	 * descending order by time.
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
