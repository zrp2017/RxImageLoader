package com.zrp.imageloadermodule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zeng on 2018/2/5.
 */

public class NetWorkCacheObservable extends CacheObservable {

    @Override
    public Image getDataFromCache(String url) {
        LogUtil.d("getDataFromNetCache");
        return new Image(url,downLoadImage(url));
    }

    @Override
    public void putDataToCache(Image image) {

    }
    private Bitmap downLoadImage(String url){
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }
}
