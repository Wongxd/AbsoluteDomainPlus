package com.wongxd.absolutedomain.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentImgAction
import com.github.wongxd.core_lib.ComponentTextAction
import com.github.wongxd.core_lib.ComponentVideoAction
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.core_lib.util.TU
import com.qmuiteam.qmui.widget.QMUITabSegment
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.ui.data.bean.FgtWithTitleBean
import kotlinx.android.synthetic.main.fgt_favorite.*
import me.yokeyword.fragmentation.SupportFragment
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtFavorite : BaseBackFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fgt_favorite
    }


    companion object {

        fun newInstance(type: TaskType): FgtFavorite {
            val fgt = FgtFavorite()
            val b = Bundle()
            b.putInt("type", type.ordinal)
            return fgt
        }

    }


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        val list: MutableList<FgtWithTitleBean> = ArrayList()

        doAsync {
            CC.obtainBuilder("cImg")
                    .setActionName(ComponentImgAction.GetFavoritClass)
                    .build().call()
                    .getDataItem<Class<SupportFragment>>("cls")?.let {
                list.add(FgtWithTitleBean(it.newInstance(), "图集收藏"))
            }



            CC.obtainBuilder("cVideo")
                    .setActionName(ComponentVideoAction.GetFavoritClass)
                    .build().call()
                    .getDataItem<Class<SupportFragment>>("cls")?.let {
                list.add(FgtWithTitleBean(it.newInstance(), "视频收藏"))
            }


            CC.obtainBuilder("cText")
                    .setActionName(ComponentTextAction.GetFavoritClass)
                    .build().call()
                    .getDataItem<Class<SupportFragment>>("cls")?.let {
                list.add(FgtWithTitleBean(it.newInstance(), "文字收藏"))
            }



            uiThread {
                if (list.isEmpty()) {
                    TU.cT("没有可用模块")
                    pop()
                    return@uiThread
                }

                tv_tips_favorite.visibility = View.GONE

                val adapter = VpAdapter(childFragmentManager, list)
                vp_favorite.adapter = adapter


                tab_fgt_favorite.setHasIndicator(true)
                tab_fgt_favorite.setupWithViewPager(vp_favorite)
                tab_fgt_favorite.mode = QMUITabSegment.MODE_FIXED


                vp_favorite.currentItem = 0
            }
        }
    }


    class VpAdapter(fm: FragmentManager, val list: List<FgtWithTitleBean>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = list[position].fgt

        override fun getCount(): Int = list.size

        override fun getPageTitle(position: Int): CharSequence? = list[position].title

    }

}