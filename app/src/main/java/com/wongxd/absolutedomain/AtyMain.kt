package com.wongxd.absolutedomain

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.v3.BmobUser
import cn.jzvd.JZVideoPlayer
import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.*
import com.github.wongxd.core_lib.base.kotin.permission.PermissionType
import com.github.wongxd.core_lib.base.kotin.permission.getPermissions
import com.github.wongxd.core_lib.custom.whatsnew.WhatsNew
import com.github.wongxd.core_lib.custom.whatsnew.item.item
import com.github.wongxd.core_lib.custom.whatsnew.item.whatsNew
import com.github.wongxd.core_lib.custom.whatsnew.util.PresentationOption
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.data.bean.UserBean
import com.github.wongxd.core_lib.data.net.WNet
import com.github.wongxd.core_lib.fragmenaction.BaseActivity
import com.github.wongxd.core_lib.util.AlipayUtil
import com.github.wongxd.core_lib.util.SPUtils
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.core_lib.util.apk.UpdateUtil
import com.github.wongxd.core_lib.util.cache.DataCleanManager
import com.github.wongxd.core_lib.util.cache.GlideCatchUtil
import com.github.wongxd.img_lib.ComponentImg
import com.github.wongxd.text_lib.ComponentText
import com.github.wongxd.video_lib.ComponentVideo
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.wongxd.absolutedomain.event.LockDrawerEvent
import com.wongxd.absolutedomain.event.LogStateChangeEvent
import com.wongxd.absolutedomain.event.ToggleDrawerEvent
import com.wongxd.absolutedomain.receiver.MyPushReceiver
import com.wongxd.absolutedomain.ui.*
import com.wongxd.absolutedomain.ui.login.FgtLogin
import com.wongxd.absolutedomain.ui.user.FgtUser
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.android.synthetic.main.layout_w_toolbar.*
import loadHeader
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.notificationManager
import org.json.JSONObject
import java.io.File
import java.util.*


class AtyMain : BaseActivity() {


    private lateinit var drawer: Drawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aty_main)

        EventBus.getDefault().register(this)

        CC.registerComponent(ComponentApp())
        CC.registerComponent(ComponentImg())

        if (SPUtils.get(key = IS_LOAD_VIDEO, defaultObject = true) as Boolean)
            CC.registerComponent(ComponentVideo())
        if (SPUtils.get(key = IS_LOAD_TEXT, defaultObject = true) as Boolean)
            CC.registerComponent(ComponentText())

        initPermission()

        if (findFragment(FgtMain::class.java) == null) {
            loadRootFragment(R.id.fl_content_aty_main, FgtMain.newInstance())
        }


        initDrawer()


        CoreApp.user = BmobUser.getCurrentUser(this, UserBean::class.java)
        updateDrawerHeader()

        if (!BuildConfig.DEBUG && SPUtils.get(key = IS_SHOW_ACTIVITY, defaultObject = true) as Boolean) {
            val c = Calendar.getInstance()//
            val mYear = c.get(Calendar.YEAR) // 获取当前年份
            val mMonth = c.get(Calendar.MONTH) + 1// 获取当前月份
            val mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当日期
//                    val mWay = c.get(Calendar.DAY_OF_WEEK)// 获取当前日期的星期
            val mHour = c.get(Calendar.HOUR_OF_DAY)//时
//                    val mMinute = c.get(Calendar.MINUTE)//分
            val spDay = SPUtils.get(key = "alipayRedCount", defaultObject = 0)

            if (spDay != mDay) {
                if ((mMonth <= 3 && mYear == 2018))
                    getRedPacket()
                if (mHour > 21 || mHour < 8) {
                    WNet.checkVersion(App.instance.getPkgVersionName(), MyPushReceiver.NEW_VERSION_ACTION, application)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        com.orhanobut.logger.Logger.e("onNewIntent")
        intent?.let {
            val isFromPush = it.getBooleanExtra("isFromPush", false)
            if (!isFromPush) return

            val pushTitle = it.getStringExtra("title") //对应 Portal 推送通知界面上的“通知标题”字段。
            val pushContent = it.getStringExtra("content") //对应 Portal 推送通知界面上的“通知内容”字段。
            val pushExtras = it.getStringExtra("extras") //json 字符串 对应 Portal 推送消息界面上的“可选设置”里的附加字段。
            val notificationId = it.getIntExtra("notificationId", 0) // 通知栏的Notification ID，可以用于清除Notification

            val json = JSONObject(pushExtras ?: """{"url":"https://wongxd.github.io"}""")

            val url = json.optString("url", WhatsNew.DEFAULT_DOWNLOADURL)


            val whatsnew = whatsNew {
                item {
                    title = pushTitle
                    content = pushContent
                }

                if (!url.isNullOrBlank()) {
                    item {
                        title = "下载地址"
                        content = url
                    }
                }
            }.apply { presentationOption = PresentationOption.DEBUG }
            whatsnew.presentAutomatically(this, lis = {
                openUrl(it)
            }, downLoadUrl = url)


            try {
                if (notificationId != 0)
                    notificationManager.cancel(notificationId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Subscribe
    fun changeDrawerLock(event: LockDrawerEvent) {

        val dlayout = drawer.drawerLayout

        if (event.isShouldLock) {
            dlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            dlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    @Subscribe
    fun toggleDrawer(event: ToggleDrawerEvent) {
        if (drawer.isDrawerOpen)
            drawer.closeDrawer()
        else
            drawer.openDrawer()
    }

    @Subscribe
    fun logStateChange(event: LogStateChangeEvent) {
        updateDrawerHeader()
    }


    override fun onPause() {
        super.onPause()
        JZVideoPlayer.releaseAllVideos()
    }

    override fun onBackPressedSupport() {
        if (JZVideoPlayer.backPress()) {
            return
        }
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport()
    }

    override fun onCreateFragmentAnimator(): FragmentAnimator {
        // 设置横向(和安卓4.x动画相同)
        return DefaultHorizontalAnimator()
    }

    private fun initDrawer() {


        //if you want to update the items at a later time it is recommended to keep it in a variable
        val menuFavorateTu = PrimaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_TU_FAVORITE.id)
                .withName(Menu.MENU_TU_FAVORITE.title)
                .withIcon(Menu.MENU_TU_FAVORITE.icon).withSelectable(false)

        val menuDownloadManager = PrimaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_DOWNLOAD_MANAGER.id)
                .withName(Menu.MENU_DOWNLOAD_MANAGER.title)
                .withIcon(Menu.MENU_DOWNLOAD_MANAGER.icon).withSelectable(false)

        val menuDonate = PrimaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_DONATE.id)
                .withName(Menu.MENU_DONATE.title)
                .withIcon(Menu.MENU_DONATE.icon).withSelectable(false)

        val menuAlipayRed = PrimaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_ALIPAY_RED.id)
                .withName(Menu.MENU_ALIPAY_RED.title)
                .withIcon(Menu.MENU_ALIPAY_RED.icon).withSelectable(false)

        val menuTool = ExpandableDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_TOOL.id)
                .withName(Menu.MENU_TOOL.title)
                .withIcon(Menu.MENU_TOOL.icon).withSelectable(false)
                .withSubItems(
                        SecondaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_THEME.id)
                                .withName(Menu.MENU_THEME.title)
                                .withIcon(Menu.MENU_THEME.icon).withSelectable(false),

                        SecondaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_CACHE.id)
                                .withName(Menu.MENU_CACHE.title)
                                .withIcon(Menu.MENU_CACHE.icon).withSelectable(false),

                        SecondaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_RATING.id)
                                .withName(Menu.MENU_RATING.title)
                                .withIcon(Menu.MENU_RATING.icon).withSelectable(false),

                        SecondaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_UPGRADE.id)
                                .withName(Menu.MENU_UPGRADE.title)
                                .withIcon(Menu.MENU_UPGRADE.icon).withSelectable(false),

                        SecondaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_ABOUT.id)
                                .withName(Menu.MENU_ABOUT.title)
                                .withIcon(Menu.MENU_ABOUT.icon).withSelectable(false)
                )

        val menuSetting = PrimaryDrawerItem().withIconTintingEnabled(true).withIdentifier(Menu.MENU_SETTING.id)
                .withName(Menu.MENU_SETTING.title)
                .withIcon(Menu.MENU_SETTING.icon).withSelectable(false)

        //create the drawer and remember the `Drawer` result object
        drawer = DrawerBuilder()
                .withItemAnimator(SlideInUpAnimator())
                .withActivity(this)
                .withHeader(R.layout.nav_header_main)
                .addDrawerItems(
                        menuFavorateTu,
                        menuDownloadManager,
                        menuDonate,
                        menuAlipayRed,
                        DividerDrawerItem(),
                        menuTool,
                        menuSetting
                )
                .withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                    override fun onItemClick(view: View?, position: Int, drawerItem: IDrawerItem<*, *>?): Boolean {
                        when (drawerItem?.identifier) {
                            Menu.MENU_TU_FAVORITE.id -> {

                                tv_left?.let { start(FgtFavorite.newInstance(TaskType.IMG)) }
                                drawer.closeDrawer()

                            }

                            Menu.MENU_DOWNLOAD_MANAGER.id -> {
                                tv_left?.let { start(FgtDownload.newInstance(TaskType.IMG)) }
                                drawer.closeDrawer()
                            }

                            Menu.MENU_DONATE.id -> {
                                donate()
                            }

                            Menu.MENU_ALIPAY_RED.id -> {
                                getRedPacket()
                            }

                            Menu.MENU_THEME.id -> {
                                tv_left?.let { start(FgtTheme()) }
                                drawer.closeDrawer()
                            }

                            Menu.MENU_CACHE.id -> {
                                cacheThing()
                            }


                            Menu.MENU_RATING.id -> {
                                UpdateUtil.goToAppMarket(this@AtyMain)
                            }

                            Menu.MENU_UPGRADE.id -> {
                                WNet.checkVersion(App.instance.getPkgVersionName(), MyPushReceiver.NEW_VERSION_ACTION, application)

                            }

                            Menu.MENU_ABOUT.id -> {
                                showAbout()
                            }

                            Menu.MENU_SETTING.id -> {
                                tv_left?.let { start(FgtSetting()) }
                                drawer.closeDrawer()
                            }
                        }

                        return true
                    }

                })
                .build()

        drawer.header.setOnClickListener {
            if (CoreApp.user == null) {
                start(FgtLogin())
            } else {
                start(FgtUser())
            }
        }

        drawer.setSelection(-1)
    }

    private fun updateDrawerHeader() {
        val userImg = drawer.header.findViewById<ImageView>(R.id.iv_user_header)
        val userName = drawer.header.findViewById<TextView>(R.id.tv_user_name)

        userImg.loadHeader(R.drawable.ic_user)
        userName.text = "未登录用户"

        CoreApp.user?.let {
            it.qqHeader?.let { userImg.loadHeader(it) } ?: userImg.loadHeader(R.drawable.login_suc)
            userName.text = it.nickName
            SPUtils.put(key = IS_SHOW_AD, `object` = it.showAd ?: true)
            SPUtils.put(key = IS_SHOW_ACTIVITY, `object` = it.showActivity ?: true)
        }
    }

    private fun initPermission() {
        getPermissions(PermissionType.READ_EXTERNAL_STORAGE, PermissionType.WRITE_EXTERNAL_STORAGE, isGoSetting = true) { grantedPers, deniedPers ->


        }
    }

    private fun openUrl(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun openApk(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            val mime = MimeTypeMap.getSingleton()
            val ext = file.getName().substring(file.getName().lastIndexOf(".") + 1)
            val type = mime.getMimeTypeFromExtension(ext)
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(this,
                        "com.github.wongxd.domain.fileProvider",
                        file)
                intent.setDataAndType(contentUri, type)
            } else {
                intent.setDataAndType(Uri.fromFile(file), type)
            }
            startActivity(intent)
        } else {
            TU.cT("文件不存在，是否被删除？")
        }
    }

    /**
     * 缓存
     */
    private fun cacheThing() {
        val imgCache = GlideCatchUtil.getInstance().cacheSize
        val totalCache = DataCleanManager.getTotalCacheSize(applicationContext)
        AlertDialog.Builder(this)
                .setTitle("缓存信息")
                .setMessage("图片缓存: $imgCache \n全部缓存: $totalCache")
                .setNeutralButton("清除全部缓存", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        DataCleanManager.clearAllCache(applicationContext)
                        dialog?.dismiss()
                    }

                })
                .setNegativeButton("清除图片缓存", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        GlideCatchUtil.getInstance().clearCacheDiskSelf()
                        dialog?.dismiss()
                    }

                })
                .create()
                .show()
    }

    /**
     * 给我发邮件
     */
    private fun eMailMe(mail: String = "974501076@qq.com", subject: String = """“域”反馈""") {
        val data = Intent(Intent.ACTION_SENDTO)
        data.data = Uri.parse("mailto:${mail.trim()}")
        data.putExtra(Intent.EXTRA_SUBJECT, subject)
        data.putExtra(Intent.EXTRA_TEXT, "")
        startActivity(data)
    }

    /**
     * 展示关于信息
     */
    private fun showAbout() {
        AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("""         数据来源于网络，仅供学习交流使用。
                 切勿 违法及商用。
         对滥用本软件造成的一切后果，请自行承担。
             如有侵权，请联系该网站管理员。
                """)
                .setNeutralButton("联系我") { dialog, which -> eMailMe();dialog.dismiss() }
                .create()
                .show()
    }

    /**
     * 捐赠
     */
    fun donate() {
        QMUIDialog.MessageDialogBuilder(this)
                .setTitle("么么哒")
                .setMessage("捐赠开发者？（开发者升级成商家账户了，可以用支付宝红包抵扣了）\n\n")
                .addAction("捐赠") { dialog, index ->
                    AlipayUtil.startAlipayClient(this, "FKX07373TRZS7EQ7SUVI9A")
                    dialog.dismiss()
                }
                .addAction("不捐赠") { dialog, index -> dialog.dismiss() }
                .show()
    }

    /**
     * 获取支付宝红包
     */
    fun getRedPacket() {
        QMUIDialog.MessageDialogBuilder(this)
                .setTitle("么么哒")
                .setMessage("领取支付宝红包，帮助开发者领取赏金吗？\n如果没有地方使用红包，可以在应用内打赏给开发者（开发者升级成商家账户了，可以用红包抵扣了）。\n")
                .addAction("领取") { dialog, index ->
                    AlipayUtil.startAlipayClient(this, "c1x03491e5pr1lnuoid3e22")

                    val c = Calendar.getInstance()//
//                    val mYear = c.get(Calendar.YEAR) // 获取当前年份
//                    val mMonth = c.get(Calendar.MONTH) + 1// 获取当前月份
                    val mDay = c.get(Calendar.DAY_OF_MONTH)// 获取当日期
//                    val mWay = c.get(Calendar.DAY_OF_WEEK)// 获取当前日期的星期
//                    val mHour = c.get(Calendar.HOUR_OF_DAY)//时
//                    val mMinute = c.get(Calendar.MINUTE)//分
                    SPUtils.put(key = "alipayRedCount", `object` = mDay)
                    showDonateTips()
                    dialog.dismiss()
                }
                .addAction("不需要") { dialog, index -> dialog.dismiss() }
                .show()
    }

    /**
     * 提醒用户可以把红包 打赏给我
     */
    private fun showDonateTips() {
        QMUIDialog.MessageDialogBuilder(this)
                .setTitle("么么哒")
                .setMessage("如果没有地方使用支付宝红包，可以在应用内打赏给开发者（开发者升级成商家账户了，可以用红包抵扣了）。\n"
                )
                .addAction("将红包打赏给开发者") { dialog, index ->
                    AlipayUtil.startAlipayClient(this, "FKX07373TRZS7EQ7SUVI9A");
                    dialog.dismiss()
                }
                .addAction("不用了") { dialog, index -> dialog.dismiss() }
                .show()
    }


    override fun recreate() {
        pop()
        super.recreate()
    }

    override fun onDestroy() {
        CC.unregisterComponent(ComponentApp())
        CC.unregisterComponent(ComponentImg())

        CC.unregisterComponent(ComponentVideo())
        CC.unregisterComponent(ComponentText())


        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}
