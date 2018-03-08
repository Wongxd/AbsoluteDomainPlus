package com.github.wongxd.video_lib.video.videosSite

import com.github.wongxd.core_lib.data.bean.video.DuanZiBean
import com.github.wongxd.core_lib.data.net.WNet
import com.github.wongxd.video_lib.data.bean.VideoListBean
import com.github.wongxd.video_lib.video.BaseVideoSite
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/23.
 */
class NeiHan : BaseVideoSite {
    override fun getList(page: Int, succeeded: (list: List<VideoListBean>?) -> Unit, failed: (info: String) -> Unit) {
        doAsync {
            WNet.getNeHanList("http://iu.snssdk.com/neihan/stream/mix/v1/",
                    {
                        try {
                            val nehanList = Gson().fromJson(it, DuanZiBean::class.java)
                            val list: MutableList<VideoListBean> = ArrayList()
                            nehanList.data.data.mapTo(list, {
                                var videoPath: String = ""
                                try {
                                    videoPath = it.group.`_$360p_video`.url_list[0].url
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (videoPath.isBlank()) {
                                    try {
                                        videoPath = it.group.`_$480p_video`.url_list[0].url
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                if (videoPath.isBlank()) {
                                    try {
                                        videoPath = it.group.`_$720p_video`.url_list[0].url
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                var title = ""
                                try {
                                    title = it.group.title
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                if (title.isBlank()) {
                                    try {
                                        title = it.group.content
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                var imgPath = ""
                                try {
                                    imgPath = it.group.large_cover.url_list[0].url
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                if (imgPath.isBlank()) {
                                    try {
                                        imgPath = it.group.medium_cover.url_list[0].url
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                val duration = it.group.duration.toLong()
                                VideoListBean(title, imgPath, videoPath, duration)
                            })

                            uiThread { succeeded.invoke(list) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            uiThread { succeeded.invoke(null) }
                        }
                    },
                    { val info = it;uiThread { failed.invoke(info) } })


        }
    }
}