package com.github.wongxd.core_lib

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.IDynamicComponent

/**
 * Created by wongxd on 2018/3/2.
 */

abstract class WComponent(val componentName: String) : IDynamicComponent {

    final override fun onCall(cc: CC?): Boolean {
        cc?.let { doLogic(it) }
        return false
    }

    final override fun getName(): String = componentName


    abstract fun doLogic(cc: CC)


}


class ComponentAppAction {
    companion object {
        val FgtMainStartNewFgt = "fgtMain.startNewFgt"

        val IntoFgtDownload = "FgtDownload.newInstance(TaskType)"

        val IntoFgtFavorite = "FgtFavorite"
    }
}


class ComponentImgAction {
    companion object {

        val Into = "intoImg"
    }

}


class ComponentVideoAction {
    companion object {

        val Into = "intoVideo"
    }

}


class ComponentTextAction {
    companion object {

        val Into = "intoText"
    }

}