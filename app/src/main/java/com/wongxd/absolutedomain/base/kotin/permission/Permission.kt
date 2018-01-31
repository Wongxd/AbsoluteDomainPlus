package com.wongxd.absolutedomain.base.kotin.permission

import android.Manifest
import android.app.Activity
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.wongxd.absolutedomain.base.CircularAnim
import com.wongxd.absolutedomain.base.utils.utilcode.util.IntentUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.PermissionUtils
import com.wongxd.absolutedomain.base.utils.utilcode.util.ScreenUtils
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
fun Activity.getPermissions(vararg pers: PermissionType, isGoSetting: Boolean = true, result: (grantedPers: List<PermissionType>,
                                                                                               deniedPers: List<PermissionType>) -> Unit) {
    val perNames = pers.map { it.permission }
    PermissionUtils.singlePermission(*perNames.toTypedArray())
            .rationale { it.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    val grantedPers: MutableList<PermissionType> = ArrayList<PermissionType>()
                    permissionsGranted?.let {
                        grantedPers.addAll(pers.filter { permissionsGranted.contains(it.permission) })
                    }

                    result.invoke(grantedPers, emptyList())
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    // Denied permission without ask never again
                    val deniedPers: MutableList<PermissionType> = ArrayList<PermissionType>()

                    permissionsDeniedForever?.let {
                        deniedPers.addAll(pers.filter { permissionsDeniedForever.contains(it.permission) })
                    }

                    permissionsDenied?.let {
                        deniedPers.addAll(pers.filter { permissionsDenied.contains(it.permission) })
                    }


                    val temp = deniedPers.distinct()
                    deniedPers.clear()
                    deniedPers.addAll(temp)

                    if (deniedPers.isNotEmpty() && isGoSetting) {
                        val dlg: SweetAlertDialog = SweetAlertDialog(this@getPermissions, SweetAlertDialog.WARNING_TYPE).also {
                            it.titleText = "有如下权限被禁止"
                            val sb = StringBuilder()
                            deniedPers.forEach { sb.append("${it.permissionName}\n") }
                            sb.append("(将会导致应用不能正常运行)")
                            it.contentText = sb.toString()
                            it.confirmText = "前往设置给予权限"
                            it.setConfirmClickListener {

                                CircularAnim.fullActivity(this@getPermissions, window.decorView)
                                        .go { startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName)) }
                            }
                        }
                        dlg.setCancelable(false)
                        dlg.show()
                    }

                    result.invoke(emptyList(), deniedPers)
                }
            })
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
}


/**
 * 获取特定权限
 */
fun Activity.getPermission(per: PermissionType, isGoSetting: Boolean = true, result: (isGet: Boolean) -> Unit) {
    PermissionUtils.singlePermission(per.permission)
            .rationale { it.again(true) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: MutableList<String>?) {
                    result.invoke(true)
                }

                override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                    // Denied permission without ask never again
                    val perName = per.permissionName
                    TU.cT(perName + " 权限被禁止，无法进行操作")

                    if (isGoSetting) {
                        val dlg: SweetAlertDialog = SweetAlertDialog(this@getPermission, SweetAlertDialog.WARNING_TYPE)
                                .also {
                                    it.titleText = "有如下权限被禁止"
                                    val sb = StringBuilder()
                                    sb.append("${per.permissionName}\n")
                                    sb.append("(将会导致应用不能正常运行)")
                                    it.contentText = sb.toString()
                                    it.confirmText = "前往设置给予权限"
                                    it.setConfirmClickListener {

                                        CircularAnim.fullActivity(this@getPermission, window.decorView)
                                                .go { startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName)) }
                                    }
                                }
                        dlg.setCancelable(false)
                        dlg.show()
                    }
                    result.invoke(false)
                }
            })
            .theme { activity -> ScreenUtils.setFullScreen(activity) }
            .request()
}