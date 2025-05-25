package com.bingoeventautomated.server;

import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.discord.BEADiscordClient;
import com.bingoeventautomated.lootsender.models.ActionResult;
import com.bingoeventautomated.lootsender.models.ActionDataModel;
import com.google.gson.Gson;
import okhttp3.*;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;

public class BEAServerClient  {
    @Inject
    private BEADiscordClient discordClient;
    @Inject
    protected OkHttpClient okHttpClient;
    @Inject
    protected IEventConfig eventConfig;
    @Inject
    protected Gson gson;

    public void sendActionDataModel(ActionDataModel actionData, Image image) {
        String body =  gson.toJson(actionData);
        sendPostRequestWithScreenshot(body, image);
    }

    public void AsyncPostRequest(ActionDataModel actionData) {
        String body =  gson.toJson(actionData);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                response.close();
            }
        };
        sendPostRequest(body, callback);
    }

    private void sendPostRequestWithScreenshot(String body, Image image) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        String responseBody = response.body().string();
                        ActionResult actionResult = gson.fromJson(responseBody, ActionResult.class);
                        if (actionResult.isMessageSet) {
                            discordClient.sendToDiscord(actionResult.message, image);
                        }
                    }
                }
                response.close();
            }
        };
        sendPostRequest(body, callback);
    }

    private void sendPostRequest(String body, Callback callback) {
        HttpUrl httpUrl = HttpUrl.parse(eventConfig.urlInput())
                .newBuilder()
                .build();

        RequestBody requestBody = RequestBody
                .create(
                        MediaType.get("application/json; charset=utf-8"),
                        body
                );
        Request request = new Request.Builder()
                .url(httpUrl)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }
}
