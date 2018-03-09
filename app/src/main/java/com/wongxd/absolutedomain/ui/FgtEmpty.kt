package com.wongxd.absolutedomain.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wongxd.absolutedomain.R
import me.yokeyword.fragmentation.SupportFragment

/**
 * Created by wongxd on 2018/3/9.
 */
class FgtEmpty : SupportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fgt_empty, container, false)
        return v
    }
}