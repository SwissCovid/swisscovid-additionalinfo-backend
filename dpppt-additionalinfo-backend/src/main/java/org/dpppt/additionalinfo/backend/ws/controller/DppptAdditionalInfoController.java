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

import org.dpppt.additionalinfo.backend.ws.cache.CacheConfig;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.dpppt.additionalinfo.backend.ws.splunk.SplunkClient;
import org.dpppt.backend.shared.util.HeaderUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1")
public class DppptAdditionalInfoController {

    private static final Logger logger =
            LoggerFactory.getLogger(DppptAdditionalInfoController.class);
    private final SplunkClient splunkClient;

    public DppptAdditionalInfoController(SplunkClient splunkClient) {
        this.splunkClient = splunkClient;
    }

    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @GetMapping(value = "")
    public @ResponseBody String hello() {
        return "Hello from DP3T Additional Info WS";
    }

    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @GetMapping(value = "/statistics")
    public @ResponseBody ResponseEntity<Statistics> getStatistics() {
        return new ResponseEntity<>(
                splunkClient.getStatistics(),
                HeaderUtility.createHeaders(
                        CacheConfig.MAX_AGE_STATISTICS, CacheConfig.NEXT_REFRESH_STATISTICS),
                HttpStatus.OK);
    }
}
