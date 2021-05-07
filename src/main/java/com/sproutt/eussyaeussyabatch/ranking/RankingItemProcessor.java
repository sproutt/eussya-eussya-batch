package com.sproutt.eussyaeussyabatch.ranking;

import com.sproutt.eussyaeussyabatch.ranking.dto.MemberRanking;
import org.springframework.batch.item.ItemProcessor;

public class RankingItemProcessor implements ItemProcessor<MemberRanking, MemberRanking> {
    private static Long index = 1L;
    private static Long ranking = 1L;
    private static int beforeActivityCount = Integer.MAX_VALUE;

    @Override
    public MemberRanking process(MemberRanking item) {
        int activityCount = item.getActivityCount();
        if (activityCount == beforeActivityCount) {
            item.inputRanking(ranking);
        } else {
            item.inputRanking(index);
            beforeActivityCount = activityCount;
            ranking = index;
        }
        index++;
        return item;
    }
}