package com.github.wongxd.img_lib

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.github.wongxd.core_lib.ComponentImgAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.img_lib.img.FgtImg
import com.github.wongxd.img_lib.img.FgtTuFavorite

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentImg : WComponent("cImg") {
    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentImgAction.Into -> {
                val fgt = FgtImg.newInstance()
                val result = CCResult.success().addData("fgt", fgt)
                CC.sendCCResult(cc.callId, result)
            }


            ComponentImgAction.GetIntoClass -> {
                val cls = FgtImg::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }


            ComponentImgAction.GetFavoritClass -> {
                val cls = FgtTuFavorite::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }


            else -> {
                TU.cT("cImg 未找到对应的操作")
            }
        }
    }

}