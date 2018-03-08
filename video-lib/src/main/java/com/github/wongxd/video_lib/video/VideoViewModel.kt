package com.github.wongxd.video_lib.video

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.wongxd.video_lib.data.bean.VideoListBean


/**
 * Created by wongxd on 2018/1/23.
 */
class VideoViewModel : ViewModel() {

    val videoList: MutableLiveData<MutableList<VideoListBean>> = MutableLiveData()



}