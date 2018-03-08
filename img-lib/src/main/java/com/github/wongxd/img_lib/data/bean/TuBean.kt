package com.github.wongxd.img_lib.data.bean

import android.widget.ImageView
import com.github.wongxd.img_lib.img.BaseTuSite
import java.io.Serializable
import java.util.*

/**
 * Created by wongxd on 2018/3/8.
 */
//图
class ImgSiteBean(var title: String, var site: Class<out BaseTuSite>)

data class ImgTypeBean(val url: String, val title: String)

data class TuListBean(val title: String, val preview: String, val url: String, val date: String) : Serializable

data class TuChildDetailBean(val title: String, val urls: List<String>) : Serializable

data class SeeBigPicBean(val position: Int, val v: ImageView, val urls: ArrayList<String>? = null)
//图