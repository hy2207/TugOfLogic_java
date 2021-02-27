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

    public DB_Bout(){

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
