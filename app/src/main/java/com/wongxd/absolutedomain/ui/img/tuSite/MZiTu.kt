package com.wongxd.absolutedomain.ui.img.tuSite

import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.wongxd.absolutedomain.RequestState
import com.wongxd.absolutedomain.data.bean.ImgTypeBean
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.ui.img.BaseTuSite
import com.wongxd.absolutedomain.ui.img.SeePicViewModel
import com.wongxd.absolutedomain.ui.img.TuViewModel
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by wongxd on 2018/2/5.
 */
class MZiTu : BaseTuSite {

    companion object {
        val typeList = arrayListOf(
                ImgTypeBean("http://www.mzitu.com/", "最新"),
                ImgTypeBean("http://www.mzitu.com/xinggan/", "性感"),
                ImgTypeBean("http://www.mzitu.com/japan/", "日本"),
                ImgTypeBean("http://www.mzitu.com/taiwan/", "台湾"),
                ImgTypeBean("http://www.mzitu.com/mm/", "清纯")
//                ,
//                ImgTypeBean("http://www.mzitu.com/zipai/comment-page-332/#comments", "自拍")
        )
    }

    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }

    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {
        var originUrl = url
        var suffix = ""

        if (page != 1) {
            suffix = "page/$page"
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val request = OkGo.get<String>(originUrl)
            request.headers("Connection", "Keep-Alive")
            request.headers("Referer", "http://www.mzitu.com/")
            val res = request.execute()
            if (res.isSuccessful) {
                res.body()?.string()?.let {
                    list.addAll(parseMeiZiTuList(it, page))
                    state = list.size
                }
            } else {
                info = ""
                state = 0
            }

        } catch (e: Exception) {
            info = e.message ?: ""
        }


        tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, info, state)

        return list
    }

    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {

        if (TextUtils.isEmpty(url)) return null

        val requestType = if (page == 1) RequestState.REFRESH else RequestState.LOADMORE

        if (page > 1) {
            tViewModel.changeGetListState(requestType, "图集全部加载完成", 0)
            return null
        }


        useCache(url)?.let {
            if (requestType == RequestState.LOADMORE) {
                tViewModel.changeGetListState(requestType, "已是最后一页", 0)
                return null
            } else {
                tViewModel.changeGetListState(requestType, "", 1)
                return it
            }
        }

        val urls = ArrayList<String>()
        try {
            val request = OkGo.get<String>(url)
            request.headers("Connection", "Keep-Alive")
            request.headers("Referer", url)
            val res = request.execute()
            if (res.isSuccessful) {
                res.body()?.string()?.let {
                    urls.addAll(parsePicturePage(it))
                    tViewModel.changeGetListState(requestType, "", 1)
                    setCache(url, urls)
                }
            } else
                tViewModel.changeGetListState(requestType, "", 0)

            return urls
        } catch (e: Exception) {
            e.printStackTrace()
            tViewModel.changeGetListState(requestType, "是不是网络出问题了呢", 0)
        }

        return null

    }


    fun parseMeiZiTuList(html: String, page: Int): List<TuListBean> {

        val doc = Jsoup.parse(html)
        val ulPins = doc.getElementById("pins")
        val lis = ulPins.select("li")
        val meiZiTuList = ArrayList<TuListBean>()
        for (li in lis) {
            var id = ""

            val contentUrl = li.select("a").first().attr("href")
            val index = contentUrl.lastIndexOf("/")
            if (index >= 0 && index + 1 < contentUrl.length) {
                val idStr = contentUrl.substring(index + 1, contentUrl.length)
                if (!TextUtils.isEmpty(idStr) && TextUtils.isDigitsOnly(idStr)) {
                    id = idStr
                }
            }
            val imageElement = li.selectFirst("img")
            val name = imageElement.attr("alt")
            val thumbUrl = imageElement.attr("data-original")
            val date = li.getElementsByClass("time").first().text()
            val meiZiTu = TuListBean(name, thumbUrl, "http://www.mzitu.com/$id", date)
//            Logger.e("$name---$thumbUrl---$id---$date")
            meiZiTuList.add(meiZiTu)
        }


        return meiZiTuList
    }

    fun parsePicturePage(html: String): List<String> {


        val doc = Jsoup.parse(html)

        val pageElement = doc.getElementsByClass("pagenavi").first()

        val aElements = pageElement.select("a")
        var totalPage = 1
        if (aElements != null && aElements.size > 3) {
            val pageStr = aElements[aElements.size - 2].getElementsByTag("span").text()
            if (!TextUtils.isEmpty(pageStr) && TextUtils.isDigitsOnly(pageStr)) {
                totalPage = Integer.parseInt(pageStr)
            }
        }

        val imageUrlList = ArrayList<String>()

        val imageUrl = doc.getElementsByClass("main-image").first().selectFirst("img").attr("src")
        if (totalPage == 1) {
            imageUrlList.add(imageUrl)
        }
//        Logger.e("妹子图 totalPage--$totalPage")
        for (i in 1..totalPage) {
            val tmp: String
            if (i < 10) {
                tmp = imageUrl.replace("01.", "0$i.")
            } else {
                tmp = imageUrl.replace("01.", "" + i + ".")
            }
            imageUrlList.add(tmp)
//            Logger.e(tmp)
        }

        return imageUrlList
    }
}