package com.wongxd.absolutedomain.base.kotin.extension.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import cn.bmob.v3.BmobRole.tableName
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.data.database.TuTable
import org.jetbrains.anko.db.*

/**
 * extension
 */

/**
 * 解析器接收的immutable map被我们转化成了一个mutable map（我们需要在database model中是可以修改的）通过使用相应的HashMap构造函数。在DayForecast中的构造函数中会使用到这个HashMap。

所以，这个查询返回了一个Cursor，要理解这个场景的背后到底发生了什么。parseList中会迭代它，然后得到Cursor的每一行直到最后一个。对于每一行，它会创建一个包含这列的key和给对应的key赋值后的map。然后把这个map返回给这个解析器。

如果查询没有任何结果，parseList会返回一个空的list。
 */
fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
        parseList(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })


/**
 * 我们使用的是parseOpt。这个函数返回一个可null的对象。结果可以使一个null或者单个的对象，这取决于请求是否能在数据库中查询到数据。
 * <p>这里有另外一个叫parseSingle的函数，本质上是一样的，但是它返回的事一个不可null的对象。所以如果没有在数据库中找到这一条数据，它会抛出一个异常。
 */
fun <T : Any> SelectQueryBuilder.parseOpt(parser: (Map<String, Any?>) -> T): T? =
        parseOpt(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })


/**
 * 删除表中的所用内容
 */
fun SQLiteDatabase.clear(tableName: String) {
    execSQL("delete from $tableName")
}


fun SelectQueryBuilder.byId(id: Long) = whereSimple("_id = ?", id.toString())


/**
 * 在代码块，我们可以在不使用引用和变量的情况下使用dailyForecast和map，只是像我们在这个类内部一样就可以了。
 * 针对插入我们使用另外一个Anko函数，它需要一个表名和一个vararg修饰的Pair<String, Any>作为参数。这个函数会把vararg转换成Android SDK需要的ContentValues对象。
 * 所以我们的任务组成是把map转换成一个vararg数组。我们为MutableMap创建了一个扩展函数：
 * 它是支持可null的值的（这是map delegate的条件），把它转换为非null值（select函数需要）的Array所组成的Pairs。
 * 所以，这个新的函数我们可以这么使用：
 *
 * insert(CityForecastTable.NAME, *map.toVarargArray())
 *
 * 它在CityForecast中插入了一个一行新的数据。在toVarargArray函数结果前面使用  *  表示这个array会被分解成为一个vararg参数。
 *这个在Java中是自动处理的，但是我们需要在Kotlin中明确指明。
 */
fun <K, V : Any> MutableMap<K, V?>.toVarargArray(): Array<out Pair<K, V>> =
        map({ Pair(it.key, it.value!!) }).toTypedArray()


inline fun <T, R : Any> Iterable<T>.firstResult(predicate: (T) -> R?): R {
    for (element in this) {
        val result = predicate(element)
        if (result != null) return result
    }
    throw NoSuchElementException("No element matching predicate was found.")
}


/**
 * openhelper
 */
open class DatabaseOpenHelper(
        ctx: Context = App.instance,
        dbName: String = DB_NAME,
        dbVersion: Int = DB_VERSION)
    : ManagedSQLiteOpenHelper(ctx, dbName, null, dbVersion) {

    companion object {
        val DB_NAME = App.instance.packageName + ".db"
        val DB_VERSION = 3
    }


    override fun onCreate(db: SQLiteDatabase) {

        db.createTable("main", true,
                TuTable.ID to INTEGER + PRIMARY_KEY + UNIQUE,
                TuTable.NAME to TEXT,
                TuTable.ADDRESS to TEXT)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(tableName, true)
        onCreate(db)
    }


    open fun createTable(tableName: String, ifNotExists: Boolean=true, columns: Array<Pair<String, SqlType>>) {
        writableDatabase.createTable(tableName, ifNotExists, *columns)
        writableDatabase.close()
    }
}













