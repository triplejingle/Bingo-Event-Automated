package com.bingoeventautomated.dynamicconfig;

import com.bingoeventautomated.config.IEventConfig;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BEADynamicConfigClient {
    @Inject
    protected OkHttpClient okHttpClient;
    @Inject
    protected IEventConfig eventConfig;
    @Inject
    protected Gson gson;

    private final Cache<CacheKeys, ArrayList<String>> configCache;

    public BEADynamicConfigClient() {
        int nrOfEntries = 1;
        int nrOfHours = 1;
        configCache = CacheBuilder.newBuilder()
                .maximumSize(nrOfEntries)
                .expireAfterWrite(nrOfHours, TimeUnit.HOURS)
                .build();
    }

    public ArrayList<String> getDynamicConfiguration() {
        ArrayList<String> config = configCache.getIfPresent(CacheKeys.ITEMSOURCES);
        if (config != null) {
            return config;
        }
        HttpUrl.Builder urlBuilder
                = Objects.requireNonNull(HttpUrl.parse(eventConfig.getDynamicConfigUrl())).newBuilder();
        urlBuilder.addQueryParameter("eventcode", eventConfig.eventCodeInput());

        ArrayList<String> configuration = getConfig(urlBuilder, CacheKeys.ITEMSOURCES);
        return configuration;
    }

    private ArrayList<String> getConfig(HttpUrl.Builder urlBuilder, CacheKeys cacheKey) {
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
                response.close();
                return json;
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
