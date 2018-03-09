package com.wongxd.absolutedomain.ui.user

import android.os.Bundle
import cn.bmob.v3.BmobUser
import com.github.wongxd.core_lib.CoreApp
import com.github.wongxd.core_lib.IS_SHOW_ACTIVITY
import com.github.wongxd.core_lib.IS_SHOW_AD
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.core_lib.util.SPUtils
import com.github.wongxd.core_lib.util.TU
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.event.LogStateChangeEvent
import kotlinx.android.synthetic.main.fgt_user.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by wongxd on 2018/1/22.
 */
class FgtUser : BaseBackFragment() {
    override fun getLayoutRes(): Int = R.layout.fgt_user

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        initTopBar(topbar_user, "用户信息")

        CoreApp.user?.let {

            tv_user_name_aty_user_info.text = it.nickName
            btn_logout_aty_user_info.setOnClickListener { logOut() }

            ll_update_pwd_aty_user_info.setOnClickListener { start(FgtUpdatePwd()) }
        }
    }


    fun logOut() {
        BmobUser.logOut(activity)
        TU.cT("已经退出账户")
        CoreApp.user = null

        SPUtils.put(key = IS_SHOW_AD,`object` = true)
        SPUtils.put(key = IS_SHOW_ACTIVITY,`object` = true)

        EventBus.getDefault().post(LogStateChangeEvent())
        pop()
    }
}