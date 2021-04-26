package com.sproutt.eussyaeussyabatch.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.format.DateTimeFormatter;

@Entity
@NoArgsConstructor
@Getter
public class Grass {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int memberId;
    private String date;
    private int completeCount;

    public Grass(int id, int memberId, String date, int completeCount) {
        this.id = id;
        this.memberId = memberId;
        this.date = date;
        this.completeCount = completeCount;
    }

    @Override
    public String toString() {
        return "Grass{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", date='" + date + '\'' +
                ", completeCount=" + completeCount +
                '}';
    }
}
