package com.example.tugoflogic_java;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

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

    public DB_Player(String newEmail){
        this.email = newEmail;
    }

    public DB_Player(String name, String email){
        this.name = name;
        this.email = email;
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

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("name", name);
        result.put("strawResult",strawResult);
        result.put("finalResult",finalResult);
        result.put("email",email);

        return result;
    };
}