package com.github.wongxd.img_lib.img.tuSite

import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.core_lib.data.bean.tu.GankBean
import com.github.wongxd.img_lib.data.bean.ImgTypeBean
import com.github.wongxd.img_lib.data.bean.TuListBean
import com.github.wongxd.img_lib.img.BaseTuSite
import com.github.wongxd.img_lib.img.SeePicViewModel
import com.github.wongxd.img_lib.img.TuViewModel
import com.google.gson.Gson
import java.net.URL

/**
 * Created by wongxd on 2018/1/27.
 */
class Gank : BaseTuSite {
    override fun getTypeList(): List<ImgTypeBean> {
        return listOf(ImgTypeBean("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/", "时间排序"))
    }

    override fun getSpecificPage(tViewModel: TuViewModel, url: String, page: Int): MutableList<TuListBean> {

        val list: MutableList<TuListBean> = ArrayList()
        var state =0
        try {
            val txt = URL(url + page).readText()
            val gankList: GankBean = Gson().fromJson(txt, GankBean::class.java)
            gankList.results.forEach { list.add(TuListBean(it.desc, it.url, it.url, it.desc)) }
            state =1
        } catch (e: Exception) {
            e.printStackTrace()
            state =0
        }
        tViewModel.changeGetListState(if (page == 1) RequestState.REFRESH else RequestState.LOADMORE, "", state)
        return list
    }

    override fun getChildDetail(tViewModel: SeePicViewModel, url: String, page: Int): List<String>? {
        if (page != 1) {
            tViewModel.changeGetListState( RequestState.LOADMORE, "可能没有下一页了哦", 0)
            return null
        }
        val requestType = if (page == 1) RequestState.REFRESH else RequestState.LOADMORE
        tViewModel.changeGetListState(requestType, "GANK一个图集只有一张图哦", 1)
        return listOf(url)
    }
}