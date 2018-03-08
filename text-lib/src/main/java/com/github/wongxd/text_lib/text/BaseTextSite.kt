package com.github.wongxd.text_lib.text

import com.github.wongxd.text_lib.data.bean.TextListBean


/**
 * Created by wongxd on 2018/1/25.
 */
interface BaseTextSite {

    fun getList(page: Int, succeeded: (list: List<TextListBean>?) -> Unit, failed: (info: String) -> Unit)

    fun getContent(textId: String, succeeded: (content: String?) -> Unit, failed: (info: String) -> Unit)
}