package com.wongxd.absolutedomain.ui

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.github.wongxd.core_lib.base.utils.utilcode.util.ActivityUtils
import com.github.wongxd.core_lib.custom.NumberProgressBar
import com.github.wongxd.core_lib.data.bean.TaskType
import com.github.wongxd.core_lib.util.Tips
import com.lzy.okgo.db.DownloadManager
import com.lzy.okgo.model.Progress
import com.lzy.okserver.OkDownload
import com.lzy.okserver.download.DownloadListener
import com.lzy.okserver.download.DownloadTask
import com.orhanobut.logger.Logger
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.wongxd.absolutedomain.R
import loadImg
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.text.NumberFormat
import kotlin.properties.Delegates

/**
 * Created by wongxd on 2018/1/10.
 */
class DownloadAdapter() : BaseQuickAdapter<DownloadTask,
        DownloadAdapter.ViewHolder>(R.layout.item_download_manager), LifecycleObserver {

    override fun convert(helper: ViewHolder?, item: DownloadTask?) {
        helper?.let {
            val task = item!!
            val tag = createTag(task)
            task.register(ListDownloadListener(tag, helper))//
//                .register(LogDownloadListener())
            helper.tag = tag
            helper.task = task
            helper.bind()
        }
    }

    private val numberFormat: NumberFormat by lazy { NumberFormat.getPercentInstance() }
    private var type: Int = 0

    init {
        numberFormat.minimumFractionDigits = 2
    }


    constructor(type: Int, lifecycle: Lifecycle) : this() {
        this.type = type
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun updateData() {
//        com.orhanobut.logger.Logger.e("下载适配器 updateData")
        //这里是将数据库的数据恢复
        doAsync {
            var values: List<DownloadTask> = ArrayList()
            if (type == TYPE_DOWNLOADING) values = OkDownload.restore(DownloadManager.getInstance().downloading)
            else {
                val allFinished = OkDownload.restore(DownloadManager.getInstance().finished)
                if (type == TYPE_IMG) values = allFinished.filter {
                    val taskType = it.progress.extra1 as TaskType
                    taskType == TaskType.IMG
                }
                else if (type == TYPE_VIDEO) values = allFinished.filter {
                    val taskType = it.progress.extra1 as TaskType
                    taskType == TaskType.VIDEO
                }
                else if (type == TYPE_TEXT) values = allFinished.filter {
                    val taskType = it.progress.extra1 as TaskType
                    taskType == TaskType.TEXT
                }
            }

            uiThread { setNewData(values.sortedByDescending { it.progress.date }) }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unRegister() {
//        com.orhanobut.logger.Logger.e("下载适配器 unRegister")
        val taskMap = OkDownload.getInstance().taskMap
        for (task in taskMap.values) {
            task.unRegister(createTag(task))
        }
    }

    private fun createTag(task: DownloadTask): String {
        return task.progress.extra1.toString() + "_" + task.progress.tag
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val priority: TextView = itemView.findViewById(R.id.priority)
        private val downloadSize: TextView = itemView.findViewById(R.id.downloadSize)
        private val tvProgress: TextView = itemView.findViewById(R.id.netSpeed)
        private val netSpeed: TextView = itemView.findViewById(R.id.name)
        private val pbProgress: NumberProgressBar = itemView.findViewById(R.id.pbProgress)
        private val download: Button = itemView.findViewById(R.id.start)
        private val remove: Button = itemView.findViewById(R.id.remove)

        var tag: String by Delegates.notNull()

        var task: DownloadTask by Delegates.notNull()


        fun bind() {
            val progress = task.progress
            val taskType = progress.extra1 as TaskType?
            if (taskType != null) {
                val iconRes: Any = when (taskType) {
                    TaskType.IMG -> progress.url

                    TaskType.VIDEO -> progress.extra2 as String? ?: R.drawable.video

                    TaskType.TEXT -> R.drawable.text
                }

                Logger.e("下载路径" + progress.filePath)
                download.setOnClickListener { start() }
                remove.setOnClickListener { remove() }
                icon.loadImg(iconRes)
                name.text = progress.fileName
                priority.text = String.format("优先级：%s", progress.priority)
                refresh(progress)
            }
        }

        fun refresh(progress: Progress) {
            val currentSize = Formatter.formatFileSize(mContext, progress.currentSize)
            val totalSize = Formatter.formatFileSize(mContext, progress.totalSize)
            downloadSize.text = currentSize + "/" + totalSize
            priority.text = String.format("优先级：%s", progress.priority)
            when (progress.status) {
                Progress.NONE -> {
                    netSpeed.text = "停止"
                    download.text = "下载"
                }
                Progress.PAUSE -> {
                    netSpeed.text = "暂停中"
                    download.text = "继续"
                }
                Progress.ERROR -> {
                    netSpeed.text = "下载出错"
                    download.text = "出错"
                }
                Progress.WAITING -> {
                    netSpeed.text = "等待中"
                    download.text = "等待"
                }
                Progress.FINISH -> {
                    netSpeed.text = "下载完成  ${progress.filePath}"
                    download.text = "完成"
                }
                Progress.LOADING -> {
                    val speed = Formatter.formatFileSize(mContext, progress.speed)
                    netSpeed.text = String.format("%s/s", speed)
                    download.text = "暂停"
                }
            }
            tvProgress.text = numberFormat.format(progress.fraction.toDouble())
            pbProgress.setMax(10000)
            pbProgress.setProgress((progress.fraction * 10000).toInt())
        }


        fun start() {
            val progress = task.progress
            when (progress.status) {
                Progress.PAUSE, Progress.NONE, Progress.ERROR -> task.start()
                Progress.LOADING -> task.pause()
                Progress.FINISH -> {

                }
            }
            refresh(progress)
        }


        fun remove() {
            QMUIDialog.MessageDialogBuilder(ActivityUtils.getTopActivity())
                    .setMessage("是否删除任务？")
                    .setTitle("警告")
                    .addAction("删除任务及文件") { dialog, index ->
                        task.remove(true)
                        updateData()
                        dialog.dismiss()
                    }
                    .addAction("删除任务") { dialog, index ->
                        task.remove(false)
                        updateData()
                        dialog.dismiss()
                    }

                    .addAction("取消") { dialog, index ->
                        dialog.dismiss()
                    }
                    .show()


        }


    }

    private inner class ListDownloadListener internal constructor(tag: Any, private val holder: ViewHolder) : DownloadListener(tag) {

        override fun onStart(progress: Progress) {}

        override fun onProgress(progress: Progress) {
            if (tag === holder.tag) {
                holder.refresh(progress)
            }
        }

        override fun onError(progress: Progress) {
            val throwable = progress.exception
            throwable?.printStackTrace()
        }

        override fun onFinish(file: File, progress: Progress) {
            Tips.showSuccessTips(title = "下载完成:", text = progress.filePath)
            updateData()
        }

        override fun onRemove(progress: Progress) {}
    }

    companion object {

        val TYPE_DOWNLOADING = 0
        val TYPE_IMG = 1
        val TYPE_VIDEO = 2
        val TYPE_TEXT = 3
    }
}
