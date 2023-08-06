package com.bingoeventautomated.validator;

import com.bingoeventautomated.Mapper.ActionDataItem;
import com.bingoeventautomated.Mapper.ActionDataModel;
import com.bingoeventautomated.events.PlayerEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionDataItemValidator {
    public ActionDataModel validate(ActionDataModel actionData) {
        ArrayList<String> invalidItemList = PlayerEvent.invalidItems;
        actionData.items = ExtractValidItems(actionData, invalidItemList);
        return actionData;
    }

    private ArrayList ExtractValidItems(ActionDataModel actionData, ArrayList<String> invalidItemList) {
        ArrayList validItemList = new ArrayList();
        Iterator iterator = actionData.items.iterator();
        while (iterator.hasNext()) {
            ActionDataItem actionDataItem = (ActionDataItem) iterator.next();
            String itemName = actionDataItem.getName();
            if (!invalidItemList.contains(itemName)) {
                validItemList.add(actionDataItem);
            }
        }
        return validItemList;
    }
}
