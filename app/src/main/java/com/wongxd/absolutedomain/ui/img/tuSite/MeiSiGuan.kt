package com.wongxd.absolutedomain.ui.img.tuSite

import android.text.TextUtils
import com.wongxd.absolutedomain.RequestState
import com.wongxd.absolutedomain.data.bean.TuChildDetailBean
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.data.bean.ImgTypeBean
import com.wongxd.absolutedomain.ui.img.BaseTuSite
import com.wongxd.absolutedomain.ui.img.SeePicViewModel
import com.wongxd.absolutedomain.ui.img.TuViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

/**
 * Created by wongxd on 2018/1/6.
 */
class MeiSiGuan : BaseTuSite {

    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {
        if (TextUtils.isEmpty(url)) return null
        var tagUrl = ""
        tagUrl = url.replace(".html", "")
        val requestType = if (page == 1) RequestState.REFRESH else RequestState.LOADMORE

        if (page != 1) {
            tViewModel.changeGetListState(requestType, "可能没有下一页了哦", 0)
            return null
        }

        useCache(url)?.let {
            tViewModel.changeGetListState(requestType, "", 1)
            return it
        }

        var doc: Document? = null
        var childList: TuChildDetailBean? = null
        val urls = ArrayList<String>()
        try {

            doc = Jsoup.parse(URL(url).readText())

            if (doc == null) return null

            val div = doc.getElementById("content")
            val imgs = div?.getElementsByClass("content_left")?.first()?.getElementsByTag("img")

            val title = doc.title() ?: "美丝馆"


            imgs?.mapTo(urls) { it.attr("src") }

            tViewModel.setTitle(title)
            tViewModel.changeGetListState(requestType, "", 1)
            return urls
        } catch (e: Exception) {
            e.printStackTrace()
            tViewModel.changeGetListState(requestType, if (e is NullPointerException) {
                setCache(url, urls)
                "可能没有下一页了哦"
            } else "是不是网络出问题了呢", 0)
        }

        return urls
    }

    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }


    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        var originUrl = url
        var suffix = ""
        if (page != 1) suffix = "page/$page/"
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val s = URL(originUrl).readText()

            val doc = Jsoup.parse(s)
            val ul = doc.getElementsByClass("update_area_lists cl").first()
            val lis = ul.getElementsByClass("i_list list_n1")

            for (li in lis) {
                val img = li.getElementsByClass("waitpic")
                val imgPreview = img.attr("data-original")
                val title = img.attr("alt")

                val a = li.getElementsByTag("a")
                val durl = a.attr("href")


                val time = li.getElementsByClass("meta-post").text()

                val homeListBean = TuListBean(title, imgPreview, durl, time)
                list.add(homeListBean)
//                Logger.e("imgPreview---$imgPreview----title----$title----durl---$durl---time---$time")
            }
            state = list.size
        } catch (e: Exception) {
            info = e.message ?: ""
        }


        tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, info, state)

        return list


    }


    companion object {
        val typeList = arrayListOf(
                ImgTypeBean("http://www.meisiguan.com/", "时间排序")
        )
    }
}