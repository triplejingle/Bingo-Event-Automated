package com.bingoeventautomated.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DynamicConfigClient extends BEAClientBase {
    private final Cache<CacheKeys, ArrayList<String>> configCache;

    public DynamicConfigClient() {
        int nrOfEntries = 1;
        int nrOfHours = 1;
        configCache = CacheBuilder.newBuilder()
                .maximumSize(nrOfEntries)
                .expireAfterWrite(nrOfHours, TimeUnit.HOURS)
                .build();
    }

    public ArrayList<String> GetDynamicConfiguration() {
        ArrayList<String> config = configCache.getIfPresent(CacheKeys.ITEMSOURCES);
        if (config != null) {
            return config;
        }
        HttpUrl.Builder urlBuilder
                = Objects.requireNonNull(HttpUrl.parse(eventConfig.GetDynamicConfigUrl())).newBuilder();
        urlBuilder.addQueryParameter("eventcode", eventConfig.eventCodeInput());

        ArrayList<String> configuration = GetConfig(urlBuilder, CacheKeys.ITEMSOURCES);
        return configuration;
    }

    private ArrayList<String> GetConfig(HttpUrl.Builder urlBuilder, CacheKeys cacheKey) {
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);

        try {
            Response response = call.execute();
            if (response.body() != null) {
                String body = response.body().string();
                Type type = new TypeToken<List<String>>() {
                }.getType();
                ArrayList<String> json = gson.fromJson(body, type);
                configCache.put(cacheKey, json);
                return json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
