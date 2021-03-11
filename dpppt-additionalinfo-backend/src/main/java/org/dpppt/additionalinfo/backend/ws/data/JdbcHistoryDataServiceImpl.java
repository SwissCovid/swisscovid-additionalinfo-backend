package org.dpppt.additionalinfo.backend.ws.data;

import java.time.LocalDate;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public class JdbcHistoryDataServiceImpl implements HistoryDataService {
    private final NamedParameterJdbcTemplate jt;
    private final SimpleJdbcInsert sevenDayAvgHistoryInsert;

    public JdbcHistoryDataServiceImpl(DataSource dataSource) {
        this.jt = new NamedParameterJdbcTemplate(dataSource);
        this.sevenDayAvgHistoryInsert =
                new SimpleJdbcInsert(dataSource)
                        .withTableName("t_seven_day_avg_history")
                        .usingGeneratedKeyColumns("pk_seven_day_avg_history_id");
    }

    @Override
    public void upsertLatestSevenDayAvgForDay(Integer latestSevenDayAvg, LocalDate day) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("latest_seven_day_avg", latestSevenDayAvg);
        params.addValue("day", day);
        if (findLatestSevenDayAvgForDay(day) != null) {
            removeLatestSevenDayAvgForDay(day, false);
        }
        sevenDayAvgHistoryInsert.execute(params);
    }

    private void removeLatestSevenDayAvgForDay(LocalDate day, boolean deleteBefore) {
        jt.update(
                "delete from t_seven_day_avg_history where day "
                        + (deleteBefore ? "<" : "=")
                        + " :day",
                new MapSqlParameterSource("day", day));
    }

    @Override
    public Integer findLatestSevenDayAvgForDay(LocalDate day) {
        try {
            return jt.queryForObject(
                    "select latest_seven_day_avg from t_seven_day_avg_history where day = :day",
                    new MapSqlParameterSource("day", day),
                    Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
