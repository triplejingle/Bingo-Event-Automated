package com.bingoeventautomated.lootsender.models;

import java.util.ArrayList;
import java.util.List;

public class ActionDataModel {
    public String username;
    public String eventcode;
    public String itemsource;
    public List<ActionDataItem> items = new ArrayList<>();
}
