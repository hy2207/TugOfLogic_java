package com.example.tugoflogic_java;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DB_Player {
    public String name;
    public Boolean strawResult;
    public Boolean finalResult;

    public DB_Player(){

    }

    public DB_Player(String name, Boolean strawResult, Boolean finalResult){
        this.name = name;
        this.strawResult = strawResult;
        this.finalResult = finalResult;
    }
}