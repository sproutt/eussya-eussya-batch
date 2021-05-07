package com.sproutt.eussyaeussyabatch.ranking.dto;

import lombok.Getter;

@Getter
public class MemberRanking {
    private Long id;
    private int activityCount;
    private Long ranking;

    public MemberRanking(Long id, int activityCount) {
        this.id = id;
        this.activityCount = activityCount;
    }

    public void inputRanking(Long ranking) {
        this.ranking = ranking;
    }

    @Override
    public String toString() {
        return "MemberRanking{" +
                "id=" + id +
                ", activityCount=" + activityCount +
                ", ranking=" + ranking +
                '}';
    }
}
