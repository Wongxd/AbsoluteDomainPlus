package com.wongxd.absolutedomain

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.wongxd.core_lib.IS_SHOW_AD
import com.github.wongxd.core_lib.base.kotin.permission.PermissionType
import com.github.wongxd.core_lib.base.kotin.permission.getPermission
import com.github.wongxd.core_lib.base.utils.utilcode.util.ScreenUtils
import com.github.wongxd.core_lib.util.SPUtils
import com.github.wongxd.core_lib.util.TU
import com.luomi.lm.ad.ADType
import com.luomi.lm.ad.DRAgent
import com.luomi.lm.ad.IAdSuccessBack
import com.luomi.lm.ad.LogUtil
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.aty_splash.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL

/**
 * Created by wongxd on 2018/1/3.
 *
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_splash)
        ScreenUtils.setFullScreen(this)
        if (SPUtils.get(key = IS_SHOW_AD, defaultObject = true) as Boolean) {
            initPermission()
        } else {
            intoAtyMain()
        }
    }

    fun initPermission() {
        getPermission(PermissionType.READ_PHONE_STATE, isGoSetting = false) { showAds() }
    }


    fun showAds() {
        LogUtil.setENABLE_LOGCAT(true)
        /**
         * this  上下文
         * adtype 广告类型（详情请看附录表）
         * true  针对开屏是否显示倒计时展示 针对banner是是否显示关闭按钮
         * IAdSuccessBack 广告展示回调接口
         */
        DRAgent.getInstance().getOpenView(applicationContext, ADType.FULL_SCREEN, true, object : IAdSuccessBack {

            override fun onError(result: String) {
                LogUtil.e("luomiAd", ">>>>>>广告展示失败:" + result)
                intoAtyMain()
            }

            override fun onClick(result: String) {
                LogUtil.e("luomiAd", ">>>>>广告被点击:" + result)
            }

            override fun OnSuccess(result: String) {
                LogUtil.e("luomiAd", ">>>广告展示成功:" + result)
                if (result == "7") {
                    intoAtyMain()
                }

            }

            override fun OnLoadAd(view: View) {
                LogUtil.e("luomiAd", ">>>>>>广告加载成功")
                fl_splash.addView(view)
            }
        })
    }

    private fun intoAtyMain() {

        doAsync {
            val text = URL("https://raw.githubusercontent.com/Wongxd/AbsoluteDomainPlus/master/versionHolder/version").readText()
            //{"is":false,version:"1.0.0",data:{"title":"版本更新","content":"内容描述","extras":{"url":"http://wongxd.github.io"}}}
            val json = JSONObject(text)
            val bool = json.optBoolean("is", false)

            uiThread {

                if (bool)
                    startActivity(Intent(this@SplashActivity, AtyMain::class.java))
                else
                    startActivity(Intent(this@SplashActivity, AtyMain::class.java))

                overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out)
                finish()
            }

        }


    }

}