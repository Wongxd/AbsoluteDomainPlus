package com.github.wongxd.core_lib.fragmenaction

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github.wongxd.core_lib.base.CircularAnim
import com.github.wongxd.core_lib.base.exception.AppManager
import com.qmuiteam.qmui.widget.QMUITopBar


open class BaseActivity : BaseThemeActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.getAppManager().addActivity(this)
    }


    /**
     *格式化topBar
     */
    protected fun initTopBar(mTopBar: QMUITopBar, title: String, isHadReturn: Boolean = true) {
        mTopBar.setTitle(title)
        if (!isHadReturn) return
        mTopBar.addLeftBackImageButton().setOnClickListener(View.OnClickListener {
            finish()
        })
    }





    fun startAty(aty: BaseActivity, v: View=window.decorView, intent: Intent) {
        CircularAnim.fullActivity(aty, v)
                .go { startActivity(intent) }
    }


    override fun finish() {
        CircularAnim.fullActivity(this, window.decorView)
                .go { super.finish() }
    }


}
