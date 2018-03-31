package com.zrp.imageloadermodule;

import android.content.Context;
import android.widget.ImageView;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by zeng on 2018/2/5.
 */

public class ImageLoader {
    private static ImageLoader singleton;
    private String url;
    private RequestCreate requestCreate;

    private ImageLoader(Builder builder) {
        requestCreate = new RequestCreate(builder.context);
    }

    public static ImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (ImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public ImageLoader load(String url) {
        this.url = url;
        return singleton;
    }

    public void into(final ImageView imageView) {
        Observable.concat(
                requestCreate.getImageFromMemory(url),
                requestCreate.getImageFromDisk(url),
                requestCreate.getImageFromNetWork(url))
                .firstElement()
                .filter(new Predicate<Image>() {
                    @Override
                    public boolean test(Image image) throws Exception {
                        return image.getBitmap() != null;
                    }
                })
                .subscribe(new Consumer<Image>() {
                    @Override
                    public void accept(Image image) throws Exception {
                        imageView.setImageBitmap(image.getBitmap());
                    }
                });
    }

    public static class Builder {
        private Context context;
        private ImageView imageView;

        public Builder(Context context) {
            this.context = context;
        }

        public ImageLoader build() {
            return new ImageLoader(this);
        }

    }
}
