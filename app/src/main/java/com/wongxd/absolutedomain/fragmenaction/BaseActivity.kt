package com.wongxd.absolutedomain.fragmenaction

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.qmuiteam.qmui.widget.QMUITopBar
import com.wongxd.absolutedomain.base.CircularAnim
import com.wongxd.absolutedomain.base.exception.AppManager


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


    companion object {
        enum class PermissionType(val permission: String, val permissionName: String) {
            READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, "读取手机状态"),
            CAMERA(Manifest.permission.CAMERA, "拍照"),
            READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储卡"),
            WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储卡"),
            CALL_PHONE(Manifest.permission.CALL_PHONE, "拨打电话"),
            FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置"),
            COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, "获取大致位置");
        }
    }


    fun startAty(aty: BaseActivity, v: View, intent: Intent) {
        CircularAnim.fullActivity(aty, v)
                .go { startActivity(intent) }
    }


    override fun finish() {
        CircularAnim.fullActivity(this, window.decorView)
                .go { super.finish() }
    }


}
