package com.github.wongxd.core_lib.fragmenaction

import android.os.Bundle
import com.github.wongxd.core_lib.base.exception.AppManager
import com.github.wongxd.core_lib.util.ConfigUtils
import me.yokeyword.fragmentation.SupportActivity

/**
 * Created by wxd1 on 2017/7/13.
 */
open class BaseThemeActivity : SupportActivity() {
    fun setTheme() {
        val theme = ConfigUtils.getTheme(this)
        this.setTheme(theme)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        AppManager.getAppManager().addActivity(this)
    }

    override fun onDestroy() {
        AppManager.getAppManager().removeActivity(this)
        super.onDestroy()
    }
}