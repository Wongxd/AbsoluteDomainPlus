package com.wongxd.absolutedomain.data.bean

import android.widget.ImageView
import com.wongxd.absolutedomain.ui.img.BaseTuSite
import com.wongxd.absolutedomain.ui.text.FgtTextItem
import com.wongxd.absolutedomain.ui.video.FgtVideoItem
import com.wongxd.absolutedomain.util.file.FileUtils
import java.io.Serializable
import java.util.*

/**
 * Created by wxd1 on 2017/7/10.
 */

//图
class ImgSiteBean(var title: String, var site: Class<out BaseTuSite>)

data class ImgTypeBean(val url: String, val title: String)

data class TuListBean(val title: String, val preview: String, val url: String, val date: String) : Serializable

data class TuChildDetailBean(val title: String, val urls: List<String>) : Serializable

data class SeeBigPicBean(val position: Int, val v: ImageView, val urls: ArrayList<String>? = null)
//图


//视频
class VideoSiteBean(val title: String, val fgt: FgtVideoItem)

data class VideoListBean(val title: String, val preview: String, val url: String, val duration: Long) : Serializable

//视频


//文字
class TextSiteBean(val title: String, val fgt: FgtTextItem)

data class TextListBean(val title: String, val author: String, val preview: String, val content: String="", val textId: String = "") : Serializable

//文字


//下载
enum class TaskType(var savePath: String) : Serializable {

    IMG("/img/"), VIDEO("/video/"), TEXT("/txt/");


    init {
        savePath = FileUtils.getAppRootDirPath() + "/download" + savePath
    }
}

data class Task(val url: String, val title: String = "", val preview: String = "", val type: TaskType) : Serializable {
    val tag: String = url
}

//下载
