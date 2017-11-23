package com.techjumper.lib2.utils;

import android.graphics.Bitmap;
import android.net.Uri;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.techjumper.corelib.utils.Utils;

import java.io.File;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 16/2/19
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class PicassoHelper {

    private static Picasso mPicasso = new Picasso.Builder(Utils.appContext)
            .downloader(new OkHttp3Downloader(OkHttpHelper.getDefault()))
            .build();


    public static RequestCreator load(String path) {
        RequestCreator creator = mPicasso.load(path);
        return createDefault(creator);
    }


    public static RequestCreator load(File file) {
        RequestCreator creator = mPicasso.load(file);
        return createDefault(creator);
    }

    public static RequestCreator load(int resId) {
        RequestCreator creator = mPicasso.load(resId);
        return createDefault(creator);
    }

    public static RequestCreator load(Uri uri) {
        RequestCreator creator = mPicasso.load(uri);
        return createDefault(creator);
    }

    public static Picasso getDefault() {
        return mPicasso;
    }

    private static RequestCreator createDefault(RequestCreator creator) {
        return creator.config(Bitmap.Config.RGB_565);
    }
}
