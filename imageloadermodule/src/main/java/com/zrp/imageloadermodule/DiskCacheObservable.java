package com.zrp.imageloadermodule;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zeng on 2018/2/5.
 */

public class DiskCacheObservable extends CacheObservable {
    private DiskLruCache diskLruCache;
    private Context context;
    private static final int MAX_SIZE = 20 * 1024 * 1024;

    public DiskCacheObservable(Context context) {
        this.context = context;
        initDiskLruCache();
    }

    @Override
    public Image getDataFromCache(String url) {
        return new Image(url, getDataFromDiskLruCache(url));
    }

    @Override
    public void putDataToCache(final Image image) {
        //放在io线程
        Observable.just(image)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image.getBitmap() != null;
                    }
                }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        putDataToDiskCache(image);
                    }
                });

    }

    private void putDataToDiskCache(Image image) {
        String key = MD5Utils.toMD5(image.getUrl());
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (persistDataDisk(image, outputStream)) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean persistDataDisk(Image image, OutputStream outputStream) {
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        image.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void initDiskLruCache() {
        /**
         * 1）缓存使用的路径。可以指定SD卡上的一个路径也可以指定app的cache目录

         2）当前应用程序的版本号。该参数的意义是当app的版本升级时，旧有版本号对应的缓存内容会被清除

         3）1个key对应几个缓存内容，一般传1

         4）缓存使用的磁盘空间大小
         */
        try {
            diskLruCache = DiskLruCache.open(DiskCacheUtil.getDiskCache(context), AppUtil.getVersionCode(context), 1, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getDataFromDiskLruCache(String url) {
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        DiskLruCache.Snapshot snapshot = null;
        Bitmap bitmap = null;

        String key = MD5Utils.toMD5(url);
        try {
            snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
