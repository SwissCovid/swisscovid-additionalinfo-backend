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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public DppptAdditionalInfoController() {}

    @CrossOrigin(origins = {"https://editor.swagger.io"})
    @GetMapping(value = "")
    public @ResponseBody String hello() {
        return "Hello from DP3T Additional Info WS";
    }
}
