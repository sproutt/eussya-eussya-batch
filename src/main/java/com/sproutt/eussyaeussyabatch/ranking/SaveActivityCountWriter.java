package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.entity.Grass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SaveActivityCountWriter implements ItemWriter<Grass> {
    private ExecutionContext jobExecutionContext;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
        jobExecutionContext.put("activityCountMap", new HashMap<Long, Integer>());
    }

    @Override
    public void write(List<? extends Grass> items) throws Exception {
        Map<Long, Integer> activityCountMap = (Map<Long, Integer>) jobExecutionContext.get("activityCountMap");
        for (Grass grass : items) {
            activityCountMap.put(grass.getMemberId(), activityCountMap.getOrDefault(grass.getMemberId(), 0) + grass.getCompleteCount());
        }
    }
}
