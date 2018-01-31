package com.wongxd.absolutedomain.ui.video

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import cn.jzvd.JZVideoPlayer
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.data.bean.VideoSiteBean
import com.wongxd.absolutedomain.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.ui.video.videosSite.Eyepetizer
import kotlinx.android.synthetic.main.fgt_video.*

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtVideo : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_video
    }

    companion object {

        fun newInstance(args: Bundle = Bundle()): FgtVideo {
            val fragment = FgtVideo()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(mView: View?) {

        val vpItems = listOf(VideoSiteBean("Eyepetizer", FgtVideoItem.newInstance(Eyepetizer::class.java)))

        vp_video.adapter = VpAdapter(childFragmentManager, vpItems)
        vp_video.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                JZVideoPlayer.releaseAllVideos()
            }
        })
        tab_video.setupWithViewPager(vp_video)

        vp_video.currentItem = 0

    }


    class VpAdapter(fm: FragmentManager, val sites: List<VideoSiteBean>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = sites[position].fgt

        override fun getCount(): Int = sites.size

        override fun getPageTitle(position: Int): CharSequence = sites[position].title


    }


}