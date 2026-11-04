package com.springbatchtest.batch;

import com.springbatchtest.entity.AfterEntity;
import com.springbatchtest.entity.BeforeEntity;
import com.springbatchtest.entity.CustomBeforeRowMapper;
import com.springbatchtest.repository.AfterRepository;
import com.springbatchtest.repository.BeforeRepository;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.data.RepositoryItemReader;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Configuration
public class SixthBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;
    private final DataSource dataSource;

    public SixthBatch(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, BeforeRepository beforeRepository, AfterRepository afterRepository, @Qualifier("dataDBSource") DataSource dataSource) {

        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.beforeRepository = beforeRepository;
        this.afterRepository = afterRepository;
        this.dataSource = dataSource;
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {

        return new JobExecutionListener() {

            private LocalDateTime startTime;

            @Override
            public void beforeJob(JobExecution jobExecution) {
                startTime = LocalDateTime.now();
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                LocalDateTime endTime = LocalDateTime.now();

                long nanos = ChronoUnit.NANOS.between(startTime, endTime);
                double seconds = nanos / 1_000_000_000.0;

                System.out.println("Job 실행 시간: " + seconds + " 초");
            }
        };
    }

    @Bean
    public Job sixthJob() {

        return new JobBuilder("sixthJob", jobRepository)
                .start(sixthStep())
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public Step sixthStep() {

        return new StepBuilder("sixthStep", jobRepository)
                .<BeforeEntity, AfterEntity> chunk(10)
                .transactionManager(platformTransactionManager)
                .reader(beforeSixthReader())
                .processor(middleSixthProcessor())
                .writer(afterSixthWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<BeforeEntity> beforeSixthReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

//    @Bean
//    public JdbcPagingItemReader<BeforeEntity> beforeSixthReader() {
//        try {
//            return new JdbcPagingItemReaderBuilder<BeforeEntity>()
//                    .name("beforeSixthReader")
//                    .dataSource(dataSource)
//                    .selectClause("SELECT id, username")
//                    .fromClause("FROM BeforeEntity")
//                    .sortKeys(Map.of("id", Order.ASCENDING))
//                    .rowMapper(new CustomBeforeRowMapper())
//                    .pageSize(10)
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleSixthProcessor() {

        return new ItemProcessor<BeforeEntity, AfterEntity>() {

            @Override
            public AfterEntity process(BeforeEntity item) throws Exception {

                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUsername(item.getUsername());

                return afterEntity;
            }
        };
    }

    @Bean
    public RepositoryItemWriter<AfterEntity> afterSixthWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }

//    @Bean
//    public JdbcBatchItemWriter<AfterEntity> afterSixthWriter() {
//
//        String sql = "INSERT INTO AfterEntity (username) VALUES (:username)";
//
//        return new JdbcBatchItemWriterBuilder<AfterEntity>()
//                .dataSource(dataSource)
//                .sql(sql)
//                .itemSqlParameterSourceProvider(
//                        new BeanPropertyItemSqlParameterSourceProvider<>()
//                )
//                .build();
//    }
}