package com.wongxd.absolutedomain.base.kotin.permission

import android.Manifest
import android.app.Activity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.wongxd.absolutedomain.base.utils.utilcode.util.PermissionUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.ScreenUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.Utils
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
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, "读取手机状态"),
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
fun Activity.getPermissions(vararg pers: PermissionType, result: (grantedPers: List<PermissionType>,
                                                                  deniedPers: List<PermissionType>) -> Unit) {
    if (Utils.notInit()) Utils.init(application)
    val deniedPers: MutableList<PermissionType> = ArrayList<PermissionType>()
    val grantedPers: MutableList<PermissionType> = ArrayList<PermissionType>()

    val perNames = pers.map { it.permission }
    PermissionUtils.singlePermission(*perNames.toTypedArray())
            .rationale(object : PermissionUtils.OnRationaleListener {
                override fun rationalePers(rationalePers: MutableList<String>?) {
                    val dlg: SweetAlertDialog = SweetAlertDialog(this@getPermissions, SweetAlertDialog.WARNING_TYPE).also {
                        it.setCancelable(true)
                        it.titleText = "有如下权限被禁止(将会导致应用不能正常运行)"
                        val sb = StringBuilder()
                        pers.filter {
                            rationalePers?.contains(it.permission) ?: false
                        }.forEach { sb.append("${it.permissionName}\n") }
                        it.contentText = sb.toString()
                        it.confirmText = "前往设置给予权限"
                        it.setConfirmClickListener { PermissionUtils.openAppSettings() }
                    }
                    dlg.show()
                }

                override fun rationale(shouldRequest: PermissionUtils.OnRationaleListener.ShouldRequest?) {
                    shouldRequest?.again(true)
                }
            })
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {

                    permissionsGranted?.let {
                        deniedPers.addAll(pers.filter { permissionsGranted.contains(it.permission) })
                    }

                    result.invoke(grantedPers, deniedPers)
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    // Denied permission without ask never again
//                    permissionsDeniedForever?.let {
//                        deniedPers.addAll(pers.filter { permissionsDeniedForever.contains(it.permission) })
//                    }

                    permissionsDenied?.let {
                        deniedPers.addAll(pers.filter { permissionsDenied.contains(it.permission) })
                    }

                    result.invoke(grantedPers, deniedPers)
                }
            })
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
}


/**
 * 获取特定权限
 */
fun Activity.getPermission(per: PermissionType, result: (isGet: Boolean) -> Unit) {
    PermissionUtils.singlePermission(per.permission)
            .rationale(object : PermissionUtils.OnRationaleListener {
                override fun rationalePers(rationalePers: MutableList<String>?) {
                    val dlg: SweetAlertDialog = SweetAlertDialog(this@getPermission, SweetAlertDialog.WARNING_TYPE).also {
                        it.setCancelable(true)
                        it.titleText = "有如下权限被禁止(将会导致应用不能正常运行)"
                        it.contentText = per.permissionName
                        it.confirmText = "前往设置给予权限"
                        it.setConfirmClickListener { PermissionUtils.openAppSettings() }
                    }
                    dlg.show()
                }

                override fun rationale(shouldRequest: PermissionUtils.OnRationaleListener.ShouldRequest?) {
                    shouldRequest?.again(true)
                }
            })
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
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
}