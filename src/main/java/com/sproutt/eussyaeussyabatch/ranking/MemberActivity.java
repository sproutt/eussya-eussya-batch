package com.sproutt.eussyaeussyabatch.ranking;

import lombok.Getter;

@Getter
public class MemberActivity {
    private Long memberId;
    private int activityCount;
    private int ranking;

    public MemberActivity(Long memberId, int activityCount) {
        this.memberId = memberId;
        this.activityCount = activityCount;
    }

    public void saveRanking(int ranking) {
        this.ranking = ranking;
    }
}
