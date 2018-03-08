package com.github.wongxd.core_lib.data.download

import com.github.wongxd.core_lib.util.TU
import com.github.wongxd.core_lib.util.file.FileUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.request.GetRequest
import com.lzy.okserver.OkDownload
import com.github.wongxd.core_lib.data.bean.Task
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
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
        doAsync {
            var hadCreatedTask = false
            tasks.iterator().forEach {
                //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
                val request = OkGo.get<File>(it.url)//
                doSpecialHeaderLogic(request, it.url)
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
            uiThread { if (hadCreatedTask) TU.cT("已智能忽略 下载中任务") }
        }

    }

    /**
     * 有的站点做了反爬虫，需要header信息
     */
    private fun doSpecialHeaderLogic(request: GetRequest<File>?, url: String) {
        request?.let {
            if (url.contains("mzitu") || url.contains("meizitu")) {
                request.headers("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                        .headers("Connection", "Keep-Alive")
                        .headers("Host", "i.meizitu.net")
                        .headers("Referer", "http://www.mzitu.com/")
                        .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
            }
        }
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