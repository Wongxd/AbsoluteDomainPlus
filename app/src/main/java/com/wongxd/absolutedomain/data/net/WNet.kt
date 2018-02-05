package com.wongxd.absolutedomain.data.net

import android.content.Intent
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.orhanobut.logger.Logger
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.base.utils.utilcode.util.DeviceUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.SPUtils
import com.wongxd.absolutedomain.receiver.MyPushReceiver
import com.wongxd.absolutedomain.util.TU
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

/**
 * Created by wongxd on 2018/1/5.
 */
class WNet {

    companion object {


        fun checkVersion() {
            doAsync {
                val text = URL("https://raw.githubusercontent.com/Wongxd/AbsoluteDomainPlus/master/versionHolder/version").readText()
                //{version:"1.0.0",data:{"title":"版本更新","content":"内容描述","extras":{"url":"http://wongxd.github.io"}}}
                val json = JSONObject(text)
                val pkgVersionName = App.instance.packageManager.getPackageInfo(App.instance.packageName, 0).versionName
                        ?: "0.0.1"
                val version = json.optString("version") ?: pkgVersionName
                Logger.e("$version---appversion--$pkgVersionName")
                if (version == pkgVersionName) {
                    uiThread { TU.cT("已是最新版本") }
                    return@doAsync
                }
                val data = json.optString("data")
                val intent = Intent(MyPushReceiver.NEW_VERSION_ACTION)
                intent.putExtra("info", data)
                App.instance.sendBroadcast(intent)
            }
        }


        fun getNeHanList(url: String, successed: (json: String) -> Unit, failed: (info: String) -> Unit) {

            val screen_width = "1080"
            val am_longitude = "110"
            val am_latitude = "120"
            val iid = DeviceUtils.getAndroidID()
            Logger.e("设备id---$iid")

            val longTime = System.currentTimeMillis()

            OkGo.get<String>(url)
                    .headers("webp", "1")
                    .headers("essence", "1")
                    .headers("content_type", "-104")
                    .headers("message_cursor", "-1")
                    .headers("am_longitude", am_longitude)
                    .headers("am_latitude", am_latitude)
                    .headers("am_city", "北京市")
                    .headers("am_loc_time", longTime.toString())
                    .headers("count", "10")
                    .headers("min_time", getLastNeiHanTime())
                    .headers("screen_width", screen_width)
                    .headers("double_col_mode", "0")
                    .headers("iid", "326135942187625")
                    .headers("ac", "wifi")
                    .headers("channel", "360")
                    .headers("aid", "7")
                    .headers("app_name", "joke_essay")
                    .headers("version_code", "612")
                    .headers("version_name", "6.1.2")
                    .headers("device_platform", "android")
                    .headers("ssmix", "a")
                    .headers("device_type", "hongmi")
                    .headers("device_brand", "xiaomi")
                    .headers("os_api", "25")
                    .headers("os_version", "7.1.1")
                    .headers("uuid", "326135942187625")
                    .headers("openudid", "3dg6s95rhg2a3dg5")
                    .headers("resolution", "1080*1920")
                    .headers("dpi", "620")
                    .headers("update_version_code", "6120")

//            uuid=326135942187625&openudid=3dg6s95rhg2a3dg5&manifest_version_code=612&resolution=1450*2800&dpi=620&update_version_code=6120
                    .execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>?) {
                            response?.body()?.let {
                                lastNeihanTime = (longTime / 1000).toString()
                                SPUtils.getInstance().put("lastNeihanTime", lastNeihanTime)
                                successed.invoke(it)
                            }
                                    ?: let { onError(Response()) }
                        }

                        override fun onError(response: Response<String>?) {
                            super.onError(response)
                            failed.invoke("获取内涵视频出错  " + response?.message())
                        }
                    })
        }

        var lastNeihanTime = ""
        private fun getLastNeiHanTime(): String {
            return if (lastNeihanTime.isBlank()) {
                SPUtils.getInstance().getString("lastNeihanTime", (System.currentTimeMillis() / 1000).toString())
            } else
                lastNeihanTime
        }


        fun getVideoList(url: String, vararg header: Pair<String, String>, successed: (json: String) -> Unit, failed: (info: String) -> Unit) {

            val request = OkGo.get<String>(url)

            for (p in header) {
                request.headers(p.first, p.second)
            }

            request.execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.body()?.let {
                        successed.invoke(it)
                    }
                            ?: let { onError(Response()) }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    failed.invoke("获取视频出错  " + response?.message())
                }
            })
        }


        fun getTextList(url: String, vararg header: Pair<String, String>, successed: (json: String) -> Unit, failed: (info: String) -> Unit) {

            val request = OkGo.get<String>(url)

            for (p in header) {
                request.headers(p.first, p.second)
            }

            request.execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.body()?.let {
                        successed.invoke(it)
                    }
                            ?: let { onError(Response()) }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    failed.invoke("获取文字出错  " + response?.message())
                }
            })
        }


        fun getString(url: String, vararg header: Pair<String, String>, successed: (json: String) -> Unit, failed: (info: String) -> Unit) {

            val request = OkGo.get<String>(url)

            for (p in header) {
                request.headers(p.first, p.second)
            }

            request.headers("User-Agent",
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Mobile Safari/537.36")
            request.execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    response?.body()?.let {
                        successed.invoke(it)
                    }
                            ?: let { onError(Response()) }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    failed.invoke("出错  " + response?.message())
                }
            })
        }
    }

}