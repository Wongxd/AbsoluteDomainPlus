package com.wongxd.absolutedomain.base.kotin.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.wongxd.absolutedomain.base.utils.utilcode.util.PermissionUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.ScreenUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.Utils
import com.wongxd.absolutedomain.fragmenaction.BaseActivity
import com.wongxd.absolutedomain.util.TU

/**
 *
 *
 *
 *
 *
 *
 * Created by wongxd on 2018/1/17.
 */

enum class PermissionType(val permission: String, val permissionName: String) {
    CAMERA(Manifest.permission.CAMERA, "拍照"),
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储卡"),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储卡"),
    CALL_PHONE(Manifest.permission.CALL_PHONE, "拨打电话"),
    FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置"),
    COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, "获取大致位置");
}


/**
 * 获取特定权限
 */
fun Activity.getPermissions(vararg pers: PermissionType, result: (isHadDenied: Boolean, deniedPers: List<PermissionType>) -> Unit) {
    if (Utils.notInit()) Utils.init(application)
    var isHadDenied = false
    val deniedPers: MutableList<PermissionType> = ArrayList<PermissionType>()
    val last = pers.size - 1
    for ((i, per) in pers.withIndex()) {

        PermissionUtils.permission(per.permission)
                .rationale {
                    // Denied permission with ask never again
                    // Need to go to the settings
                    val perName = per.permissionName

                    val dialog = AlertDialog.Builder(this)
                            .setMessage(perName + "\n权限被禁止，请到 设置-权限 中给予")
                            .setPositiveButton("确定", { dialog1, which ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }).create()
                    dialog.show()

                }
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(permissionsGranted: MutableList<String>?) {
                        if (i == last) {
                            result.invoke(isHadDenied, deniedPers)
                        }
                    }

                    override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                        // Denied permission without ask never again
                        val perName = per.permissionName
                        TU.t(perName + " 权限被禁止，无法进行操作")
                        isHadDenied = true
                        deniedPers.add(per)
                    }
                })
                .theme { ScreenUtils.setFullScreen(it) }
                .request()
    }
}


/**
 * 获取特定权限
 */
fun Activity.getPermission(per: BaseActivity.Companion.PermissionType, result: (isGet: Boolean) -> Unit) {
    if (Utils.notInit()) Utils.init(application)
    PermissionUtils.permission(per.permission)
            .rationale {
                // Denied permission with ask never again
                // Need to go to the settings
                val perName = per.permissionName

                val dialog = AlertDialog.Builder(this)
                        .setMessage(perName + "\n权限被禁止，请到 设置-权限 中给予")
                        .setPositiveButton("确定", { dialog1, which ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }).create()
                dialog.show()

            }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    result.invoke(true)
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    // Denied permission without ask never again
                    val perName = per.permissionName
                    TU.t(perName + " 权限被禁止，无法进行操作")
                    result.invoke(false)
                }
            })
            .theme { ScreenUtils.setFullScreen(it) }
            .request()
}