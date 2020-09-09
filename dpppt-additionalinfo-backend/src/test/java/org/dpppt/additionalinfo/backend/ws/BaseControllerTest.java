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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.PublicKey;
import java.util.Map;
import org.dpppt.backend.shared.security.filter.ResponseWrapperFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"cloud-dev"})
public abstract class BaseControllerTest {
    @Autowired protected ObjectMapper objectMapper;
    protected MockMvc mockMvc;
    @Autowired protected WebApplicationContext webApplicationContext;
    @Autowired protected ResponseWrapperFilter filter;
    protected PublicKey publicKey;

    @Test
    public void testHello() throws Exception {
        final MockHttpServletResponse response =
                mockMvc.perform(get("/v1"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        assertNotNull(response);
        assertEquals("Hello from DP3T Additional Info WS", response.getContentAsString());
    }

    private final Map<String, String> headers =
            Map.of(
                    "X-Content-Type-Options",
                    "nosniff",
                    "X-Frame-Options",
                    "DENY",
                    "X-Xss-Protection",
                    "1; mode=block");

    @Test
    public void testSecurityHeaders() throws Exception {
        final MockHttpServletResponse response =
                mockMvc.perform(get("/v1"))
                        .andExpect(status().is2xxSuccessful())
                        .andReturn()
                        .getResponse();
        for (final var header : headers.keySet()) {
            assertTrue(response.containsHeader(header));
            assertEquals(headers.get(header), response.getHeader(header));
        }
    }
}
