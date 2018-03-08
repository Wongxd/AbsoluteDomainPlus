package com.wongxd.absolutedomain.ui

import android.os.Bundle
import com.wongxd.absolutedomain.R
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.fragmenaction.BaseBackFragment
import com.github.wongxd.img_lib.img.FgtTuFavorite
import com.github.wongxd.text_lib.text.FgtTextFavorite
import com.github.wongxd.video_lib.video.FgtVideoFavorite
import com.qmuiteam.qmui.widget.QMUITabSegment
import kotlinx.android.synthetic.main.fgt_favorite.*
import me.yokeyword.fragmentation.SupportFragment

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtFavorite : BaseBackFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fgt_favorite
    }


    companion object {
        val FIRST = 0
        val SECOND = 1

        val THIRD = 2
        fun newInstance(type: TaskType): FgtFavorite {
            val fgt = FgtFavorite()
            val b = Bundle()
            b.putInt("type", type.ordinal)
            return fgt
        }

    }


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)

        val firstFragment = findChildFragment(FgtTuFavorite::class.java)
        if (firstFragment == null) {
            mFragments[FIRST] = FgtTuFavorite()
            mFragments[SECOND] = FgtVideoFavorite()
            mFragments[THIRD] = FgtTextFavorite()

            loadMultipleRootFragment(R.id.fl_fgt_favorite, FIRST,
                    mFragments[FIRST]
                    ,
                    mFragments[SECOND]
                    ,
                    mFragments[THIRD]
            )
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = findChildFragment(FgtTuFavorite::class.java)
            mFragments[SECOND] = findChildFragment(FgtVideoFavorite::class.java)
            mFragments[THIRD] = findChildFragment(FgtTextFavorite::class.java)
        }


        tab_fgt_favorite.setHasIndicator(true)
        tab_fgt_favorite.setIndicatorWidthAdjustContent(true)
        tab_fgt_favorite.addTab(QMUITabSegment.Tab("图收藏"))
        tab_fgt_favorite.addTab(QMUITabSegment.Tab("视频收藏"))
        tab_fgt_favorite.addTab(QMUITabSegment.Tab("文字收藏"))

        tab_fgt_favorite.addOnTabSelectedListener(object : QMUITabSegment.OnTabSelectedListener {
            override fun onTabReselected(index: Int) {

            }

            override fun onTabUnselected(index: Int) {
            }

            override fun onTabSelected(index: Int) {
                showHideFragment(mFragments[index])
            }

            override fun onDoubleTap(index: Int) {
            }

        })

        tab_fgt_favorite.notifyDataChanged()
        tab_fgt_favorite.selectTab(0)
    }


    private val mFragments = arrayOfNulls<SupportFragment>(3)

}