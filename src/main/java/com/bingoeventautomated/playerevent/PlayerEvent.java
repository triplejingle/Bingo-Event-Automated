package com.bingoeventautomated.playerevent;

import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.ArrayList;

public class PlayerEvent {
    public static ArrayList<String> invalidItems = new ArrayList<>();
    @Inject
    private ItemManager itemManager;

    public void process(MenuOptionClicked event) {
        String menuOption = event.getMenuOption();
        addDroppedItemToInvalidItems(event, menuOption);
        removeTakenItemFromInvalidItems(event, menuOption);
    }

    private void addDroppedItemToInvalidItems(MenuOptionClicked event, String menuOption) {
        if (!menuOption.equals("Drop"))
            return;

        int itemId = event.getItemId();
        String item = this.itemManager.getItemComposition(itemId).getName();
        invalidItems.add(item);
    }

    private void removeTakenItemFromInvalidItems(MenuOptionClicked event, String menuOption) {
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
