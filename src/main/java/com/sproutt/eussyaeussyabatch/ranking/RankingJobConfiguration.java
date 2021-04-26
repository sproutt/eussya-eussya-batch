package com.sproutt.eussyaeussyabatch.ranking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RankingJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job saveRankingJob() {
        return this.jobBuilderFactory.get("saveRankingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.saveRankingStep())
                .build();
    }

    @Bean
    public Step saveRankingStep() {
        return this.stepBuilderFactory.get("saveRangkinStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>Step");
                    return RepeatStatus.FINISHED;
                }).build();
    }
}
