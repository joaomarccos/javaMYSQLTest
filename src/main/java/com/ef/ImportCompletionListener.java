package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@Component
public class ImportCompletionListener extends JobExecutionListenerSupport {

    public static final String DAILY = "daily";
    public static final String HOURLY = "hourly";

    @Autowired
    private LogLineRepository repository;

    private static final Logger log = LoggerFactory.getLogger(ImportCompletionListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {

            String startDateParam = jobExecution.getJobParameters().getString(Args.START_DATE);
            String durationParam = jobExecution.getJobParameters().getString(Args.DURATION);
            String thresholdParam = jobExecution.getJobParameters().getString(Args.THRESHOLD);

            try {

                LocalDateTime startDate = LocalDateTime.parse(startDateParam, DateTimeFormatter.ofPattern(LogLine.DATE_PATTERN));
                LocalDateTime endDate =
                        HOURLY.equals(durationParam) ? startDate.plusHours(1)
                                : DAILY.equals(durationParam) ? startDate.plusDays(1) : null;

                if (endDate == null) log.error("Duration is inválid");

                int threshold = Integer.valueOf(thresholdParam).intValue();

                log.info("\n\n" + repository.findIpByRequestFilter(startDate, endDate, threshold).stream()
                        .collect(Collectors.joining(", ")) + "\n\n");

            } catch (DateTimeParseException dtpe) {
                log.error("Date format is invalid: [" + startDateParam + " ]");
            } catch (NumberFormatException nfe) {
                log.error("Threshold is inválid: [" + thresholdParam + " ]");
            } catch (SQLException e) {
                log.error(e.getMessage());
            }

        }
    }
}

