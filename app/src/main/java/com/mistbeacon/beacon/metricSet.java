package com.mistbeacon.beacon;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class metricSet {
    public int value;
    public long createdAt;
    public int stressed;
    //protected Map<String, Object> pack = new HashMap<String, Object>();

    //constructors
    public metricSet(){

    }

    public metricSet(Integer val, long createdAt, int stressed){
        this.value = val;
        this.createdAt = createdAt;
        this.stressed = stressed;
    }

    //getters
    protected int getValue(){
        return this.value;
    }

    protected long getCreatedAt(){
        return this.createdAt;
    }

    //setters - Value
    protected void addValue(int data){
        this.value = data;
    }

//    protected void populate(){
//        this.value = val;
//        this.createdAt = createdAt;
//        this.stressed = stressed;
//    }

//    protected Map<String, Object> packToSend(){
//        this.pack.put("values", this.values);
//        this.pack.put("createdAt", this.createdAt);
//        return this.pack;
//    }
}
