package org.dpppt.additionalinfo.backend.ws.statistics;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SplunkStatisticClient implements StatisticClient {

	private final String url;
	private final String username;
	private final String password;
	private final String activeAppsQuery;
	private final String usedAuthCodeCountQuery;
	private final String positiveTestCountQuery;
	private final RestTemplate rt;

	private static final int CONNECT_TIMEOUT = 30_000;
	private static final int SOCKET_TIMEOUT = 30_000;

	private static final Logger logger = LoggerFactory.getLogger(SplunkStatisticClient.class);

	public SplunkStatisticClient(String splunkUrl, String splunkUsername, String splunkpassword, String activeAppsQuery,
			String usedAuthCodeCountQuery, String positiveTestCountQuery) {
		this.url = splunkUrl;
		this.username = splunkUsername;
		this.password = splunkpassword;
		this.activeAppsQuery = activeAppsQuery;
		this.usedAuthCodeCountQuery = usedAuthCodeCountQuery;
		this.positiveTestCountQuery = positiveTestCountQuery;

		// Setup rest template for making http requests to Splunk. This configures a
		// custom HTTP client with some good defaults and a custom user agent.
		PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
		manager.setDefaultMaxPerRoute(20);
		manager.setMaxTotal(30);
		HttpClientBuilder builder = HttpClients.custom().setUserAgent("dp3t-additional-info-backend");
		builder.setConnectionManager(manager).disableCookieManagement().setDefaultRequestConfig(
				RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build());
		CloseableHttpClient httpClient = builder.build();
		this.rt = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
	}

	@Override
	public Statistics getStatistics() {
		long start = System.currentTimeMillis();
		logger.info("Loading statistics from Splunk: " + this.url);

		Statistics result = new Statistics();

		try {
			loadActiveApps(result);

		} catch (Exception e) {
			logger.error("Could not load statistics from Splunk: " + e);
			throw new RuntimeException(e);
		}

		long end = System.currentTimeMillis();
		logger.info("Statistics loaded from Spunk in: " + (end - start) + " [ms]");
		return result;
	}

	private void loadActiveApps(Object statistics) throws URISyntaxException {
		logger.info("Loading active apps");
		RequestEntity<MultiValueMap<String, String>> request = RequestEntity.post(new URI(url))
				.accept(MediaType.APPLICATION_JSON).headers(createHeaders()).body(createRequestParams(activeAppsQuery));
		ResponseEntity<String> result = rt.exchange(request, String.class);
		logger.info("Result: " + result.getBody());
		logger.info("Active apps loaded");
	}

	private MultiValueMap<String, String> createRequestParams(String query) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("search", query);
		params.add("earliest_time", "-30d@d");
		params.add("latest_time", "now");
		params.add("output_mode", "json");
		return params;
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(username, password);
		return headers;
	}
}
