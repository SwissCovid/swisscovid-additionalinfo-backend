/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.additionalinfo.backend.ws.data;

import java.time.LocalDate;

public interface HistoryDataService {

    /**
     * upserts the latest seven day average for the given day
     *
     * @param latestSevenDayAvg
     * @param day
     */
    void upsertLatestSevenDayAvgForDay(Integer latestSevenDayAvg, LocalDate day);

    /**
     * returns the latest seven day average for the given day
     * @param day
     * @return
     */
    Integer findLatestSevenDayAvgForDay(LocalDate day);
}
