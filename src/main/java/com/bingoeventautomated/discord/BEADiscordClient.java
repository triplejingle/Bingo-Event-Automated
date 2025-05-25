package com.bingoeventautomated.discord;


import com.bingoeventautomated.config.IEventConfig;
import com.bingoeventautomated.discord.models.DiscordChatMessageModel;
import com.google.gson.Gson;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BEADiscordClient {
    @Inject
    protected OkHttpClient okHttpClient;
    @Inject
    protected IEventConfig eventConfig;
    @Inject
    protected Gson gson;

    public void sendToDiscord(String message, Image image) {
        byte[] imageBytes = createImage((BufferedImage) image);

        DiscordChatMessageModel discordMessageModel = new DiscordChatMessageModel();
        discordMessageModel.setContent(message);

        String body = gson.toJson(discordMessageModel);

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", body);
        requestBodyBuilder.addFormDataPart("file", "image.png",
                RequestBody.create(MediaType.parse("image/png"), imageBytes));

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

        Request request = new Request.Builder()
                .url(eventConfig.webhookUrl())
                .post(requestBodyBuilder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private byte[] createImage(BufferedImage image) {
        byte[] imageBytes = null;
        try {
            imageBytes = convertImageToByteArray(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBytes;
    }

    private static byte[] convertImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
