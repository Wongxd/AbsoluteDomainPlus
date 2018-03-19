package com.github.wongxd.core_lib.fragmenaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.wongxd.core_lib.base.kotin.extension.getPrimaryColor
import com.luomi.lm.ad.ADType
import com.luomi.lm.ad.DRAgent
import com.luomi.lm.ad.IAdSuccessBack
import com.luomi.lm.ad.LogUtil
import com.qmuiteam.qmui.widget.QMUITopBar
import me.yokeyword.fragmentation_swipeback.SwipeBackFragment

/**
 * Created by wongxd on 2017/12/30.
 * Copyright © 2017年 no. All rights reserved.
 */

abstract class BaseBackFragment : SwipeBackFragment() {

    protected abstract fun getLayoutRes(): Int

    protected lateinit var mRootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(getLayoutRes(), container, false)!!
        return attachToSwipeBack(mRootView)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setParallaxOffset(0.5f)
//        Logger.e("新的BaseBackFragment---"+this.toString())
    }

    /**
     *格式化topBar
     */
    @SuppressLint("ResourceType")
    protected fun initTopBar(mTopBar: QMUITopBar, title: String, isHadReturn: Boolean = true) {
        mTopBar.setBackgroundColor(activity!!.getPrimaryColor())
        mTopBar.setTitle(title)
        if (!isHadReturn) return
        mTopBar.addLeftBackImageButton().setOnClickListener(View.OnClickListener {
            _mActivity.onBackPressed()
        })
    }

    companion object {
        interface netCallback {
            fun successed(s: String)

            fun failure(e: Throwable)
        }

    }


    protected fun startRequest(url: String, vararg params: Pair<String, String>, callback: netCallback, msg: String = "", isShowDialog: Boolean = false) {
        val map = HashMap<String, String>()
        map.putAll(params)


    }


    override fun onDestroyView() {

        super.onDestroyView()
    }

    protected fun showAds(v: ViewGroup, adType: Int = ADType.MESSAGE_IMGS) {
        LogUtil.setENABLE_LOGCAT(true)
        /**
         * this  上下文
         * adtype 广告类型（详情请看附录表）
         * true  针对开屏是否显示倒计时展示 针对banner是是否显示关闭按钮
         * IAdSuccessBack 广告展示回调接口
         */
        DRAgent.getInstance().getOpenView(context, adType, false, object : IAdSuccessBack {

            override fun onError(result: String) {
                LogUtil.e("luomiAd", ">>>>>>广告展示失败:" + result)
            }

            override fun onClick(result: String) {
                LogUtil.e("luomiAd", ">>>>>广告被点击:" + result)
            }

            override fun OnSuccess(result: String) {
                LogUtil.e("luomiAd", ">>>广告展示成功:" + result)
                if (result == "7") {

                }

            }

            override fun OnLoadAd(view: View) {
                LogUtil.e("luomiAd", ">>>>>>广告加载成功")
                v.addView(view)
            }
        })
    }
}
