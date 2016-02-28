package com.goka.doctor.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListDialogFragment extends DialogFragment {

    public interface OnItemClickListener {
        void onItemClick(String value);
    }

    private static final String TAG = ListDialogFragment.class.getName();

    public static final String EXTRA_LIST = "list";

    private OnItemClickListener onItemClickListener;

    public static ListDialogFragment newInstance(ArrayList<String> list, OnItemClickListener listener) {
        Bundle args = new Bundle();
        args.putStringArrayList(EXTRA_LIST, list);
        ListDialogFragment fragment = new ListDialogFragment();
        fragment.setArguments(args);
        fragment.onItemClickListener = listener;
        return fragment;
    }

    public static void show(FragmentActivity activity, ArrayList<String> message, OnItemClickListener listener) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ListDialogFragment dialogFragment = ListDialogFragment.newInstance(message, listener);
        dialogFragment.show(fragmentManager, TAG);
    }

    public static void dismiss(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(TAG);
        if (fragment == null) {
            Log.i(TAG, "apply::fragment is not attached");
            return;
        }
        if (!(fragment instanceof ListDialogFragment)) {
            String message = "A fragment in the FragmentManager with tag name " + TAG + " is not in " + TAG;
            throw new IllegalStateException(message);
        }
        ListDialogFragment dialogFragment = (ListDialogFragment) fragment;
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
        final ArrayList<String> list = args.getStringArrayList(EXTRA_LIST);
        ListView lv = new ListView(getContext());
        lv.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list));
        lv.setScrollingCacheEnabled(false);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> items, View view, int position, long id) {
                onItemClickListener.onItemClick(list.get(position));
            }
        });
        return new AlertDialog.Builder(getContext())
                .setTitle("Reportable list")
                .setView(lv)
                .create();
    }
}
