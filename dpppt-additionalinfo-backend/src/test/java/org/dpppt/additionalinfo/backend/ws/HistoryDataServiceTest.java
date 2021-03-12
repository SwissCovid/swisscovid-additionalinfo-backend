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

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.dpppt.additionalinfo.backend.ws.data.HistoryDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = {"org.dpppt.additionalinfo.backend.ws.config"})
@ActiveProfiles("postgres-test")
public class HistoryDataServiceTest {
	
	@Autowired HistoryDataService historyDataService;
	
	@Test
	public void testUpsertFindAndDelete() {
		LocalDate now = LocalDate.now();
		for (int i = 0; i < 100; i++) {
			historyDataService.upsertLatestSevenDayAvgForDay(10, now.minusDays(i));
		}	
		for (int i = 0; i < 100; i++) {
			assertEquals(10, historyDataService.findLatestSevenDayAvgForDay(now.minusDays(i)));
		}
		historyDataService.removeBefore(now.minusDays(10));
		
		assertNull(historyDataService.findLatestSevenDayAvgForDay(now.minusDays(11)));
		assertEquals(10, historyDataService.findLatestSevenDayAvgForDay(now.minusDays(10)));
	}
	
}
