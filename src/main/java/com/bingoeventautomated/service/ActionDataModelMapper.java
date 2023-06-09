package com.bingoeventautomated.service;

import com.bingoeventautomated.config.IEventConfig;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class ActionDataModelMapper {
    @Inject
    ItemManager itemManager;
    @Inject
    Client client;
    @Inject
    IEventConfig eventconfig;
    public ActionDataModel ToActionData(final NpcLootReceived npcLootReceived){
        ActionDataModel actionData = new ActionDataModel();
        NPC npc = npcLootReceived.getNpc();
        actionData.itemsource= npc.getName();
        actionData.username= client.getLocalPlayer().getName();
        actionData.eventcode = eventconfig.eventCodeInput();
        Collection<ItemStack> lootList = npcLootReceived.getItems();
        Iterator iterator = lootList.iterator();

        while (iterator.hasNext()) {
            ItemStack item = (ItemStack) iterator.next();
            int itemId = item.getId();
            String itemName = getItemName(itemId);
            actionData.items.add(itemName);
        }

        return actionData;
    }

    private String getItemName(int itemId) {
        return itemManager.getItemComposition(itemId).getName();
    }

    public ActionDataModel ToActionData(int chestId) {
        ActionDataModel actionData =new ActionDataModel();
        switch (chestId)
        {
            case WidgetID.BARROWS_REWARD_GROUP_ID:
                actionData = ChestToActionData(InventoryID.BARROWS_REWARD, "Barrows");
                break;
            case WidgetID.CHAMBERS_OF_XERIC_REWARD_GROUP_ID:
                actionData = ChestToActionData(InventoryID.CHAMBERS_OF_XERIC_CHEST, "COX");
                break;
            case WidgetID.THEATRE_OF_BLOOD_GROUP_ID:
                actionData = ChestToActionData(InventoryID.THEATRE_OF_BLOOD_CHEST, "TOB");
                break;
            case WidgetID.TOA_REWARD_GROUP_ID:
                actionData = ChestToActionData(InventoryID.TOA_REWARD_CHEST, "TOA");
                break;
            case WidgetID.FISHING_TRAWLER_REWARD_GROUP_ID:
                actionData = ChestToActionData(InventoryID.FISHING_TRAWLER_REWARD, "Fishing Trawler");
                break;
            case WidgetID.WILDERNESS_LOOT_CHEST:
                actionData = ChestToActionData(InventoryID.WILDERNESS_LOOT_CHEST, "Wilderniss Loot Chest");
                break;
        }
        actionData.username= client.getLocalPlayer().getName();
        actionData.eventcode = eventconfig.eventCodeInput();
        return actionData;
    }

    public ActionDataModel ChestToActionData(InventoryID inventoryID, String itemsource){
        ActionDataModel actionData = new ActionDataModel();
        actionData.itemsource = itemsource;
        ItemContainer container = this.client.getItemContainer(inventoryID);
        try {
            Collection<ItemStack> items = Arrays.stream(container.getItems()).filter((item) -> {
                return item.getId() > 0;
            }).map((item) -> {
                return new ItemStack(item.getId(), item.getQuantity(), this.client.getLocalPlayer().getLocalLocation());
            }).collect(Collectors.toList());

            Iterator iterator = items.iterator();
            while (iterator.hasNext()) {
                ItemStack itemStack = (ItemStack) iterator.next();
                String name = this.itemManager.getItemComposition(itemStack.getId()).getName();
                actionData.items.add(name);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return actionData;
    }


}
