package com.wongxd.absolutedomain.ui.text

import android.os.Bundle
import android.webkit.WebView
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.kotin.extension.database.parseList
import com.wongxd.absolutedomain.base.kotin.extension.database.toVarargArray
import com.wongxd.absolutedomain.data.bean.TextListBean
import com.wongxd.absolutedomain.data.database.Text
import com.wongxd.absolutedomain.data.database.TextTable
import com.wongxd.absolutedomain.data.database.textDB
import com.wongxd.absolutedomain.fragmenaction.BaseBackFragment
import com.wongxd.absolutedomain.ui.text.event.TextFavoriteEvent
import com.wongxd.absolutedomain.util.Tips
import kotlinx.android.synthetic.main.fgt_see_text.*
import kotlinx.android.synthetic.main.layout_w_toolbar.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import org.jetbrains.anko.db.transaction

/**
 * Created by wongxd on 2018/1/25.
 */
class FgtSeeText : BaseBackFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fgt_see_text
    }

    companion object {

        fun newInstance(bean: TextListBean, siteClass: String): FgtSeeText {
            val args: Bundle = Bundle()
            args.putString("title", bean.title)
            args.putString("author", bean.author)
            args.putString("preview", bean.preview)
            args.putString("content", bean.content)
            args.putString("siteClass", siteClass)
            args.putString("textId", bean.textId)
            val fragment = FgtSeeText()
            fragment.arguments = args
            return fragment
        }
    }


    val title: String by lazy { arguments.getString("title") }
    val author: String by lazy { arguments.getString("author") }
    val preview: String by lazy { arguments.getString("preview") }
    val content: String by lazy { arguments.getString("content") ?: "" }
    val siteClass: String by lazy { arguments.getString("siteClass") }
    val textId: String by lazy { arguments.getString("textId") }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tv_title.text = title + "  by  $author"
        webview_see_text.isVerticalScrollBarEnabled = true
        webview_see_text.scrollBarStyle = WebView.SCROLLBARS_INSIDE_INSET

        doFavoriteLogic(title)

        if (textId.isNotBlank()) {
            val site: BaseTextSite = Class.forName(siteClass).newInstance() as BaseTextSite
            site.getContent(textId, { content ->
                showContent(content ?: "小域 没能加载到内容")
            }, {
              Tips.showErrorTips(text = it)
            })
        } else
            showContent()
    }

    private fun showContent(content: String = this.content) {
        webview_see_text.loadDataWithBaseURL(null, getHtmlData("$content\n<p  align=\"center\">--end--</p>"),
                "text/html", "utf-8", null)
    }

    /**
     * webView 适配图片
     * @param bodyHTML
     * @return
     */
    private fun getHtmlData(bodyHTML: String): String {
        val head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>"
        return "<html>$head<body>$bodyHTML</body></html>"
    }

    private fun doFavoriteLogic(title: String) {


        activity.textDB.use {

            val items = select(TextTable.TABLE_NAME).whereSimple(TextTable.NAME + "=?", title)
                    .parseList({ Text(HashMap(it)) })
            if (items.isEmpty()) {  //如果是空的
                tv_left.text = "收藏"
            } else {
                tv_left.text = "取消收藏"
            }


        }


        tv_left.setOnClickListener {
            activity.textDB.use {
                transaction {
                    val items = select(TextTable.TABLE_NAME).whereSimple(TextTable.NAME + "=?", title)
                            .parseList({ Text(HashMap(it)) })

                    if (items.isEmpty()) {  //如果是空的
                        val text = Text()
                        text.author = author
                        text.name = title
                        text.preview = preview
                        text.time = System.currentTimeMillis()
                        text.siteClass = siteClass
                        insert(TextTable.TABLE_NAME, *text.map.toVarargArray())
                        tv_left.text = "取消收藏"
                    } else {
                        delete(TextTable.TABLE_NAME, TextTable.NAME + "=?", arrayOf(title))
                        tv_left.text = "收藏"
                    }

                    EventBus.getDefault().post(TextFavoriteEvent(0))
                }
            }

        }
    }
}