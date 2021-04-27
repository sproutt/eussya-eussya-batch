package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.entity.Grass;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.*;

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
                .start(this.calculateMemberActivityCountStep())
                .next(this.sortMemberActivityCountStep())
                .next(this.saveRankingStep())
                .build();
    }

    @Bean
    public Step calculateMemberActivityCountStep() throws Exception {
        return this.stepBuilderFactory.get("calculateMemberActivityCountStep")
                .<Grass, Grass>chunk(10)
                .reader(jdbcCursorItemReader())
                .writer(new SaveActivityCountWriter())
                .build();
    }

    @Bean
    public Step sortMemberActivityCountStep() {
        return this.stepBuilderFactory.get("sortMemberActivityCountStep")
                .tasklet(this.tasklet())
                .build();
    }

    @Bean
    public Step saveRankingStep() {
        return this.stepBuilderFactory.get("saveRankingStep")
                .<MemberActivity, MemberActivity>chunk(10)
                .reader(new CustomItemReader<>())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
            List<MemberActivity> memberActivityList = new ArrayList<MemberActivity>();
            Map<Long, Integer> activityCountMap = (Map<Long, Integer>) jobExecutionContext.get("activityCountMap");
            for (Long memberId : activityCountMap.keySet()) {
                memberActivityList.add(new MemberActivity(memberId, activityCountMap.get(memberId)));
            }
            Collections.sort(memberActivityList, new Comparator<MemberActivity>() {
                @Override
                public int compare(MemberActivity o1, MemberActivity o2) {
                    return o2.getActivityCount() - o1.getActivityCount();
                }
            });
            for (int i = 0; i < memberActivityList.size(); i++) {
                MemberActivity memberActivity = memberActivityList.get(i);
                memberActivity.saveRanking(i + 1);
                log.info("memberId : " + memberActivity.getMemberId() + " ranking is : " + memberActivity.getRanking() + " and activity count : " + memberActivity.getActivityCount());
            }
            jobExecutionContext.put("memberActivityList", memberActivityList);
            return RepeatStatus.FINISHED;
        };
    }

    private JdbcCursorItemReader<Grass> jdbcCursorItemReader() throws Exception {
        JdbcCursorItemReader<Grass> itemReader = new JdbcCursorItemReaderBuilder<Grass>()
                .name("jdbcCursorItemReader")
                .dataSource(dataSource)
                .sql("SELECT id, member_id, date, complete_count from grass")
                .rowMapper((rs, rowNum) -> new Grass(
                        rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getInt(4)))
                .build();
        itemReader.afterPropertiesSet();
        return itemReader;
    }

    private ItemWriter<MemberActivity> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<MemberActivity> itemWriter = new JdbcBatchItemWriterBuilder<MemberActivity>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("UPDATE member SET ranking = :ranking  WHERE id = :memberId")
                .build();
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
}
