package com.github.wongxd.core_lib.custom.whatsnew.item

import android.graphics.Color
import android.support.annotation.DrawableRes
import com.github.wongxd.core_lib.custom.whatsnew.WhatsNew
import com.github.wongxd.core_lib.custom.whatsnew.util.PresentationOption

/**
 * Created by yaoda on 12/1/17.
 */

@DslMarker
internal annotation class WhatsNews

fun whatsNew(init: WhatNewsBuilder.() -> Unit): WhatsNew {
    val list = WhatNewsBuilder()
    list.init()
    return WhatsNew.newInstance(list).apply {
        this.presentationOption = list.presentationOption
        this.titleText = list.titleText
        this.titleColor = list.titleColor
        this.itemTitleColor = list.itemTitleColor
        this.itemContentColor = list.itemContentColor
        this.buttonBackground = list.buttonBackground
        this.buttonText = list.buttonText
        this.buttonTextColor = list.buttonTextColor
    }
}

fun WhatNewsBuilder.item(init: WhatsNewItemBuilder.() -> Unit) {
    val builder = WhatsNewItemBuilder()
    builder.init()
    val item = builder.build()
    this.add(item)
}


@WhatsNews
class WhatsNewItemBuilder {
    var title: String = ""
    var content: String = ""
    @DrawableRes
    var imageRes: Int? = null

    fun build(): WhatsNewItem {
        return WhatsNewItem(title, content, imageRes)
    }
}

@WhatsNews
class WhatNewsBuilder : ArrayList<WhatsNewItem>() {
    var presentationOption: PresentationOption = PresentationOption.IF_NEEDED
    var titleText: CharSequence = "What's New"
    var titleColor: Int = Color.parseColor("#000000")
    var itemTitleColor: Int? = null
    var itemContentColor: Int? = null
    var buttonBackground: Int = Color.parseColor("#000000")
    var buttonText: String = "Continue"
    var buttonTextColor: Int = Color.parseColor("#FFEB3B")
}