package com.bingoeventautomated.service;

import com.bingoeventautomated.Mapper.ActionDataModel;
import com.bingoeventautomated.config.IEventConfig;
import com.google.gson.Gson;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;

public abstract class BEAClientBase {
    @Inject
    protected OkHttpClient okHttpClient;
    @Inject
    protected Gson gson;
    @Inject
    protected IEventConfig eventConfig;

    protected String Serialize(ActionDataModel actionData) {
        return gson.toJson(actionData);
    }

    protected ActionResult Deserialize(String responseBody) {
        return gson.fromJson(responseBody, ActionResult.class);
    }

    protected void AsyncPostRequest(String body) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        };
        MakeAsyncPostCall(body, callback);
    }

    protected void MakeAsyncPostCall(String body, Callback callback) {
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
