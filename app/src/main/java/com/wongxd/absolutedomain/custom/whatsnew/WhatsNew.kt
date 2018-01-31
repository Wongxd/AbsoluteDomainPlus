package com.wongxd.absolutedomain.custom.whatsnew

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.custom.whatsnew.adapter.ItemsAdapter
import com.wongxd.absolutedomain.custom.whatsnew.item.WhatsNewItem
import com.wongxd.absolutedomain.custom.whatsnew.util.PresentationOption


/**
 * Created by lizhaotailang on 30/11/2017.
 *
 *
 * WhatsNew.newInstance(
WhatsNewItem("Nice Icons", "Completely customize colors, texts and icons.", R.drawable.ic_heart),
WhatsNewItem("Such Easy", "Setting this up only takes 2 lines of code, impressive you say?", R.drawable.ic_thumb_up),
WhatsNewItem("Very Sleep", "It helps you get more sleep by writing less code.", R.drawable.ic_satisfied_face),
WhatsNewItem("Text Only", "No icons? Just go with plain text.")
).presentAutomatical


 *
 * val whatsnew = whatsNew {
item {
title = "Nice Icons"
content = "Completely customize colors, texts and icons."
imageRes = R.drawable.ic_heart
}
item {
title = "Such Easy"
content = "Setting this up only takes 2 lines of code, impressive you say?"
imageRes = R.drawable.ic_thumb_up
}
}
whatsnew.presentAutomatically(this)
 *
 *
 */
class WhatsNew : DialogFragment() {

    var mItems: Array<WhatsNewItem>? = null
    var presentationOption: PresentationOption = PresentationOption.IF_NEEDED
    var titleText: CharSequence = "What's New"
    var titleColor: Int = Color.parseColor("#000000")
    var itemTitleColor: Int? = null
    var itemContentColor: Int? = null
    var buttonBackground: Int = Color.parseColor("#000000")
    var buttonText: String = "继续"
    var buttonTextColor: Int = Color.parseColor("#FFEB3B")

    private val TAG = "WhatsNew"

    companion object {
        @JvmField
        val ARGUMENT = "argument"

        private val LAST_VERSION_CODE = "LAST_VERSION_CODE"
        private val LAST_VERSION_NAME = "LAST_VERSION_NAME"

        val DEFAULT_DOWNLOADURL = "https://wongxd.github.io"

        @JvmStatic
        fun newInstance(vararg items: WhatsNewItem): WhatsNew {
            val bundle = Bundle()
            bundle.putParcelableArray(ARGUMENT, items)
            return WhatsNew().apply { arguments = bundle }
        }

        @JvmStatic
        fun newInstance(items: List<WhatsNewItem>): WhatsNew {
            val bundle = Bundle()
            bundle.putParcelableArray(ARGUMENT, items.toTypedArray())
            return WhatsNew().apply { arguments = bundle }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { mItems = it.getParcelableArray(ARGUMENT) as Array<WhatsNewItem>? }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.whatsnew_main, container, false)

        // The title text view.
        with(view.findViewById<TextView>(R.id.titleTextView)) {
            text = titleText
            setTextColor(titleColor)
        }

        // The recycler view.
        with(view.findViewById<RecyclerView>(R.id.itemsRecyclerView)) {
            if (mItems != null && context != null) {
                layoutManager = LinearLayoutManager(context)
                adapter = ItemsAdapter(mItems!!, context!!).apply {
                    itemContentColor?.let { this.contentColor = it }
                    itemTitleColor?.let { this.titleColor = it }
                }
            }
        }

        // The button.
        with(view.findViewById<Button>(R.id.button)) {
            text = buttonText
            setTextColor(buttonTextColor)
            setBackgroundColor(buttonBackground)
            setOnClickListener { dismiss() }
        }


        with(view.findViewById<Button>(R.id.btn_download)) {
            text = "下载"
            setTextColor(buttonTextColor)
            setBackgroundColor(buttonBackground)
            setOnClickListener {
                lis?.invoke(downloadUrl)
                dismiss()
            }
        }

        // Make the dialog fullscreen.
        val window = dialog.window
        window.setBackgroundDrawableResource(android.R.color.white)
        window.decorView.setPadding(0, 0, 0, 0)
        with(window.attributes) {
            gravity = Gravity.BOTTOM
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        // Animate.
        window.setWindowAnimations(R.style.WhatsNewDialogAnimation)

        return view
    }

    fun presentAutomatically(activity: AppCompatActivity, lis: ((url: String) -> Unit)? = null, downLoadUrl: String = this.downloadUrl) {

        when (presentationOption) {
            PresentationOption.DEBUG -> {
                show(activity.supportFragmentManager, TAG)
            }
            PresentationOption.NEVER -> {
                return
            }
            else -> {
                // Obtain the last version code from sp.
                val lastVersionCode = PreferenceManager.getDefaultSharedPreferences(activity)
                        .getInt(LAST_VERSION_CODE, 0)
                var nowVersionCode = 0

                var lastFirstNumOfVersionName = 0
                var nowFirstNumOfVersionName = 0
                var lastSecondNumOfVersionName = 0
                var nowSecondNumOfVersionName = 0

                try {
                    var tmp = ""
                    activity.packageManager
                            .getPackageInfo(activity.packageName, 0)
                            .let {
                                nowVersionCode = it.versionCode
                                tmp = it.versionName
                            }

                    // Obtain the first two numbers of current version name.
                    tmp.split("\\.".toRegex())
                            .filter { !it.isEmpty() && !it.isBlank() }
                            .apply {
                                if (size >= 1) {
                                    nowFirstNumOfVersionName = this[0].toInt()
                                }

                                if (size >= 2) {
                                    nowSecondNumOfVersionName = this[1].toInt()
                                }
                            }

                    // Obtain the first two numbers of last version name.
                    PreferenceManager.getDefaultSharedPreferences(activity)
                            .getString(LAST_VERSION_NAME, "")
                            .split("\\.".toRegex())
                            .filter { !it.isEmpty() && !it.isBlank() }
                            .apply {
                                if (size >= 1) {
                                    lastFirstNumOfVersionName = this[0].toInt()
                                }

                                if (size >= 2) {
                                    lastSecondNumOfVersionName = this[1].toInt()
                                }
                            }

                    if (presentationOption == PresentationOption.ALWAYS) {
                        if (nowVersionCode >= 0 && nowVersionCode > lastVersionCode) {

                            // Show the dialog.
                            show(activity.supportFragmentManager, TAG)
                            // Save the latest version code to sp.
                            PreferenceManager.getDefaultSharedPreferences(activity)
                                    .edit()
                                    .putInt(LAST_VERSION_CODE, nowVersionCode)
                                    .apply()
                        }
                    } else { // presentationOption == PresentationOption.IF_NEEDED
                        if (((nowFirstNumOfVersionName >= 0 && nowFirstNumOfVersionName > lastFirstNumOfVersionName)
                                        || (nowSecondNumOfVersionName >= 0 && nowSecondNumOfVersionName > lastSecondNumOfVersionName))
                                && (nowVersionCode >= 0 && lastVersionCode >= 0 && nowVersionCode > lastVersionCode)) {

                            // Show the dialog.
                            show(activity.supportFragmentManager, TAG)
                            // Save the latest version name to sp.
                            PreferenceManager.getDefaultSharedPreferences(activity)
                                    .edit()
                                    .putInt(LAST_VERSION_CODE, nowVersionCode)
                                    .putString(LAST_VERSION_NAME, "$nowFirstNumOfVersionName.$nowSecondNumOfVersionName")
                                    .apply()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }


        setDownloadUrl(downLoadUrl)
        lis?.let { setDownloadListener(it) }

    }


    private var downloadUrl: String = DEFAULT_DOWNLOADURL
    private var lis: ((url: String) -> Unit)? = null
    fun setDownloadListener(lis: (url: String) -> Unit) {
        this.lis = lis
    }

    fun setDownloadUrl(url: String) {
        this.downloadUrl = url
    }

}