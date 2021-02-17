package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_Bout {

    public Integer boutNumber;
    public Integer numContensted;
    public Integer numEstablished;
    public String player;
    public String rip;

    public DB_Bout(){

    }

    public DB_Bout(Integer boutNumber, String player, String rip, Integer numContensted, Integer numEstablished){
        this.boutNumber = boutNumber;
        this.player = player;
        this.rip = rip;
        this.numContensted = numContensted;
        this.numEstablished = numEstablished;
    }

}
