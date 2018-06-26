package com.github.wongxd.img_lib.img.tuSite

import android.text.TextUtils
import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.img_lib.data.bean.ImgTypeBean
import com.github.wongxd.img_lib.data.bean.TuListBean
import com.github.wongxd.img_lib.img.BaseTuSite
import com.github.wongxd.img_lib.img.SeePicViewModel
import com.github.wongxd.img_lib.img.TuViewModel
import com.orhanobut.logger.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.SocketTimeoutException
import java.net.URL

/**
 * Created by wongxd on 2018/1/6.
 */
class T192TT : BaseTuSite {
    override fun getChildDetail(tViewModel: SeePicViewModel, orgUrl: String, page: Int): List<String>? {


        if (TextUtils.isEmpty(orgUrl)) return null
        var url = orgUrl
        var tagUrl = ""

        url = url.replace("_1.html", ".html")
                .replace("_1.html#p", ".html")
        tagUrl = url.replace("https://www.192tt.com", "")
                .replace("http://www.192tt.com", "")
                .replace("_1.html#p", "")
                .replace(".html", "")



        url = url.replace(".html", ".html#p")

        Logger.e("tag--" + tagUrl)
        Logger.e(url)

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
                val imgUrl = doc.select("#p > center > img").first().attr("lazysrc")
                val title = doc.select("#p > center > img").first().attr("alt")
                urls.add(imgUrl)

                tViewModel.setTitle(title)
            } else
                get192TTDeep(tagUrl, page, urls)





            tViewModel.changeGetListState(requestType, "", 1)
            return urls
        } catch (e: Exception) {
            e.printStackTrace()
            tViewModel.changeGetListState(requestType, if (e is SocketTimeoutException) {
                "是不是网络出问题了呢"
            } else {
                if (page != 1 && tViewModel.picTotalList.value?.size ?: 0 > 2)
                    setCache(url, tViewModel.picTotalList.value)
                "可能没有下一页了哦"
            }, 0)
        }
        return null
    }


    /**
     * 192tt 递归调用 爬取
     */
    fun get192TTDeep(tagUrl: String, page: Int, urls: ArrayList<String>) {
        val url = "https://www.192tt.com" + tagUrl + "_$page.html#p"
        Logger.e("递归调用url--$url")
        val doc = Jsoup.parse(URL(url).readText())
        val imgUrl = doc.select("#p > center > img").first().attr("lazysrc")
        urls.add(imgUrl)
    }

    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }


    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        var originUrl = url
        var suffix = ""

        var info = ""
        var state = 0

        if (page != 1) {
            if (originUrl.contains(".com/new/")) {
                info = "最新分类只有一页噢"
                tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, info, state)
                return mutableListOf()
            }
            suffix = "index_$page.html"
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()

        try {
            val s = URL(originUrl).readText()

            val u = URL(originUrl)
            val referer = u.getProtocol() + "://" + u.getHost()
            val doc = Jsoup.connect(originUrl)
                    .userAgent("Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.1.2) Gecko/20090803")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                    .timeout(20000)
                    .ignoreContentType(true).referrer(referer)
                    .get()
            Logger.e(doc.toString())

            val select = doc.select("body > div.mainer > div.piclist > ul > li")
            for (element in select) {
                val preview = element.select("a > img").attr("lazysrc")

                val imgUrl = element.select("a").attr("href")

                val description = element.select("a > span").text()

                val date = element.select("b.b1").text()

                list.add(TuListBean(description, preview, imgUrl, date))
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
                ImgTypeBean("http://www.192tt.com/new/", "最新"),
                ImgTypeBean("http://www.192tt.com/meitu/xingganmeinv/", "性感"),
                ImgTypeBean("http://www.192tt.com/meitu/siwameitui/", "丝袜"),
                ImgTypeBean("http://www.192tt.com/meitu/weimeixiezhen/", "写真"),
                ImgTypeBean("http://www.192tt.com/meitu/wangluomeinv/", "网络"),
                ImgTypeBean("http://www.192tt.com/meitu/gaoqingmeinv/", "高清"),
                ImgTypeBean("http://www.192tt.com/meitu/motemeinv/", "模特"),
                ImgTypeBean("http://www.192tt.com/meitu/tiyumeinv/", "体育"),
                ImgTypeBean("http://www.192tt.com/meitu/dongmanmeinv/", "动漫")
        )
    }
}