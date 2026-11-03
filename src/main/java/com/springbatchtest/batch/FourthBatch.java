package com.springbatchtest.batch;

import com.springbatchtest.entity.AfterEntity;
import com.springbatchtest.repository.AfterRepository;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemStreamReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FourthBatch {
    private static final String EXCEL_PATH = "C:\\Users\\young\\OneDrive\\바탕 화면\\random_names.xlsx";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AfterRepository afterRepository;

    @Bean
    public Job fourthJob() {
        System.out.println("fourth job");
        return new JobBuilder("fourthJob", jobRepository)
                .start(fourthStep())
                .build();
    }

    @Bean
    public Step fourthStep() {
        System.out.println("fourth step");
        return new StepBuilder("fourthStep", jobRepository)
                .<Row, AfterEntity>chunk(10)
                .transactionManager(transactionManager)
                .reader(excelReader())
                .processor(fourthProcessor())
                .writer(fourthAfterWriter())
                .skipPolicy(fourthSkipPolicy())
                .retryPolicy(fourthRetryPolicy())
                // .retryLimit(3)
                // .retry(FileNotFoundException.class)
                // .retry(IOException.class)
                // .noRetry(FileNotFoundException.class) // noRetry는 Spring Batch 6부터 사용 X
                .build();
    }

    @Bean
    public SkipPolicy fourthSkipPolicy() {
        return (t, skipCount) ->  {
            if (t instanceof FileNotFoundException) return false;
            if (t instanceof IOException) return skipCount < 10;
            return true;
        };
    }

    @Bean
    public RetryPolicy fourthRetryPolicy() {
        return RetryPolicy.builder()
                .maxRetries(3)
                .includes(IOException.class, SQLException.class)
                .excludes(FileNotFoundException.class)
                .build();
    }

    public ItemStreamReader<Row> excelReader() {
        try {
            return new ExcelRowReader(EXCEL_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ItemProcessor<Row, AfterEntity> fourthProcessor() {
        return item -> {
            AfterEntity afterEntity = new AfterEntity();
            afterEntity.setUsername(item.getCell(0).getStringCellValue());
            return afterEntity;
        };
    }

    @Bean
    public RepositoryItemWriter<AfterEntity> fourthAfterWriter() {
        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
