package com.github.wongxd.img_lib.img

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.wongxd.core_lib.DEFAULT_TU_SITE
import com.github.wongxd.core_lib.RequestState
import com.github.wongxd.core_lib.base.utils.utilcode.util.CacheUtils
import com.github.wongxd.core_lib.util.SPUtils
import com.github.wongxd.img_lib.data.bean.ImgSiteBean
import com.github.wongxd.img_lib.data.bean.ImgTypeBean
import com.github.wongxd.img_lib.data.bean.TuListBean
import com.github.wongxd.img_lib.img.tuSite.Gank
import com.github.wongxd.img_lib.img.tuSite.KeKe123
import com.github.wongxd.img_lib.img.tuSite.MZiTu
import com.github.wongxd.img_lib.img.tuSite.T192TT
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/5.
 */
class TuViewModel : ViewModel() {

    companion object {
        var defaultTuSite: BaseTuSite = Gank()
    }


    val typeList: MutableLiveData<List<ImgTypeBean>> = MutableLiveData()
    val tuList: MutableLiveData<MutableList<TuListBean>> = MutableLiveData<MutableList<TuListBean>>()
    val getListState: MutableLiveData<RequestState> = MutableLiveData()
    val siteList: MutableLiveData<List<ImgSiteBean>> = MutableLiveData()

    var currentPage: Int = 1
    lateinit var siteMaker: BaseTuSite
    lateinit var url: String

    init {
        val site = SPUtils.get(key = DEFAULT_TU_SITE, defaultObject = Gank::class.java.name) as String
        defaultTuSite = Class.forName(site).newInstance() as BaseTuSite

        initSite()

        val siteList = ArrayList<ImgSiteBean>()

        siteList.add(ImgSiteBean(MZiTu::class.java.simpleName, MZiTu::class.java))
//        siteList.add(ImgSiteBean(T99mm::class.java.simpleName, T99mm::class.java))
        siteList.add(ImgSiteBean(Gank::class.java.simpleName, Gank::class.java))
//        siteList.add(ImgSiteBean(Jdlingyu::class.java.simpleName, Jdlingyu::class.java))
        siteList.add(ImgSiteBean(T192TT::class.java.simpleName, T192TT::class.java))
        siteList.add(ImgSiteBean(KeKe123::class.java.simpleName, KeKe123::class.java))

        this.siteList.value = siteList
    }

    fun initSite(site: BaseTuSite = defaultTuSite) {
        siteMaker = defaultTuSite
        typeList.value = site.getTypeList()
        url = typeList.value!![0].url
    }

    fun changeSite(site: Class<out BaseTuSite>) {
        defaultTuSite = site.newInstance()
        initSite(defaultTuSite)
    }

    fun changeType(pos: Int) {
        url = typeList.value!![pos].url
    }

    fun refreshList() {
        tuList.value?.clear()
        //刷新时 清除缓存
        CacheUtils.getInstance("String").remove(url)
        getList(1)
    }

    fun addPageList() {
        getList(currentPage + 1)
    }

    private fun getList(page: Int) {

        doAsync {
            val list = TuSiteImp(siteMaker).getSpecificPage(this@TuViewModel, url, page)

            uiThread {
                if (list.size != 0) {
                    currentPage = page
                    tuList.value = list
                }
            }
        }
    }


    fun changeGetListState(type: RequestState, info: String, state: Int) {
        doAsync {
            uiThread {
                type.state = state
                type.info = info
                getListState.value = type
            }
        }
    }

}