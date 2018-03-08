package com.github.wongxd.text_lib

import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentTextAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentText : WComponent("cText") {

    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentTextAction.Into -> {

            }


            else -> {
                TU.cT("未找到对应的操作")
            }
        }
    }


}