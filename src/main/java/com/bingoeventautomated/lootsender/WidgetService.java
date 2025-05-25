package com.bingoeventautomated.lootsender;

import com.bingoeventautomated.lootsender.Mappers.InventoryActionDataModelMapper;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.dynamicconfig.BEADynamicConfigClient;
import com.bingoeventautomated.server.BEAServerClient;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.ui.DrawManager;

import javax.inject.Inject;
import java.util.ArrayList;

public class WidgetService {
    @Inject
    private Client client;
    @Inject
    private BEAServerClient actionDataItemClient;
    @Inject
    private InventoryActionDataModelMapper inventoryActionDataModelMapper;
    @Inject
    private IEventConfig eventconfig;
    @Inject
    private DrawManager drawManager;
    @Inject
    private BEADynamicConfigClient dynamicConfigClient;

    public void process(WidgetLoaded widgetLoaded) {
        int groupId = widgetLoaded.getGroupId();
        if (!(client.getGameState() == GameState.LOGGED_IN) || !isSupportedWidget(groupId)) {
            return;
        }

        if (eventconfig.sendScreenshot()) {
            drawManager.requestNextFrameListener(image ->
            {
                ActionDataModel actionData = inventoryActionDataModelMapper.map(groupId);
                actionDataItemClient.sendActionDataModel(actionData, image);
            });
            return;
        }

        ActionDataModel actionData = inventoryActionDataModelMapper.map(groupId);
        actionDataItemClient.AsyncPostRequest(actionData);
    }

    private boolean isSupportedWidget(int inventoryId) {
        String inventoryName = inventoryActionDataModelMapper.getInventoryName(inventoryId);
        ArrayList<String> configuration = dynamicConfigClient.getDynamicConfiguration();
        return inventoryName != "" && configuration.contains(inventoryName) && configuration.size() > 0;
    }
}
