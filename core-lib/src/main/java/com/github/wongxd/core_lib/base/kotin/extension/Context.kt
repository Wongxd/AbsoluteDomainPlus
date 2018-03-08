package com.github.wongxd.core_lib.base.kotin.extension

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import com.github.wongxd.core_lib.R

/**
 * Created by wongxd on 2018/1/20.
 */

fun Context.getPrimaryColor(): Int {
    val typeValue = TypedValue()
    this.theme.resolveAttribute(R.attr.primary_color, typeValue, true)
    return ContextCompat.getColor(this, typeValue.resourceId)
}