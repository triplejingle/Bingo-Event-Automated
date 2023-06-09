package com.bingoeventautomated.service;

import com.bingoeventautomated.config.IEventConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.runelite.client.ui.DrawManager;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.http.api.RuneLiteAPI.GSON;

public class EventClient {
    private static OkHttpClient client;
    @Inject
    DrawManager drawManager;
    public EventClient(){
        if(client==null) {
            client = new OkHttpClient
                    .Builder()
                    .retryOnConnectionFailure(true)
                    .build();
        }
    }

    @Inject
    IEventConfig eventConfig;
    public void SendActionData(ActionDataModel actionData) {
        boolean isActionDataSet = actionData.IsSet();
        if(!isActionDataSet){
            return;
        }

        HttpUrl.Builder urlBuilder
                = HttpUrl.parse(eventConfig.GetDynamicConfigUrl()).newBuilder();
        urlBuilder.addQueryParameter("eventcode", actionData.eventcode);

        ArrayList<String> itemsources = GetAll(urlBuilder);

        if(!itemsources.contains(actionData.itemsource))
            return;

        String body = ConvertToBody(actionData);
        if(eventConfig.SendScreenshot()) {
            SendPostRequestWithScreenshot( body);
        }else{
            SendPostRequest(body);
        }
    }
    public ArrayList<String> GetAll(HttpUrl.Builder urlBuilder){
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String body = response.body().string();
            Type type =  new TypeToken<List<String>>(){}.getType();
            return GSON.fromJson(body,type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private String ConvertToBody(ActionDataModel actionData) {
        Gson gson = new Gson();
        return gson.toJson(actionData);
    }

    private void SendPostRequestWithScreenshot( String body) {
        Callback callback  = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    if(response.body()!=null){
                        String responseBody =response.body().string();
                        ActionResult actionResult =GSON.fromJson(responseBody, ActionResult.class);

                        if(actionResult.isMessageSet){
                            SendImage(actionResult.message);
                        }
                    }
                }
            }
        };
        MakeAsyncCall(body, callback);
    }

    private void MakeAsyncCall( String body, Callback callback) {
        RequestBody requestBody = RequestBody
                .create(
                        MediaType.get("application/json; charset=utf-8"),
                        body
                );
        Request request = new Request.Builder()
                .url(eventConfig.urlInput())
                .post(requestBody)
                .build();
        Call call =client.newCall(request);
        call.enqueue(callback);
    }

    private void SendImage(String message) {
        drawManager.requestNextFrameListener(image ->
        {
             byte[] imageBytes= CreateImage((BufferedImage)image);
            if(imageBytes==null)
                return;
            SendImageToDiscord(message, imageBytes);
        });
    }

    public byte[] CreateImage(BufferedImage image) {
        byte[] imageBytes= null;
        try {
            imageBytes = convertImageToByteArray(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

    private static byte[] convertImageToByteArray(BufferedImage bufferedImage) throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void SendImageToDiscord(String message, byte[] imageBytes) {
        DiscordWebhookBody discordWebhookBody = new DiscordWebhookBody();
        discordWebhookBody.setContent(message);

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(discordWebhookBody));

        requestBodyBuilder.addFormDataPart("file", "image.png",
                RequestBody.create(MediaType.parse("image/png"), imageBytes));

        Callback callback= new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        };

        Request request = new Request.Builder()
                .url(eventConfig.webhookUrl())
                .post(requestBodyBuilder.build())
                .build();
        Call call =client.newCall(request);
        call.enqueue(callback);
    }

    private void SendPostRequest(String body) {
        Callback callback  = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        };
        MakeAsyncCall(body, callback);
    }
}
