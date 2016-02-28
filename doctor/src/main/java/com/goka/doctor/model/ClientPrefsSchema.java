package com.goka.doctor.model;

import android.content.Context;

import com.rejasupotaro.android.kvs.annotations.Key;
import com.rejasupotaro.android.kvs.annotations.Table;

@Table("com.goka.doctor.client_preferences")
public abstract class ClientPrefsSchema {

    @Key("slack_token")
    String slackToken;

    private static ClientPrefs prefs;

    public static synchronized ClientPrefs get(Context context) {
        if (prefs == null) {
            prefs = new ClientPrefs(context);
        }
        return prefs;
    }
}
