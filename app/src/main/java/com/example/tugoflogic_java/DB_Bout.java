package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_Bout {

    public Integer boutNumber;
    public Integer numContested;
    public Integer numEstablished;
    public String player;
    public String rip;
    public Integer numGround;
    public String comments;

    public DB_Bout(){

    }


    public DB_Bout(Integer boutNumber, Integer numContested, Integer numEstablished, String player, String rip, Integer numGround, String comments) {
        this.boutNumber = boutNumber;
        this.numContested = numContested;
        this.numEstablished = numEstablished;
        this.player = player;
        this.rip = rip;
        this.numGround = numGround;
        this.comments = comments;
    }

    public DB_Bout(Integer boutNumber, String player, String rip, Integer numContested, Integer numEstablished, Integer numGround){
        this.boutNumber = boutNumber;
        this.player = player;
        this.rip = rip;
        this.numContested = numContested;
        this.numEstablished = numEstablished;
        this.numGround = numGround;
    }

}
