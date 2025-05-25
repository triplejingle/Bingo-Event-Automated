package com.bingoeventautomated.discord.models;

import lombok.Data;

@Data
public class DiscordChatMessageModel {
    private String content;
    private Embed embed;

    @Data
    static class Embed {
        final UrlEmbed image;
    }

    @Data
    static class UrlEmbed {
        final String url;
    }
}