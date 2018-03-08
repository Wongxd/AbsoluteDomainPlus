package com.wongxd.absolutedomain.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.github.wongxd.core_lib.base.decorator.DividerItemDecoration
import com.github.wongxd.core_lib.fragmenaction.MainTabFragment
import com.wongxd.absolutedomain.R
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.fgt_download_item.*

/**
 * Created by wongxd on 2018/1/10.
 */
class FgtDownloadItem : MainTabFragment() {

    companion object {

        fun newInstance(type: Int): FgtDownloadItem {
            val fgt = FgtDownloadItem()
            val b = Bundle()
            b.putInt("type", type)
            fgt.arguments = b
            return fgt
        }

    }

    override fun getLayout(): Int {
        return R.layout.fgt_download_item
    }

    private lateinit var adapter: DownloadAdapter
    override fun initView(mView: View?) {

        srl_fgt_download_item.isEnableLoadmore = false
        srl_fgt_download_item.isEnableRefresh = false

        adapter = DownloadAdapter(arguments?.getInt("type") ?: 0, lifecycle)

        rv_fgt_download_item.adapter = adapter
        rv_fgt_download_item.layoutManager = LinearLayoutManager(activity)
        rv_fgt_download_item.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
        rv_fgt_download_item.itemAnimator = LandingAnimator()
        adapter.setEmptyView(R.layout.item_rv_empty, rv_fgt_download_item)

    }


}