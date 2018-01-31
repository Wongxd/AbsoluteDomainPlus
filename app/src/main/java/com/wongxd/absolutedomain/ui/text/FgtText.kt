package com.wongxd.absolutedomain.ui.text

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.View
import com.wongxd.absolutedomain.FgtMain
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.data.bean.TextSiteBean
import com.wongxd.absolutedomain.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.ui.text.textSite.One
import com.wongxd.absolutedomain.ui.text.textSite.OneArticle
import kotlinx.android.synthetic.main.fgt_text.*
import me.yokeyword.fragmentation.SupportFragment

/**
 * Created by wongxd on 2018/1/5.
 */
class FgtText : MainTabFragment() {

    override fun getLayout(): Int {
        return R.layout.fgt_text
    }

    companion object {

        fun newInstance(args: Bundle = Bundle()): FgtText {
            val fragment = FgtText()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initView(mView: View?) {

        val vpItems = listOf(TextSiteBean("OneArticle", FgtTextItem.newInstance(OneArticle::class.java)),
                TextSiteBean("ONE·一个", FgtTextItem.newInstance(One::class.java))

        )

        vp_text.adapter = VpAdapter(childFragmentManager, vpItems)
        tab_text.setupWithViewPager(vp_text)

        vp_text.currentItem = 0

    }


    class VpAdapter(fm: FragmentManager, val sites: List<TextSiteBean>) : FragmentStatePagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = sites[position].fgt

        override fun getCount(): Int = sites.size

        override fun getPageTitle(position: Int): CharSequence = sites[position].title


    }

    fun startNewFgt(fgt: SupportFragment) {
        (parentFragment as FgtMain).startBrotherFragment(fgt)
    }

}