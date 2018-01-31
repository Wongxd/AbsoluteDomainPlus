package com.wongxd.absolutedomain.ui.img.tuSite

import android.text.TextUtils
import com.wongxd.absolutedomain.RequestState
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.data.bean.ImgTypeBean
import com.wongxd.absolutedomain.ui.img.BaseTuSite
import com.wongxd.absolutedomain.ui.img.SeePicViewModel
import com.wongxd.absolutedomain.ui.img.TuViewModel
import com.orhanobut.logger.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.URL

/**
 * Created by wongxd on 2018/1/6.
 */
class NvShens : BaseTuSite {
    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {
        if (TextUtils.isEmpty(url)) return null
        var tagUrl = ""
        tagUrl = url.replace(".html", "")
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
                doc = Jsoup.parse(URL(url).readText())
                if (doc == null) return null
                val imgs = doc.getElementById("idiv").getElementsByTag("img")

                val title = imgs[0].attr("alt")

                imgs.mapTo(urls) { it.attr("src") }
                tViewModel.setTitle(title)

            } else
                getNvShensDeep(tagUrl, page, urls)


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
    fun getNvShensDeep(tagUrl: String, page: Int, urls: ArrayList<String>) {
        //https://m.nvshens.com/g/24724/2.html
        val url = tagUrl + "/$page.html"
        var doc: Document? = null
        doc = Jsoup.parse(URL(url).readText())
        val imgs = doc!!.getElementById("idiv").getElementsByTag("img")
        imgs.mapTo(urls) { it.attr("src") }
    }

    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }


    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        var originUrl = url
        var suffix = ""

        if (page != 1) {
            suffix = "/$page.html"
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val s = URL(originUrl).readText()

            val doc = Jsoup.parse(s)

            val select = doc.select("ul.clearfix > li")

            if (select.isEmpty()) {
                mapNvShensNormal(s, list)
            } else {

                Logger.e(select.toString())

                for (i in select) {

                    val element = i.select("a").first()

                    val date = ""
                    val preview = element.select("img").attr("lazysrc")

                    val url = "https://www.nvshens.com" + element.attr("href")

                    val description = element.select("img").attr("alt")

                    list.add(TuListBean(description, preview, url, date))
//            Logger.e("女神 $preview   $url  $description")
                }
            }



            state = list.size
        } catch (e: Exception) {
            info = e.message ?: ""
        }


        tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, info, state)

        return list


    }


    /**
     * https://www.nvshens.com/gallery/
     *
     * nvshens 图集列表
     */
    fun mapNvShensNormal(s: String, list: MutableList<TuListBean>) {

        val doc = Jsoup.parse(s)

        val select = doc.select("div#gallerydiv div.ck-initem")


        for (i in select) {

            val a = i.select("a").first()

            val url = a.attr("href")
            val date = ""
            val element = i.select("mip-img")
            val preview = element.attr("src")


            val description = element.attr("alt")

            list.add(TuListBean(description, preview, url, date))
//            Logger.e("女神 $preview   $url  $description")
        }

    }


    companion object {
        val typeList = arrayListOf(
                ImgTypeBean("https://www.nvshens.com/gallery/", "最新"),
                ImgTypeBean("https://www.nvshens.com/gallery/yazhou/", "亚洲"),
                ImgTypeBean("https://www.nvshens.com/gallery/rihan/", "日韩"),
                ImgTypeBean("https://www.nvshens.com/gallery/neidi/", "内地"),
                ImgTypeBean("https://www.nvshens.com/gallery/taiwan/", "台湾"),
                ImgTypeBean("https://www.nvshens.com/gallery/xianggang/", "香港"),
                ImgTypeBean("https://www.nvshens.com/gallery/aomen/", "澳门"),
                ImgTypeBean("https://www.nvshens.com/gallery/riben/", "日本"),
                ImgTypeBean("https://www.nvshens.com/gallery/hanguo/", "韩国"),
                ImgTypeBean("https://www.nvshens.com/gallery/malaixiya/", "马来西亚"),
                ImgTypeBean("https://www.nvshens.com/gallery/yuenan/", "越南"),
                ImgTypeBean("https://www.nvshens.com/gallery/taiguo/", "泰国"),
                ImgTypeBean("https://www.nvshens.com/gallery/feilvbin/", "菲律宾"),
                ImgTypeBean("https://www.nvshens.com/gallery/hunxue/", "混血"),
                ImgTypeBean("https://www.nvshens.com/gallery/oumei/", "欧美"),
                ImgTypeBean("https://www.nvshens.com/gallery/yindu/", "印度"),
                ImgTypeBean("https://www.nvshens.com/gallery/feizhou/", "非洲")
        )
    }
}