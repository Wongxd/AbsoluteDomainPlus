package com.github.wongxd.text_lib.text

import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import cn.jzvd.JZVideoPlayer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.wongxd.core_lib.base.decorator.DividerItemDecoration
import com.github.wongxd.core_lib.base.kotin.extension.database.parseList
import com.github.wongxd.core_lib.base.kotin.extension.database.toVarargArray
import com.github.wongxd.core_lib.data.database.Text
import com.github.wongxd.core_lib.data.database.TextTable
import com.github.wongxd.core_lib.data.database.textDB
import com.github.wongxd.core_lib.fragmenaction.MainTabFragment
import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.core_lib.util.Tips
import com.github.wongxd.text_lib.R
import com.github.wongxd.text_lib.data.bean.TextListBean
import com.github.wongxd.text_lib.text.event.TextFavoriteEvent
import com.wongxd.absolutedomain.data.database.Tu
import kotlinx.android.synthetic.main.fgt_text_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtTextItem : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_text_item
    }

    companion object {

        fun newInstance(siteClass: Class<out BaseTextSite>): FgtTextItem {
            val args: Bundle = Bundle()
            args.putString("siteClass", siteClass.name)
            val fragment = FgtTextItem()
            fragment.arguments = args
            return fragment
        }
    }

    private val siteClass: BaseTextSite by lazy { Class.forName(arguments?.getString("siteClass")).newInstance() as BaseTextSite }

    private lateinit var adapter: RvAdapter

    private var page = 1

    override fun initView(mView: View?) {

        EventBus.getDefault().register(this)

        srl_text.setOnRefreshListener { page = 1; getList() }

        srl_text.setOnLoadmoreListener { getList() }

        adapter = RvAdapter {
            try {
                (parentFragment as FgtText).startNewFgt(FgtSeeText.newInstance(it, siteClass.javaClass.name))
            } catch (e: Exception) {
                TU.cT("唉呀，小域已经更新了，以前的备份不能用了啦。")
            }
        }
        adapter.setOnItemLongClickListener { adapter1, view, position ->

            val bean = adapter.data[position]
            activity?.textDB?.use {
                transaction {
                    val items = select(TextTable.TABLE_NAME).whereSimple(TextTable.NAME + "=?", bean.title)
                            .parseList({ Tu(HashMap(it)) })

                    if (items.isEmpty()) {  //如果是空的
                        val text = Text()
                        text.author = bean.author
                        text.name = bean.title
                        text.preview = bean.preview
                        text.content = bean.content
                        text.time = System.currentTimeMillis()
                        text.textId = bean.textId
                        text.siteClass = siteClass.javaClass.name
                        insert(TextTable.TABLE_NAME, *text.map.toVarargArray())
                    } else {
                        delete(TextTable.TABLE_NAME, TextTable.NAME + "=?", arrayOf(bean.title))
                    }
                    adapter.notifyItemChanged(position, "changeFavorite")
                }
            }

            return@setOnItemLongClickListener true
        }
        rv_text.adapter = adapter
        rv_text.layoutManager = LinearLayoutManager(activity)
//        adapter.setEmptyView(R.layout.item_rv_empty, rv_video)

        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
        rv_text.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))

        srl_text.autoRefresh()
    }

    private fun getList() {
        JZVideoPlayer.releaseAllVideos()
        siteClass.getList(page, {
            if (page != 1) {
                srl_text.finishLoadmore()
                adapter.addData(it ?: listOf(null))
            } else {
                srl_text.finishRefresh()
                adapter.setNewData(it ?: listOf(null))
            }

            page++

        }, {
            if (page != 1)
                srl_text.finishLoadmore()
            else
                srl_text.finishRefresh()
            Tips.showErrorTips(text = it)
        })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
    fun doSyncFavorite(event: TextFavoriteEvent) {
        adapter.notifyDataSetChanged()
    }


    class RvAdapter(val click: (bean: TextListBean) -> Unit) : BaseQuickAdapter<TextListBean, BaseViewHolder>(R.layout.item_rv_text) {

        override fun convert(helper: BaseViewHolder?, item: TextListBean?) {
            setData(helper, item, null)
        }

        override fun onBindViewHolder(holder: BaseViewHolder?, position: Int, payloads: MutableList<Any>?) {
            setData(holder, mData[(position - headerLayoutCount)], payloads)
        }


        private fun setData(helper: BaseViewHolder?, item: TextListBean?, payloads: MutableList<Any>?) {
            if (payloads == null || payloads.isEmpty()) {
                helper?.let {

                    it.setText(R.id.tv_title, item?.title)
                            .setText(R.id.tv_author, item?.author)
                            .setText(R.id.tv_preview, item?.preview)

                    it.itemView.setOnClickListener { click(item!!) }
                    changeFavoriteState(item, helper)
                }
            } else {
                //适配局部刷新
                item?.let { changeFavoriteState(it, helper) }
            }
        }


        /**
         * 改变收藏状态
         */
        private fun changeFavoriteState(item: TextListBean?, helper: BaseViewHolder?) {

            if (helper == null) return

            mContext.textDB.use {
                val list = select(TextTable.TABLE_NAME).whereSimple(TextTable.NAME + "=?", item?.title
                        ?: "")
                        .parseList { Text(HashMap(it)) }
                if (list.isNotEmpty()) {
                    helper.getView<LinearLayout>(R.id.ll).setBackgroundColor(Color.parseColor("#f97198"))
                } else {
                    helper.getView<LinearLayout>(R.id.ll).setBackgroundColor(Color.WHITE)
                }
            }
        }


    }
}