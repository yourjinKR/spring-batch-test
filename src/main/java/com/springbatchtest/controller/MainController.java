package com.springbatchtest.controller;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final JobOperator jobOperator;
    private final JobRegistry jobRegistry;

    @GetMapping("/first")
    public String firstApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobOperator.start(jobRegistry.getJob("firstJob"), jobParameters);

        return "ok";
    }

    @GetMapping("/second")
    public String secondApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobOperator.start(jobRegistry.getJob("secondJob"), jobParameters);

        return "ok";
    }

    @GetMapping("/fourth")
    public String forthApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobOperator.start(jobRegistry.getJob("fourthJob"), jobParameters);

        return "ok";
    }

    @GetMapping("/fifth")
    public String fifthApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobOperator.start(jobRegistry.getJob("fifthJob"), jobParameters);
        return "ok";
    }

    @GetMapping("/sixth")
    public String sixthApi(@RequestParam("value") String value) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", value)
                .toJobParameters();

        jobOperator.start(jobRegistry.getJob("sixthJob"), jobParameters);
        return "ok";
    }
}
