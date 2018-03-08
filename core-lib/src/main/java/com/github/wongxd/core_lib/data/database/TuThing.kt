package com.wongxd.absolutedomain.data.database

import android.content.Context
import com.github.wongxd.core_lib.base.kotin.extension.database.DatabaseOpenHelper
import org.jetbrains.anko.db.INTEGER
import org.jetbrains.anko.db.PRIMARY_KEY
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.UNIQUE

/**
 * Created by wxd1 on 2017/6/28.
 */


/**
 * 图集
 */
object TuTable {
    val TABLE_NAME = "domain_tu"
    val ID = "id"
    val NAME = "name"
    val ADDRESS = "address"
    val PREVIEW = "preview"
    val TIME = "time"
    val SITE_CLASS = "siteClass"
}


/**
 * 图集
 */
data class Tu(val map: MutableMap<String, Any?>) {
    var id: Long by map
    var name: String by map
    var address: String by map
    var preview: String by map
    var time: Long by map
    var siteClass: String by map

    constructor() : this(HashMap())

    constructor(id: Long, name: String, address: String, preview: String, time: Long, siteClass: String) : this(HashMap()) {
        this.id = id
        this.name = name
        this.address = address
        this.preview = preview
        this.time = time
        this.siteClass = siteClass
    }
}


val Context.tuDB: DatabaseOpenHelper
        by lazy {
            val td = DatabaseOpenHelper()
            td.createTable(TuTable.TABLE_NAME, columns = arrayOf(
                    TuTable.ID to INTEGER + PRIMARY_KEY + UNIQUE,
                    TuTable.NAME to TEXT,
                    TuTable.ADDRESS to TEXT,
                    TuTable.PREVIEW to TEXT,
                    TuTable.TIME to INTEGER,
                    TuTable.SITE_CLASS to TEXT
            ))
            td
        }


