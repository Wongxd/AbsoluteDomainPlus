package com.wongxd.absolutedomain.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Xiamin on 2017/2/12.
 */

public class SystemUtils {
    private static final String TAG = "SystemUtils";

    private SystemUtils() {

    }

    public static void share(Context context, String text, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void copyText(Context context, String text) {
        ClipboardManager c = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        c.setText(text);
    }

    public static void openUrlByBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    /**
     * 判断当前应用是否在后台
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {  
                /* 
                BACKGROUND=400 EMPTY=500 FOREGROUND=100 
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200 
                 */
//                Log.i(context.getPackageName(), "此appimportace ="
//                        + appProcess.importance
//                        + ",context.getClass().getName()="
//                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    Log.i(context.getPackageName(), "处于后台"
//                            + appProcess.processName);
//                    ToastUtil.cT(context, context.getPackageName() + "--" + appProcess.processName + "处于后台");
                    return true;
                } else {
//                    Log.i(context.getPackageName(), "处于前台"
//                            + appProcess.processName);
//                    ToastUtil.cT(context, context.getPackageName() + "--" + appProcess.processName + "处于前台");
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 获取状态栏高度
     *
     * @param res
     * @return
     */
    public int getStatusBarHeight(Resources res) {
        int result = 0;
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 跳转到指定activity 并且清空任务栈
     *
     * @param thisActivity
     * @param clazz
     */
    public static void cleanTask2Activity(Activity thisActivity, Class<?> clazz) {
        Intent intent = new Intent(thisActivity, clazz)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        thisActivity.startActivity(intent);
    }

    /**
     * 判断是否有字符串 为 empty
     *
     * @param txts
     * @return
     */
    public static boolean isHadEmptyText(String... txts) {
        for (String s : txts) {
            if (TextUtils.isEmpty(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 遍历对象 并自动转换 其属性为String（不安全）
     *
     * @param e
     * @return map 对象属性 值 的 map
     */
    public static Map<String, String> foreachObject(Object e) {
        Map<String, String> map = null;
        try {
            map = new HashMap<>();
            Class cls = e.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                map.put(f.getName(), (String) f.get(e));
                Log.e(TAG, "属性名:" + f.getName() + "- 属性值:" + f.get(e));
            }
        } catch (SecurityException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        return map;
    }


    /**
     * 遍历对象 自动判断对象 （安全）
     *
     * @param obj
     * @return map 对象属性 值 的 map
     */
    public static Map<String, String> reflect(Object obj) {
        if (obj == null)
            return null;

        Map<String, String> map = new HashMap<>();

        Field[] fields = obj.getClass().getDeclaredFields();
        String[] types1 = {"int", "java.lang.String", "boolean", "char", "float", "double", "long", "short", "byte"};
        String[] types2 = {"Integer", "java.lang.String", "java.lang.Boolean", "java.lang.Character", "java.lang.Float", "java.lang.Double", "java.lang.Long", "java.lang.Short", "java.lang.Byte"};
        for (int j = 0; j < fields.length; j++) {
            Field f = fields[j];
            f.setAccessible(true);
            // 字段名   fields[j].getName()

            // 字段值
            for (int i = 0; i < types1.length; i++) {
                if (f.getType().getName().equalsIgnoreCase(types1[i])
                        || f.getType().getName().equalsIgnoreCase(types2[i])) {
                    try {
                        map.put(f.getName(), String.valueOf(f.get(obj)));
                        Log.e(TAG, "属性名 " + f.getName() + " : " + "属性值 " + String.valueOf(f.get(obj)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return map;
    }


    /**
     * 设置添加屏幕的背景透明度
     */
    public static void backgroundAlpha(Activity aty, float bgAlpha) {
        WindowManager.LayoutParams lp = aty.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        aty.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        aty.getWindow().setAttributes(lp);
        if (bgAlpha == 1)
            aty.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }


    /**
     * 从一个apk文件去获取该文件的版本信息
     *
     * @param context         本应用程序上下文
     * @param archiveFilePath APK文件的路径。如：/sdcard/download/XX.apk
     * @return
     */
    public static int getVersionCodeFromApk(Context context, String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        String version = packInfo.versionName;
        int code = packInfo.versionCode;
        return code;
    }


    /**
     * 获取 versionCode
     *
     * @param ctx 本应用程序上下文
     * @return
     */
    public int getVersionCode(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int packageCode = packageInfo.versionCode;
            return packageCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    /**
     * 获取设备唯一ID
     *
     * @param context
     * @return
     */
    public static String getDeviceUniqID(Context context) {
        String unique_id = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            unique_id = tm.getDeviceId();
        }
        if (TextUtils.isEmpty(unique_id)) {
            unique_id = android.os.Build.SERIAL;
        }
        return unique_id;
    }


}