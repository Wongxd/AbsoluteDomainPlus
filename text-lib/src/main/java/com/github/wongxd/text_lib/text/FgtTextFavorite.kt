package com.github.wongxd.text_lib.text

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.DeleteListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.billy.cc.core.component.CC
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.wongxd.core_lib.ComponentAppAction
import com.github.wongxd.core_lib.CoreApp
import com.github.wongxd.core_lib.base.decorator.DividerItemDecoration
import com.github.wongxd.core_lib.base.kotin.extension.database.parseList
import com.github.wongxd.core_lib.base.kotin.extension.database.toVarargArray
import com.github.wongxd.core_lib.custom.SwipeDeleteLayout.SwipeLayout
import com.github.wongxd.core_lib.data.bean.UserBean
import com.github.wongxd.core_lib.data.database.Text
import com.github.wongxd.core_lib.data.database.TextTable
import com.github.wongxd.core_lib.data.database.Video
import com.github.wongxd.core_lib.data.database.textDB
import com.github.wongxd.core_lib.fragmenaction.MainTabFragment
import com.github.wongxd.core_lib.util.SystemUtils
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.core_lib.util.file.DomainFileFilter
import com.github.wongxd.core_lib.util.file.FileUtils
import com.github.wongxd.text_lib.R
import com.github.wongxd.text_lib.data.bean.TextListBean
import com.github.wongxd.text_lib.text.event.TextFavoriteEvent
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.logger.Logger
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fgt_text_favorite.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by wongxd on 2018/1/24.
 */
class FgtTextFavorite : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_text_favorite
    }

    private var tvImport: TextView? = null
    private var tvExport: TextView? = null

    override fun initView(mView: View?) {
        tvImport = mView?.findViewById(R.id.tv_left)
        tvExport = mView?.findViewById(R.id.tv_right)
        tvImport?.text = "还原"
        tvExport?.text = "备份"
        val tvTitle = mView?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text = "文字收藏"
        initRecycle()
        initData()
        tvImport?.setOnClickListener {
            AlertDialog.Builder(activity).setTitle("警告").setMessage("需要从 本地 或 云 中增量还原吗？")
                    .setPositiveButton("从本地", { dialog, which -> dialog.dismiss();inportFromFile() })
                    .setNegativeButton("从云中", { dialog, which -> dialog.dismiss();doRestoreFromBmob() })
                    .setNeutralButton("不需要", { dialog, which -> dialog.dismiss(); })
                    .setCancelable(false)
                    .show()
        }

        tvExport?.setOnClickListener {
            AlertDialog.Builder(activity).setMessage("需要备份收藏到 本地 或 云  中吗？").setTitle("警告")
                    .setPositiveButton("到本地", { dialog, which -> dialog.dismiss();exportToFile() })
                    .setNegativeButton("到云中", { dialog, which -> dialog.dismiss();exportToBmob() })
                    .setNeutralButton("不需要", { dialog, which -> dialog.dismiss(); })
                    .setCancelable(false)
                    .show()
        }
    }


    /**
     * 从云中还原
     */
    private fun doRestoreFromBmob() {

        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "从云端同步"
        pDialog.setCancelable(false)
        pDialog.show()

        val current = BmobUser.getCurrentUser(activity, UserBean::class.java)
        if (current == null) {
            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
            pDialog.contentText = "请先登录"
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
            return
        }

        val bmobF = current.textFavorite
        if (bmobF == null) {
            pDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE)
            pDialog.contentText = "云端没有备份"
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
            return
        }
        Logger.e("从云端还原备份文件--" + bmobF.getFileUrl(activity))

        doAsync {
            val info = URL(bmobF.getFileUrl(activity)).readText() ?: " "

            doRestoreFromJson(info, {
                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                pDialog.contentText = "从云端同步完成"
                pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
                initData()
            }, {
                pDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE)
                pDialog.contentText = "云中备份无法还原"
                pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
            })
        }
    }

    /**
     *把收藏信息同步到云
     */
    private fun exportToBmob() {
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "同步到云端"
        pDialog.setCancelable(false)
        pDialog.show()

        val current = BmobUser.getCurrentUser(activity, UserBean::class.java)
        if (current == null) {
            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
            pDialog.contentText = "请先登录"
            pDialog.setCancelClickListener {
                pDialog.dismissWithAnimation()
            }
            return
        }

        getFavoriteJson({
            doAsync {
                val file = File(FileUtils.getAppRootDirPath() + current.username + "--cloud--text--temp.ttt")
                if (!file.exists()) {
                    val dir = File(file.parent)
                    dir.mkdirs()
                    file.createNewFile()
                } else {
                    file.delete()
                }
                file.writeText(it)

                val bmobFile = BmobFile(file)
                //上传新的备份文件
                bmobFile.uploadblock(activity, object : UploadFileListener() {
                    override fun onSuccess() {

                        //删除旧的备份文件
                        val oldFile = current.textFavorite
                        oldFile?.delete(activity, object : DeleteListener() {
                            override fun onSuccess() {
                                Logger.d("视频备份到云端", "清除历史备份成功")
                            }

                            override fun onFailure(p0: Int, p1: String?) {
                                TU.cT("清除历史备份失败---$p1")
                            }
                        })

                        //备份文件与用户关联
                        val user = UserBean()
                        user.textFavorite = bmobFile
                        user.update(activity, current.objectId, object : UpdateListener() {
                            override fun onSuccess() {
                                uiThread {
                                    CoreApp.user = BmobUser.getCurrentUser(activity, UserBean::class.java)
                                    pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                    pDialog.contentText = "成功备份到云端"
                                    pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
                                }
                            }

                            override fun onFailure(p0: Int, p1: String?) {
                                uiThread {
                                    pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                                    pDialog.contentText = p1
                                    pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
                                }
                            }
                        })
                    }

                    override fun onFailure(p0: Int, p1: String?) {
                        uiThread {
                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                            pDialog.contentText = p1
                            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
                        }
                    }
                })
            }
        }, {
            pDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE)
            pDialog.contentText = "没有收藏的视频"
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
        })


    }


    /**
     * 查找所有的备份文件
     */
    private fun queryFilesByJava(result: (list: MutableList<DomainVideo>) -> Unit) {

        doAsync {
            val list: MutableList<DomainVideo> = ArrayList()

            try {
                val files = DomainFileFilter(DomainFileFilter.txtFileExtension).getAllFilePath(FileUtils.getAppRootDirPath())

                files.sortByDescending { it.lastModified() }

                for (f in files) {
                    val path = f.path
                    val size = f.length()
                    val dot = path.lastIndexOf("/");
                    val name = path.substring(dot + 1);
                    list.add(DomainVideo(name, path, FileUtils.getFileSize(size)))
                }

                uiThread { result.invoke(list) }
            } catch (e: Exception) {
                e.printStackTrace()
                uiThread { TU.cT("读取存储卡失败！") }
            }
        }

    }

    data class DomainVideo(val name: String, val path: String, val size: String)

    private fun inportFromFile() {

        queryFilesByJava({
            if (it.size <= 0) {
                TU.cT("没有找到您的备份文件")
            } else showPop(it)
        })
    }


    private fun showPop(baks: MutableList<DomainVideo>) {
        val pop = PopupWindow(activity)
        SystemUtils.backgroundAlpha(activity, 0.7f)
        val v = View.inflate(activity, R.layout.layout_bak_list, null)
        val lv = v.findViewById<ListView>(R.id.lv)

        val adapter = LvAdapter(baks)
        adapter.setItemClick(object : LvListener {
            override fun onClick(data: DomainVideo) {
                doRestoreFromLocal(data.path)
                if (pop.isShowing) pop.dismiss()
            }
        })

        lv.adapter = adapter


        pop.contentView = v
        pop.height = rv_fgt_favorite_text.height - 500
        pop.width = rv_fgt_favorite_text.width - 200

        pop.isOutsideTouchable = true
        pop.isFocusable = true
        //让pop可以点击外面消失掉
        pop.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        pop.setOnDismissListener { SystemUtils.backgroundAlpha(activity, 1f) }
        pop.setTouchInterceptor(View.OnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_OUTSIDE) {
                pop.dismiss();
                return@OnTouchListener true
            }
            false;
        });
        pop.showAsDropDown(toolbar_layout_fgt_favorite, 100, 20)
    }

    interface LvListener {
        fun onClick(data: DomainVideo)
    }

    inner class LvAdapter() : BaseAdapter() {

        private lateinit var list: MutableList<DomainVideo>

        constructor(list: MutableList<DomainVideo>) : this() {
            this.list = list
        }


        private val swipeList = java.util.ArrayList<SwipeLayout>()

        fun closeOtherSwipe() {
            for (s in swipeList)
                s.close()
        }

        private var listener: LvListener? = null

        fun setItemClick(lis: LvListener) {
            this.listener = lis
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val data = list[position]

            var realView = convertView

            if (realView == null) {
                realView = View.inflate(activity, R.layout.item_rv_favorite_bak, null)
            }


            val tvName: TextView
            val tvPath: TextView
            val tvSize: TextView



            tvName = realView?.findViewById(R.id.tv_file_name)!!
            tvPath = realView.findViewById(R.id.tv_file_paht)!!
            tvSize = realView.findViewById(R.id.tv_file_size)!!


            val rlItem = realView.findViewById<RelativeLayout>(R.id.rl_item)

            rlItem.setOnClickListener {
                if (listener != null) {
                    listener?.onClick(data)
                }
            }

            val tvDelete = realView.findViewById<TextView>(R.id.tv_delete)


            tvDelete?.setOnClickListener {
                val path = data.path
                val f = File(path)
                if (f.exists()) {
                    val b = f.delete()
                    TU.cT(if (b) "删除成功" else "删除失败")
                    if (b) {
                        list.remove(data)
                        this.notifyDataSetChanged()
                    }
                }
            }

            val swipeLayout = realView.findViewById<SwipeLayout>(R.id.swipelayout)


            swipeLayout?.listener = object : SwipeLayout.OnSwipeListener {
                override fun onSwipe(swipeLayout: SwipeLayout?) {

                }

                override fun onColse(swipeLayout: SwipeLayout?) {
                    swipeList.remove(swipeLayout!!)
                }

                override fun onOpen(swipeLayout: SwipeLayout?) {

                    swipeList.add(swipeLayout!!)
                }

                override fun onStartOpen(swipeLayout: SwipeLayout?) {
                    closeOtherSwipe()
                }

            }


            tvName.text = data.name
            tvPath.text = data.path
            tvSize.text = data.size

            return realView
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return list.size
        }

    }


    /**
     * 从备份文件中还原
     */
    private fun doRestoreFromLocal(sPaht: String = "") {
        if (sPaht.isBlank()) return
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "从本地中还原"
        pDialog.setCancelable(false)
        pDialog.show()
        doAsync {
            val file = File(sPaht)
            val info = file.readText()
            doRestoreFromJson(info, {
                pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                pDialog.contentText = it
                pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
                initData()
            }, {
                pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                pDialog.contentText = it
                pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
            })
        }
    }


    /**
     * 从数据库中获取到收藏的信息，并转化为json （运行在子线程）
     *
     * @param ifGet 收藏信息不为空 uiThread
     * @param whenEmpty 搜藏信息为空  uiThread
     */
    private fun getFavoriteJson(ifGet: (json: String) -> Unit, whenEmpty: (info: String) -> Unit) {
        doAsync {
            activity?.textDB?.use {
                val list = select(TextTable.TABLE_NAME).parseList { (Text(HashMap(it))) }
                if (list.isNotEmpty()) {

                    val sb = StringBuilder()
                    sb.append("{\"list\":[")
                    val length = list.size - 1

                    for (i in list.indices) {
                        sb.append("{\"name\":\"")
                        sb.append(list[i].name)
                        sb.append("\",")

                        sb.append("\"author\":\"")
                        sb.append(list[i].author)
                        sb.append("\",")

                        sb.append("\"preview\":\"")
                        sb.append(list[i].preview)
                        sb.append("\",")

                        sb.append("\"content\":\"")
                        sb.append(list[i].content)
                        sb.append("\",")

                        sb.append("\"time\":\"")
                        sb.append(list[i].time)
                        sb.append("\",")

                        sb.append("\"textId\":\"")
                        sb.append(list[i].textId)
                        sb.append("\",")

                        sb.append("\"siteClass\":\"")
                        sb.append(list[i].siteClass)


                        if (i == length) {
                            sb.append("\"}")
                        } else {
                            sb.append("\"},")
                        }
                    }

                    sb.append("]}")
//                    Logger.d(sb.toString())
                    uiThread { ifGet.invoke(sb.toString()) }
                } else {
                    uiThread { whenEmpty.invoke("未能成功获取到收藏文字") }
                }
            }
        }
    }


    /**
     * 从json 把数据还原到数据库  （子线程）
     *
     * @param jsonStr json 字符串
     *
     * @param successed uithread
     *
     * @param failed uithread
     *
     */
    private fun doRestoreFromJson(jsonStr: String, successed: (info: String) -> Unit, failed: (info: String) -> Unit) {
        doAsync {
            try {

                val json = JSONObject(jsonStr)
                val list = json.optJSONArray("list")
                var i = 0
                val length = list.length()

                if (list.length() == 0) {
                    uiThread { failed.invoke("未能获取到备份") }
                    return@doAsync
                }

                while (i < length) {
                    val obj = list.optJSONObject(i)
                    val name = obj.optString("name")
                    val author = obj.optString("author")
                    val preview = obj.optString("preview")
                    val time = obj.optString("time").toLong()
                    val content = obj.optString("content")
                    val textId = obj.optString("textId")
                    val siteClass = obj.optString("siteClass")
                    restoreToDB(name, author, preview, content, time, textId, siteClass)
                    i++
                }

                uiThread { successed.invoke("增量还原成功") }
            } catch (e: Exception) {
                e.printStackTrace()
                uiThread { failed.invoke("备份文件损坏") }
            }


        }
    }


    /**
     * 增量还原单条数据
     * @param adress
     * @param name
     */
    private fun restoreToDB(name: String, author: String, preview: String, content: String, time: Long, textId: String, siteClass: String) {
        activity?.textDB?.use {
            transaction {
                val items = select(TextTable.TABLE_NAME).whereSimple(TextTable.NAME + "=?", name)
                        .parseList({ Video(HashMap(it)) })
                if (items.isEmpty()) {  //如果是空的
                    val text = Text()
                    text.author = author
                    text.name = name
                    text.preview = preview
                    text.content = content
                    text.siteClass = siteClass
                    text.textId = textId
                    text.time = time

                    insert(TextTable.TABLE_NAME, *text.map.toVarargArray())
                }
//                Logger.d("还原单条数据  $name  $adress  $preview  $siteClass ")
            }
        }
    }


    /**
     * 从数据库加载数据
     */
    private fun initData() {
        activity?.textDB?.use {
            val list = select(TextTable.TABLE_NAME).parseList { (Text(HashMap(it))) }
            if (list.isNotEmpty()) {
                val textList = list.sortedByDescending { it.time }
                adpater.setNewData(textList.toMutableList())
            }
        }
    }

    /**
     * 导出收藏到文件中
     */
    private fun exportToFile() {
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.titleText = "导出收藏到本地文件"
        pDialog.setCancelable(false)
        pDialog.show()

        getFavoriteJson({
            val result = saveFile(it, getString(R.string.app_name) + "-收藏备份" + stampToDate(System.currentTimeMillis()))
            pDialog.changeAlertType(if (result) SweetAlertDialog.SUCCESS_TYPE else SweetAlertDialog.ERROR_TYPE)
            pDialog.contentText = if (result) "导出成功！位于\n《 ${FileUtils.getAppRootDirPath()} 》\n文件夹下" else "导出失败"
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
        }, {
            pDialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE)
            pDialog.contentText = it
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
        })
    }


    /**
     * 将时间戳转换为时间
     */
    fun stampToDate(lt: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }

    /**
     * @param content
     *
     */
    fun saveFile(content: String, fileName: String): Boolean {
        var filePath: String? = null

        filePath = FileUtils.getAppRootDirPath() + File.separator + fileName + DomainFileFilter.txtFileExtension

        try {
            val file = File(filePath)
            if (!file.exists()) {
                val dir = File(file.parent)
                dir.mkdirs()
                file.createNewFile()
            } else {
                file.delete()
            }
            file.writeText(content)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    private lateinit var adpater: RvAdapter
    /**
     * recycleView and smartRefreshLayout
     */
    private fun initRecycle() {
        adpater = RvAdapter {
            //            (parentFragment as FgtFavorite).start(
//                    FgtSeeText.newInstance(TextListBean(it.name, it.author, it.preview, it.content, it.textId), it.siteClass)
//            )
            val fgt = FgtSeeText.newInstance(TextListBean(it.name, it.author, it.preview, it.content ?: "", it.textId), it.siteClass)
            CC.obtainBuilder("cApp").setActionName(ComponentAppAction.FgtMainStartNewFgt)
                    .addParam("fgt", fgt)
                    .build()
                    .call()
        }

        rv_fgt_favorite_text.adapter = adpater
        rv_fgt_favorite_text.itemAnimator = LandingAnimator()
        val layoutManager = LinearLayoutManager(activity)

        rv_fgt_favorite_text.layoutManager = layoutManager
        adpater.setEmptyView(R.layout.item_rv_empty, rv_fgt_favorite_text)
        adpater.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        rv_fgt_favorite_text.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))


    }


    class RvAdapter(val click: (Text) -> Unit) : BaseQuickAdapter<Text, BaseViewHolder>(R.layout.item_rv_text) {
        override fun convert(helper: BaseViewHolder?, item: Text?) {
            helper?.setText(R.id.tv_title, item?.name)
                    ?.setText(R.id.tv_author, item?.author)
                    ?.setText(R.id.tv_preview, item?.preview)
            helper?.itemView?.setOnClickListener {
                item?.let(click)
            }
            helper?.itemView?.setOnLongClickListener { re(helper.layoutPosition); true }
        }

        fun re(position: Int) {

            //删除收藏
            var isDelete = 0
            this.data.let {
                val item = it[position]
                mContext.textDB.use {
                    isDelete = delete(TextTable.TABLE_NAME, TextTable.NAME + "=?", arrayOf(item.name))
                }
            }
            if (isDelete != 0) {
                this.remove(position)
            }

            EventBus.getDefault().post(TextFavoriteEvent(0))
        }
    }
}