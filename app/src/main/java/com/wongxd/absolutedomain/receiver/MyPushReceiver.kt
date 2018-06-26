package com.wongxd.absolutedomain.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import cn.jpush.android.api.JPushInterface
import com.orhanobut.logger.Logger
import com.wongxd.absolutedomain.AtyMain
import org.json.JSONObject


/**
 * Created by wongxd on 2018/1/27.
 */
class MyPushReceiver : BroadcastReceiver() {

    companion object {
        val NEW_VERSION_ACTION = "com.wongxd.absolutedomain.NEW_VERSION"
    }

    override fun onReceive(context: Context?, intent: Intent?) {


        intent?.let {

            when (it.action) {
                JPushInterface.ACTION_NOTIFICATION_RECEIVED,
                JPushInterface.ACTION_NOTIFICATION_OPENED -> {//用户点击了通知
                    val bundle = intent.extras
                    val title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE)
                            ?: "版本更新" //对应 Portal 推送通知界面上的“通知标题”字段。
                    val content = bundle.getString(JPushInterface.EXTRA_ALERT)
                            ?: "版本更新"//对应 Portal 推送通知界面上的“通知内容”字段。
                    val extras = bundle.getString(JPushInterface.EXTRA_EXTRA)
                            ?: "版本更新"//json 字符串 对应 Portal 推送消息界面上的“可选设置”里的附加字段。
                    val notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID, 0)
                    // 通知栏的Notification ID，可以用于清除Notification

                    Logger.e("通知 送达  或 点击--$title--$content--$extras---$notificationId")
                    context?.let {
                        val i = Intent(it, AtyMain::class.java)
                        i.putExtra("isFromPush", true)
                        i.putExtra("title", title)
                        i.putExtra("content", content)
                        i.putExtra("extras", extras)
                        i.putExtra("notificationId", notificationId)

                        it.startActivity(i)
                    }
                }
                JPushInterface.ACTION_MESSAGE_RECEIVED -> {//收到了自定义消息
                    val bundle = intent.extras
                    val message = bundle.getString(JPushInterface.EXTRA_MESSAGE)
                    Logger.e("推送--$message")
                }

                NEW_VERSION_ACTION -> {
                    Logger.e("推送 自主检测")
                    context?.let {

                        val info = intent.getStringExtra("info") ?: ""
                        val json = JSONObject(info)
                        Logger.e("自主检测版本 获取到的信息    $info")
                        val title = json.optString("title") ?: ""
                        val content = json.optString("content") ?: ""
                        val extras = json.optString("extras")
                                ?: """{"url":"https://wongxd.github.io"}"""
                        val i = Intent(it, AtyMain::class.java)
                        i.putExtra("isFromPush", true)
                        i.putExtra("title", title)
                        i.putExtra("content", content)
                        i.putExtra("extras", extras)
                        i.putExtra("notificationId", 0)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                        it.startActivity(i)
                    }
                }
                else -> {
                    Logger.e("推送 未知类型")
                }
            }

        }
    }
}