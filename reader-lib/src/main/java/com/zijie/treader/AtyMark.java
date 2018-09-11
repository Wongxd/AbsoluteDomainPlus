package com.zijie.treader;

import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.zijie.treader.adapter.MyPagerAdapter;
import com.zijie.treader.base.BaseActivity;
import com.zijie.treader.db.BookCatalogue;
import com.zijie.treader.util.FileUtils;
import com.zijie.treader.util.PageFactory;

import java.util.ArrayList;


/**
 * Created by Administrator on 2016/1/6.
 */
public class AtyMark extends BaseActivity {

    private Toolbar toolbar;
    private AppBarLayout appbar;
    private TabLayout tabs;
    private ViewPager pager;
    private PageFactory pageFactory;
    private Config config;
    private Typeface typeface;
    private ArrayList<BookCatalogue> catalogueList = new ArrayList<>();
    private DisplayMetrics dm;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_mark;
    }

    @Override
    protected void initData() {
        toolbar = findView(R.id.toolbar);
        appbar = findView(R.id.appbar);
        tabs = findView(R.id.tabs);
        toolbar = findView(R.id.toolbar);
        pager = findView(R.id.pager);

        pageFactory = PageFactory.getInstance();
        config = Config.getInstance();
        dm = getResources().getDisplayMetrics();
        typeface = config.getTypeface();


        setSupportActionBar(toolbar);
        //设置导航图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(FileUtils.getFileName(pageFactory.getBookPath()));
        }


        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), pageFactory.getBookPath()));
        tabs.setupWithViewPager(pager);
    }


    @Override
    protected void initListener() {

    }

}
