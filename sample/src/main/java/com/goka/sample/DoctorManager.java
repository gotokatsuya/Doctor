package com.goka.sample;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentActivity;

import com.goka.doctor.view.ReportFragment;

import java.util.HashMap;

public class DoctorManager {

    private static DoctorManager doctorManager;

    private ActivityLifecycleCallbacksAdapter activityLifecycleCallbacksAdapter;

    private ActivityLifecycleCallbacksAdapter.Callback callback = new ActivityLifecycleCallbacksAdapter.Callback() {
        @Override
        public void onCreated(Activity activity) {
            if (activity instanceof FragmentActivity) {
                ReportFragment.apply((FragmentActivity) activity);
            }
        }
    };

    public DoctorManager(Application application) {
        this.activityLifecycleCallbacksAdapter = new ActivityLifecycleCallbacksAdapter(application, callback);
    }

    public static synchronized void start(Application application) {
        if (doctorManager == null) {
            doctorManager = new DoctorManager(application);
        }
    }

    public static synchronized void stop(Application application) {
        if (doctorManager != null) {
            doctorManager.activityLifecycleCallbacksAdapter.unregister(application);
            doctorManager = null;
        }
    }

    public static HashMap<String, String> slackTokenMap = new HashMap<String, String>();

    static {
        slackTokenMap.put("gotokatsuya", "xxxx");
    }

    public static HashMap<String, String> slackChannelMap = new HashMap<String, String>();

    static {
        slackChannelMap.put("#general", "xxxx");
    }

}
