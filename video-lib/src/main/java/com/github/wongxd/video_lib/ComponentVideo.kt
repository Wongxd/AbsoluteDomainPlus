package com.github.wongxd.video_lib

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.github.wongxd.core_lib.ComponentVideoAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.video_lib.video.FgtVideo
import com.github.wongxd.video_lib.video.FgtVideoFavorite

/**
 * Created by wongxd on 2018/3/2.
 */
class ComponentVideo : WComponent("cVideo") {

    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentVideoAction.Into -> {
                val fgt = FgtVideo.newInstance()
                val result = CCResult.success().addData("fgt", fgt)
                CC.sendCCResult(cc.callId, result)
            }

            ComponentVideoAction.GetIntoClass -> {
                val cls = FgtVideo::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }

            ComponentVideoAction.GetFavoritClass -> {
                val cls = FgtVideoFavorite::class.java
                val result = CCResult.success().addData("cls", cls)
                CC.sendCCResult(cc.callId, result)
            }

            else -> {
                TU.cT("cVideo 未找到对应的操作")
            }
        }
    }
}