package com.github.wongxd.video_lib.data.bean

import com.github.wongxd.video_lib.video.FgtVideoItem
import java.io.Serializable

/**
 * Created by wongxd on 2018/3/8.
 */
//视频
class VideoSiteBean(val title: String, val fgt: FgtVideoItem)

data class VideoListBean(val title: String, val preview: String, val url: String, val duration: Long) : Serializable

//视频