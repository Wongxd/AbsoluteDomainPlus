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


object TextTable {
    val TABLE_NAME = "domain_text"
    val ID = "id"
    val NAME = "name"
    val AUTHOR = "author"
    val PREVIEW = "preview"
    val CONTENT = "content"
    val TIME = "time"
    val SITE_CLASS = "siteClass"
}


data class Text(val map: MutableMap<String, Any?>) {
    var id: Long by map
    var name: String by map
    var author: String by map
    var preview: String by map
    var content: String by map
    var time: Long by map
    var siteClass: String by map

    constructor() : this(HashMap())

    constructor(id: Long, name: String, author: String, preview: String, time: Long, content: String, siteClass: String) : this(HashMap()) {
        this.id = id
        this.name = name
        this.author = author
        this.preview = preview
        this.time = time
        this.content = content
        this.siteClass = siteClass
    }

}


val Context.textDB: DatabaseOpenHelper by lazy {
    val vh = DatabaseOpenHelper()

    vh.createTable(tableName = TextTable.TABLE_NAME, columns = arrayOf(
            TextTable.ID to INTEGER + PRIMARY_KEY + UNIQUE,
            TextTable.NAME to TEXT,
            TextTable.AUTHOR to TEXT,
            TextTable.PREVIEW to TEXT,
            TextTable.CONTENT to TEXT,
            TextTable.TIME to INTEGER,
            TextTable.SITE_CLASS to TEXT
    ))

    vh
}


