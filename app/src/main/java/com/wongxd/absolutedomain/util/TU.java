package com.wongxd.absolutedomain.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wongxd.absolutedomain.R;


/**
 * Created by wxd1 on 2017/2/17.
 */

public class TU {
    private static volatile Toast toast;
    public static Context c;


    private TU() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    public static void register(Context context) {
        c = context;
    }

    public static void t(String msg) {
        if (toast == null) {
            synchronized (TU.class) {
                if (toast == null) {
                    toast = Toast.makeText(c, msg, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        } else {
            toast.setText(msg);
            toast.show();
        }
    }

    public static void t(int msgRes) {
        cT(c.getString(msgRes));
    }


    //  #######################################自定义布局的Toast#############################

    private static String oldMsg;
    private static long firstShowTime;
    private static long lastShowTime;
    private static TextView tv;

    public static void cT(String s) {

        if (toast == null) {

            toast = new Toast(c);

            View v = View.inflate(c, R.layout.toast_layout, null);
            tv = (TextView) v.findViewById(R.id.tv_toast);
            tv.setText(s);
            tv.setTextColor(Color.WHITE);

            toast.setView(v);

            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            firstShowTime = System.currentTimeMillis();
            oldMsg = s;
        } else {
            lastShowTime = System.currentTimeMillis();
            if (oldMsg.equals(s) && lastShowTime - firstShowTime < Toast.LENGTH_SHORT) {
                toast.show();
            } else {
                tv.setText(s);
                tv.setTextColor(Color.WHITE);

                toast.show();
                oldMsg = s;
            }
            firstShowTime = lastShowTime;
        }


    }

    public static void cT(int res) {
        cT(c.getString(res));
    }


}
