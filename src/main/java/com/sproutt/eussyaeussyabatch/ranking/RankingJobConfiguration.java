package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.entity.Grass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RankingJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;

    @Bean
    public Job saveRankingJob() throws Exception {
        return this.jobBuilderFactory.get("saveRankingJob")
                .incrementer(new RunIdIncrementer())
                .start(this.saveRankingStep())
                .build();
    }

    @Bean
    public Step saveRankingStep() throws Exception {
        return this.stepBuilderFactory.get("saveRangkinStep")
                .<Grass, Grass>chunk(10)
                .reader(jdbcCursorItemReader())
                .writer(itemWriter())
                .build();
    }

    private JdbcCursorItemReader<Grass> jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader<Grass> itemReader = new JdbcCursorItemReaderBuilder<Grass>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT id, member_id, date, complete_count from grass")
                .rowMapper((rs, rowNum) -> new Grass(
                        rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4)))
                .build();
        itemReader.afterPropertiesSet();
        return itemReader;
    }

    private ItemWriter<Grass> itemWriter() {
        return items -> log.info(items.stream()
                .map(Grass::toString)
                .collect(Collectors.joining("\n")));
    }
}
