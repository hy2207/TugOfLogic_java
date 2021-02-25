package com.example.tugoflogic_java;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DB_Player {
    public String name;
    public Boolean strawResult;
    public Boolean finalResult;
    public String email;

    public DB_Player(){

    }

    public DB_Player(String newEmail){
        this.email = newEmail;
    }

    public DB_Player(String name, String email){
        this.name = name;
        this.email = email;
    }

    public DB_Player(String name, Boolean strawResult, Boolean finalResult, String email){
        this.name = name;
        this.strawResult = strawResult;
        this.finalResult = finalResult;
        this.email = email;
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