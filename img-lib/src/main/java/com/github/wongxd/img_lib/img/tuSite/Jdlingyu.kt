package com.github.wongxd.img_lib.img.tuSite

import android.text.TextUtils
import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.img_lib.data.bean.ImgTypeBean
import com.github.wongxd.img_lib.data.bean.TuListBean
import com.github.wongxd.img_lib.img.BaseTuSite
import com.github.wongxd.img_lib.img.SeePicViewModel
import com.github.wongxd.img_lib.img.TuViewModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL

/**
 * Created by wongxd on 2018/1/6.
 */
class Jdlingyu : BaseTuSite {
    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {
        if (TextUtils.isEmpty(url)) return null
        var doc: Document? = null
        val requestType = if (page == 1) RequestState.REFRESH else RequestState.LOADMORE
        if (page != 1) {
            tViewModel.changeGetListState(requestType, "可能没有下一页了哦", 0)
            return null
        }

        useCache(url)?.let {
            tViewModel.changeGetListState(requestType, "", 1)
            return it
        }

        try {
            doc = Jsoup.parse(URL(url).readText())
            val es_item = doc.getElementsByClass("main-body").first()
            val `as` = es_item.getElementsByTag("a")
            val urls = `as`.indices
                    .mapNotNull {
                        `as`[it]
                    }
                    .map {
                        it.attr("href")
                    }

            setCache(url, urls.toMutableList())
            tViewModel.changeGetListState(requestType, "", 1)
            return urls
        } catch (e: Exception) {
            e.printStackTrace()
            tViewModel.changeGetListState(requestType, if (e is NullPointerException) "可能没有下一页了哦" else "是不是网络出问题了呢", 0)
        }

        return null
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

            val select = Jsoup.parse(s).select("#postlist > div.pin")
            for (element in select) {

                var preview: String? = element.select("div.pin-coat > a > img").attr("original")
                        ?: " "
                if (preview == null || preview.length < 5) {
                    preview = element.select("div.pin-coat > a > img").attr("src") ?: ""
                }
                val title = element.select("div.pin-coat > a > img").attr("alt")

                val imgUrl = element.select("div.pin-coat > a").attr("href")

                val date = element.select("div.pin-coat > div.pin-data > span.timer > span").text()

                val like = element.select("div.pin-coat > div.pin-data > a.likes > span > span").text()

                val view = element.select("div.pin-coat > div.pin-data > a.viewsButton > span").text()

                list.add(TuListBean(title, preview, imgUrl, date))
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
                ImgTypeBean("http://www.jdlingyu.fun/", "最新"),
                ImgTypeBean("http://www.jdlingyu.fun/%e8%83%96%e6%ac%a1/", "胖次"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e4%b8%9d%e8%a2%9c/", "丝袜"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e6%b1%89%e6%9c%8d/", "汉服"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e6%ad%bb%e5%ba%93%e6%b0%b4/", "死库水"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e4%bd%93%e6%93%8d%e6%9c%8d/", "体操服"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e5%a5%b3%e4%bb%86%e8%a3%85/", "女仆装"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e6%b0%b4%e6%89%8b%e6%9c%8d/", "水手服&JK"),
                ImgTypeBean("http://www.jdlingyu.fun/%e7%89%b9%e7%82%b9/%e5%92%8c%e6%9c%8d%e6%b5%b4%e8%a1%a3/", "和服&浴衣")
        )
    }
}