package com.goka.doctor.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.goka.doctor.IIntentReceiveCallbackInterface;
import com.goka.doctor.IIntentReceiveInterface;

public class IntentReceiveService extends Service {

    public static String TAG = IntentReceiveService.class.getName();

    public static Intent createIntent(Context context) {
        return new Intent(context, IntentReceiveService.class);
    }

    public static PendingIntent createPendingIntent(Context context, int requestCode, int flags) {
        Intent intent = createIntent(context);
        return PendingIntent.getService(context, requestCode, intent, flags);
    }

    private RemoteCallbackList<IIntentReceiveCallbackInterface> remoteCallbackList =
            new RemoteCallbackList<>();

    private IIntentReceiveInterface.Stub stub = new IIntentReceiveInterface.Stub() {
        @Override
        public void registerCallback(IIntentReceiveCallbackInterface callback)
                throws RemoteException {
            remoteCallbackList.register(callback);
        }

        @Override
        public void unregisterCallback(IIntentReceiveCallbackInterface callback)
                throws RemoteException {
            remoteCallbackList.unregister(callback);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int countOfCallbacks = remoteCallbackList.beginBroadcast();
        for (int i = 0; i < countOfCallbacks; i++) {
            try {
                IIntentReceiveCallbackInterface callback = remoteCallbackList.getBroadcastItem(i);
                callback.onReceiveReportIntent();
            } catch (RemoteException e) {
                Log.e(TAG, "onReceiveReportIntent", e);
            }
        }

        remoteCallbackList.finishBroadcast();

        stopSelf();

        return START_NOT_STICKY;
    }
}
