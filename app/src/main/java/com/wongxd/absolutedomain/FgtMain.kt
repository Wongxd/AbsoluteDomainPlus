package com.wongxd.absolutedomain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.billy.cc.core.component.CC
import com.github.wongxd.core_lib.ComponentImgAction
import com.github.wongxd.core_lib.ComponentTextAction
import com.github.wongxd.core_lib.ComponentVideoAction
import com.github.wongxd.core_lib.custom.view.BottomBar
import com.github.wongxd.core_lib.custom.view.BottomBarTab
import com.github.wongxd.core_lib.fragmenaction.BaseMainFragment
import com.github.wongxd.img_lib.img.FgtImg
import com.wongxd.absolutedomain.event.CCEvent
import com.wongxd.absolutedomain.event.LockDrawerEvent
import com.wongxd.absolutedomain.event.TabSelectedEvent
import com.wongxd.absolutedomain.event.ToggleDrawerEvent
import com.wongxd.absolutedomain.ui.data.bean.FgtWithTitleBean
import me.yokeyword.fragmentation.ISupportFragment
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * Created by wongxd on 2017/12/26.
 * Copyright © 2017年 no. All rights reserved.
 */

class FgtMain : BaseMainFragment() {

    private val mFragments: MutableList<FgtWithTitleBean> = ArrayList()

    private var mBottomBar: BottomBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fgt_main, container, false)
        initView(view)
        EventBus.getDefault().register(this)
        return view
    }


    private lateinit var tvLeft: TextView
    private lateinit var tvRight: TextView
    private lateinit var tvTitle: TextView
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadFgts()
    }

    private fun loadFgts() {
        //        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,SystemBarHelper.getStatusBarHeight(activity))
        //        v_fake_statusbar.layoutParams = params


        mFragments.clear()

        val firstFragment =
                CC.obtainBuilder("cImg")
                        .setActionName(ComponentImgAction.GetIntoClass)
                        .build().call().getDataItem<Class<SupportFragment>>("cls")
                        ?.let {
                            findChildFragment(it)
                        }

        if (firstFragment == null) {

            //测试下 通过 cc 拿 fgt 实例
            CC.obtainBuilder("cImg")
                    .setActionName(ComponentImgAction.Into)
                    .build().call()
                    .getDataItem<SupportFragment>("fgt")?.let {
                mFragments.add(FgtWithTitleBean(it, "图"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.img, "图"))
            }

            CC.obtainBuilder("cVideo")
                    .setActionName(ComponentVideoAction.Into)
                    .build().call()
                    .getDataItem<SupportFragment>("fgt")?.let {
                mFragments.add(FgtWithTitleBean(it, "视"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.video, "视"))
            }


            CC.obtainBuilder("cText")
                    .setActionName(ComponentTextAction.Into)
                    .build().call()
                    .getDataItem<SupportFragment>("fgt")?.let {
                mFragments.add(FgtWithTitleBean(it, "文"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.text, "文"))
            }


            loadMultipleRootFragment(R.id.fl_tab_container, FIRST, *mFragments.map { it.fgt }.toTypedArray())

        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用

            CC.obtainBuilder("cImg")
                    .setActionName(ComponentImgAction.GetIntoClass)
                    .build().call()
                    .getDataItem<Class<FgtImg>>("cls")?.let {
                mFragments.add(FgtWithTitleBean(it.newInstance(),"图"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.img, "图"))
            }


            CC.obtainBuilder("cVideo")
                    .setActionName(ComponentVideoAction.GetIntoClass)
                    .build().call()
                    .getDataItem<Class<FgtImg>>("cls")?.let {
                mFragments.add(FgtWithTitleBean(it.newInstance(),"视"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.video, "视"))
            }


            CC.obtainBuilder("cText")
                    .setActionName(ComponentTextAction.GetIntoClass)
                    .build().call()
                    .getDataItem<Class<FgtImg>>("cls")?.let {
                mFragments.add(FgtWithTitleBean(it.newInstance(),"文"))
                mBottomBar!!
                        .addItem(BottomBarTab(_mActivity, R.drawable.text, "文"))
            }


        }
    }

    private fun initView(view: View) {
        tvLeft = view.findViewById(R.id.tv_left)
        tvRight = view.findViewById(R.id.tv_right)
        tvTitle = view.findViewById(R.id.tv_title)

        tvLeft.text = "菜单"
        tvTitle.text = "图"
        tvLeft.setOnClickListener { EventBus.getDefault().post(ToggleDrawerEvent()) }

        mBottomBar = view.findViewById<View>(R.id.bottomBar) as BottomBar


        // 模拟未读消息
        //        mBottomBar.getItem(FIRST).setUnreadCount(9);

        mBottomBar!!.setOnTabSelectedListener(object : BottomBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int, prePosition: Int) {
                showHideFragment(mFragments[position].fgt, mFragments[prePosition].fgt)

                tvTitle.text = mFragments[position].title
                
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
        EventBus.getDefault().post(LockDrawerEvent(false))
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        EventBus.getDefault().post(LockDrawerEvent(true))
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
        EventBus.getDefault().unregister(this)
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

    @Subscribe
    fun onCCevent(event: CCEvent) {
        startBrotherFragment(event.fgt)
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
