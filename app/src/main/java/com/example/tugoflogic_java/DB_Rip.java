package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_Rip {

    public Integer boutNumber;
    public String playerName;
    public String rip;
    public Boolean isGround;

    public DB_Rip(Integer boutNumber, String playerName, String rip, Boolean isGround) {
        this.boutNumber = boutNumber;
        this.playerName = playerName;
        this.rip = rip;
        this.isGround = isGround;
    }

    public DB_Rip(Integer boutNumber, String playerName, String rip) {
        this.boutNumber = boutNumber;
        this.playerName = playerName;
        this.rip = rip;
    }

}
