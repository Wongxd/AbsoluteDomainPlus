package com.wongxd.absolutedomain.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.qmuiteam.qmui.widget.QMUITabSegment
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.kotin.extension.getPrimaryColor
import com.wongxd.absolutedomain.data.bean.TaskType
import com.wongxd.absolutedomain.fragmenaction.BaseBackFragment
import kotlinx.android.synthetic.main.fgt_download.*

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtDownload : BaseBackFragment() {
    override fun getLayoutRes(): Int {
        return R.layout.fgt_download
    }

    companion object {

        fun newInstance(type: TaskType): FgtDownload {
            val fgt = FgtDownload()
            val b = Bundle()
            b.putInt("type", type.ordinal)
            return fgt
        }

    }


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        initVp()
    }


    private fun initVp() {

        vp_fgt_download.adapter = VpAdapter(fgts = mFragments)

        val type = arguments?.getInt("type", 0)
        vp_fgt_download.currentItem = when (type) {
            TaskType.IMG.ordinal -> 1

            TaskType.VIDEO.ordinal -> 2

            TaskType.TEXT.ordinal -> 3

            else -> 0
        }

        tab_fgt_download.addTab(QMUITabSegment.Tab("下载中"))
        tab_fgt_download.addTab(QMUITabSegment.Tab("图"))
        tab_fgt_download.addTab(QMUITabSegment.Tab("视"))
//        tab_fgt_download.addTab(QMUITabSegment.Tab("文"))
        tab_fgt_download.setHasIndicator(true)
        activity?.getPrimaryColor()?.let { tab_fgt_download.setBackgroundColor(it) }
        tab_fgt_download.setDefaultSelectedColor(Color.WHITE)
        tab_fgt_download.setIndicatorWidthAdjustContent(true)
        tab_fgt_download.setupWithViewPager(vp_fgt_download, false)
        tab_fgt_download.mode = QMUITabSegment.MODE_FIXED


    }


    private val mFragments = arrayOf<FgtDownloadItem>(
            FgtDownloadItem.newInstance(DownloadAdapter.TYPE_DOWNLOADING),
            FgtDownloadItem.newInstance(DownloadAdapter.TYPE_IMG),
            FgtDownloadItem.newInstance(DownloadAdapter.TYPE_VIDEO)
//            ,
//            FgtDownloadItem.newInstance(DownloadAdapter.TYPE_TEXT)
    )


    inner class VpAdapter(fragmentManager: FragmentManager = childFragmentManager, val fgts: Array<FgtDownloadItem>)
        : FragmentPagerAdapter(fragmentManager) {
        override fun getItem(position: Int): Fragment {
            return fgts[position]
        }

        override fun getCount(): Int {
            return fgts.size
        }

    }

}