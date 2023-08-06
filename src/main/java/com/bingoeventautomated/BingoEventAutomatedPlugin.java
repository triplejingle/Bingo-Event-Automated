package com.bingoeventautomated;

import com.bingoeventautomated.Mapper.ActionDataModel;
import com.bingoeventautomated.Mapper.InventoryActionDataModelMapper;
import com.bingoeventautomated.Mapper.NpcActionDataModelMapper;
import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.events.PlayerEvent;
import com.bingoeventautomated.service.ActionDataModelClient;
import com.bingoeventautomated.service.DynamicConfigClient;
import com.bingoeventautomated.validator.ActionDataItemValidator;
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
import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
        name = "Bingo Event Automated",
        description = "To send bingo items to the server."
)
public class BingoEventAutomatedPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ActionDataModelClient actionDataItemClient;
    @Inject
    private DynamicConfigClient dynamicConfigClient;
    @Inject
    private NpcActionDataModelMapper npcActionDataModelMapper;
    @Inject
    private InventoryActionDataModelMapper inventoryActionDataModelMapper;
    @Inject
    private ActionDataItemValidator actionDataItemValidator;
    @Inject
    private PlayerEvent playerEvent;
    @Inject
    private IEventConfig eventconfig;

    @Subscribe
    private void onNpcLootReceived​(NpcLootReceived npcLootReceived) {
        String itemsource = npcLootReceived.getNpc().getName();
        if (!IsLoggedIn() || !IsSupported(itemsource)) {
            return;
        }

        if (eventconfig.SendScreenshot()) {
            //image must always be set first so it gets the screenshot asap
            actionDataItemClient.SetScreenshot();
        }

        ActionDataModel unvalidatedActionData = npcActionDataModelMapper.ToActionDataModel(npcLootReceived);
        ActionDataModel validatedActionData = actionDataItemValidator.validate(unvalidatedActionData);
        actionDataItemClient.SendActionDataModel(validatedActionData);
    }

    private boolean IsSupported(String npcName) {
        ArrayList<String> configuration = dynamicConfigClient.GetDynamicConfiguration();
        return configuration.contains(npcName) && configuration.size() > 0;
    }

    private boolean IsLoggedIn() {
        return client.getGameState() == GameState.LOGGED_IN;
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        if (!IsLoggedIn())
            return;

        playerEvent.UpdateInvalidItemList(event);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        int groupId = widgetLoaded.getGroupId();
        if (!IsLoggedIn() || !IsSupportedWidget(groupId)) {
            return;
        }

        if (eventconfig.SendScreenshot()) {
            //image must always be set first so it gets the screenshot asap
            actionDataItemClient.SetScreenshot();
        }

        ActionDataModel actionData = inventoryActionDataModelMapper.Map(groupId);
        actionDataItemClient.SendActionDataModel(actionData);
    }

    private boolean IsSupportedWidget(int inventoryId) {
        String inventoryName = inventoryActionDataModelMapper.GetInventoryName(inventoryId);
        return inventoryName != "" && IsSupported(inventoryName);
    }


    @Provides
    IEventConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(IEventConfig.class);
    }
}
