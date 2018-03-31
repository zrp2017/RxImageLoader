package com.zrp.imageloadermodule;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by zeng on 2018/2/5.
 */

public class RequestCreate {
    private MemoryCacheObservable memoryCacheObservable;
    private DiskCacheObservable diskCacheObservable;
    private NetWorkCacheObservable netWorkCacheObservable;

    public RequestCreate(Context context) {
        this.memoryCacheObservable = new MemoryCacheObservable();
        this.diskCacheObservable = new DiskCacheObservable(context);
        this.netWorkCacheObservable = new NetWorkCacheObservable();
    }

    public Observable<Image> getImageFromMemory(String url) {
        return memoryCacheObservable.getImage(url);
    }

    public Observable<Image> getImageFromDisk(String url) {
        return diskCacheObservable.getImage(url).filter(new Predicate<Image>() {
            @Override
            public boolean test(Image image) throws Exception {
                return image.getBitmap() != null;
            }
        }).doOnNext(new Consumer<Image>() {
            @Override
            public void accept(Image image) throws Exception {
                memoryCacheObservable.putDataToCache(image);
            }
        });
    }

    public Observable<Image> getImageFromNetWork(String url) {
        return netWorkCacheObservable.getImage(url)
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image.getBitmap() != null;
                    }
                })
                .doOnNext(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        memoryCacheObservable.putDataToCache(image);
                        diskCacheObservable.putDataToCache(image);
                    }
                });
    }
}
