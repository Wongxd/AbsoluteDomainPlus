package com.wongxd.absolutedomain.ui.img

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.wongxd.absolutedomain.RequestState
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/8.
 */
class SeePicViewModel : ViewModel() {

    val title: MutableLiveData<String> = MutableLiveData()
    val picList: MutableLiveData<MutableList<String>> = MutableLiveData()
    val picTotalList: MutableLiveData<MutableList<String>> = MutableLiveData()
    val getListState: MutableLiveData<RequestState> = MutableLiveData()
    val currentImgPos: MutableLiveData<Int> = MutableLiveData()

    var currentPage: Int = 1

    private lateinit var siteMaker: BaseTuSite
    private lateinit var url: String

    fun shouldFirst(site: BaseTuSite, url: String) {
        siteMaker = site
        this.url = url
    }

    /**
     * 在 FgtSeePic 销毁时 重置数据
     */
    fun cleanData() {
        currentPage = 1
        picList.value?.clear()
        picTotalList.value?.clear()
        currentImgPos.value = 0
    }

    fun refreshList() {
        picList.value?.clear()
        picTotalList.value?.clear()
        getList(1)
    }

    fun addPageList() {
        getList(currentPage + 1)
    }

    private fun getList(page: Int) {
        currentPage = page
        doAsync {
            val list = TuSiteImp(siteMaker).getChildDetail(this@SeePicViewModel, url, page)

            uiThread {
                list?.let {
                    picList.value = it.toMutableList()
                    if (page == 1)
                        picTotalList.value = it.toMutableList()
                    else
                        picTotalList.value?.addAll(it)
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

    fun setTitle(t: String) {
        doAsync {
            uiThread {
                title.value = t
            }
        }
    }
}