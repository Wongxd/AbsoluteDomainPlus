package com.github.wongxd.img_lib.img.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.wongxd.img_lib.R
import com.github.wongxd.img_lib.data.bean.SeeBigPicBean
import loadImg


/**
 * Created by wongxd on 2018/1/5.
 * Copyright © 2017年 no. All rights reserved.
 */


class RvSeePicAdapter(val click: (SeeBigPicBean) -> Unit) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_rv_see_pic) {

    override fun convert(helper: BaseViewHolder, item: String) {
        with(helper) {
            val iv: ImageView = getView<ImageView>(R.id.iv)

            itemView.setOnClickListener { click(SeeBigPicBean(helper.layoutPosition, getView(R.id.iv))) }

            iv.loadImg(item)

        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder?, position: Int, payloads: MutableList<Any>?) {
        if (payloads == null || payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        convert(holder!!, mData[holder.layoutPosition - headerLayoutCount])
    }

}




