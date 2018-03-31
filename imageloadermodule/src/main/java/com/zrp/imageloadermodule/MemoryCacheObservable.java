package com.zrp.imageloadermodule;

import android.graphics.Bitmap;
import android.util.LruCache;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zeng on 2018/2/5.
 */

public class MemoryCacheObservable extends CacheObservable {
    int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);//kb
    private LruCache<String,Bitmap> lruCache = new LruCache<String, Bitmap>(maxMemory / 8){
        @Override
        protected int sizeOf(String key, Bitmap value) {
            //重写sizeOf方法，返回图片的占用字节数而不是图片的个数，每次添加图片是会被调用
            return value.getRowBytes() * value.getHeight() / 1024;//kb
        }
    };
    @Override
    public Image getDataFromCache(String url) {
        LogUtil.d("getDataFromMemoryCache");
        return new Image(url,lruCache.get(MD5Utils.toMD5(url)));
    }

    @Override
    public void putDataToCache(final Image image) {
        //放在io线程
        Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
               lruCache.put(MD5Utils.toMD5(image.getUrl()),image.getBitmap());
            }
        }).filter(new Predicate<Image>() {
            @Override
            public boolean test(Image image) throws Exception {
                return image.getBitmap() != null;
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
