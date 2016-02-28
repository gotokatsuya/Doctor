package com.goka.doctor.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.goka.doctor.Doctor;
import com.goka.doctor.IIntentReceiveCallbackInterface;
import com.goka.doctor.IIntentReceiveInterface;
import com.goka.doctor.R;
import com.goka.doctor.model.AppProfile;
import com.goka.doctor.model.client.SlackClient;
import com.goka.doctor.service.IntentReceiveService;
import com.goka.doctor.service.NotificationService;
import com.goka.doctor.service.ScreenshotService;

import java.io.File;
import java.io.IOException;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class ReportFragment extends Fragment {

    private static final String TAG = ReportFragment.class.getName();

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    public static ReportFragment apply(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ReportFragment fragment = (ReportFragment) fragmentManager.findFragmentByTag(TAG);
        if (fragment != null) {
            Log.i(TAG, "apply::fragment is attached");
            return fragment;
        }
        fragment = newInstance();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, TAG);
        fragmentTransaction.commit();
        return fragment;
    }

    private CompositeSubscription compositeSubscription = new CompositeSubscription();;

    private IIntentReceiveInterface intentReceiveInterface;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            intentReceiveInterface = IIntentReceiveInterface.Stub.asInterface(service);
            try {
                intentReceiveInterface.registerCallback(new IIntentReceiveCallbackInterface.Stub() {
                    @Override
                    public void onReceiveReportIntent() throws RemoteException {
                        try {
                            takeScreenshotThenUpload();
                        } catch (IOException e) {
                            Log.e(TAG, "takeScreenshotThenUpload", e);
                            showMessage(R.string.failed_to_take_screen_shot);
                        }
                    }
                });
            } catch (RemoteException e) {
                Log.e(TAG, "takeScreenshotThenUpload", e);
                intentReceiveInterface = null;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            intentReceiveInterface = null;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        NotificationService.show(getContext(),
                IntentReceiveService.createPendingIntent(getContext(), 0, 0),
                Doctor.getInstance().usingClient);
        Intent intent = IntentReceiveService.createIntent(getContext());
        getContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        NotificationService.cancel(getContext());
        getContext().unbindService(connection);
    }

    private void takeScreenshotThenUpload() throws IOException {
        ProgressDialogFragment.show(getActivity(), R.string.just_a_moment);
        compositeSubscription.add(takeScreenShot(getActivity()));
    }

    private Subscription takeScreenShot(final FragmentActivity activity) {
        return ScreenshotService.createFile(activity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ProgressDialogFragment.dismiss(getActivity());
                        showMessage(R.string.failed_to_take_screen_shot);
                        Log.e(TAG, "takeScreenShot", throwable);
                    }
                })
                .doOnNext(new Action1<File>() {
                    @Override
                    public void call(final File file) {
                        Log.i(TAG, "takeScreenShot::onNext");
                        ListDialogFragment.show(getActivity(),
                                Doctor.getInstance().getSlackChannelIDs(),
                                new ListDialogFragment.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(String channel) {
                                        ListDialogFragment.dismiss(getActivity());
                                        AppProfile appProfile = new AppProfile(activity);
                                        compositeSubscription.add(uploadScreenShot(activity,
                                                appProfile,
                                                file,
                                                channel));
                                    }
                                });
                    }
                })
                .subscribe();
    }

    private Subscription uploadScreenShot(final FragmentActivity activity,
                                          AppProfile appProfile,
                                          File bitmapFile,
                                          String channel) {
        return SlackClient.getInstance().uploadScreenShot(activity, appProfile.description(), bitmapFile, channel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "uploadScreenShot", throwable);
                        ProgressDialogFragment.dismiss(activity);
                        showMessage(R.string.failed_to_report);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "uploadScreenShot::onCompleted");
                        ProgressDialogFragment.dismiss(activity);
                        showMessage(R.string.successful_reporting);
                    }
                })
                .subscribe();
    }

    @Override
    public void onDestroyView() {
        compositeSubscription.unsubscribe();
        super.onDestroyView();
    }

    private void showMessage(int resID) {
        if (getContext() == null) {
            Log.w(TAG, "showMessage::getContext() == null");
            return;
        }
        Toast.makeText(getContext(), resID, Toast.LENGTH_SHORT).show();
    }
}
