import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.GlideApp
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.glide.GlideRoundTransform

/**
 * Created by wxd1 on 2017/6/30.
 */


fun ImageView.loadImg(imgPath: Any) {
    val url =
            if (imgPath is String && (imgPath.contains("mzitu") || imgPath.contains("meizitu"))) {
                buildMZiTuUrl(imgPath)
            } else imgPath

    val options: RequestOptions = RequestOptions()
            .placeholder(ColorDrawable(Color.RED))
            .error(R.drawable.error)
            .transform(GlideRoundTransform(App.instance, 5))
            .centerCrop()
    GlideApp.with(App.instance).load(url)
            .apply(options)
            .transition(DrawableTransitionOptions().crossFade(300))
            .into(this)

}

fun ImageView.loadOriginScaleImg(imgPath: Any) {

    val url =
            if (imgPath is String && (imgPath.contains("mzitu") || imgPath.contains("meizitu"))) {
                buildMZiTuUrl(imgPath)
            } else imgPath

    val options: RequestOptions = RequestOptions()
            .placeholder(ColorDrawable(Color.RED))
            .error(R.drawable.error)
    GlideApp.with(App.instance).load(url)
            .apply(options)
            .transition(DrawableTransitionOptions().crossFade(300))
            .into(this)

}

fun ImageView.loadHeader(imgPath: Any) {

    val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.ic_user)
            .error(R.drawable.error)
            .transform(CircleCrop())
    Glide.with(App.instance).load(imgPath)
            .apply(options)
            .transition(DrawableTransitionOptions().crossFade(300))
            .into(this)
}


/**
 * mzitu.com 做了反爬虫
 */
private fun buildMZiTuUrl(url: String): GlideUrl? {
    return if (TextUtils.isEmpty(url)) {
        null
    } else {
        GlideUrl(url, LazyHeaders.Builder()
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Host", "i.meizitu.net")
                .addHeader("Referer", "http://www.mzitu.com/")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                .build())
    }
}

