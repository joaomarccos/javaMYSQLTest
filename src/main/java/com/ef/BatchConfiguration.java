package com.ef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    public static final String DAILY = "daily";
    public static final String HOURLY = "hourly";

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);
    public static final Resource INJECTED_RESOURCE = null;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LogLineRepository repository;

    @Bean
    @StepScope
    public FlatFileItemReader<LogLine> reader(@Value("#{jobParameters['" + Args.ACCESS_LOG + "']}") Resource in) {
        return new FlatFileItemReaderBuilder<LogLine>()
                .name("log-reader")
                .resource(in)
                .targetType(LogLine.class)
                .delimited()
                .delimiter("|")
                .names(new String[]{"date", "ip", "request", "status", "userAgent"})
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<LogLine> writer() {
        return new JdbcBatchItemWriterBuilder<LogLine>()
                .dataSource(dataSource)
                .sql("insert into t_logs (dt, ip, request, status, user_agent) values (:date, :ip, :request, :status, :userAgent)")
                .beanMapped()
                .build();
    }

    @Bean
    public Step importToDB() {
        return stepBuilderFactory
                .get("import-db")
                .<LogLine, LogLine>chunk(1000)
                .reader(reader(INJECTED_RESOURCE))
                .writer(writer())
                .build();
    }

    @Bean("db-import-job")
    public Job jobLogImport(ImportCompletionListener listener) {
        return jobBuilderFactory.get("db-import-job")
                .incrementer(new RunIdIncrementer())
                .start(importToDB())
                .listener(listener)
                .build();

    }

}
