package com.github.wongxd.text_lib.data.bean

import com.github.wongxd.text_lib.text.FgtTextItem
import java.io.Serializable

/**
 * Created by wongxd on 2018/3/8.
 */
//文字
class TextSiteBean(val title: String, val fgt: FgtTextItem)

data class TextListBean(val title: String, val author: String, val preview: String, val content: String = "", val textId: String = "") : Serializable

//文字