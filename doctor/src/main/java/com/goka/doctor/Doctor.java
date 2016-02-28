package com.goka.doctor;

import android.content.Context;

import com.goka.doctor.model.ClientPrefsSchema;

import java.util.ArrayList;
import java.util.HashMap;

public class Doctor {

    private static Doctor doctor = new Doctor();

    public static Doctor getInstance() {
        return doctor;
    }

    public String usingClient = "slack";

    public HashMap<String, String> slackChannelMap = new HashMap<String, String>();

    public void setSlackToken(Context context, String slackToken) {
        ClientPrefsSchema.get(context).setSlackToken(slackToken);
    }

    public void setSlackChannelMap(HashMap<String, String> slackChannelMap) {
        this.slackChannelMap = slackChannelMap;
    }

    public ArrayList<String> getSlackChannelNames() {
        ArrayList<String> channelNames = new ArrayList<>();
        for (String key: slackChannelMap.keySet()) {
            channelNames.add(key);
        }
        return channelNames;
    }

    public ArrayList<String> getSlackChannelIDs() {
        ArrayList<String> channelIDs = new ArrayList<>();
        for (String key: slackChannelMap.keySet()) {
            channelIDs.add(slackChannelMap.get(key));
        }
        return channelIDs;
    }
}
