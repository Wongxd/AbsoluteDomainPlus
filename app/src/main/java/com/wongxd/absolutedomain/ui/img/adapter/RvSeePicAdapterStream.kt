package com.wongxd.absolutedomain.ui.img.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.data.bean.SeeBigPicBean
import loadImg
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * Created by wongxd on 2018/1/5.
 * Copyright © 2017年 no. All rights reserved.
 */


class RvSeePicAdapterStream(val click: (SeeBigPicBean) -> Unit) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_rv_see_pic) {

    private val IMAGE_WIDTH by lazy {
        QMUIDisplayHelper.getScreenWidth(mContext) / 2 - QMUIDisplayHelper.dp2px(mContext, 5)
    }

    private val DEFAULT_IMAGE_HEIGHT by lazy {
        QMUIDisplayHelper.getScreenWidth(mContext) / 3
    }

    private val DEFAULT_SCALE = 16.toFloat() / 9.toFloat()

    /**
     * 存储对应位置的 宽高比  宽/高 的值
     */
    private val imageSizeMap = HashMap<Int, Float>()

    /**
     * @param scaleType 宽/高 的值
     */
    private fun resizeItemView(frontCoverImage: ImageView, scaleType: Float) {
        val params = frontCoverImage.layoutParams
        params.width = IMAGE_WIDTH
        params.height = (IMAGE_WIDTH / scaleType).toInt()
        frontCoverImage.layoutParams = params
    }

    /**
     * 需要在子线程执行
     *
     * 获取图片的 宽高比 宽/高
     *
     * @param context
     * @param url
     * @return  宽/高
     */
    fun load(context: Context, url: String): Float {
        try {
            val bmp =
                    Glide.with(context)
                            .load(url)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
                            .get()
            val oriWidth = bmp.width.toFloat()
            val oriHeight = bmp.height.toFloat()

            return oriWidth / oriHeight

        } catch (e: Exception) {
            e.printStackTrace()
            return 16.toFloat() / 9.toFloat()
        }

    }

    override fun convert(helper: BaseViewHolder, item: String) {
        with(helper) {
            val iv: ImageView = getView<ImageView>(R.id.iv)

            itemView.setOnClickListener { click(SeeBigPicBean(helper.layoutPosition, getView(R.id.iv))) }

                doAsync {
                    val pos = helper.layoutPosition
                    val scale: Float
                    if (imageSizeMap.containsKey(pos)) {
                        scale = imageSizeMap[pos] ?: DEFAULT_SCALE
                    } else {
                        scale = load(mContext, item)
                        imageSizeMap.put(pos, scale)
                    }
                    uiThread {
                        resizeItemView(iv, scale)
                        iv.loadImg(item)
                    }
                }


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




