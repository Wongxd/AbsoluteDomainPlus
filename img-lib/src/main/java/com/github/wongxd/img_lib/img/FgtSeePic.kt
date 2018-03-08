package com.github.wongxd.img_lib.img

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.billy.cc.core.component.CC
import com.chad.library.adapter.base.BaseQuickAdapter
import com.github.wongxd.core_lib.IMG_AUTOLOAD_NETX_PAGE
import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.core_lib.base.kotin.extension.database.parseList
import com.github.wongxd.core_lib.base.kotin.extension.database.toVarargArray
import com.github.wongxd.core_lib.data.bean.Task
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.data.download.DownLoadManager
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.core_lib.util.SPUtils
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.core_lib.util.Tips
import com.github.wongxd.img_lib.R
import com.github.wongxd.img_lib.img.adapter.RvSeePicAdapter
import com.github.wongxd.img_lib.img.event.TuFavoriteEvent
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.wongxd.absolutedomain.data.database.Tu
import com.wongxd.absolutedomain.data.database.TuTable
import com.wongxd.absolutedomain.data.database.tuDB
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fgt_see_pic.*
import kotlinx.android.synthetic.main.layout_w_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class FgtSeePic : BaseBackFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fgt_see_pic
    }

    companion object {

        fun newInstance(args: Bundle = Bundle()): FgtSeePic {
            val fragment = FgtSeePic()
            fragment.arguments = args
            return fragment
        }
    }

    private var isAddToDownload = false
    private lateinit var adpater: RvSeePicAdapter
    private lateinit var mVm: SeePicViewModel
    private lateinit var siteClass: String

    private val isAutoLoad by lazy { SPUtils.get(key = IMG_AUTOLOAD_NETX_PAGE, defaultObject = true) as Boolean }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val url = arguments?.getString("url")
        val title = arguments?.getString("title")
        siteClass = arguments?.getString("siteClass")!!

        if (arguments?.getBoolean("isFromFavorite", false)!!)
            tv_left.visibility = View.GONE

        tv_left.text = "收藏"
        tv_right.text = "下载"


        mVm = ViewModelProviders.of(_mActivity).get(SeePicViewModel::class.java)
        val onwClass: Class<*>?
        try {
            onwClass = Class.forName(siteClass)
            val o = onwClass.newInstance()
            mVm.shouldFirst(url = url ?: "", site = o as BaseTuSite)
            initRv()
        } catch (e: Exception) {
            e.printStackTrace()
            TU.cT("唉呀，小域已经更新了，以前的备份不能用了啦...")
            pop()
        }


        mVm.title.value = title
        mVm.title.observe(this, Observer { tv_title.text = it ?: "图集详情" })

        mVm.picList.observe(this, Observer {
            it?.let {
                if (mVm.currentPage == 1) {
                    adpater.setNewData(it)
                } else {
                    adpater.addData(it)
                    isAddToDownload = false
                }
            }
        })

        mVm.getListState.observe(this, Observer {
            if (it == RequestState.LOADMORE) {
                srl_aty_see_pic.finishLoadmore()
            } else {
                srl_aty_see_pic.finishRefresh()
            }

            if (it?.state == 0) {
                Tips.showErrorTips(text = it.info)
            } else if (it?.state == 1) {
                if (isAutoLoad) {
                    mVm.addPageList()
//                    Logger.e("自动加载下一页")
                }
            }
        })


        mVm.currentImgPos.observe(this, Observer {
            it?.let { rv_aty_see_pic.scrollToPosition(it) }
        })

        tv_right.setOnClickListener {
            if (isAddToDownload) {
                CC.obtainBuilder("cApp").addParam("type", TaskType.IMG).build().call()
            } else
                QMUIDialog.MessageDialogBuilder(activity)
                        .setTitle("提示")
                        .setMessage("要下载本页已加载的所有图片吗？")
                        .addAction("下载") { dialog, index -> createTask(); isAddToDownload = true; dialog.dismiss() }
                        .addAction("取消") { dialog, index -> dialog.dismiss() }
                        .show()
        }



        srl_aty_see_pic.isEnableLoadmore = true
        srl_aty_see_pic.setOnRefreshListener { mVm.refreshList() }
        srl_aty_see_pic.setOnLoadmoreListener { mVm.addPageList() }

        doFavoriteLogic(url ?: "")
        srl_aty_see_pic.autoRefresh()
    }

    private fun createTask() {
        doAsync {
            val tasks: MutableList<Task> = ArrayList()
            adpater.data.mapTo(tasks) { Task(url = it, type = TaskType.IMG) }

            DownLoadManager.get().addTask(*tasks.toTypedArray())
            uiThread { Tips.showSuccessTips(title = "批量下载任务", text = "添加成功") }
        }
    }

    private fun initRv() {
        adpater = RvSeePicAdapter {
            val b = Bundle()
            mVm.currentImgPos.value = it.position

            extraTransaction().start(FgtViewBigPic.newInstance(b))

        }
        adpater.setEnableLoadMore(false)


        rv_aty_see_pic.adapter = adpater
//        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//                .apply { this.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE }// gapStrategy 解决 RecycleView做瀑布流滚动时，已加载item的位置来回变动

        val layoutManager = GridLayoutManager(activity, 3).apply {
            this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (adpater.data.size == 0) return 3
                    return 1
                }
            }
        }
        rv_aty_see_pic.layoutManager = layoutManager

        rv_aty_see_pic.itemAnimator = LandingAnimator()

//        rv_aty_see_pic.addItemDecoration(SGSpacingItemDecoration(3, DensityUtil.dp2px(4f)))

        //        RecyclerView滑动过程中不断请求layout的Request，不断调整item见的间隙，并且是在item尺寸显示前预处理，因此解决RecyclerView滑动到顶部时仍会出现移动问题
//        rv_aty_see_pic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                layoutManager.invalidateSpanAssignments() //防止第一行到顶部有空白区域
//            }
//        })

        adpater.setEmptyView(R.layout.item_rv_empty, rv_aty_see_pic)
        adpater.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
    }

    private fun doFavoriteLogic(url: String) {


        activity?.tuDB?.use {

            val items = select(TuTable.TABLE_NAME).whereSimple(TuTable.ADDRESS + "=?", url)
                    .parseList({ Tu(HashMap(it)) })
            if (items.isEmpty()) {  //如果是空的
                tv_left.text = "收藏"
            } else {
                tv_left.text = "取消收藏"
            }


        }


        tv_left.setOnClickListener {
            activity?.tuDB?.use {
                transaction {
                    val items = select(TuTable.TABLE_NAME).whereSimple(TuTable.ADDRESS + "=?", url)
                            .parseList({ Tu(HashMap(it)) })

                    if (items.isEmpty()) {  //如果是空的
                        val tu = Tu()
                        tu.address = url
                        tu.name = tv_title.text.toString()
                        tu.preview = arguments?.getString("preview") ?: ""
                        tu.time = System.currentTimeMillis()
                        tu.siteClass = this@FgtSeePic.siteClass
                        insert(TuTable.TABLE_NAME, *tu.map.toVarargArray())
                        tv_left.text = "取消收藏"
                    } else {
                        delete(TuTable.TABLE_NAME, TuTable.ADDRESS + "=?", arrayOf(url))
                        tv_left.text = "收藏"
                    }

                    EventBus.getDefault().post(TuFavoriteEvent(0))
                }
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mVm.cleanData()
    }
}
