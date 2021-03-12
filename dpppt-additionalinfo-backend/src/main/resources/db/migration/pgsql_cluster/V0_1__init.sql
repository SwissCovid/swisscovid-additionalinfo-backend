/*
 * Created by Ubique Innovation AG
 * https://www.ubique.ch
 * Copyright (c) 2021. All rights reserved.
 */

CREATE TABLE t_seven_day_avg_history(
 pk_seven_day_avg_history_id Serial NOT NULL,
 day DATE DEFAULT CURRENT_DATE NOT NULL,
 latest_seven_day_avg INTEGER NOT NULL
)
WITH (autovacuum_enabled=true);

-- Add keys for table t_exposed

ALTER TABLE t_seven_day_avg_history ADD CONSTRAINT PK_t_seven_day_avg_history PRIMARY KEY (pk_seven_day_avg_history_id);
ALTER TABLE t_seven_day_avg_history ADD CONSTRAINT unique_t_seven_day_avg_history UNIQUE (day);