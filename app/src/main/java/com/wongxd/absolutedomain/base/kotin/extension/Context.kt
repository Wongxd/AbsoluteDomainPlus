package com.wongxd.absolutedomain.base.kotin.extension

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import com.wongxd.absolutedomain.R

/**
 * Created by wongxd on 2018/1/20.
 */

fun Context.getPrimaryColor(): Int {
    val typeValue = TypedValue()
    this.theme.resolveAttribute(R.attr.primary_color, typeValue, true)
    return ContextCompat.getColor(this, typeValue.resourceId)
}