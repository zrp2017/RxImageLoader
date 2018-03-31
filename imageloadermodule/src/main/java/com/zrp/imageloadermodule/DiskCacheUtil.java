package com.zrp.imageloadermodule;

import java.io.File;

import android.content.Context;

public class DiskCacheUtil{
	

   /**
    * 得到缓存目录 /storage/emulated/0/Android/data/you.xiaochen.imageloader/cache/image
    * @param context
    * @return
    */
	public static File getDiskCache(Context context) {
		return new File(context.getExternalCacheDir().getAbsolutePath(),"image");
	}
}
