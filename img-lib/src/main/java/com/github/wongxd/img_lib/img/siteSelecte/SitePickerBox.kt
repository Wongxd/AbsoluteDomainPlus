package com.github.wongxd.img_lib.img.siteSelecte

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.github.wongxd.core_lib.custom.MarqueeTextview
import com.github.wongxd.core_lib.custom.directselect.DSAbstractPickerBox
import com.github.wongxd.img_lib.R
import com.github.wongxd.img_lib.data.bean.ImgSiteBean

/**
 * Created by wongxd on 2018/3/12.
 */
class SitePickerBox : DSAbstractPickerBox<ImgSiteBean> {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v: View = mInflater.inflate(R.layout.layout_site_picker_box, this, true)
        text = v.findViewById<MarqueeTextview>(R.id.tv)
        cellRoot = v.findViewById(R.id.fl_root)
    }


    private var cellRoot: View
    private var lis: SiteSelecterListener? = null
    private var text: MarqueeTextview? = null

    private var currentIndex = -1

    override fun onSelect(selectedItem: ImgSiteBean?, selectedIndex: Int) {
        if (currentIndex == selectedIndex) return
        selectedItem?.let {
            lis?.onCall(it, selectedIndex)
            text?.text = "切换站点( ${it.title})"
            currentIndex = selectedIndex
        }
    }


    override fun getCellRoot(): View = cellRoot


    fun setSelectedListener(lis: SiteSelecterListener) {
        this.lis = lis
    }


    interface SiteSelecterListener {
        fun onCall(item: ImgSiteBean, selectedIndex: Int)
    }

}