package com.sproutt.eussyaeussyabatch.ranking;

import lombok.Getter;

@Getter
public class MemberActivity {
    private Long memberId;
    private int activityCount;

    public MemberActivity(Long memberId, int activityCount) {
        this.memberId = memberId;
        this.activityCount = activityCount;
    }
}
