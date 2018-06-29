package com.wongxd.absolutedomain.ui

import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.R

/**
 * Created by wongxd on 2018/1/4.
 */
enum class Menu(val icon: Int, private val titleRes: Int) {

    MENU_TU_FAVORITE(R.drawable.menu_tu_favorit, R.string.menu_favorite),
    MENU_DOWNLOAD_MANAGER(R.drawable.menu_download, R.string.menu_download_manager),
    MENU_BOOK_LIST(R.drawable.menu_book_list, R.string.menu_book_list),
    MENU_ALIPAY_RED(R.drawable.menu_alipay, R.string.menu_alipay_red),
    MENU_DONATE(R.drawable.menu_donate, R.string.menu_donate),

    MENU_TOOL(R.drawable.menu_tool, R.string.menu_tool),
    MENU_THEME(R.drawable.menu_theme, R.string.menu_theme),
    MENU_CACHE(R.drawable.menu_cache, R.string.menu_cache),
    MENU_RATING(R.drawable.menu_rating, R.string.menu_rating),
    MENU_UPGRADE(R.drawable.menu_upgrage, R.string.menu_upgrade),
    MENU_ABOUT(R.drawable.menu_about, R.string.menu_about),


    MENU_SETTING(R.drawable.menu_setting, R.string.menu_setting);

    var title: String
    var id: Long = this.ordinal.toLong()

    init {
        title = getTitle(titleRes)
    }

    private fun getTitle(res: Int): String = App.instance.getString(res)

}