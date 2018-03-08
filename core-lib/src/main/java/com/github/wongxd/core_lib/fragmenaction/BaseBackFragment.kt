package com.github.wongxd.core_lib.fragmenaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.wongxd.core_lib.base.kotin.extension.getPrimaryColor
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

}
