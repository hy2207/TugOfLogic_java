package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_GameSetting {
    public int votingTime = 0;

    public DB_GameSetting(){

    }

    public DB_GameSetting(int votingTime) {
        this.votingTime = votingTime;
    }
}
