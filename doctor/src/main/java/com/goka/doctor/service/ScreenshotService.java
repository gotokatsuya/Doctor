package com.goka.doctor.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.goka.doctor.util.GraphicsUtil;
import com.goka.doctor.util.IOUtil;

import java.io.File;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

public class ScreenshotService {

    public static String TAG = ScreenshotService.class.getName();

    public static Observable<File> createFile(final Activity activity) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    Log.w(TAG, "createFile::isUnsubscribed");
                    return;
                }
                try {
                    Bitmap bitmap = GraphicsUtil.getDecorViewBitmap(activity);
                    File bitmapFile = obtainNewBitmap(activity);
                    Log.i(TAG, "createFile:Path" + bitmapFile.getAbsolutePath());
                    IOUtil.saveBitmap(bitmap, bitmapFile);
                    subscriber.onNext(bitmapFile);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                    Log.e(TAG, "createFile", e);
                }
            }
        });
    }

    private static File obtainNewBitmap(Context context) throws IOException {
        File directory = IOUtil.getCacheDirectory(context);
        String cacheDirectoryPath = directory.getAbsolutePath();
        return IOUtil.newUniqueTempFile(cacheDirectoryPath, "jpg");
    }

}
