package com.bingoeventautomated.service;

import java.util.ArrayList;
import java.util.List;

public class ActionDataModel {
    public String username;
    public String eventcode;
    public String itemsource;
    public List<String> items = new ArrayList<>();

    public boolean IsSet() {
        if(username== null){
            return false;
        }
        if(eventcode==null){
            return false;
        }
        if(itemsource==null){
            return false;
        }
        if(items.size()==0){
            return false;
        }
        return true;
    }
}
