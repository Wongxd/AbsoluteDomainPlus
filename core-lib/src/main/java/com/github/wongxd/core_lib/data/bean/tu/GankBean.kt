package com.github.wongxd.core_lib.data.bean.tu

/**
 * Created by wongxd on 2018/1/27.
 */
data class GankBean(val error:Boolean,val results:List<ResultBean>) {
    data class ResultBean(val url: String, val desc: String)
}