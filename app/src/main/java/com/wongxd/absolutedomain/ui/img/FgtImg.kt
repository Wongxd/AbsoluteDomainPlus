package com.wongxd.absolutedomain.ui.img

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.tapadoo.alerter.Alerter
import com.wongxd.absolutedomain.FgtMain
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.RequestState
import com.wongxd.absolutedomain.base.decorator.DividerItemDecoration
import com.wongxd.absolutedomain.base.kotin.extension.database.parseList
import com.wongxd.absolutedomain.base.kotin.extension.database.toVarargArray
import com.wongxd.absolutedomain.data.bean.ImgSiteBean
import com.wongxd.absolutedomain.data.bean.ImgTypeBean
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.data.database.Tu
import com.wongxd.absolutedomain.data.database.TuTable
import com.wongxd.absolutedomain.data.database.tuDB
import com.wongxd.absolutedomain.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.ui.img.adapter.RvFgtImgAdapter
import com.wongxd.absolutedomain.ui.img.event.TuFavoriteEvent
import kotlinx.android.synthetic.main.fgt_img.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction


/**
 * Created by wongxd on 2018/1/5.
 */
class FgtImg : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_img
    }

    companion object {

        fun newInstance(args: Bundle = Bundle()): FgtImg {
            val fragment = FgtImg()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var siteAdapter: SiteAdapter
    private lateinit var adapter: RvFgtImgAdapter
    private lateinit var mVm: TuViewModel

    @SuppressLint("SetTextI18n")
    override fun initView(mView: View?) {
        EventBus.getDefault().register(this)
        mVm = ViewModelProviders.of(_mActivity).get(TuViewModel::class.java)
        initRecycle()
        bottom_sheet_fgt_img.setOnClickListener {
            bottom_sheet_fgt_img.toggle()
        }
        initSiteSwitch()
        bottom_sheet_fgt_img.setOnProgressListener {

            if (it > 0.8) {
                tab_fgt_img.visibility = View.GONE
            } else {
                tab_fgt_img.visibility = View.VISIBLE
            }
            tab_fgt_img.alpha = 1.toFloat() - it


        }
        srl_fgt_img.setOnRefreshListener { rv_fgt_img.scrollToPosition(0);mVm.refreshList() }
        srl_fgt_img.setOnLoadmoreListener { mVm.addPageList() }

        mVm.siteList.observe(this, Observer {
            siteAdapter.setNewData(it)
        })
        mVm.tuList.observe(this, object : Observer<MutableList<TuListBean>> {
            override fun onChanged(t: MutableList<TuListBean>?) {
                t?.let {
                    if (mVm.currentPage != 1)
                        adapter.addData(it)
                    else
                        adapter.setNewData(it)

                }
            }

        })
        mVm.getListState.observe(this, object : Observer<RequestState> {
            override fun onChanged(t: RequestState?) {
                t?.let {
                    if (it == RequestState.REFRESH) {
                        srl_fgt_img.finishRefresh()
                    } else srl_fgt_img.finishLoadmore()

                    if (it.state == 0) {
                        activity?.let { it1 ->
                            Alerter.create(it1)
                                    .setTitle("get wrong img ---")
                                    .setText(it.info)
                                    .enableSwipeToDismiss()
                                    .setBackgroundColorInt(Color.RED)
                                    .show()
                        }
                    }
                }
            }

        })


        mVm.typeList.observe(this, Observer {
            it?.let {
                initTab(it)
                srl_fgt_img.autoRefresh()
            }
        })

        tv_site_switch_fgt_img.text = "切换站点 （${TuViewModel.defaultTuSite.javaClass.simpleName}）"

        bottom_sheet_fgt_img.collapse()
    }


    private fun initSiteSwitch() {
        siteAdapter = SiteAdapter()
        rv_site_fgt_img.adapter = siteAdapter
        rv_site_fgt_img.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_site_fgt_img.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))

    }

    inner class SiteAdapter : BaseQuickAdapter<ImgSiteBean, BaseViewHolder>(R.layout.item_rv_site) {
        override fun convert(helper: BaseViewHolder?, item: ImgSiteBean?) {

            helper?.getView<TextView>(R.id.tv)?.apply {
                text = if (item?.site?.name == TuViewModel.defaultTuSite.javaClass.name) item?.title + "(当前)" else item?.title
            }

            helper?.itemView?.setOnClickListener {
                mVm.changeSite(item?.site!!)
                notifyDataSetChanged()
                bottom_sheet_fgt_img.toggle()
                tv_site_switch_fgt_img.text = "切换站点  (${item.title}) "
            }
        }
    }

    private fun initTab(typeList: List<ImgTypeBean>) {
        tab_fgt_img.removeAllTabs()
        typeList.forEach { tab_fgt_img.addTab(tab_fgt_img.newTab().setText(it.title)) }
        tab_fgt_img.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                rv_fgt_img.scrollToPosition(0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    mVm.changeType(it.position)
                    srl_fgt_img.autoRefresh()
                }
            }
        })

    }


    private fun initRecycle() {

        adapter = RvFgtImgAdapter { view, tuListBean ->

            val b = Bundle()
            b.putString("url", tuListBean.url)
            b.putString("preview", tuListBean.preview)
            b.putString("title", tuListBean.title)
            b.putString("siteClass", TuViewModel.defaultTuSite.javaClass.name)
            (parentFragment as FgtMain).startBrotherFragment(FgtSeePic.newInstance(b))

        }
        adapter.setEnableLoadMore(true)
        adapter.setOnItemLongClickListener { adapter1, view1, position ->
            //收藏
            adapter.data.let {
                val bean = it[position]
                activity?.tuDB?.use {
                    transaction {
                        val items = select(TuTable.TABLE_NAME).whereSimple(TuTable.ADDRESS + "=?", bean.url)
                                .parseList({ Tu(HashMap(it)) })

                        if (items.isEmpty()) {  //如果是空的
                            val tu = Tu()
                            tu.address = bean.url
                            tu.name = bean.title
                            tu.preview = bean.preview
                            tu.time = System.currentTimeMillis()
                            tu.siteClass = TuViewModel.defaultTuSite.javaClass.name
                            insert(TuTable.TABLE_NAME, *tu.map.toVarargArray())
                        } else {
                            delete(TuTable.TABLE_NAME, TuTable.ADDRESS + "=?", arrayOf(bean.url))
                        }
                        adapter.notifyItemChanged(position, "changeFavorite")
                    }
                }
            }

            return@setOnItemLongClickListener true
        }


        rv_fgt_img.adapter = adapter
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adapter.data.size == 0)
                    return 2
                return 1
            }

        }
        rv_fgt_img.layoutManager = layoutManager
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
//        adapter.setEmptyView(R.layout.item_rv_empty, rv_fgt_img)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun doSyncFavorite(event: TuFavoriteEvent) {
        adapter.notifyDataSetChanged()
    }


}