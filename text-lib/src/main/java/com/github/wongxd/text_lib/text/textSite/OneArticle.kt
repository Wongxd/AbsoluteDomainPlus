package com.github.wongxd.text_lib.text.textSite

import com.github.wongxd.core_lib.data.bean.text.OneArticleBean
import com.github.wongxd.core_lib.data.net.WNet
import com.github.wongxd.text_lib.data.bean.TextListBean
import com.github.wongxd.text_lib.text.BaseTextSite
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/25.
 */
class OneArticle : BaseTextSite {
    override fun getContent(textId: String, succeeded: (info: String?) -> Unit, failed: (info: String) -> Unit) {

    }

    private val todayUrl = "https://interface.meiriyiwen.com/article/today?dev=1"

    private val prevUrlPer = "https://interface.meiriyiwen.com/article/day?dev=1&date="

    private var prevUrl = prevUrlPer

    override fun getList(page: Int, succeeded: (list: List<TextListBean>?) -> Unit, failed: (info: String) -> Unit) {
        val url = if (page == 1) todayUrl else prevUrl
        doAsync {
            WNet.getTextList(url, successed = {
                val article = Gson().fromJson(it, OneArticleBean::class.java)
                val list: MutableList<TextListBean> = ArrayList()
                prevUrl = prevUrlPer + article.data.date.prev

                article.data.let { list.add(TextListBean(it.title, it.author, it.digest, it.content)) }

                uiThread { succeeded.invoke(list) }
            }, failed = {
                val info = it
                uiThread { failed.invoke(info) }
            })
        }
    }
}