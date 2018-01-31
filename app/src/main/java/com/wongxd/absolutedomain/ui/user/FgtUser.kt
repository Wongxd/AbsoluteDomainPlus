package com.wongxd.absolutedomain.ui.user

import android.os.Bundle
import cn.bmob.v3.BmobUser
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.event.LogStateChangeEvent
import com.wongxd.absolutedomain.fragmenaction.BaseBackFragment
import com.wongxd.absolutedomain.util.TU
import kotlinx.android.synthetic.main.fgt_user.*
import me.yokeyword.eventbusactivityscope.EventBusActivityScope

/**
 * Created by wongxd on 2018/1/22.
 */
class FgtUser : BaseBackFragment() {
    override fun getLayoutRes(): Int = R.layout.fgt_user

    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        initTopBar(topbar_user, "用户信息")

        App.user?.let {

            tv_user_name_aty_user_info.text = it.nickName
            btn_logout_aty_user_info.setOnClickListener { logOut() }

            ll_update_pwd_aty_user_info.setOnClickListener { start(FgtUpdatePwd()) }
        }
    }


    fun logOut() {
        BmobUser.logOut(activity)
        TU.cT("已经退出账户")
        App.user = null

        EventBusActivityScope.getDefault(_mActivity).post(LogStateChangeEvent())
        pop()
    }
}