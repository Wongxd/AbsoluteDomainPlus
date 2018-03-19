package com.github.wongxd.img_lib.img.siteSelecte

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.wongxd.img_lib.R
import com.github.wongxd.img_lib.data.bean.ImgSiteBean

/**
 * Created by wongxd on 2018/3/12.
 */
class SiteAdapter(val ctx: Context, val items: List<ImgSiteBean>, resource: Int = R.layout.item_rv_site)
    : ArrayAdapter<ImgSiteBean>(ctx, resource, items) {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val holder: ViewHolder?

        if (null == convertView) {
            val vi = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = vi.inflate(R.layout.item_rv_site, parent, false)
            holder = ViewHolder()
            holder.text = convertView.findViewById(R.id.tv)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.text?.text = items[position].title

        return convertView!!
    }

    private inner class ViewHolder {
        internal var text: TextView? = null
    }
}
