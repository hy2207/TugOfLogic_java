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
//    public long numPlayer;
    public boolean isReferee;
    public String votingRip;
    public String ground;
    public String comment;
    public String playerRip;

    public DB_Player(){

    }

    public DB_Player(String email, String name, String strawResult, String finalResult, boolean isReferee, String votingRip, String ground, String comment, String playerRip) {
        this.email = email;
        this.name = name;
        this.strawResult = strawResult;
        this.finalResult = finalResult;
        this.isReferee = isReferee;
        this.votingRip = votingRip;
        this.ground = ground;
        this.comment = comment;
        this.playerRip = playerRip;
    }

    public DB_Player(String email, String name, String strawResult, String finalResult, boolean isReferee, String votingRip, String ground, String comment) {
        this.email = email;
        this.name = name;
        this.strawResult = strawResult;
        this.finalResult = finalResult;
        this.isReferee = isReferee;
        this.votingRip = votingRip;
        this.ground = ground;
        this.comment = comment;
    }

    public String getPlayerRip() {
        return playerRip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlayerRip(String playerRip) {
        this.playerRip = playerRip;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("name", name);
        result.put("strawResult",strawResult);
        result.put("finalResult",finalResult);
        result.put("email",email);
        result.put("isReferee",isReferee);
        result.put("votingRip",votingRip);
        result.put("ground",ground);
        result.put("comment",comment);
        result.put("playerRip", playerRip);
        return result;
    };
}