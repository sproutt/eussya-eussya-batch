package com.sproutt.eussyaeussyabatch.ranking;

import lombok.NoArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.*;

import java.util.List;

@NoArgsConstructor
public class CustomItemReader<T> implements ItemReader<T> {

    private ExecutionContext jobExecutionContext;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        List<MemberActivity> items = (List<MemberActivity>) jobExecutionContext.get("memberActivityList");
        if (!items.isEmpty()) {
            return (T) items.remove(0);
        }
        return null;
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.jobExecutionContext = stepExecution.getJobExecution().getExecutionContext();
    }
}
