/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.additionalinfo.backend.ws.controller;

import java.time.Duration;

import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.dpppt.additionalinfo.backend.ws.statistics.StatisticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1")
public class DppptAdditionalInfoController {

	private static final Logger logger = LoggerFactory.getLogger(DppptAdditionalInfoController.class);

	private final StatisticClient statisticClient;
	private final Duration cacheControl;

	private Statistics currentStatistics = new Statistics();

	public DppptAdditionalInfoController(StatisticClient statisticClient, Duration cachControl) {
		this.statisticClient = statisticClient;
		this.cacheControl = cachControl;
		reloadStats();
	}

	@CrossOrigin(origins = { "https://editor.swagger.io" })
	@GetMapping(value = "")
	public @ResponseBody String hello() {
		return "Hello from DP3T Additional Info WS";
	}

	@CrossOrigin(origins = { "https://editor.swagger.io" })
	@GetMapping(value = "/statistics")
	public @ResponseBody ResponseEntity<Statistics> getStatistics() {
		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(cacheControl)).body(currentStatistics);
	}

	public void reloadStats() {
		logger.info("Refresh statistics");
		try {
			Statistics newStatistics = statisticClient.getStatistics();
			currentStatistics = newStatistics;
			logger.info("Successfully refreshed statistics");
		} catch (Exception e) {
			logger.error("Could not load statistics: ", e);
		}
	}
}
