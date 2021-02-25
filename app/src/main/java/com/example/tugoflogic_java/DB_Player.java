package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_Player {

    public String email;
    public String name;
    public String strawResult;
    public String finalResult;
    public Integer numPlayer;
    public String votingRip;
    public String ground;
    public String comment;

    public DB_Player(){

    }

    public DB_Player(String email, String name, String strawResult, String finalResult, Integer numPlayer, String votingRip, String ground, String comment) {
        this.email = email;
        this.name = name;
        this.strawResult = strawResult;
        this.finalResult = finalResult;
        this.numPlayer = numPlayer;
        this.votingRip = votingRip;
        this.ground = ground;
        this.comment = comment;
    }
}