package com.zijie.treader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.zijie.treader.Config;
import com.zijie.treader.R;

/**
 * Created by wongxd on 2018/06/28.
 *            https://github.com/wongxd
 *            wxd1@live.com
 *
 */
public class PageModeDialog extends Dialog implements View.OnClickListener {


    private TextView tv_simulation;
    private TextView tv_cover;
    private TextView tv_slide;
    private TextView tv_none;

    private Config config;
    private PageModeListener pageModeListener;

    private PageModeDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public PageModeDialog(Context context) {
        this(context, R.style.setting_dialog);
    }

    public PageModeDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_pagemode);
        tv_simulation = (TextView) findViewById(R.id.tv_simulation);
        tv_cover = (TextView) findViewById(R.id.tv_cover);
        tv_slide = (TextView) findViewById(R.id.tv_slide);
        tv_none = (TextView) findViewById(R.id.tv_none);

        tv_simulation.setOnClickListener(this);
        tv_cover.setOnClickListener(this);
        tv_slide.setOnClickListener(this);
        tv_none.setOnClickListener(this);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);

        config = Config.getInstance();
        selectPageMode(config.getPageMode());
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_simulation) {
            selectPageMode(Config.PAGE_MODE_SIMULATION);
            setPageMode(Config.PAGE_MODE_SIMULATION);

        } else if (i == R.id.tv_cover) {
            selectPageMode(Config.PAGE_MODE_COVER);
            setPageMode(Config.PAGE_MODE_COVER);

        } else if (i == R.id.tv_slide) {
            selectPageMode(Config.PAGE_MODE_SLIDE);
            setPageMode(Config.PAGE_MODE_SLIDE);

        } else if (i == R.id.tv_none) {
            selectPageMode(Config.PAGE_MODE_NONE);
            setPageMode(Config.PAGE_MODE_NONE);

        }
    }

    //设置翻页
    public void setPageMode(int pageMode) {
        config.setPageMode(pageMode);
        if (pageModeListener != null) {
            pageModeListener.changePageMode(pageMode);
        }
    }

    //选择怕翻页
    private void selectPageMode(int pageMode) {
        if (pageMode == Config.PAGE_MODE_SIMULATION) {
            setTextViewSelect(tv_simulation, true);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_COVER) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, true);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_SLIDE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, true);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == Config.PAGE_MODE_NONE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, true);
        }
    }

    //设置按钮选择的背景
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_select_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.read_dialog_button_select));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.button_bg));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        }
    }

    public void setPageModeListener(PageModeListener pageModeListener) {
        this.pageModeListener = pageModeListener;
    }

    public interface PageModeListener {
        void changePageMode(int pageMode);
    }
}
