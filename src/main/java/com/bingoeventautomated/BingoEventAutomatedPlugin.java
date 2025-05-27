package com.bingoeventautomated;

import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.playerevent.PlayerEvent;
import com.bingoeventautomated.lootsender.NpcService;
import com.bingoeventautomated.lootsender.WidgetService;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
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
        name = "Bingo Event Automated",
        description = "To send bingo items to the server and discord"
)
public class BingoEventAutomatedPlugin extends Plugin {
    @Inject
    private PlayerEvent playerEvent;
    @Inject
    private WidgetService widgetService;
    @Inject
    private NpcService npcService;

    @Subscribe
    private void onNpcLootReceived(NpcLootReceived npcLootReceived) {
        npcService.process(npcLootReceived);
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
       playerEvent.process(event);
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
        widgetService.process(widgetLoaded);
    }

    @Provides
    IEventConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(IEventConfig.class);
    }
}
