package com.wongxd.absolutedomain.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import cn.jzvd.JZVideoPlayer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.decorator.DividerItemDecoration
import com.wongxd.absolutedomain.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.ui.video.adapter.RvVideoAdapter
import com.wongxd.absolutedomain.ui.video.event.VideoFavoriteEvent
import com.wongxd.absolutedomain.util.Tips
import kotlinx.android.synthetic.main.fgt_video_item.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtVideoItem : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_video_item
    }

    companion object {

        fun newInstance(siteClass: Class<out BaseVideoSite>): FgtVideoItem {
            val args: Bundle = Bundle()
            args.putString("siteClass", siteClass.name)
            val fragment = FgtVideoItem()
            fragment.arguments = args
            return fragment
        }
    }

    private val siteClass: BaseVideoSite by lazy { Class.forName(arguments.getString("siteClass")).newInstance() as BaseVideoSite }

    private lateinit var adapter: RvVideoAdapter

    private var page = 1

    override fun initView(mView: View?) {

        EventBus.getDefault().register(this)

        srl_video.setOnRefreshListener { page = 1; getList() }

        srl_video.setOnLoadmoreListener { getList() }

        adapter = RvVideoAdapter()

        rv_video.adapter = adapter
        rv_video.layoutManager = LinearLayoutManager(activity)
        rv_video.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))

        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM)
//        adapter.setEmptyView(R.layout.item_rv_empty, rv_video)

        srl_video.autoRefresh()
    }

    private fun getList() {
        JZVideoPlayer.releaseAllVideos()
        siteClass.getList(page, {
            if (page != 1) {
                srl_video.finishLoadmore()
                adapter.addData(it ?: listOf(null))
            } else {
                srl_video.finishRefresh()
                adapter.setNewData(it ?: listOf(null))
            }

            page++

        }, {
            if (page != 1)
                srl_video.finishLoadmore()
            else
                srl_video.finishRefresh()
            Tips.showErrorTips(text = it)
        })
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        JZVideoPlayer.releaseAllVideos()
    }
    override fun onBackPressedSupport(): Boolean {

        if (JZVideoPlayer.backPress()) {
            return true
        }
        return super.onBackPressedSupport()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe
    fun doSyncFavorite(event: VideoFavoriteEvent) {
        adapter.notifyDataSetChanged()
    }


}