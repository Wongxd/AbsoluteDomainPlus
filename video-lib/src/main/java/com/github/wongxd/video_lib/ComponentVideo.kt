package com.github.wongxd.video_lib

import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentVideoAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentVideo : WComponent("cVideo") {
    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentVideoAction.Into -> {

            }


            else -> {
                TU.cT("未找到对应的操作")
            }
        }
    }
}