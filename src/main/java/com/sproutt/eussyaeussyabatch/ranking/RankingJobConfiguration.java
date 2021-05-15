package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.ranking.dto.MemberRanking;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

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
                .listener(new RankingJobExecutionListener())
                .build();
    }

    @Bean
    public Step saveRankingStep() throws Exception {
        return this.stepBuilderFactory.get("saveRankingStep")
                .<MemberRanking, MemberRanking>chunk(10)
                .reader(jdbcCursorItemReader())
                .processor(new RankingItemProcessor())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    private JdbcCursorItemReader<MemberRanking> jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader<MemberRanking> itemReader = new JdbcCursorItemReaderBuilder<MemberRanking>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT m.id, ifnull(a.activity_count,0) AS activity_count FROM member AS m " +
                        "LEFT OUTER JOIN " +
                        "(SELECT member_id, sum(complete_count) AS activity_count " +
                        "FROM grass AS g " +
                        "GROUP BY member_id) " +
                        "AS a ON m.id = a.member_id " +
                        "ORDER BY activity_count DESC;")
                .rowMapper((rs, rowNum) -> new MemberRanking(
                        rs.getLong(1), rs.getInt(2)))
                .build();
        itemReader.afterPropertiesSet();
        return itemReader;
    }

    private ItemWriter<MemberRanking> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<MemberRanking> itemWriter = new JdbcBatchItemWriterBuilder<MemberRanking>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE member SET ranking = :ranking, modify_date = :modifyDate WHERE id = :id")
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
}
