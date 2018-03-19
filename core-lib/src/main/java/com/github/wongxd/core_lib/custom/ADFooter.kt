package com.github.wongxd.core_lib.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.wongxd.core_lib.R
import com.luomi.lm.ad.ADType
import com.luomi.lm.ad.DRAgent
import com.luomi.lm.ad.IAdSuccessBack
import com.luomi.lm.ad.LogUtil
import com.scwang.smartrefresh.layout.api.RefreshFooter
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle

/**
 * Created by wongxd on 2018/3/19.
 */
class ADFooter : View, RefreshFooter {
    override fun onLoadmoreReleased(layout: RefreshLayout?, footerHeight: Int, extendHeight: Int) {
    }

    override fun onPullReleasing(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {
    }

    override fun onPullingUp(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {
    }

    override fun setLoadmoreFinished(finished: Boolean): Boolean = true

    constructor (context: Context) : this(context, null)


    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        v = inflate(context, R.layout.item_ad_footer, null)
        flAD = v.findViewById(R.id.fl_ad_footer)
        showAds(flAD)
    }

    private lateinit var flAD: FrameLayout
    private lateinit var v: View


    private fun showAds(v: ViewGroup, adType: Int = ADType.BANNER) {
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

    override fun getSpinnerStyle(): SpinnerStyle = SpinnerStyle.Translate

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {

        return 0
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, extendHeight: Int) {
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun getView(): View = v

    override fun setPrimaryColors(vararg colors: Int) {
    }


    override fun onStartAnimator(layout: RefreshLayout, height: Int, extendHeight: Int) {
    }

    override fun onStateChanged(refreshLayout: RefreshLayout?, oldState: RefreshState?, newState: RefreshState?) {
    }


    override fun isSupportHorizontalDrag(): Boolean = false
}