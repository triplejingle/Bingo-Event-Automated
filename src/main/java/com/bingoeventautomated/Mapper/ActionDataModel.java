package com.bingoeventautomated.Mapper;

import java.util.ArrayList;
import java.util.List;

public class ActionDataModel {
    public String username;
    public String eventcode;
    public String itemsource;
    public List<ActionDataItem> items = new ArrayList<>();

    public boolean IsSet() {
        if (username == null) {
            return false;
        }
        if (eventcode == null) {
            return false;
        }
        if (itemsource == null) {
            return false;
        }
        return items.size() != 0;
    }
}
