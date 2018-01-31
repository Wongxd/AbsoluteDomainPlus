package com.wongxd.absolutedomain.base.click;

import android.util.Log;
import android.view.View;

public class ClickProxy implements View.OnClickListener {

    private View.OnClickListener origin;
    private long lastclick = 0;
    private long timems = 1000; //ms
    private IAgain mIAgain;

    public ClickProxy(View.OnClickListener origin, long timems, IAgain again) {
        this.origin = origin;
        this.mIAgain = again;
        this.timems = timems;
    }

    public ClickProxy(View.OnClickListener origin) {
        this.origin = origin;
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastclick >= timems) {
            origin.onClick(v);
            lastclick = System.currentTimeMillis();
            Log.d("优雅点击", "一秒内多次点击");
        } else {
            if (mIAgain != null) mIAgain.onAgain();
        }
    }

    public interface IAgain {
        void onAgain();//重复点击
    }

}