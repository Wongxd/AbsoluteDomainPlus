package com.github.wongxd.img_lib.img.tuSite

import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.img_lib.data.bean.ImgTypeBean
import com.github.wongxd.img_lib.data.bean.TuListBean
import com.github.wongxd.img_lib.img.BaseTuSite
import com.github.wongxd.img_lib.img.SeePicViewModel
import com.github.wongxd.img_lib.img.TuViewModel
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by wongxd on 2018/2/5.
 */
class T99mm : BaseTuSite {
    companion object {

        val typeList = arrayListOf(
                ImgTypeBean("http://www.99mm.me/meitui/", "美腿"),
                ImgTypeBean("http://www.99mm.me/xinggan/", "性感"),
                ImgTypeBean("http://www.99mm.me/qingchun/", "清纯"),
                ImgTypeBean("http://www.99mm.me/hot/", "HOT")
        )
    }


    override fun getTypeList(): List<ImgTypeBean> {
        return typeList
    }

    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {
        var originUrl = url
        var suffix = ""

        if (page != 1) {
            suffix = when (url) {
                typeList[0].url -> {
                    "mm_1_$page.html"
                }

                typeList[1].url -> {
                    "mm_2_$page.html"
                }

                typeList[2].url -> {
                    "mm_3_$page.html"
                }

                else -> {
                    "mm_4_$page.html"
                }
            }
        }
        originUrl += suffix

        val list: MutableList<TuListBean> = ArrayList()
        var info = ""
        var state = 0
        try {
            val request = OkGo.get<String>(originUrl)
            val res = request.execute()
            if (res.isSuccessful) {
                res.body()?.string()?.let {
                    list.addAll(parse99MmList(it))
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

        val currentId = url.split("-/")[0]
        val imgUrl = url.split("-/")[1]

        val urls = ArrayList<String>()
        try {
            //?act=view&id=$currentId"
            val request = OkGo.get<String>("http://www.99mm.me/url.php")
                    .params("act", "view")
                    .params("id", currentId)
            request.headers("Referer", "http://www.99mm.me/meitui/")
            val res = request.execute()
            if (res.isSuccessful) {
                res.body()?.string()?.let {
//                    Logger.e("id---$currentId---" + it)
                    val tags = it.split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    tags.indices.mapTo(urls) {
                        imgUrl.replace("small/", "")
                                .replace(".jpg", "/" + (it + 1) + "-" + tags[it]) + ".jpg"
                    }
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


    fun parse99MmList(html: String): List<TuListBean> {
        val doc = Jsoup.parse(html)
        val ul = doc.getElementById("piclist")
        val lis = ul.select("li")
        val mm99List: MutableList<TuListBean> = ArrayList()
        for (li in lis) {

            val a = li.selectFirst("dt").selectFirst("a")
            val contentUrl = "http://www.99mm.me" + a.attr("href")

            val startIndex = contentUrl.lastIndexOf("/")
            val endIndex = contentUrl.lastIndexOf(".")
            val idStr = subString(contentUrl, startIndex + 1, endIndex)

            val img = a.selectFirst("img")
            val title = img.attr("alt")
            val preview = img.attr("src")

            val date = li.selectFirst("em").text()

            val mm99 = TuListBean(title, preview, "$idStr-/$preview", date)
//            Logger.e("$title---$imgUrl---$idStr---")
            mm99List.add(mm99)
        }


        return mm99List
    }


    /**
     * \u3000\u3000 首行缩进
     * 空格：&#160;
     * &#8194;半个中文字更准确点，
     * &#8195;一个中文字但用起来会比中文字宽一点点。
     */

    fun subString(str: String, startIndex: Int, endIndex: Int): String {
        if (TextUtils.isEmpty(str) || startIndex < 0 || endIndex < 0 || startIndex >= str.length || endIndex - startIndex < 0) {
            return ""
        }
        return if (endIndex > str.length) {
            str.substring(startIndex, str.length)
        } else str.substring(startIndex, endIndex)
    }
}