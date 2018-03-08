package com.wongxd.absolutedomain

import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentAppAction
import com.github.wongxd.core_lib.WComponent
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.util.TU
import com.wongxd.absolutedomain.event.CCEvent
import com.wongxd.absolutedomain.ui.FgtDownload
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.EventBus

/**
 * Created by wongxd on 2018/3/8.
 */
class ComponentApp : WComponent("cApp") {
    override fun doLogic(cc: CC) {
        when (cc.actionName) {
            ComponentAppAction.FgtMainStartNewFgt -> {
                cc.getParamItem<SupportFragment>("fgt")?.let {
                    EventBus.getDefault().post(CCEvent(it))
                }
            }


            ComponentAppAction.IntoFgtDownload -> {
                cc.getParamItem<TaskType>("type")?.let {
                    EventBus.getDefault().post(CCEvent(FgtDownload.newInstance(it)))
                }
            }

            ComponentAppAction.IntoFgtFavorite -> {
                cc.getParamItem<SupportFragment>("fgt")?.let {
                    EventBus.getDefault().post(CCEvent(it))
                }
            }


            else -> {
                TU.cT("未找到对应的操作")
            }
        }
    }
}