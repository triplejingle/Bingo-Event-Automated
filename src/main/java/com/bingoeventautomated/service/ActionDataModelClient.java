package com.bingoeventautomated.service;

import com.bingoeventautomated.Mapper.ActionDataModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;

public class ActionDataModelClient extends BEAClientBase {
    @Inject
    private BEADiscordClient discordClient;

    public void SendActionDataModel(ActionDataModel actionData) {
        boolean isActionDataSet = actionData.IsSet();
        if (!isActionDataSet) {
            RemoveScreenshot();
            return;
        }

        String body = Serialize(actionData);
        if (eventConfig.SendScreenshot()) {
            SendPostRequestWithScreenshot(body);
        } else {
            AsyncPostRequest(body);
        }
    }

    private void SendPostRequestWithScreenshot(String body) {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RemoveScreenshot();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        String responseBody = response.body().string();
                        ActionResult actionResult = Deserialize(responseBody);
                        if (actionResult.isMessageSet) {
                            discordClient.SendImageToDiscord(actionResult.message);
                        }
                    }
                }
                RemoveScreenshot();
            }
        };
        MakeAsyncPostCall(body, callback);
    }

    public void SetScreenshot() {
        //setting screenshot like allows the expansion of sending multiple screenshots (will be added in the future)
        //do not remove otherwise the sreenshot variable in discordclient has to become static. Which is undesirable.
        discordClient.SetScreenshot();
    }

    public void RemoveScreenshot() {
        discordClient.removeNextInLineScreenshot();
    }
}
