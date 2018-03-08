package com.github.wongxd.video_lib.video

import com.github.wongxd.video_lib.data.bean.VideoListBean


/**
 * Created by wongxd on 2018/1/23.
 */
interface BaseVideoSite {

    fun getList(page: Int, succeeded: (list: List<VideoListBean>?) -> Unit, failed: (info: String) -> Unit)

}