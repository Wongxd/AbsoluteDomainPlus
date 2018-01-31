package com.wongxd.absolutedomain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wongxd.absolutedomain.custom.view.BottomBar
import com.wongxd.absolutedomain.custom.view.BottomBarTab
import com.wongxd.absolutedomain.event.LockDrawerEvent
import com.wongxd.absolutedomain.event.TabSelectedEvent
import com.wongxd.absolutedomain.event.ToggleDrawerEvent
import com.wongxd.absolutedomain.fragmenaction.BaseMainFragment
import com.wongxd.absolutedomain.ui.img.FgtImg
import com.wongxd.absolutedomain.ui.text.FgtText
import com.wongxd.absolutedomain.ui.video.FgtVideo
import me.yokeyword.eventbusactivityscope.EventBusActivityScope
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wongxd on 2017/12/26.
 * Copyright © 2017年 no. All rights reserved.
 */

class FgtMain : BaseMainFragment() {

    private val mFragments = arrayOfNulls<SupportFragment>(4)

    private var mBottomBar: BottomBar? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fgt_main, container, false)
        initView(view)
        EventBusActivityScope.getDefault(_mActivity).register(this)
        return view
    }


    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private lateinit var tvTitle: TextView
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,SystemBarHelper.getStatusBarHeight(activity))
//        v_fake_statusbar.layoutParams = params

        val firstFragment = findChildFragment(FgtImg::class.java)
        if (firstFragment == null) {
            mFragments[FIRST] = FgtImg.newInstance()
            mFragments[SECOND] = FgtVideo.newInstance()
            mFragments[THIRD] = FgtText.newInstance()

            loadMultipleRootFragment(R.id.fl_tab_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD])
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = findChildFragment(FgtImg::class.java)
            mFragments[SECOND] = findChildFragment(FgtVideo::class.java)
            mFragments[THIRD] = findChildFragment(FgtText::class.java)
        }
    }

    private fun initView(view: View) {
        tvLeft = view.findViewById(R.id.tv_left)
        tvRight = view.findViewById(R.id.tv_right)
        tvTitle = view.findViewById(R.id.tv_title)

        tvLeft.text = "菜单"
        tvTitle.text = "图"
        tvLeft.setOnClickListener { EventBusActivityScope.getDefault(_mActivity).post(ToggleDrawerEvent()) }

        mBottomBar = view.findViewById<View>(R.id.bottomBar) as BottomBar

        mBottomBar!!
                .addItem(BottomBarTab(_mActivity, R.drawable.img, "图"))
                .addItem(BottomBarTab(_mActivity, R.drawable.video, "视"))
                .addItem(BottomBarTab(_mActivity, R.drawable.text, "文"))

        // 模拟未读消息
        //        mBottomBar.getItem(FIRST).setUnreadCount(9);

        mBottomBar!!.setOnTabSelectedListener(object : BottomBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int, prePosition: Int) {
                showHideFragment(mFragments[position], mFragments[prePosition])

                val title = when (position) {
                    FIRST -> "图"
                    SECOND -> "视"
                    else -> "文"
                }
                tvTitle.text = title
                //                BottomBarTab tab = mBottomBar.getItem(FIRST);
                //                if (position == FIRST) {
                //                    tab.setUnreadCount(0);
                //                } else {
                //                    tab.setUnreadCount(tab.getUnreadCount() + 1);
                //                }
            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabReselected(position: Int) {
                // 在FirstPagerFragment,FirstHomeFragment中接收, 因为是嵌套的Fragment
                // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
//                EventBusActivityScope.getDefault(_mActivity).post(TabSelectedEvent(position))
            }
        })
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        EventBusActivityScope.getDefault(_mActivity).post(LockDrawerEvent(false))
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        EventBusActivityScope.getDefault(_mActivity).post(LockDrawerEvent(true))
    }

    /**
     * 选择tab事件
     */
    @Subscribe
    fun onTabSelectedEvent(event: TabSelectedEvent) {
        mBottomBar!!.setCurrentItem(event.position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBusActivityScope.getDefault(_mActivity).unregister(this)
    }


    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == REQ_MSG && resultCode == ISupportFragment.RESULT_OK) {

        }
    }

    /**
     * start other BrotherFragment
     */
    fun startBrotherFragment(targetFragment: SupportFragment) {
        start(targetFragment)
    }

    companion object {
        private val REQ_MSG = 10

        val FIRST = 0
        val SECOND = 1
        val THIRD = 2


        fun newInstance(args: Bundle = Bundle()): FgtMain {
            val fragment = FgtMain()
            fragment.arguments = args
            return fragment
        }
    }
}
