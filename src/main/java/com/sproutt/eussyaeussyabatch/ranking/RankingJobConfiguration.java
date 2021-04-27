package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.entity.Grass;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.*;
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
                .start(this.calculateMemberActivityCountStep())
                .next(this.sortMemberActivityCountStep())
                //.next(this.saveRankingStep())
                .build();
    }

    @Bean
    public Step calculateMemberActivityCountStep() throws Exception {
        return this.stepBuilderFactory.get("calculateMemberActivityCountStep")
                .<Grass, Grass>chunk(10)
                .reader(jdbcCursorItemReader())
                .writer(new saveActivityCountWriter())
                .build();
    }

    @Bean
    public Step sortMemberActivityCountStep() {
        return this.stepBuilderFactory.get("sortMemberActivityCountStep")
                .tasklet(this.tasklet())
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
            jobExecutionContext.put("listMemberActivityCount", memberActivityList);
            for (int i = 0; i < memberActivityList.size(); i++) {
                MemberActivity memberActivity = memberActivityList.get(i);
                log.info("memberId : " + memberActivity.getMemberId() + " ranking is : " + (i + 1) + " and activity count : " + memberActivity.getActivityCount());
            }
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
}
