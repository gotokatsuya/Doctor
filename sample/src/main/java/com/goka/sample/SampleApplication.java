package com.goka.sample;

import android.app.Application;

import com.goka.doctor.Doctor;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Doctor doctor = Doctor.getInstance();
        doctor.setSlackToken(getApplicationContext(),
                DoctorManager.slackTokenMap.get("gotokatsuya"));
        doctor.setSlackChannelMap(DoctorManager.slackChannelMap);

        DoctorManager.start(this);
    }

    @Override
    public void onTerminate() {
        DoctorManager.stop(this);
        super.onTerminate();
    }
}
