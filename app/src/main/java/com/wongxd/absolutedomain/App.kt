package com.wongxd.absolutedomain

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import cn.bmob.v3.Bmob
import cn.jpush.android.api.JPushInterface
import com.luomi.lm.ad.DRAgent
import com.luomi.lm.ad.LogUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.orhanobut.logger.LogLevel
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.wongxd.absolutedomain.base.CircularAnim
import com.wongxd.absolutedomain.base.exception.CrashHandler
import com.wongxd.absolutedomain.base.utils.subutil.util.Utils
import com.wongxd.absolutedomain.custom.storeHouseHeader.StoreHouseHeader
import com.wongxd.absolutedomain.data.bean.UserBean
import com.wongxd.absolutedomain.util.TU
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


/**
 * Created by wxd1 on 2017/7/10.
 */
class App : Application() {
    companion object {
        var instance: App by Delegates.notNull()
        //这里我就不写管理类了,捡个懒,直接在 Application 中管理单例 Okhttp
        private var mOkHttpClient: OkHttpClient by Delegates.notNull()
        var user: UserBean? = null
        val BMOB_ID: String = "33c3293abda15ed00bbb74776573e9be"
        val QQ_APP_KEY: String = "6cecc44d7a8f5bc1eb41f4f8a5743a73"
        val QQ_APP_ID = "101447420"
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this as Context)
    }


    override fun onCreate() {
        super.onCreate()
        instance = this


        Utils.init(this)

        TU.register(this)



        initOkGo()

        if (BuildConfig.LOG_DEBUG) {
            Logger.init().logLevel(LogLevel.FULL)
        } else {
            CrashHandler.getInstance().init(this)
            Logger.init().logLevel(LogLevel.NONE)
        }

        //smartRefresh
        SmartRefreshLayout.setDefaultRefreshHeaderCreater({ context, layout ->
            val header = StoreHouseHeader(context)
            header.initWithString("ABSOLUTE-DOMAIN")
            header
        })

        SmartRefreshLayout.setDefaultRefreshFooterCreater({ context, layout -> ClassicsFooter(context) })


        //广告
//        if (BuildConfig.LOG_DEBUG)
              LogUtil.setENABLE_LOGCAT(true)
        DRAgent.getInstance().init(this.applicationContext, "65a3f31939037d2f2329fcf80a1069ca", true)

        CircularAnim.init(500, 600, R.drawable.gaki)

        Bmob.initialize(this, App.BMOB_ID)

        JPushInterface.setDebugMode(true)
        JPushInterface.init(this)

    }


    fun getOkHttpClient() = mOkHttpClient


    fun initOkGo() {

        val builder = okhttp3.OkHttpClient.Builder()
        //全局的读取超时时间
        builder.readTimeout(10000L, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(10000L, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(10000L, TimeUnit.MILLISECONDS);

        mOkHttpClient = builder.build()

        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
//        val headers = HttpHeaders()
//        headers.put("token", "commonHeaderValue1")    //header不支持中文，不允许有特殊字符
//        val params = HttpParams()
//        params.put("commonParamsKey2", "这里支持中文参数")         //param支持中文,直接传,不要自己编码
//-------------------------------------------------------------------------------------//

        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(mOkHttpClient)               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(6 * 60 * 60 * 1000L)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(0)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params)                       //全局公共参数
    }


}