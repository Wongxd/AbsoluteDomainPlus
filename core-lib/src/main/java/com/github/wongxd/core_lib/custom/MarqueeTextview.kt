package com.github.wongxd.core_lib.custom

import android.content.Context
import android.util.AttributeSet

/**
 * Created by wongxd on 2018/1/5.
 */
class MarqueeTextview : android.support.v7.widget.AppCompatTextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun isFocused(): Boolean {
        return true
    }

}