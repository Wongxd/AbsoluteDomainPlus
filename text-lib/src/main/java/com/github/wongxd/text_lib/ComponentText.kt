package com.github.wongxd.text_lib

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.github.wongxd.core_lib.ComponentTextAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.text_lib.text.FgtText
import com.github.wongxd.text_lib.text.FgtTextFavorite

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentText : WComponent("cText") {

    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentTextAction.Into -> {
                val fgt = FgtText.newInstance()
                val result = CCResult.success().addData("fgt", fgt)
                CC.sendCCResult(cc.callId, result)
            }

            ComponentTextAction.GetIntoClass -> {
                val cls = FgtText::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }

            ComponentTextAction.GetFavoritClass -> {
                val cls = FgtTextFavorite::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }

            else -> {
                TU.cT("cText 未找到对应的操作")
            }
        }
    }


}