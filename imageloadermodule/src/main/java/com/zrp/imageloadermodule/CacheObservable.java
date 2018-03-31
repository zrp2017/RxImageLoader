package com.zrp.imageloadermodule;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zeng on 2018/2/5.
 */

public abstract class CacheObservable {
    public Observable<Image> getImage(final String url){
        return Observable.create(new ObservableOnSubscribe<Image>() {
            @Override
            public void subscribe(ObservableEmitter<Image> e) throws Exception {
                if (!e.isDisposed()) {
                    Image image = getDataFromCache(url);
                    if (image.getBitmap() != null) {
                        e.onNext(image);
                    }
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
    public abstract Image getDataFromCache(String url);
    public abstract void putDataToCache(Image image);
}
