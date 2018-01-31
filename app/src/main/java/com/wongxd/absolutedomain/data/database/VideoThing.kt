package com.wongxd.absolutedomain.data.database

import android.content.Context
import com.wongxd.absolutedomain.base.kotin.extension.database.DatabaseOpenHelper
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.db.PRIMARY_KEY
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.UNIQUE

/**
 * Created by wxd1 on 2017/6/28.
 */


object VideoTable {
    val TABLE_NAME = "domain_video"
    val ID = "id"
    val NAME = "name"
    val ADDRESS = "address"
    val PREVIEW = "preview"
    val DURATION = "duration"
    val TIME = "time"
    val SITE_CLASS = "siteClass"
}


data class Video(val map: MutableMap<String, Any?>) {
    var id: Long by map
    var name: String by map
    var address: String by map
    var preview: String by map
    var duration: String by map
    var time: Long by map
    var siteClass: String by map

    constructor() : this(HashMap())

    constructor(id: Long, name: String, address: String, preview: String, time: Long, duration: String, siteClass: String) : this(HashMap()) {
        this.id = id
        this.name = name
        this.address = address
        this.preview = preview
        this.time = time
        this.duration = duration
        this.siteClass = siteClass
    }

}


val Context.videoDB: DatabaseOpenHelper by lazy {
    val vh = DatabaseOpenHelper()

    vh.createTable(tableName = VideoTable.TABLE_NAME, columns = arrayOf(
            VideoTable.ID to INTEGER + PRIMARY_KEY + UNIQUE,
            VideoTable.NAME to TEXT,
            VideoTable.ADDRESS to TEXT,
            VideoTable.PREVIEW to TEXT,
            VideoTable.DURATION to TEXT,
            VideoTable.TIME to INTEGER,
            VideoTable.SITE_CLASS to TEXT
    ))

    vh
}


