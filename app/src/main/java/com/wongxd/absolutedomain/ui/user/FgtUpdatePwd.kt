package com.wongxd.absolutedomain.ui.user

import android.graphics.Color
import android.os.Bundle
import cn.bmob.v3.BmobUser
import cn.bmob.v3.listener.UpdateListener
import com.github.wongxd.core_lib.CoreApp
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.core_lib.util.SystemUtils
import com.github.wongxd.core_lib.util.TU
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.wongxd.absolutedomain.R
import kotlinx.android.synthetic.main.fgt_update_pwd.*

/**
 * Created by wongxd on 2018/1/22.
 */

class FgtUpdatePwd : BaseBackFragment() {
    override fun getLayoutRes(): Int = R.layout.fgt_update_pwd


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        initTopBar(topbar_update_pwd,"修改密码")
        btn_change_update_pwd.setOnClickListener { doChangePwd() }
    }

    private fun doChangePwd() {
        val old = et_old_pwd_update_pwd.editableText.toString()
        val new = et_new_pwd_update_pwd.editableText.toString()

        val confNew = etconfirm_new_pwd_aty_update_pwd.editableText.toString()

        if (SystemUtils.isHadEmptyText(old, new, confNew)) {
            TU.cT("请完整输入本页信息")
            return
        }

        if (new != confNew) {
            TU.cT("两次新密码输入不一致")
            return
        }

        val pDialog = SweetAlertDialog(_mActivity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.titleText = "修改密码中"
        pDialog.setCancelable(false)
        pDialog.show()

        BmobUser.updateCurrentUserPassword(_mActivity, old, new, object : UpdateListener() {
            override fun onSuccess() {
                pDialog.titleText = "修改密码成功，请重新登录"
                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                pDialog.setConfirmClickListener {
                    pDialog.dismissWithAnimation()
                    BmobUser.logOut(_mActivity)
                    CoreApp.user = null
                    pop()
                }

            }

            override fun onFailure(p0: Int, p1: String?) {
                pDialog.contentText = p1
                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                pDialog.setConfirmClickListener {
                    pDialog.dismissWithAnimation()
                }
            }
        })
    }
}