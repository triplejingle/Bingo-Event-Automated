package com.bingoeventautomated.lootsender.Mappers;

import com.bingoeventautomated.lootsender.models.ActionDataItem;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;

public class NpcActionDataModelMapper {
    @Inject
    Client client;
    @Inject
    IEventConfig eventconfig;
    @Inject
    ActionDataItemMapper actionDataItemMapper;

    public ActionDataModel toActionDataModel(final NpcLootReceived npcLootReceived) {
        ActionDataModel actionData = new ActionDataModel();
        NPC npc = npcLootReceived.getNpc();
        actionData.itemsource = npc.getName();
        actionData.username = client.getLocalPlayer().getName();
        actionData.eventcode = eventconfig.eventCodeInput();

        setBingoTileItems(npcLootReceived, actionData);

        return actionData;
    }

    private void setBingoTileItems(NpcLootReceived npcLootReceived, ActionDataModel actionData) {
        Collection<ItemStack> lootList = npcLootReceived.getItems();

        Iterator iterator = lootList.iterator();
        while (iterator.hasNext()) {
            ItemStack item = (ItemStack) iterator.next();
            ActionDataItem actionDataItem = actionDataItemMapper.toActionDataItem(item);
            actionData.items.add(actionDataItem);
        }
    }
}
