package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_MainClaim {
    public String mc;
    public int numStrawCon = 0;
    public int numStrawNot = 0;
    public int numFinalCon = 0;
    public int numFinalNot = 0;
    public int numGround = 0;
    public int numFinalNew = 0;


    public DB_MainClaim(String mc, int numStrawCon, int numStrawNot, int numFinalCon, int numFinalNot, int numGround, int numFinalNew) {
        this.mc = mc;
        this.numStrawCon = numStrawCon;
        this.numStrawNot = numStrawNot;
        this.numFinalCon = numFinalCon;
        this.numFinalNot = numFinalNot;
        this.numGround = numGround;
        this.numFinalNew = numFinalNew;
    }

    public DB_MainClaim(){

    }

}
