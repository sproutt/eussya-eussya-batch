package com.sproutt.eussyaeussyabatch.ranking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class RankingJobExecutionListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long time = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        log.info("회원 랭킹 계산 실행 완료");
        log.info("=============");
        log.info("처리 시간 {}ms", time);
    }
}
