package com.github.wongxd.core_lib.data.bean

import com.github.wongxd.core_lib.util.file.FileUtils
import java.io.Serializable

/**
 * Created by wxd1 on 2017/7/10.
 */


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
