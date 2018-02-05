package com.wongxd.absolutedomain.ui.img

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.DeleteListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.orhanobut.logger.Logger
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.kotin.extension.database.parseList
import com.wongxd.absolutedomain.base.kotin.extension.database.toVarargArray
import com.wongxd.absolutedomain.base.utils.utilcode.util.TimeUtils
import com.wongxd.absolutedomain.custom.SwipeDeleteLayout.SwipeLayout
import com.wongxd.absolutedomain.data.bean.UserBean
import com.wongxd.absolutedomain.data.database.Tu
import com.wongxd.absolutedomain.data.database.TuTable
import com.wongxd.absolutedomain.data.database.tuDB
import com.wongxd.absolutedomain.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.ui.FgtFavorite
import com.wongxd.absolutedomain.ui.img.event.TuFavoriteEvent
import com.wongxd.absolutedomain.util.SystemUtils
import com.wongxd.absolutedomain.util.TU
import com.wongxd.absolutedomain.util.file.DomainFileFilter
import com.wongxd.absolutedomain.util.file.FileUtils
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fgt_tu_favorite.*
import loadImg
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
 * Created by wongxd on 2018/1/9.
 */
class FgtTuFavorite : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_tu_favorite
    }

    private var tvImport: TextView? = null
    private var tvExport: TextView? = null

    override fun initView(mView: View?) {
        tvImport = mView?.findViewById(R.id.tv_left)
        tvExport = mView?.findViewById(R.id.tv_right)
        tvImport?.text = "还原"
        tvExport?.text = "备份"
        val tvTitle = mView?.findViewById<TextView>(R.id.tv_title)
        tvTitle?.text = "图收藏"
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

        val bmobF = current.imgFavorite
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
                val file = File(FileUtils.getAppRootDirPath() + current.username + "--cloud-img-temp.ttt")
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
                        val oldFile = current.imgFavorite
                        oldFile?.delete(activity, object : DeleteListener() {
                            override fun onSuccess() {
                                Logger.d("图集备份到云端", "清除历史备份成功")
                            }

                            override fun onFailure(p0: Int, p1: String?) {
                                TU.cT("清除历史备份失败---$p1")
                            }
                        })

                        //备份文件与用户关联
                        val user = UserBean()
                        user.imgFavorite = bmobFile
                        user.update(activity, current.objectId, object : UpdateListener() {
                            override fun onSuccess() {
                                uiThread {
                                    App.user = BmobUser.getCurrentUser(activity, UserBean::class.java)
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
            pDialog.contentText = "没有收藏的图集"
            pDialog.setCancelClickListener { pDialog.dismissWithAnimation() }
        })


    }


    /**
     * 查找所有的备份文件
     */
    private fun queryFilesByJava(result: (list: MutableList<DomainImg>) -> Unit) {

        doAsync {
            val list: MutableList<DomainImg> = ArrayList()

            try {
                val files = DomainFileFilter().getAllFilePath(FileUtils.getAppRootDirPath())

                files.sortByDescending { it.lastModified() }

                for (f in files) {
                    val path = f.path
                    val size = f.length()
                    val dot = path.lastIndexOf("/");
                    val name = path.substring(dot + 1);
                    list.add(DomainImg(name, path, FileUtils.getFileSize(size)))
                }

                uiThread { result.invoke(list) }
            } catch (e: Exception) {
                e.printStackTrace()
                uiThread { TU.cT("读取存储卡失败！") }
            }
        }

    }

    data class DomainImg(val name: String, val path: String, val size: String)

    private fun inportFromFile() {

        queryFilesByJava({
            if (it.size <= 0) {
                TU.cT("没有找到您的备份文件")
            } else showPop(it)
        })
    }


    private fun showPop(baks: MutableList<DomainImg>) {
        val pop = PopupWindow(activity)
        SystemUtils.backgroundAlpha(activity, 0.7f)
        val v = View.inflate(activity, R.layout.layout_bak_list, null)
        val lv = v.findViewById<ListView>(R.id.lv)

        val adapter = LvAdapter(baks)
        adapter.setItemClick(object : LvListener {
            override fun onClick(data: DomainImg) {
                doRestoreFromLocal(data.path)
                if (pop.isShowing) pop.dismiss()
            }
        })

        lv.adapter = adapter


        pop.contentView = v
        pop.height = rv_fgt_favorite_tu.height - 500
        pop.width = rv_fgt_favorite_tu.width - 200

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
        fun onClick(data: DomainImg)
    }

    inner class LvAdapter() : BaseAdapter() {

        private lateinit var list: MutableList<DomainImg>

        constructor(list: MutableList<DomainImg>) : this() {
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
            activity?.tuDB?.use {
                val list = select(TuTable.TABLE_NAME).parseList { (Tu(HashMap(it))) }
                if (list.isNotEmpty()) {

                    val sb = StringBuilder()
                    sb.append("{\"list\":[")
                    val length = list.size - 1

                    for (i in list.indices) {
                        sb.append("{\"name\":\"")
                        sb.append(list[i].name)
                        sb.append("\",")

                        sb.append("\"address\":\"")
                        sb.append(list[i].address)
                        sb.append("\",")

                        sb.append("\"preview\":\"")
                        sb.append(list[i].preview)
                        sb.append("\",")

                        sb.append("\"time\":\"")
                        sb.append(list[i].time)
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
                    uiThread { whenEmpty.invoke("未能成功获取到收藏图集") }
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
                    val adress = obj.optString("address")
                    val preview = obj.optString("preview")
                    val time = obj.optString("time").toLong()
                    val siteClass = obj.optString("siteClass")
                    restoreToDB(name, adress, preview, time, siteClass)
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
    private fun restoreToDB(name: String, adress: String, preview: String, time: Long, siteClass: String) {
        activity?.tuDB?.use {
            transaction {
                val items = select(TuTable.TABLE_NAME).whereSimple(TuTable.ADDRESS + "=?", adress)
                        .parseList({ Tu(HashMap(it)) })
                if (items.isEmpty()) {  //如果是空的
                    val tu = Tu()
                    tu.address = adress
                    tu.name = name
                    tu.preview = preview
                    tu.siteClass = siteClass
                    tu.time = time

                    insert(TuTable.TABLE_NAME, *tu.map.toVarargArray())
                }
//                Logger.d("还原单条数据  $name  $adress  $preview  $siteClass ")
            }
        }
    }


    /**
     * 从数据库加载数据
     */
    private fun initData() {
        activity?.tuDB?.use {
            val list = select(TuTable.TABLE_NAME).parseList { (Tu(HashMap(it))) }
            if (list.isNotEmpty()) {
                val tuList = list.sortedByDescending { it.time }
                adpater.setNewData(tuList.toMutableList())
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

        filePath = FileUtils.getAppRootDirPath() + File.separator + fileName + ".domainImg"

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

    private lateinit var adpater: TuAdapter
    /**
     * recycleView and smartRefreshLayout
     */
    private fun initRecycle() {
        adpater = TuAdapter {
            val b = Bundle()
            b.putString("url", it.address)
            b.putString("title", it.name)
            b.putString("preview", it.preview)
            b.putBoolean("isFromFavorite", true)
            b.putString("siteClass", it.siteClass)
            (parentFragment as FgtFavorite).start(FgtSeePic.newInstance(b))
        }


        rv_fgt_favorite_tu.adapter = adpater
        rv_fgt_favorite_tu.itemAnimator = LandingAnimator()
        val layoutManager = GridLayoutManager(activity, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adpater.data.size == 0) return 2
                return 1
            }

        }
        rv_fgt_favorite_tu.layoutManager = layoutManager
        adpater.setEmptyView(R.layout.item_rv_empty, rv_fgt_favorite_tu)
        adpater.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)


    }


    class TuAdapter(val click: (Tu) -> Unit) : BaseQuickAdapter<Tu, BaseViewHolder>(R.layout.item_rv_fgt_img) {
        override fun convert(helper: BaseViewHolder?, item: Tu?) {
            helper?.getView<ImageView>(R.id.iv)?.loadImg(item?.preview!!)
            helper?.getView<TextView>(R.id.tv_title)?.setBackgroundColor(Color.WHITE)
            helper?.setText(R.id.tv_title, item?.name)
                    ?.setText(R.id.tv_time, TimeUtils.date2String(TimeUtils.millis2Date(item?.time ?: 0)))
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
                mContext.tuDB.use {
                    isDelete = delete(TuTable.TABLE_NAME, TuTable.ADDRESS + "=?", arrayOf(item.address))
                }
            }
            if (isDelete != 0) {
                this.remove(position)
            }

            EventBus.getDefault().post(TuFavoriteEvent(0))
        }
    }
}