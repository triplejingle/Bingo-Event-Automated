package com.bingoeventautomated.lootsender.Mappers;

import com.bingoeventautomated.lootsender.models.ActionDataItem;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import net.runelite.api.Client;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InterfaceID;

import net.runelite.client.game.ItemStack;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class InventoryActionDataModelMapper {
    @Inject
    Client client;
    @Inject
    IEventConfig eventconfig;
    @Inject
    ActionDataItemMapper actionDataItemMapper;

    //todo refactor so it conforms to dry
    public String getInventoryName(int widgetId) {
        switch (widgetId) {
            case InterfaceID.BARROWS_REWARD:
                return "Barrows";
            case InterfaceID.RAIDS_REWARDS:
                return "COX";
            case InterfaceID.TOB_HUD:
                return "TOB";
            case InterfaceID.TOA_CHESTS:
                return "TOA";
            case InterfaceID.TRAWLER_REWARD:
                return "Fishing Trawler";
            case InterfaceID.WILDY_LOOT_CHEST:
                return "Wilderniss Loot Chest";
            case InterfaceID.PMOON_REWARD:
                return "Lunar Chest";
            case InventoryID.COLOSSEUM_REWARDS:
                return"Fortis Colosseum";
        }
        return "";
    }
    //todo refactor so it conforms to dry

    public int getWidgetId(int widgetId) {
        switch (widgetId) {
            case InterfaceID.BARROWS_REWARD:
                return InventoryID.TRAIL_REWARDINV;
            case InterfaceID.RAIDS_REWARDS:
                return InventoryID.RAIDS_REWARDS;
            case InterfaceID.TOB_HUD:
                return InventoryID.TOB_CHESTS;
            case InterfaceID.TOA_CHESTS:
                return InventoryID.TOA_CHESTS;
            case InterfaceID.TRAWLER_REWARD:
                return InventoryID.TRAWLER_REWARDINV;
            case InterfaceID.WILDY_LOOT_CHEST:
                return InventoryID.LOOT_INV_ACCESS;
            case InterfaceID.PMOON_REWARD:
                return InventoryID.PMOON_REWARDINV;
            case InterfaceID.COLOSSEUM_REWARD_CHEST_2:
                return InventoryID.COLOSSEUM_REWARDS;
        }
        return -1;
    }

    public ActionDataModel map(int widgetId) {
        String inventoryName = getInventoryName(widgetId);
        ActionDataModel actionData = new ActionDataModel();
        int foundInventory = getWidgetId(widgetId);
        if (foundInventory == -1)
            return actionData;

        actionData = inventoryToActionDataModel(foundInventory, inventoryName);
        actionData.username = client.getLocalPlayer().getName();
        actionData.eventcode = eventconfig.eventCodeInput();
        return actionData;
    }

    private ActionDataModel inventoryToActionDataModel(int inventoryID, String itemsource) {
        ActionDataModel actionData = new ActionDataModel();
        actionData.itemsource = itemsource;
        setBingoTileItems(inventoryID, actionData);
        return actionData;
    }

    private void setBingoTileItems(int inventoryID, ActionDataModel actionData) {
        try {
            Collection<ItemStack> InventoryItemCollection = getItemListFromInventory(inventoryID);
            addInventoryItemsToBingoTileItems(actionData, InventoryItemCollection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<ItemStack> getItemListFromInventory(int inventoryID) {
        ItemContainer container = this.client.getItemContainer(inventoryID);
        Collection<ItemStack> items = new ArrayList<>();
        if (container != null) {
            items = Arrays.stream(container.getItems())
                    .filter((item) -> {
                        return item.getId() > 0;
                    }).map((item) -> {
                        return new ItemStack(item.getId(), item.getQuantity(), this.client.getLocalPlayer().getLocalLocation());
                    }).collect(Collectors.toList());
        }
        return items;
    }

    private void addInventoryItemsToBingoTileItems(ActionDataModel actionData, Collection<ItemStack> inventoryItemCollection) {
        Iterator iterator = inventoryItemCollection.iterator();
        while (iterator.hasNext()) {
            ItemStack itemStack = (ItemStack) iterator.next();
            ActionDataItem actionDataItem = actionDataItemMapper.toActionDataItem(itemStack);
            actionData.items.add(actionDataItem);
        }
    }
}
