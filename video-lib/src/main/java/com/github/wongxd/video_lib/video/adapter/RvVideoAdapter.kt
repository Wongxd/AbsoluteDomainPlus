package com.github.wongxd.video_lib.video.adapter

import android.graphics.Color
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.jzvd.JZVideoPlayer
import cn.jzvd.JZVideoPlayerStandard
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.wongxd.core_lib.base.kotin.extension.database.parseList
import com.github.wongxd.core_lib.base.kotin.extension.database.toVarargArray
import com.github.wongxd.core_lib.base.kotin.extension.sec2mim
import com.github.wongxd.core_lib.data.bean.Task
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.data.database.Video
import com.github.wongxd.core_lib.data.database.VideoTable
import com.github.wongxd.core_lib.data.database.videoDB
import com.github.wongxd.core_lib.data.download.DownLoadManager
import com.github.wongxd.core_lib.util.Tips
import com.github.wongxd.video_lib.R
import com.github.wongxd.video_lib.data.bean.VideoListBean
import com.wongxd.absolutedomain.data.database.Tu
import loadImg
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/24.
 */
class RvVideoAdapter : BaseQuickAdapter<VideoListBean, BaseViewHolder>(R.layout.item_rv_video) {

    override fun convert(helper: BaseViewHolder?, item: VideoListBean?) {
        setData(helper!!, item!!, null)
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int, payloads: MutableList<Any>?) {
        setData(holder, mData[(position - headerLayoutCount)], payloads)
    }


    private fun setData(helper: BaseViewHolder?, item: VideoListBean?, payloads: MutableList<Any>?) {
        if (payloads == null || payloads.isEmpty()) {
            helper?.let {
                val videoPlayer = it.getView<JZVideoPlayerStandard>(R.id.videoplayer)
                videoPlayer.setUp(item?.url, JZVideoPlayer.SCREEN_WINDOW_LIST, item?.title)
                videoPlayer.thumbImageView.loadImg(item?.preview!!)

                it.getView<ImageView>(R.id.iv_download)
                        .setOnClickListener { createTask(item) }

                it.setText(R.id.tv_duration, "时长：" + item.duration.sec2mim())

                it.getView<TextView>(R.id.tv_favorite).setOnClickListener {
                    val bean = item
                    mContext.videoDB.use {
                        transaction {
                            val items = select(VideoTable.TABLE_NAME).whereSimple(VideoTable.ADDRESS + "=?", bean.url)
                                    .parseList({ Tu(HashMap(it)) })

                            if (items.isEmpty()) {  //如果是空的
                                val video = Video()
                                video.address = bean.url
                                video.name = bean.title
                                video.preview = bean.preview
                                video.duration = bean.duration.toString()
                                video.time = System.currentTimeMillis()
                                insert(VideoTable.TABLE_NAME, *video.map.toVarargArray())
                            } else {
                                delete(VideoTable.TABLE_NAME, VideoTable.ADDRESS + "=?", arrayOf(bean.url))
                            }
                            notifyItemChanged(helper.layoutPosition, "changeFavorite")
                        }
                    }
                }
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
    private fun changeFavoriteState(item: VideoListBean, helper: BaseViewHolder?) {

        if (helper == null) return

        mContext.videoDB.use {
            val list = select(VideoTable.TABLE_NAME).whereSimple(VideoTable.ADDRESS + "=?", item.url)
                    .parseList { Video(HashMap(it)) }
            if (list.isNotEmpty()) {
                helper.getView<LinearLayout>(R.id.ll).setBackgroundColor(Color.parseColor("#f97198"))
                helper.setText(R.id.tv_favorite, "取消收藏")
            } else {
                helper.getView<LinearLayout>(R.id.ll).setBackgroundColor(Color.WHITE)
                helper.setText(R.id.tv_favorite, "加入收藏")
            }
        }
    }


    private fun createTask(bean: VideoListBean) {
        doAsync {
            DownLoadManager.get().addTask(Task(bean.url, bean.title, bean.preview, TaskType.VIDEO))
            uiThread { Tips.showSuccessTips(title = "视频下载任务", text = "${bean.title}  添加成功") }
        }
    }
}