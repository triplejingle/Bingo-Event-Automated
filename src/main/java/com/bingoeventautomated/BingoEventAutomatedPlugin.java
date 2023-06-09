package com.bingoeventautomated;
import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.events.NPCEvent;
import com.bingoeventautomated.events.PlayerEvent;
import com.bingoeventautomated.service.ActionDataModel;
import com.bingoeventautomated.service.ActionDataModelMapper;
import com.bingoeventautomated.service.EventClient;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
        name = "BAN plugin"
)
public class BingoEventAutomatedPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    EventClient eventClient;
    @Inject
    private ActionDataModelMapper actionDataMapper;
    @Inject
    NPCEvent npcEvent;
    @Inject
    PlayerEvent playerEvent;

    @Subscribe
    private void onNpcLootReceived​(NpcLootReceived npcLootReceived) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        ActionDataModel unvalidatedActionData = actionDataMapper.ToActionData(npcLootReceived);
        ActionDataModel validatedActionData = npcEvent.validateActionData(unvalidatedActionData);
        eventClient.SendActionData(validatedActionData);
    }

    @Subscribe
    private  void onMenuOptionClicked(MenuOptionClicked event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        playerEvent.UpdateDroppedItemList(event);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        int chestId=widgetLoaded.getGroupId();
        ActionDataModel actionData = actionDataMapper.ToActionData(chestId);
        eventClient.SendActionData(actionData);
    }


    @Provides
    IEventConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(IEventConfig.class);
    }
}
