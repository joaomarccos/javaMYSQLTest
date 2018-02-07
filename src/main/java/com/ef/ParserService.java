package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ParserService {

    public static final String DAILY = "daily";
    public static final String HOURLY = "hourly";
    private final LogLineRepository logRepository;

    private static final Logger log = LoggerFactory.getLogger(ParserService.class);

    @Autowired
    public ParserService(LogLineRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void store(Path path) throws IOException {
        long initTime = System.currentTimeMillis();
        System.out.println("Loading data into database");
        List<LogLine> data = linesObjFromFile(path);
        logRepository.insert(data);
        System.out.printf("Loaded %d records in %d seconds!\n\n%n", data.size(), TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - initTime));
    }

    public List<String> find(String dateParam, String durationParam, String thresholdParam) throws IllegalAccessException {
        try {

            LocalDateTime startDate = LocalDateTime.parse(dateParam, DateTimeFormatter.ofPattern(LogLine.DATE_PATTERN));
            LocalDateTime endDate = HOURLY.equals(durationParam) ? startDate.plusHours(1) : DAILY.equals(durationParam) ? startDate.plusDays(1) : null;
            if (endDate == null) throw new IllegalArgumentException("Duration is inválid");
            int threshold = Integer.valueOf(thresholdParam).intValue();

            return logRepository.findIpByRequestFilter(startDate, endDate, threshold);

        } catch (DateTimeParseException dtpe) {
            throw new IllegalAccessException("Date format is invalid: [" + dateParam + " ]");
        } catch (NumberFormatException nfe){
            throw new IllegalAccessException("Threshold is inválid: [" + thresholdParam + " ]");
        } catch (SQLException e) {
            log.error(e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }


    private List<LogLine> linesObjFromFile(Path path) throws IOException {
        return Files.readAllLines(path).stream().map(line -> {
            LogLine l = new LogLine();
            List<String> fields = new ArrayList();
            StringTokenizer st = new StringTokenizer(line, "|");

            while (st.hasMoreTokens()) {
                fields.add(st.nextToken());
            }

            l.setDate(fields.get(0));
            l.setIp(fields.get(1));
            l.setRequest(fields.get(2));
            l.setStatus(fields.get(3));
            l.setUserAgent(fields.get(4));

            return l;
        }).collect(Collectors.toList());
    }

}
