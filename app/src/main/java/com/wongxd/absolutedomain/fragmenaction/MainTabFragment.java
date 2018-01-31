package com.wongxd.absolutedomain.fragmenaction;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;

import com.wongxd.absolutedomain.R;


public abstract class MainTabFragment extends BaseBackFragment {

    private View root;
    private ViewStub viewStub;
    private View emptyView;
    private View mView;

    @Nullable
    @Override
    final public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        if (null == root) {
            root = inflater.inflate(R.layout.fgt_main_tab, null);
            viewStub = root.findViewById(R.id.viewStub);
            emptyView = root.findViewById(R.id.tv_empty);
        }
        return root;
    }

    @Override
    final public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);

        if (getLayout() <= 0) {
            throw new Resources.NotFoundException("layout not found");
        }

        if (mView != null) {
            emptyView.setVisibility(View.GONE);
        } else {
            viewStub.setLayoutResource(getLayoutRes());
            try {
                mView = viewStub.inflate();
                animShow(mView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mView == null) {
            return;
        }
        initView(mView);
    }

    private void animShow(final View v) {

        //第一个参数为 view对象，第二个参数为 动画改变的类型，第三，第四个参数依次是开始透明度和结束透明度。
        ObjectAnimator alpha = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
        alpha.setDuration(500);//设置动画时间
        alpha.setInterpolator(new DecelerateInterpolator());//设置动画插入器，减速
//        alpha.setRepeatCount(-1);//设置动画重复次数，这里-1代表无限
//        alpha.setRepeatMode(Animation.REVERSE);//设置动画循环模式。
        alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float temp = 1f - animation.getAnimatedFraction();
                emptyView.setAlpha(temp);
            }

        });

        alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                emptyView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        alpha.start();//启动动画。

    }


    @Override
    protected int getLayoutRes() {
        return getLayout();
    }

    protected abstract int getLayout();

    /**
     * 初始化View
     *
     * @param mView
     */
    protected abstract void initView(View mView);


}