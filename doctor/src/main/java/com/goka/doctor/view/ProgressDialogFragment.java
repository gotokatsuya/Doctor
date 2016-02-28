package com.goka.doctor.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;

public class ProgressDialogFragment extends DialogFragment {

    private static final String TAG = ProgressDialogFragment.class.getName();

    public static final String EXTRA_MESSAGE = "message";

    public static ProgressDialogFragment newInstance(int messageResID) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_MESSAGE, messageResID);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ProgressDialogFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(EXTRA_MESSAGE, message);
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static void show(FragmentActivity activity, int message) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(message);
        dialogFragment.show(fragmentManager, TAG);
    }

    public static void show(FragmentActivity activity, String message) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ProgressDialogFragment dialogFragment = ProgressDialogFragment.newInstance(message);
        dialogFragment.show(fragmentManager, TAG);
    }

    public static void dismiss(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            Log.i(TAG, "apply::fragment is not attached");
            return;
        }
        if (!(fragment instanceof ProgressDialogFragment)) {
            String message = "A fragment in the FragmentManager with tag name " + TAG + " is not in " + TAG;
            throw new IllegalStateException(message);
        }
        ProgressDialogFragment dialogFragment = (ProgressDialogFragment) fragment;
        dialogFragment.dismiss();
    }

    public static boolean isShowing(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        return fragment != null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String message = "";
        int messageResID = args.getInt(EXTRA_MESSAGE, 0);
        if (messageResID > 0) {
            message = getString(messageResID);
        } else {
            message = args.getString(EXTRA_MESSAGE, "");
        }
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return progressDialog;
    }

}
