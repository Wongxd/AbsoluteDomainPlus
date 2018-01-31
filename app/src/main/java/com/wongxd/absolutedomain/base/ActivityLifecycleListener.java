package com.wongxd.absolutedomain.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * 检测 app 是否在后台
 * 然后在app 里面的 Application onCreate()方法注册即可：
 * registerActivityLifecycleCallbacks(new ActivityLifecycleListener());
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private int refCount = 0;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        if (refCount == 0) {
//            ToastUtil.cT(activity.getApplicationContext(), "正在后台运行");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}