package com.wongxd.absolutedomain.base.aCache;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by wxd1 on 2017/4/6.
 */

public class AcacheUtil {

    public static final String imgCache = "img";
    public static final String StringCache = "string";
    public static final String JsonCache = "json";
    public static final String ObjCache = "object";


    /**
     * 获取acache实例
     *
     * @param c
     * @param cacheDirName
     * @return
     */
    public static ACache getDefault(Context c, String cacheDirName) {
        return ACache.get(getDiskCacheDir(c, cacheDirName));
    }


    /**
     * 依据名字获取特定缓存目录
     *
     * @param context
     * @param uniqueName 缓存目录名字
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        //如果sd卡存在并且没有被移除
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File file = new File(cachePath + File.separator + "ACache" + File.separator + uniqueName);
        if (!file.exists()) file.mkdirs();
        return file;
    }


}
