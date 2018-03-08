package com.github.wongxd.core_lib.data.bean.text

/**
 * Created by wongxd on 2018/1/26.
 */

data class EssayBean(val author: List<AuthorBean>, val content_id: String,
                     val guide_word: String,
                     val hp_makettime: String, //时间
                     val hp_title: String) {
    data class AuthorBean(val user_name: String) {}
}

data class EssayListBean(val res:Int,val data:List<EssayBean>)

data class EssayContentBean(val res: Int,val data: DataBean){
    data class DataBean(var guide_word: String="", var hp_title: String="", var hp_author: String="", var hp_content: String="")
}





data class SerialBean(val author: List<AuthorBean>,
                      val id: String,
                      val excerpt: String,
                      val maketime: String,
                      val title: String) {
    data class AuthorBean(val user_name: String) {}
}



