package com.sproutt.eussyaeussyabatch.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@Getter
public class MemberRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
