package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.ranking.dto.MemberRanking;
import org.springframework.batch.item.ItemProcessor;

public class RankingItemProcessor implements ItemProcessor<MemberRanking, MemberRanking> {
    private static Long INDEX = 1L;
    private static Long RANKING = 1L;
    private static int BEFORE_ACTIVITY_COUNT = Integer.MAX_VALUE;

    @Override
    public MemberRanking process(MemberRanking item) {
        int activityCount = item.getActivityCount();
        if (activityCount == BEFORE_ACTIVITY_COUNT) {
            item.inputRanking(RANKING);
        } else {
            item.inputRanking(INDEX);
            BEFORE_ACTIVITY_COUNT = activityCount;
            RANKING = INDEX;
        }
        INDEX++;
        return item;
    }
}