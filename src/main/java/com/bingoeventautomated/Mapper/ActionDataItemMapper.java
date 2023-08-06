package com.bingoeventautomated.Mapper;

import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;

import javax.inject.Inject;

public class ActionDataItemMapper {
    @Inject
    private ItemManager itemManager;

    private String getItemName(int itemId) {
        return itemManager.getItemComposition(itemId).getName();
    }

    public ActionDataItem ToActionDataItem(ItemStack item) {
        String itemName = getItemName(item.getId());
        ActionDataItem actionDataItem = new ActionDataItem();
        actionDataItem.name = itemName;
        actionDataItem.quantity = item.getQuantity();
        return actionDataItem;
    }
}
