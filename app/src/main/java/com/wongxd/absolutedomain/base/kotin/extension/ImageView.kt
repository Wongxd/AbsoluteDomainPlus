
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.wongxd.absolutedomain.App
import com.wongxd.absolutedomain.R
import com.wongxd.absolutedomain.base.glide.GlideCircleTransform
import com.wongxd.absolutedomain.base.glide.GlideRoundTransform

/**
 * Created by wxd1 on 2017/6/30.
 */


fun ImageView.loadImg(imgPath: Any) {
    Glide.with(App.instance).load(imgPath)
            .placeholder(ColorDrawable(Color.RED))
            .error(R.drawable.error)
            .crossFade(300)
            .transform(GlideRoundTransform(App.instance, 5))
            .centerCrop()
            .into(this)

}

fun ImageView.loadOriginScaleImg(imgPath: Any) {
    Glide.with(App.instance).load(imgPath)
            .placeholder(ColorDrawable(Color.RED))
            .error(R.drawable.error)
            .crossFade(300)
            .into(this)

}

fun ImageView.loadHeader(imgPath: Any) {
    Glide.with(App.instance).load(imgPath)
            .placeholder(R.drawable.ic_user)
            .crossFade(300)
            .transform(GlideCircleTransform(App.instance))
            .into(this)
}