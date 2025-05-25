package com.bingoeventautomated.lootsender;

import com.bingoeventautomated.lootsender.Mappers.NpcActionDataModelMapper;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.dynamicconfig.BEADynamicConfigClient;
import com.bingoeventautomated.lootsender.validator.ActionDataItemValidator;
import com.bingoeventautomated.server.BEAServerClient;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.ui.DrawManager;

import javax.inject.Inject;
import java.util.ArrayList;

public class NpcService {
    @Inject
    private BEAServerClient actionDataItemClient;
    @Inject
    private NpcActionDataModelMapper npcActionDataModelMapper;
    @Inject
    private ActionDataItemValidator actionDataItemValidator;
    @Inject
    private IEventConfig eventconfig;
    @Inject
    private DrawManager drawManager;
    @Inject
    private BEADynamicConfigClient dynamicConfigClient;
    @Inject
    private Client client;

    public void process(NpcLootReceived npcLootReceived) {
        String itemsource = npcLootReceived.getNpc().getName();
        if (!(client.getGameState() == GameState.LOGGED_IN) || !isSupported(itemsource)) {
            return;
        }

        if (eventconfig.sendScreenshot()) {
            drawManager.requestNextFrameListener(image ->
            {
                ActionDataModel unvalidatedActionData = npcActionDataModelMapper.toActionDataModel(npcLootReceived);
                unvalidatedActionData.itemsource = itemsource;
                ActionDataModel validatedActionData = actionDataItemValidator.validate(unvalidatedActionData);
                actionDataItemClient.sendActionDataModel(validatedActionData, image);
            });
            return;
        }

        ActionDataModel unvalidatedActionData = npcActionDataModelMapper.toActionDataModel(npcLootReceived);
        ActionDataModel validatedActionData = actionDataItemValidator.validate(unvalidatedActionData);
        actionDataItemClient.AsyncPostRequest(validatedActionData);
    }

    private boolean isSupported(String npcName) {
        ArrayList<String> configuration = dynamicConfigClient.getDynamicConfiguration();
        return configuration.contains(npcName) && configuration.size() > 0;
    }
}

