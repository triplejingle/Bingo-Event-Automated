package com.bingoeventautomated.events;

import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.ArrayList;

public class PlayerEvent {
    public static ArrayList<String> invalidItems = new ArrayList<>();
    @Inject
    private ItemManager itemManager;

    public void UpdateInvalidItemList(MenuOptionClicked event) {
        String menuOption = event.getMenuOption();
        AddDroppedItemToInvalidItems(event, menuOption);
        RemoveTakenItemFromInvalidItems(event, menuOption);
    }

    private void AddDroppedItemToInvalidItems(MenuOptionClicked event, String menuOption) {
        if (!menuOption.equals("Drop"))
            return;

        int itemId = event.getItemId();
        String item = this.itemManager.getItemComposition(itemId).getName();
        invalidItems.add(item);
    }

    private void RemoveTakenItemFromInvalidItems(MenuOptionClicked event, String menuOption) {
        if (!menuOption.equals("Take"))
            return;

        String itemTaken = event.getMenuTarget();
        for (int i = 0; i < invalidItems.size(); i++) {
            String item = invalidItems.get(i);
            if (itemTaken.contains(item)) {
                invalidItems.remove(item);
            }
        }
    }
}
