package com.ef;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LogLineRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LogLineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> findIpByRequestFilter(LocalDateTime start, LocalDateTime end, int threshold) throws SQLException {
        String query = new StringBuilder("select ip from t_logs where dt between '")
                .append(start.format(DateTimeFormatter.ofPattern(LogLine.DATE_PATTERN)))
                .append("' and '")
                .append(end.format(DateTimeFormatter.ofPattern(LogLine.DATE_PATTERN)))
                .append("' group by ip having count(*) > ")
                .append(threshold).append(";")
                .toString();

        return queryForString(query);
    }

    public List<String> findRequestByIp(String ip) throws SQLException {
        String query = new StringBuilder().append("select request from t_logs where ip = '")
                .append(ip).append("';")
                .toString();

        return queryForString(query);
    }


    private List<String> queryForString(String query) throws SQLException {
        List<String> results = jdbcTemplate.query(query, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int row) throws SQLException {
                return rs.getString(1);

            }
        });

        return results;
    }

    public void insert(final List<LogLine> lines) {

        Lists.partition(lines, 1000).parallelStream().forEach(lineSet -> {
            StringBuilder partialSql = new StringBuilder("insert into t_logs (dt, ip, request, status, user_agent) values ");
            String sqlGroup = lineSet.stream().map(line -> new StringBuilder().append("('").append(line.getDate()).append("','").append(line.getIp()).append("','").append(line.getRequest()).append("','").append(line.getStatus()).append("','").append(line.getUserAgent()).append("')")
                    .toString()).collect(Collectors.joining(","));

            String sql = partialSql.append(sqlGroup).append(";").toString();

            jdbcTemplate.batchUpdate(sql);
        });
    }
}
