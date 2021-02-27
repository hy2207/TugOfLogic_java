package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_GameSetting {
    public int votingTime = 0;
    public boolean endGame = false;
    public boolean goNextBout = false;

    public DB_GameSetting(){

    }

    public DB_GameSetting(int votingTime, boolean endGame) {
        this.votingTime = votingTime;
        this.endGame = endGame;

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
