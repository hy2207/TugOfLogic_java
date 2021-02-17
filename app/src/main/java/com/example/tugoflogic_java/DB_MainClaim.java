package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_MainClaim {
    public String mc;
    public int numConvinced;
    public int numNotYet;

    public DB_MainClaim(){

    }

    public DB_MainClaim(String mc, int numConvinced, int numNotYet){
        this.mc = mc;
        this.numConvinced = numConvinced;
        this.numNotYet = numNotYet;
    }
}
