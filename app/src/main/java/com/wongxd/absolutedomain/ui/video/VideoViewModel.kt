package com.wongxd.absolutedomain.ui.video

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wongxd.absolutedomain.data.bean.TuListBean

/**
 * Created by wongxd on 2018/1/23.
 */
class VideoViewModel : ViewModel() {

    val videoList: MutableLiveData<MutableList<TuListBean>> = MutableLiveData()



}