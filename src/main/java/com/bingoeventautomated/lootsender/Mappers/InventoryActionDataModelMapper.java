package com.bingoeventautomated.lootsender.Mappers;

import com.bingoeventautomated.lootsender.models.ActionDataItem;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.WidgetID;
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
            case WidgetID.BARROWS_REWARD_GROUP_ID:
                return "Barrows";
            case WidgetID.CHAMBERS_OF_XERIC_REWARD_GROUP_ID:
                return "COX";
            case WidgetID.THEATRE_OF_BLOOD_GROUP_ID:
                return "TOB";
            case WidgetID.TOA_REWARD_GROUP_ID:
                return "TOA";
            case WidgetID.FISHING_TRAWLER_REWARD_GROUP_ID:
                return "Fishing Trawler";
            case WidgetID.WILDERNESS_LOOT_CHEST:
                return "Wilderniss Loot Chest";
        }
        return "";
    }
    //todo refactor so it conforms to dry
    public InventoryID getWidgetId(int widgetId) {
        switch (widgetId) {
            case WidgetID.BARROWS_REWARD_GROUP_ID:
                return InventoryID.BARROWS_REWARD;
            case WidgetID.CHAMBERS_OF_XERIC_REWARD_GROUP_ID:
                return InventoryID.CHAMBERS_OF_XERIC_CHEST;
            case WidgetID.THEATRE_OF_BLOOD_GROUP_ID:
                return InventoryID.THEATRE_OF_BLOOD_CHEST;
            case WidgetID.TOA_REWARD_GROUP_ID:
                return InventoryID.TOA_REWARD_CHEST;
            case WidgetID.FISHING_TRAWLER_REWARD_GROUP_ID:
                return InventoryID.FISHING_TRAWLER_REWARD;
            case WidgetID.WILDERNESS_LOOT_CHEST:
                return InventoryID.WILDERNESS_LOOT_CHEST;
        }
        return null;
    }

    public ActionDataModel map(int widgetId) {
        String inventoryName = getInventoryName(widgetId);
        ActionDataModel actionData = new ActionDataModel();
        InventoryID foundInventory = getWidgetId(widgetId);
        if (foundInventory == null)
            return actionData;

        actionData = inventoryToActionDataModel(foundInventory, inventoryName);
        actionData.username = client.getLocalPlayer().getName();
        actionData.eventcode = eventconfig.eventCodeInput();
        return actionData;
    }

    private ActionDataModel inventoryToActionDataModel(InventoryID inventoryID, String itemsource) {
        ActionDataModel actionData = new ActionDataModel();
        actionData.itemsource = itemsource;
        setBingoTileItems(inventoryID, actionData);
        return actionData;
    }

    private void setBingoTileItems(InventoryID inventoryID, ActionDataModel actionData) {
        try {
            Collection<ItemStack> InventoryItemCollection = getItemListFromInventory(inventoryID);
            addInventoryItemsToBingoTileItems(actionData, InventoryItemCollection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Collection<ItemStack> getItemListFromInventory(InventoryID inventoryID) {
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
