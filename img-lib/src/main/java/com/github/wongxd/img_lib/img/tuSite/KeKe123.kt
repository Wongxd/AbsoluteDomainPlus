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
 * Created by wongxd on 2018/1/6.
 */
class KeKe123 : BaseTuSite {
    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {

        if (TextUtils.isEmpty(url)) return null
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
                doc = Jsoup.parse(URL(url).readText(charset("GBK")))
                val etTitle = doc!!.getElementsByClass("pageheader entrypage").first().getElementsByTag("h2")
                val title = etTitle.text()
                val div = doc.select(".page-list").first()
                val ps = div.select("p")
                ps.mapTo(urls) { it.getElementsByTag("img").first().attr("src") }
                tViewModel.setTitle(title)
            } else {
                getkeke1234Deep(url, page, urls)
            }

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
     * 获取 keke123 某个图集的详情
     */
    private fun getkeke1234Deep(path: String, pageNo: Int, urls: ArrayList<String>) {
        val url = path.replace(".html", "") + "_$pageNo.html"
        val doc = Jsoup.parse(URL(url).readText(charset("GBK")))
        val div = doc.select(".page-list").first()
        val ps = div.select("p")
        ps.mapTo(urls) { it.getElementsByTag("img").first().attr("src") }
    }


    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }


    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        var originUrl = url
        var suffix = ""

        if (page != 1) {
            originUrl = originUrl.substring(0, originUrl.lastIndexOf("_"))
            suffix = "_$page.html"
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val s = URL(originUrl).readText(charset("GBK"))

            val doc = Jsoup.parse(s)
            val ul = doc.getElementById("msy")
            val lis = ul.getElementsByTag("div")
            var isFirst = true
            for (element in lis) {
                try {
                    if (isFirst) {
                        isFirst = false
                        continue
                    }
                    val preview: String = element.getElementsByClass("img").first().attr("src")
                            ?: ""
                    val a = element.getElementsByClass("title").first().getElementsByTag("a").first()

                    val title = a.attr("title")

                    val imgUrl = a.attr("href")

                    val date = ""

//                Logger.e("$preview    $title  $imgUrl  $date  $view")
                    list.add(TuListBean(title, preview, imgUrl, date))
                } catch (e: Exception) {
                    continue
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
     * 获取对应页面的真实的页面地址
     */
    protected fun getRealPageUrl(originUrl: String, page: Int): String {
        var url = originUrl
        //www.keke123.cc/gaoqing/list_5_2.html
        //页面判断
        var suffix = "page/$page/"
        if (page == 1) {
            suffix = ""
        } else if (url.contains("192tt.com")) {

            suffix = "index_$page.html"

        } else if (url.contains("mmonly.cc")) {

            url = originUrl.substring(0, originUrl.lastIndexOf("_"))
            suffix = "_$page.html"

        } else if (url.contains("keke123")) {

            url = originUrl.substring(0, originUrl.lastIndexOf("_"))
            suffix = "_$page.html"

        } else if (url.contains("nvshens.com")) {
            suffix = "/$page.html"
        }

        return url + suffix
    }


    companion object {
        val typeList = arrayListOf(
                ImgTypeBean("http://www.keke123.cc/", "最新"),
                ImgTypeBean("http://www.keke123.cc/gaoqing/cn/list_1_1.html", "国产"),
                ImgTypeBean("http://www.keke123.cc/gaoqing/rihan/list_2_1.html", "日韩"),
                ImgTypeBean("http://www.keke123.cc/gaoqing/oumei/list_3_1.html", "欧美")
        )
    }
}