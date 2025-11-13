package com.db_writes_service.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ParkingLotJobScheduler {

    private static final Logger log = LogManager.getLogger(ParkingLotJobScheduler.class);

    private final Job job; // need to specify which job but since here only one job defined no need to specify
    private final JobLauncher jobLauncher;

    public ParkingLotJobScheduler(Job job, JobLauncher jobLauncher) {
        this.job = job;
        this.jobLauncher = jobLauncher;
    }


    @Scheduled(cron = "0/10 * * * * *")
    @SneakyThrows
    void triggerJob() {
        log.info("Triggering job execution");
        var jobParameters = new JobParametersBuilder()
                .addDate("processDate", new Date(), true)
                .toJobParameters() ;

        try {
            log.debug("Launching job with parameters {}", jobParameters);
            jobLauncher.run(job, jobParameters);
        } catch (Exception e) {
            log.error("Job execution failed", e);
//            throw new RuntimeException(e);
        }

    }


}