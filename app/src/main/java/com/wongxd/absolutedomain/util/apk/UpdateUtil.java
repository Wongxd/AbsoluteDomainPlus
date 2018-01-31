package com.wongxd.absolutedomain.util.apk;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.wongxd.absolutedomain.util.TU;

public class UpdateUtil {

    public static void goToAppMarket(Context context) {
        if (PackageUtil.isInsatalled(context, "com.coolapk.market")) {
            Toast.makeText(context, "本应用目前仅发布在酷安，请前往酷安", Toast.LENGTH_SHORT).show();
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/apk/" + context.getPackageName())));
            } catch (ActivityNotFoundException e) {
                TU.cT("跳转出错，请向作者反馈");
            }
        } else {
            Toast.makeText(context, "未检测到酷安市场客户端，正在跳转到网页", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coolapk.com/apk/149784")));
        }
    }

}