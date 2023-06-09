package com.bingoeventautomated.events;

import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.game.ItemManager;

import javax.inject.Inject;
import java.util.ArrayList;

public class PlayerEvent {
    public static ArrayList<String> playerDroppeditems = new ArrayList<String>();
    @Inject
    ItemManager itemManager;

    public void UpdateDroppedItemList(MenuOptionClicked event) {
        String menuOption = event.getMenuOption();
        int itemId = event.getItemId();
        if (menuOption.equals("Drop")) {
            String item = this.itemManager.getItemComposition(itemId).getName();
            System.out.println(item);
            this.playerDroppeditems.add(item);
            return;
        }

        String itemTaken =event.getMenuTarget();
        if (menuOption.equals("Take")) {
            for(int i = 0; i< playerDroppeditems.size(); i++){
                String item = playerDroppeditems.get(i);
                if (itemTaken.contains(item)) {
                    this.playerDroppeditems.remove(item);
                }
            }
            return;
        }
    }
}
