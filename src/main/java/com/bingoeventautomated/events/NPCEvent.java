package com.bingoeventautomated.events;




import com.bingoeventautomated.service.ActionDataModel;

import java.util.ArrayList;
import java.util.Iterator;

public class NPCEvent {
    public ActionDataModel validateActionData(ActionDataModel actionData) {
        ArrayList<String> invalidItems = PlayerEvent.playerDroppeditems;
        ArrayList npcLoot = new ArrayList();
        Iterator iterator = actionData.items.iterator();
        while(iterator.hasNext()) {
            String itemName = (String) iterator.next();
            if (!invalidItems.contains(itemName)) {
                npcLoot.add(itemName);
            }
        }

        actionData.items=npcLoot;
        return actionData;
    }
}
