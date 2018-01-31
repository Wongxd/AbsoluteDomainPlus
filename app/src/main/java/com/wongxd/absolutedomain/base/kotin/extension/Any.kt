package com.wongxd.absolutedomain.base.kotin.extension

/**
 * Created by wongxd on 2018/1/24.
 */
fun Long.sec2mim(s: Long=this): String {

    fun unitFormat(i: Long): String {
        return if (i in 0..9)
            "0" + i.toString()
        else
            "" + i
    }

    var timeStr: String = ""
    var hour = 0L
    var minute = 0L
    var second = 0L
    if (s <= 0)
        return "00:00"
    else {
        minute = s / 60
        if (minute < 60) {
            second = s % 60
            timeStr = unitFormat(minute) + ":" + unitFormat(second)
        } else {
            hour = minute / 60
            if (hour > 99)
                return "99:59:59"
            minute = minute % 60
            second = s - hour * 3600 - minute * 60
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second)
        }
    }
    return timeStr

}