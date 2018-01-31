package com.wongxd.absolutedomain.base.click;


import android.view.View;

import java.lang.reflect.Field;

/*###########################################优雅的点击########################################################
//提供一个静态方法*/
public class ClickFilter {
    public static void setFilter(View view) {
        try {
            Field field = View.class.getDeclaredField("mListenerInfo");
            field.setAccessible(true);
            Class listInfoType = field.getType();
            Object listinfo = field.get(view);
            Field onclickField = listInfoType.getField("mOnClickListener");
            View.OnClickListener origin = (View.OnClickListener) onclickField.get(listinfo);
            onclickField.set(listinfo, new ClickProxy(origin));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}