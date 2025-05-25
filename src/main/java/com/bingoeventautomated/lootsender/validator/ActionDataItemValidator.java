package com.bingoeventautomated.lootsender.validator;

import com.bingoeventautomated.lootsender.models.ActionDataItem;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.playerevent.PlayerEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionDataItemValidator {
    public ActionDataModel validate(ActionDataModel actionData) {
        ArrayList<String> invalidItemList = PlayerEvent.invalidItems;
        actionData.items = extractValidItems(actionData, invalidItemList);
        return actionData;
    }

    private ArrayList extractValidItems(ActionDataModel actionData, ArrayList<String> invalidItemList) {
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
