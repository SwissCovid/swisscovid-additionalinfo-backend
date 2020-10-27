/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.additionalinfo.backend.ws;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.Charset;
import javax.servlet.Filter;
import org.dpppt.additionalinfo.backend.ws.controller.DppptAdditionalInfoController;
import org.dpppt.additionalinfo.backend.ws.model.statistics.Statistics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles({"actuator-security"})
@SpringBootTest(
        properties = {
            "ws.monitor.prometheus.user=prometheus",
            "ws.monitor.prometheus.password=prometheus",
            "management.endpoints.enabled-by-default=true",
            "management.endpoints.web.exposure.include=*"
        })
public class DppptAdditionalInfoControllerTest extends BaseControllerTest {

    @Autowired private Filter springSecurityFilterChain;
    @Autowired private DppptAdditionalInfoController additionalInfoController;

    @Before
    public void setup() throws Exception {
        this.publicKey = filter.getPublicKey();
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .addFilter(springSecurityFilterChain)
                        .addFilter(filter, "/*")
                        .build();
        this.objectMapper = new ObjectMapper(new JsonFactory());
        this.objectMapper.registerModule(new JavaTimeModule());
        // this makes sure, that the objectmapper does not fail, when a filter is not provided.
        this.objectMapper.setFilterProvider(new SimpleFilterProvider().setFailOnUnknownId(false));
    }

    @Test
    public void testHello() throws Exception {
        super.testHello();
    }

    @Test
    public void testSecurityHeaders() throws Exception {
        super.testSecurityHeaders();
    }

    @Test
    public void testActuatorSecurity() throws Exception {
        var response =
                mockMvc.perform(get("/actuator/health"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(get("/actuator/loggers"))
                        .andExpect(status().is(401))
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(
                                get("/actuator/loggers")
                                        .header(
                                                "Authorization",
                                                "Basic cHJvbWV0aGV1czpwcm9tZXRoZXVz"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(get("/actuator/prometheus"))
                        .andExpect(status().is(401))
                        .andReturn()
                        .getResponse();
        response =
                mockMvc.perform(
                                get("/actuator/prometheus")
                                        .header(
                                                "Authorization",
                                                "Basic cHJvbWV0aGV1czpwcm9tZXRoZXVz"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse();
    }

    @Test
    public void testStatistics() throws Exception {
        // the mock statistic client returns implausible total active users on every second reload

        Integer totalActiveUsers1 = getTotalActiveUsers(); // plausible
        additionalInfoController.reloadStats();
        Integer totalActiveUsers2 = getTotalActiveUsers(); // implausible
        additionalInfoController.reloadStats();

        Integer totalActiveUsers3 = getTotalActiveUsers(); // plausible
        additionalInfoController.reloadStats();
        Integer totalActiveUsers4 = getTotalActiveUsers(); // implausible

        // expected behaviour: the cached totalActiveUsers value is only updated if the statistic
        // client sends a plausible value
        Assert.assertEquals(totalActiveUsers1, totalActiveUsers2);
        Assert.assertEquals(totalActiveUsers3, totalActiveUsers4);
        Assert.assertNotEquals(totalActiveUsers1, totalActiveUsers3);
    }

    private Integer getTotalActiveUsers() throws Exception {
        var response =
                mockMvc.perform(get("/v1/statistics"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        Statistics statistics = objectMapper
                .readValue(response.getContentAsString(Charset.forName("utf-8")), Statistics.class);
        return statistics.getTotalActiveUsers();
    }
}
