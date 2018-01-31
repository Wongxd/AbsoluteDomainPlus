package com.wongxd.absolutedomain.ui.video.videosSite

import com.wongxd.absolutedomain.data.bean.VideoListBean
import com.wongxd.absolutedomain.data.net.WNet
import com.wongxd.absolutedomain.ui.video.BaseVideoSite
import com.wongxd.absolutedomain.data.bean.video.EyepetizerBean
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/24.
 */
class Eyepetizer : BaseVideoSite {

    private val firstPageUrl = "http://baobab.kaiyanapp.com/api/v2/feed?num=2&udid=26868b32e808498db32fd51fb422d00175e179df&vc=83"
    private var nextPageUrl = ""
    override fun getList(page: Int, succeeded: (list: List<VideoListBean>?) -> Unit, failed: (info: String) -> Unit) {
        doAsync {
            val url = if (page == 1) firstPageUrl else nextPageUrl

            WNet.getVideoList(url, successed = {
                val list: MutableList<VideoListBean> = ArrayList()
                val eyepetizerBean = Gson().fromJson(it, EyepetizerBean::class.java)

                nextPageUrl = eyepetizerBean.nextPageUrl ?: firstPageUrl

                eyepetizerBean.issueList
                        ?.flatMap { it.itemList ?: emptyList() }
                        ?.filter { it.type.equals("video") }
                        ?.forEach {
                            val title = it.data?.title ?: ""
                            val preview = it.data?.cover?.homepage ?: ""
                            val playUrl = it.data?.playUrl ?: ""
                            val duration = it.data?.duration ?: 0L
                            list.add(VideoListBean(title, preview, playUrl, duration))
                        }

                uiThread { succeeded.invoke(list) }
            }, failed = {
                val info = it
                uiThread { failed.invoke(info) }
            })
        }


    }
}