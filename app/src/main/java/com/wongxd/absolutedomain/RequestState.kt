package com.wongxd.absolutedomain

/**
 * Created by wongxd on 2018/1/6.
 */
enum class RequestState() {
    REFRESH, LOADMORE;

    var state = 0   //0-失败   otherwise-成功
    var info = ""
}