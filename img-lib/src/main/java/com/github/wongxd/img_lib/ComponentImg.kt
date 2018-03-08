package com.github.wongxd.img_lib

import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentImgAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.img_lib.img.FgtImg

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentImg : WComponent("cImg") {
    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentImgAction.Into -> {
                val fgt = FgtImg.newInstance()
            }


            else -> {
                TU.cT("未找到对应的操作")
            }
        }
    }

}