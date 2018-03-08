package com.github.wongxd.core_lib.util

import android.app.Activity
import android.graphics.Color
import com.tapadoo.alerter.Alerter
import com.github.wongxd.core_lib.base.exception.AppManager

/**
 * Created by wongxd on 2018/1/9.
 */
class Tips {

    companion object {

        fun showErrorTips(aty: Activity = AppManager.getAppManager().currentActivity(), title: String = "", text: String) {
            Alerter.create(aty)
                    .setTitle(title)
                    .setText(text)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.RED)
                    .show()
        }


        fun showWarningTips(aty: Activity=AppManager.getAppManager().currentActivity(), title: String = "", text: String) {
            Alerter.create(aty)
                    .setTitle(title)
                    .setText(text)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.YELLOW)
                    .show()
        }

        fun showSuccessTips(aty: Activity=AppManager.getAppManager().currentActivity(), title: String = "", text: String) {
            Alerter.create(aty)
                    .setTitle(title)
                    .setText(text)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.parseColor("#388E3C"))
                    .show()
        }

    }
}