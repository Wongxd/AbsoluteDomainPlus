package com.wongxd.absolutedomain.ui.img

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wongxd.absolutedomain.DEFAULT_TU_SITE
import com.wongxd.absolutedomain.RequestState
import com.wongxd.absolutedomain.data.bean.ImgSiteBean
import com.wongxd.absolutedomain.data.bean.ImgTypeBean
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.ui.img.tuSite.*
import com.wongxd.absolutedomain.util.SPUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/5.
 */
class TuViewModel : ViewModel() {

    companion object {
        var defaultTuSite: BaseTuSite = Tu4493()
    }


    val typeList: MutableLiveData<List<ImgTypeBean>> = MutableLiveData()
    val tuList: MutableLiveData<MutableList<TuListBean>> = MutableLiveData<MutableList<TuListBean>>()
    val getListState: MutableLiveData<RequestState> = MutableLiveData()
    val siteList: MutableLiveData<List<ImgSiteBean>> = MutableLiveData()

    var currentPage: Int = 1
    lateinit var siteMaker: BaseTuSite
    lateinit var url: String

    init {
        val site = SPUtils.get(key = DEFAULT_TU_SITE, defaultObject = Tu4493::class.java.name) as String
        defaultTuSite = Class.forName(site).newInstance() as BaseTuSite

        initSite()

        val siteList = ArrayList<ImgSiteBean>()

//        siteList.add(ImgSiteBean( NvShens::class.java.simpleName, NvShens::class.java))
        siteList.add(ImgSiteBean(Tu4493::class.java.simpleName, Tu4493::class.java))
        siteList.add(ImgSiteBean(Gank::class.java.simpleName, Gank::class.java))
        siteList.add(ImgSiteBean(Jdlingyu::class.java.simpleName, Jdlingyu::class.java))
        siteList.add(ImgSiteBean(T192TT::class.java.simpleName, T192TT::class.java))
        siteList.add(ImgSiteBean(KeKe123::class.java.simpleName, KeKe123::class.java))
        siteList.add(ImgSiteBean(MeiSiGuan::class.java.simpleName, MeiSiGuan::class.java))

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