package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_GameSetting {
    public int votingTime = 0;
    public boolean endGame = false;
    public boolean goNextBout = false;
    public boolean showList = false;
    public boolean showComment = false;
    public boolean startGame = false;

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout, boolean showList, boolean showComment, boolean startGame) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
        this.showList = showList;
        this.showComment = showComment;
        this.startGame = startGame;
    }

    public DB_GameSetting(){

    }

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout, boolean showList, boolean showComment) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
        this.showList = showList;
        this.showComment = showComment;
    }

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout, boolean showList) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
        this.showList = showList;
    }

    public DB_GameSetting(int votingTime, boolean endGame, boolean goNextBout) {
        this.votingTime = votingTime;
        this.endGame = endGame;
        this.goNextBout = goNextBout;
    }

    public DB_GameSetting(int votingTime) {
        this.votingTime = votingTime;
    }
}
