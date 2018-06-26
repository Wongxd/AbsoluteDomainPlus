package com.github.wongxd.core_lib.base.kotin.extension

import java.io.UnsupportedEncodingException

/**
 * Created by wongxd on 2018/1/24.
 */
fun Long.sec2mim(s: Long = this): String {

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

//有损转换
@Throws(UnsupportedEncodingException::class)
fun String.getUTF8BytesFromGBKString(gbkStr: String = this): String {
    val n = gbkStr.length
    var utfBytes = ByteArray(3 * n)
    var k = 0
    for (i in 0 until n) {
        val m = gbkStr[i].toInt()
        if (m < 128 && m >= 0) {
            utfBytes[k++] = m.toByte()
            continue
        }
        utfBytes[k++] = (0xe0 or (m shr 12)).toByte()
        utfBytes[k++] = (0x80 or (m shr 6 and 0x3f)).toByte()
        utfBytes[k++] = (0x80 or (m and 0x3f)).toByte()
    }
    if (k < utfBytes.size) {
        val tmp = ByteArray(k)
        System.arraycopy(utfBytes, 0, tmp, 0, k)
        utfBytes = tmp

    }
    return String(utfBytes, Charsets.UTF_8)
}



