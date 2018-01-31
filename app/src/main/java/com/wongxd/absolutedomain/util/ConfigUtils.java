package com.wongxd.absolutedomain.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.wongxd.absolutedomain.R;

import static android.content.Context.MODE_PRIVATE;

public class ConfigUtils {

    public static int getTheme(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt("theme", R.style.ThemePurple);
    }

    public static void saveTheme(Context context, int theme) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("theme", theme);
        edit.apply();
    }

    public static String getBanner(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString("banner", "");
    }

    public static void saveBanner(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("banner", url);
        edit.apply();
    }

    public static int getCurrentSpanCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getInt("span_count", 2);
    }

    public static void saveCurrentSpanCount(Context context, int spanCount) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("span_count", spanCount);
        edit.apply();
    }

    public static int getSpanCountConfig(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getInt("span_count_config", 3);
    }

    public static void saveSpanCountConfig(Context context, int spanCount) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("span_count_config", spanCount);
        edit.apply();
    }

    public static boolean getR(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getBoolean("r", true);
    }

    public static void saveR(Context context, boolean r) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("r", r);
        edit.apply();
    }

    public static long getOpenCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getLong("open_count", 0);
    }

    public static void saveOpenCount(Context context, long count) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong("open_count", count);
        edit.apply();
    }

    public static String getPin(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString("pin", "");
    }

    public static void savePin(Context context, String pin) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("pin", pin);
        edit.apply();
    }


    public static void setLock(Context context, int lock) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("lock", lock);
        edit.apply();
    }

    public static boolean isOpenFingerprint(Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getBoolean("fingerprint_open", false);
    }

    public static void openFingerprint(Context context, boolean open) {
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("fingerprint_open", open);
        edit.apply();
    }

}
