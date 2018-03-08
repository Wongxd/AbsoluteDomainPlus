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
import java.net.SocketTimeoutException
import java.net.URL

/**
 * Created by wongxd on 2018/1/27.
 */
class Tu4493 : BaseTuSite {
    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }

    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {
        if (TextUtils.isEmpty(url)) return null
        var tagUrl = ""
        tagUrl = url.replace("1.htm", "")
        val requestType = if (page == 1) RequestState.REFRESH else RequestState.LOADMORE

        useCache(url)?.let {
            if (requestType == RequestState.LOADMORE) {
                tViewModel.changeGetListState(requestType, "已是最后一页", 0)
                return null
            } else {
                tViewModel.changeGetListState(requestType, "", 1)
                return it
            }
        }

        var doc: Document? = null
        val urls = ArrayList<String>()
        try {
            if (page == 1) {
                doc = Jsoup.parse(URL(url).readText(charset("gbk")))
                if (doc == null) return null
                val img = doc.getElementsByClass("picsbox picsboxcenter").first()
                        .getElementsByTag("img").first()

                val title = img.attr("alt")

                urls.add(img.attr("src"))
                tViewModel.setTitle(title)

            } else
                get4493Deep(tagUrl, page, urls)


            tViewModel.changeGetListState(requestType, "", 1)
            return urls
        } catch (e: Exception) {
            e.printStackTrace()
            tViewModel.changeGetListState(requestType, if (e is SocketTimeoutException) {
                "是不是网络出问题了呢"
            } else {
                if (page != 1) setCache(url, tViewModel.picTotalList.value)
                "可能没有下一页了哦"
            }, 0)
        }


        return null
    }


    /**
     * nvshens 递归调用 爬取
     */
    fun get4493Deep(tagUrl: String, page: Int, urls: ArrayList<String>) {
        val url = "$tagUrl/$page.htm"
        var doc: Document? = null
        doc = Jsoup.parse(URL(url).readText(charset("gbk")))
        val img = doc.getElementsByClass("picsbox picsboxcenter").first()
                .getElementsByTag("img").first()
        urls.add(img.attr("src"))
    }


    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        var originUrl = url
        var suffix = ""

        if (page != 1) {
            suffix = "index-$page.htm"
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val s = URL(originUrl).readText(charset(charsetName = "gbk"))

            val doc = Jsoup.parse(s)

            val select = doc.select(".mainer > .piclist > ul > li")

//            Logger.e(select.toString())

            for (i in select) {

                val element = i.select("a").first()

                val date = i.select(".b1").text()
                val preview = element.select("img").attr("src")

                val imgUrl = "https://www.4493.com" + element.attr("href")

                val description = element.select("span").text()

                list.add(TuListBean(description, preview, imgUrl, date))
//                Logger.e("女神 $preview  url   $imgUrl  描述 $description  时间  $date")
            }


            state = list.size
        } catch (e: Exception) {
            info = e.message ?: ""
        }


        tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, info, state)

        return list


    }


    companion object {
        //index-1.htm
        val typeList = arrayListOf(
                ImgTypeBean("https://www.4493.com/gaoqingmeinv/", "最新"),
                ImgTypeBean("https://www.4493.com/xingganmote/", "性感"),
                ImgTypeBean("https://www.4493.com/siwameitui/", "丝袜"),
                ImgTypeBean("https://www.4493.com/weimeixiezhen/", "唯美"),
                ImgTypeBean("https://www.4493.com/motemeinv/", "模特"),
                ImgTypeBean("https://www.4493.com/tiyumeinv/", "体育"),
                ImgTypeBean("https://www.4493.com/dongmanmeinv/", "动漫")
        )
    }
}