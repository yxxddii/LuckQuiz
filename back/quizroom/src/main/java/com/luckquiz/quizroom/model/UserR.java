package com.luckquiz.quizroom.model;

import lombok.Getter;

@Getter
public class UserR implements Comparable<UserR>{
    private String sender;
    private int img;
    private Integer rank;
    private int score;

    public void setSender(String sender){
        this.sender=sender;
    }
    public void setImg(int img){
        this.img=img;
    }
    public void setRank(Integer rank){
        this.rank = rank;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(UserR o) {
        return o.getRank() - getRank();
    }
}
