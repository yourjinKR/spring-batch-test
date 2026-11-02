package com.springbatchtest.batch;

import com.springbatchtest.entity.AfterEntity;
import com.springbatchtest.entity.BeforeEntity;
import com.springbatchtest.repository.AfterRepository;
import com.springbatchtest.repository.BeforeRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    // Job 정의
    @Bean
    public Job firstJob() {
        System.out.println("first job");
        return new JobBuilder("firstJob", jobRepository)
                .start(firstStep())
                .build();
    }

    // Step 등록
    @Bean
    public Step firstStep() {
        System.out.println("first step");
        return new StepBuilder("firstStep", jobRepository)
                .<BeforeEntity, AfterEntity>chunk(10)
                .transactionManager(transactionManager)
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    // BeforeEntity 테이블에서 읽어오는 Reader
    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    // 읽어온 데이터를 처리하는 Process
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

        return item -> {
            AfterEntity afterEntity = new AfterEntity();
            afterEntity.setUsername(item.getUsername());
            return afterEntity;
        };
    }

    // AfterEntity에 처리한 결과를 저장하는 Writer
    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
