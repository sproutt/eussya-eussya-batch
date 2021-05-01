package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.entity.MemberRanking;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

public class CalculateRankingItemProcessor implements ItemProcessor<MemberRanking, MemberRanking> {

    private ExecutionContext executionContext;

    @BeforeStep
    void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
        executionContext.putLong("index", 1);
        executionContext.putLong("ranking", 1);
        executionContext.putInt("beforeActivityCount", 2147483647);
    }

    @Override
    public MemberRanking process(MemberRanking item) throws Exception {
        int activityCount = item.getActivityCount();
        int beforeActivityCount = executionContext.getInt("beforeActivityCount");
        Long ranking = executionContext.getLong("ranking");
        Long index = executionContext.getLong("index");
        if (activityCount == beforeActivityCount) {
            item.inputRanking(ranking);
        } else {
            item.inputRanking(index);
            executionContext.putInt("beforeActivityCount", activityCount);
            executionContext.putLong("ranking", index);
        }
        executionContext.putLong("index", ++index);
        return item;
    }

}
