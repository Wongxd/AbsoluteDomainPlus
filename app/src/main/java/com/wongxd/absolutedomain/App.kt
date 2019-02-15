package com.wongxd.absolutedomain

import android.content.Context
import android.support.multidex.MultiDex
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.github.wongxd.core_lib.CoreApp
import com.github.wongxd.core_lib.R
import com.github.wongxd.core_lib.base.CircularAnim
import com.luomi.lm.ad.DRAgent
import com.luomi.lm.ad.LogUtil
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.tencent.bugly.crashreport.CrashReport
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import kotlin.properties.Delegates


/**
 * Created by wxd1 on 2017/7/10.
 */
class App : CoreApp() {
    companion object {
        var instance: App by Delegates.notNull()

        val BUGLY_APP_ID = "1c3475b0ab"
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun getPkgVersionName(): String = packageManager.getPackageInfo(App.instance.packageName, 0).versionName
            ?: "0.0.1"

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private fun getProcessName(pid: Int): String {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName: String = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim()
            }
            return processName;
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }
        return ""
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //bugly
        // 获取当前进程名
//        val processName = getProcessName(android.os.Process.myPid())
//        // 设置是否为上报进程
//        val strategy = CrashReport.UserStrategy(this)
//        strategy.isUploadProcess = processName.isBlank() || processName == packageName
//
//        CrashReport.initCrashReport(applicationContext, BUGLY_APP_ID, BuildConfig.LOG_DEBUG,strategy)

        CrashReport.initCrashReport(applicationContext, BUGLY_APP_ID, false)

        if (BuildConfig.LOG_DEBUG) {
            Logger.init().logLevel(LogLevel.FULL)
        } else {
//            CrashHandler.getInstance().init(this)
            Logger.init().logLevel(LogLevel.NONE)
        }


        CircularAnim.init(500, 600, R.drawable.gaki)


        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)

        //广告
//        if (BuildConfig.LOG_DEBUG)
        LogUtil.setENABLE_LOGCAT(true)
        DRAgent.getInstance().init(this.applicationContext, "65a3f31939037d2f2329fcf80a1069ca", true)

    }


}