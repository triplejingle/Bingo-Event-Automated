package com.bingoeventautomated.service;

import net.runelite.client.ui.DrawManager;
import okhttp3.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class BEADiscordClient extends BEAClientBase {
    @Inject
    private DrawManager drawManager;
    private static Queue<Image> screenshots = new ArrayDeque<>();

    public void SendImageToDiscord(String message) {
        byte[] imageBytes = CreateImage((BufferedImage) screenshots.peek());

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

            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        };

        Request request = new Request.Builder()
                .url(eventConfig.webhookUrl())
                .post(requestBodyBuilder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
    }

    private byte[] CreateImage(BufferedImage image) {
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

    public void SetScreenshot() {
        drawManager.requestNextFrameListener(image ->
        {
            screenshots.add(image);
        });
    }

    public void removeNextInLineScreenshot() {
        if (screenshots.peek() != null) {
            screenshots.remove();
        }
    }
}
