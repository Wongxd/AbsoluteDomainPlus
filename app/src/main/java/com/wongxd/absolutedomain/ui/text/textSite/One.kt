package com.wongxd.absolutedomain.ui.text.textSite

import com.wongxd.absolutedomain.data.bean.TextListBean
import com.wongxd.absolutedomain.data.net.WNet
import com.wongxd.absolutedomain.ui.text.BaseTextSite
import com.wongxd.absolutedomain.data.bean.text.EssayContentBean
import com.wongxd.absolutedomain.data.bean.text.EssayListBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by wongxd on 2018/1/26.
 */
class One : BaseTextSite {


    /**
     * 获取所有的阅读
     */
    private val allReadData = "http://v3.wufazhuce.com:8000/api/all/list/1?channel=wdj&version=4.5.1&uuid=ffffffff-a10b-7162-ffff-ffffdcf5520c&platform=android"

//    private val readingListUrl = "http://v3.wufazhuce.com:8000/api/reading/index/?version=3.5.0&platform=android"


    private fun essayDetailUrl(itemId: String) = "http://v3.wufazhuce.com:8000/api/essay/$itemId?version=3.5.0&platform=android"

    /**
     *
     * 获取特定日期的短文 一次获取一个月的
     * @param date  yyyy-MM-dd
     *
     */
    private fun essayListUrl(date: String = "2018-01-1") =
            "http://v3.wufazhuce.com:8000/api/essay/bymonth/$date%2000:00:00?channel=wdj" +
                    "&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android"

    /**
     *
     * 获取特定日期的连载 一次获取一个月的
     * @param date  yyyy-MM-dd
     *
     */
    private fun serialListUrl(date: String = "2018-01-01") = "http://v3.wufazhuce.com:8000/api/serialcontent/bymonth/$date%2000:00:00?channel=wdj&version=4.0.2&uuid=ffffffff-a90e-706a-63f7-ccf973aae5ee&platform=android"


    private val c: Calendar by lazy { Calendar.getInstance() }
    private var mMonth = 1
    private var mYear = 2018

    override fun getList(page: Int, succeeded: (list: List<TextListBean>?) -> Unit, failed: (info: String) -> Unit) {

        if (page == 1) {
            mYear = c.get(Calendar.YEAR) // 获取当前年份
            mMonth = c.get(Calendar.MONTH) + 1// 获取当前月份
        } else {
            mMonth--

            if (mMonth <=0) {
                mYear--
                mMonth = 12
            }
        }


        val m = if (mMonth < 9) "0$mMonth" else "$mMonth"
        val url = essayListUrl("$mYear-$m-01")

        doAsync {

            WNet.getTextList(url, successed = {

                val list: MutableList<TextListBean> = ArrayList()
                val essayList: EssayListBean = Gson().fromJson(it, object : TypeToken<EssayListBean>() {}.type)
                essayList.data.forEach {
                    val title ="${it.hp_title}---${it.hp_makettime}"
                    val sb = StringBuilder()
                    it.author.forEach { sb.append(it.user_name + "-") }
                    val author: String = sb.toString()
                    val preview = it.guide_word
                    val textId = it.content_id

                    list.add(TextListBean(title, author, preview,textId=textId))
                }

                uiThread { succeeded.invoke(list) }
            }, failed = {
                val info = it
                uiThread { failed.invoke(info) }
            })
        }
    }


    override fun getContent(textId: String, succeeded: (content: String?) -> Unit, failed: (info: String) -> Unit) {

        doAsync {
            WNet.getTextList(essayDetailUrl(textId), successed = {
                val contentBean: EssayContentBean = Gson().fromJson(it, EssayContentBean::class.java)

                uiThread { succeeded.invoke("<h1>${contentBean.data.hp_title}</h1>${contentBean.data.hp_content}") }

            }, failed = {
                val info = it
                uiThread { failed.invoke(info) }
            })
        }
    }
}