package com.goka.doctor.model.client;

import android.content.Context;

import com.goka.doctor.model.ClientPrefsSchema;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public class SlackClient {

    private static SlackClient slackClient;

    public static synchronized SlackClient getInstance() {
        if (slackClient == null) {
            slackClient = new SlackClient();
        }
        return slackClient;
    }

    private SlackService slackService;

    public SlackClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(new OkHttpClient())
                .baseUrl("https://slack.com/api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        slackService = retrofit.create(SlackService.class);
    }

    public interface SlackService {
        @Multipart
        @POST("files.upload")
        Observable<JSONObject> sendScreenShot(
                @Part("token") String token,
                @Part("title") String title,
                @Part("file") RequestBody screenShotFile,
                @Part("channels") String channels
        );
    }

    public Observable<JSONObject> uploadScreenShot(Context context, String title, File screenshotFile, String channel) {
        RequestBody screenshotFileBody = RequestBody.create(MediaType.parse("multipart/form-data"), screenshotFile);
        return slackService.sendScreenShot(
                ClientPrefsSchema.get(context).getSlackToken(),
                title,
                screenshotFileBody,
                channel);
    }
}
