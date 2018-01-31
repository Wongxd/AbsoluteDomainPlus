package com.wongxd.absolutedomain.ui.video

import com.wongxd.absolutedomain.data.bean.VideoListBean

/**
 * Created by wongxd on 2018/1/23.
 */
interface BaseVideoSite {

    fun getList(page: Int, succeeded: (list: List<VideoListBean>?) -> Unit, failed: (info: String) -> Unit)

}