package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_MainClaim {
    public String mc;
    public int numStrawCon;
    public int numStrawNot;
    public int numFinalCon;
    public int numFinalNot;

    public DB_MainClaim(){

    }


    public DB_MainClaim(String mc, int numStrawCon, int numStrawNot, int numFinalCon, int numFinalNot){
        this.mc = mc;
        this.numStrawCon = numStrawCon;
        this.numStrawNot = numStrawNot;
        this.numFinalCon = numFinalCon;
        this.numFinalNot = numFinalNot;
    }
}
