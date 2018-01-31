package com.wongxd.absolutedomain.ui.img.adapter

import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.kotin.extension.database.parseList
import com.wongxd.absolutedomain.data.bean.TuListBean
import com.wongxd.absolutedomain.data.database.Tu
import com.wongxd.absolutedomain.data.database.TuTable
import com.wongxd.absolutedomain.data.database.tuDB
import com.wongxd.absolutedomain.util.SystemUtils
import loadImg
import org.jetbrains.anko.db.select

/**
 * Created by wongxd on 2018/1/5.
 * Copyright © 2017年 no. All rights reserved.
 */


class RvFgtImgAdapter(val click: (View, TuListBean) -> Unit) : BaseQuickAdapter<TuListBean, BaseViewHolder>(R.layout.item_rv_fgt_img) {
    override fun convert(helper: BaseViewHolder, item: TuListBean) {
        setData(helper, item, null)
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int, payloads: MutableList<Any>?) {
        setData(holder!!, mData[holder.layoutPosition - headerLayoutCount], payloads)
    }


    private fun setData(helper: BaseViewHolder, item: TuListBean, payloads: MutableList<Any>?) {
        if (payloads == null || payloads.isEmpty()) {
            with(helper) {
                setText(R.id.tv_title, item.title)
                        .setText(R.id.tv_time, item.date)
                        .setVisible(R.id.tv_time, !SystemUtils.isHadEmptyText(item.date))
                getView<ImageView>(R.id.iv).loadImg(item.preview)

                changeFavoriteState(item, helper)

                itemView.setOnClickListener { if (data.isNotEmpty()) click(itemView, item) }
            }
        } else {
            //适配局部刷新
            changeFavoriteState(item, helper)
        }
    }


    /**
     * 改变收藏状态
     */
    private fun changeFavoriteState(item: TuListBean, helper: BaseViewHolder) {

        mContext.tuDB.use {
            val list = select(TuTable.TABLE_NAME).whereSimple(TuTable.ADDRESS + "=?", item.url).parseList { Tu(HashMap(it)) }
            if (list.isNotEmpty())
                helper.getView<FrameLayout>(R.id.ll).setBackgroundColor(Color.parseColor("#f97198"))
            else
                helper.getView<FrameLayout>(R.id.ll).setBackgroundColor(Color.WHITE)
        }
    }

}
