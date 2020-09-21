/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.additionalinfo.backend.ws.config;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.dpppt.additionalinfo.backend.ws.controller.DppptAdditionalInfoController;
import org.dpppt.additionalinfo.backend.ws.statistics.MockStatisticClient;
import org.dpppt.additionalinfo.backend.ws.statistics.SplunkStatisticClient;
import org.dpppt.additionalinfo.backend.ws.statistics.StatisticClient;
import org.dpppt.backend.shared.interceptor.HeaderInjector;
import org.dpppt.backend.shared.security.filter.ResponseWrapperFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public abstract class WSBaseConfig implements WebMvcConfigurer {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	final SignatureAlgorithm algorithm = SignatureAlgorithm.ES256;

	@Value("${ws.headers.protected:}")
	List<String> protectedHeaders;

	// used for jwt token validity
	int retentionDays = 1;

	@Value("#{${ws.security.headers: {'X-Content-Type-Options':'nosniff', 'X-Frame-Options':'DENY','X-Xss-Protection':'1; mode=block'}}}")
	Map<String, String> additionalHeaders;

	@Value("${ws.statistics.splunk.url:}")
	String splunkUrl;

	@Value("${ws.statistics.splunk.activeapps.query:}")
	String activeAppsQuery;

	@Value("${ws.statistics.splunk.usedauthcodecount.query:}")
	String usedAuthCodeCountQuery;

	@Value("${ws.statistics.splunk.positivetestcount.query:}")
	String positiveTestCountQuery;

	@Value("${ws.statistics.splunk.startdaysback: 60}")
	Integer queryStartDaysBack;
	
	@Value("${ws.statistics.splunk.enddaysback: 3}")
	Integer queryEndDaysBack;

	@Value("${ws.statistics.cachecontrol:PT1H}")
	Duration cacheControl;

	abstract String getPublicKey();

	abstract String getPrivateKey();

	abstract String getSplunkUsername();

	abstract String getSplunkpassword();

	@Bean
	@ConditionalOnProperty(prefix = "ws.statistics.splunk", name = { "url", "activeapps.query",
			"usedauthcodecount.query", "positivetestcount.query" })
	public SplunkStatisticClient splunkStatisticsClient() {
		logger.info("Creating Splunk statistics client");
		return new SplunkStatisticClient(splunkUrl, getSplunkUsername(), getSplunkpassword(), activeAppsQuery,
				usedAuthCodeCountQuery, positiveTestCountQuery, queryStartDaysBack, queryEndDaysBack);
	}

	@Bean
	@ConditionalOnMissingBean
	public StatisticClient mockStatisticsClient() {
		logger.info("Creating Mock statistics client");
		return new MockStatisticClient();
	}

	@Bean
	public DppptAdditionalInfoController dppptAdditionalInfoController(StatisticClient statisticClient) {
		return new DppptAdditionalInfoController(statisticClient, cacheControl);
	}

	@Bean
	public ResponseWrapperFilter hashFilter() {
		return new ResponseWrapperFilter(getKeyPair(algorithm), retentionDays, protectedHeaders);
	}

	@Bean
	public HeaderInjector securityHeaderInjector() {
		return new HeaderInjector(additionalHeaders);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(securityHeaderInjector());
	}

	public KeyPair getKeyPair(SignatureAlgorithm algorithm) {
		Security.addProvider(new BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");
		return new KeyPair(loadPublicKeyFromString(), loadPrivateKeyFromString());
	}

	private PrivateKey loadPrivateKeyFromString() {
		try {
			String privateKey = getPrivateKey();
			Reader reader = new StringReader(privateKey);
			PemReader readerPem = new PemReader(reader);
			PemObject obj = readerPem.readPemObject();
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(obj.getContent());
			KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
			return (PrivateKey) kf.generatePrivate(pkcs8KeySpec);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException();
		}
	}

	private PublicKey loadPublicKeyFromString() {
		try {
			return CertificateFactory.getInstance("X.509")
					.generateCertificate(new ByteArrayInputStream(getPublicKey().getBytes())).getPublicKey();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException();
		}
	}
}
