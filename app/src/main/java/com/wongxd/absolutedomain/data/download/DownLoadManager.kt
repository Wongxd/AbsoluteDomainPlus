package com.wongxd.absolutedomain.data.download

import com.wongxd.absolutedomain.data.bean.Task
import com.wongxd.absolutedomain.util.TU
import com.wongxd.absolutedomain.util.file.FileUtils
import com.lzy.okgo.OkGo
import com.lzy.okserver.OkDownload
import java.io.File

/**
 * Created by wongxd on 2018/1/9.
 */
class DownLoadManager private constructor() {

    private val okDownload: OkDownload by lazy {
        val obj = OkDownload.getInstance()
        val path = FileUtils.getAppRootDirPath() + "/download/"
        obj.folder = path
        obj.threadPool.setCorePoolSize(3)
        obj
    }


    companion object {
        private val instance by lazy { DownLoadManager() }
        fun get(): DownLoadManager = instance
    }


    fun addTask(vararg tasks: Task) {
        var hadCreatedTask = false
        tasks.iterator().forEach {
            //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
            val request = OkGo.get<File>(it.url)//

            if (!okDownload.hasTask(it.tag)) {

                val fileName = if (it.title.isBlank()) {
                    it.url.replace("/", "")
                } else it.title

                //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一,我这里用url作为了tag
                OkDownload.request(it.tag, request)//
                        .folder(it.type.savePath)
                        .fileName(fileName)
                        .extra1(it.type)
                        .extra2(it.preview)
                        .save()//
                        .start()
            } else hadCreatedTask = true
        }
        if (hadCreatedTask) TU.cT("已智能忽略 下载中任务")

    }


    fun removeTask(vararg taskTags: String, isDeleteFile: Boolean = false) {

        taskTags.iterator().forEach {
            val task = okDownload.getTask(it) ?: null

            task?.remove(isDeleteFile)

        }
    }


    fun startAllTask() {
        okDownload.startAll()
    }


    fun pauseAllTask() {
        okDownload.pauseAll()
    }


    fun deldeteAllTask(isDeleteFile: Boolean = false) {
        okDownload.removeAll(isDeleteFile)
    }


}