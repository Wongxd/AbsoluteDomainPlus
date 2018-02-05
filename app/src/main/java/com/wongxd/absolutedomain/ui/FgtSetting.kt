package com.wongxd.absolutedomain.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView
import com.wongxd.absolutedomain.DEFAULT_TU_SITE
import com.wongxd.absolutedomain.IMG_AUTOLOAD_NETX_PAGE
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.data.bean.ImgSiteBean
import com.wongxd.absolutedomain.data.net.WNet
import com.wongxd.absolutedomain.fragmenaction.BaseBackFragment
import com.wongxd.absolutedomain.ui.img.TuViewModel
import com.wongxd.absolutedomain.util.SPUtils
import com.wongxd.absolutedomain.util.TU
import com.wongxd.absolutedomain.util.apk.PackageUtil
import kotlinx.android.synthetic.main.fgt_setting.*
import org.jetbrains.anko.selector

/**
 * Created by wongxd on 2018/1/30.
 */
class FgtSetting : BaseBackFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fgt_setting
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initTopBar(topbar_setting, "设置")


        val mGroupListView = groupListView_setting

        val versionCheck = mGroupListView.createItemView("当前版本:  ${PackageUtil.getAppVersionName(activity)
                ?: "未能获取到版本"}")
        versionCheck.setDetailText("点击查询版本")


        val defaultSite: String = SPUtils.get(key = DEFAULT_TU_SITE, defaultObject = "未设置") as String
        val defaultSiteSeting = mGroupListView.createItemView("设置图集默认站点")
        defaultSiteSeting.setDetailText(defaultSite.replace("com.wongxd.absolutedomain.ui.img.tuSite.", ""))


        val tuAutoLoad = mGroupListView.createItemView("图集浏览自动加载下一页")
        tuAutoLoad.accessoryType = QMUICommonListItemView.ACCESSORY_TYPE_SWITCH
        val isAutoLoad = SPUtils.get(key = IMG_AUTOLOAD_NETX_PAGE, defaultObject = true) as Boolean
        tuAutoLoad.switch.isChecked = isAutoLoad
        tuAutoLoad.switch.setOnCheckedChangeListener { buttonView, isChecked ->
            SPUtils.put(key = IMG_AUTOLOAD_NETX_PAGE, `object` = isChecked)
        }




        QMUIGroupListView.newSection(context)
                .addItemView(versionCheck, {
                    WNet.checkVersion()
                })
                .addTo(mGroupListView)

        QMUIGroupListView.newSection(context)
                .setTitle("图 (图模块的相关设置)")
                .addItemView(defaultSiteSeting, {
                    val mVm = ViewModelProviders.of(this).get(TuViewModel::class.java)
                    mVm.siteList.observe(this, Observer<List<ImgSiteBean>> {

                        if (it == null) return@Observer
                        val sites = it
                        val titles = sites.map { it.title }
                        activity?.selector("选择你要设为默认的站点", titles) { di, i ->
                            val def = sites[i].site.name
                            SPUtils.put(key = DEFAULT_TU_SITE, `object` = def)
                            TU.cT("当前默认站点为  ${titles[i]}  ，下次启动生效")
                            defaultSiteSeting.setDetailText(titles[i])
                        }
                    })
                })
                .addItemView(tuAutoLoad, null)
                .addTo(mGroupListView)
    }
}