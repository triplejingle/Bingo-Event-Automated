package com.bingoeventautomated.config;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bingoEvent")
public interface IEventConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "url",
            name = "Send data to",
            description = "The url of the server which processes the data."
    )
    default String urlInput() {
        return "http://localhost:5000";
    }

    @ConfigItem(
            position = 2,
            keyName = "config",
            name = "Dynamic configuration url",
            description = "The url of the server to set the dynamic config."
    )
    default String getDynamicConfigUrl() {
        return "http://localhost:5000";
    }

    @ConfigItem(
            position = 3,
            keyName = "eventcode",
            name = "Eventcode",
            description = "The code of the event."
    )
    default String eventCodeInput() {
        return "1A2B3C";
    }

    @ConfigItem(
            position = 4,
            keyName = "Send screenshot",
            name = "Send screenshot",
            description = "Configures whether a message will be automatically sent to discord when you obtain items."
    )
    default boolean sendScreenshot()
    {
        return false;
    }
    @ConfigItem(
            position = 5,
            keyName = "webhook",
            name = "Discord webhook",
            description = "The webhook used to send messages to Discord."
    )
    String webhookUrl();
}
