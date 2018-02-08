package com.ef;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

@SpringBootApplication
public class Parser {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Parser.class, args);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        Map<String, String> params = Args.buildParams(args);

        if (Args.isDefined(params, Args.ACCESS_LOG, Args.DURATION, Args.START_DATE, Args.THRESHOLD)) {

            JobParameters jobParameters = jobParametersBuilder.addString(Args.ACCESS_LOG, "file://"+params.get(Args.ACCESS_LOG))
                    .addString(Args.START_DATE, params.get(Args.START_DATE))
                    .addString(Args.DURATION, params.get(Args.DURATION))
                    .addString(Args.THRESHOLD, params.get(Args.THRESHOLD))
                    .addLong("time", System.currentTimeMillis()).toJobParameters(); //to run again

            JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
            Job job = ctx.getBean("db-import-job", Job.class);

            try {
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            } catch (JobExecutionAlreadyRunningException | JobParametersInvalidException | JobInstanceAlreadyCompleteException | JobRestartException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Required params not given");
        }


    }
}
