package com.wongxd.absolutedomain.custom.SwipeDeleteLayout;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by wxd1 on 2016/12/28.
 *
 */

public class SwipeLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private View firstView;
    private View deleteView;
    private int mDeleteWidth;
    private int mWidth;
    private int mHeight;
    private OnSwipeListener listener;
    private Status mStatus = Status.close;
    private int mTouchSlop = 20;
    SwipeLayout swipeLayout;
    private float mDownX;
    private float mDownY;

    public enum Status {
        close, swipe, open, onOpen
    }

    public OnSwipeListener getListener() {
        return listener;
    }

    public void setListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeListener {
        void onSwipe(SwipeLayout swipeLayout);

        void onColse(SwipeLayout swipeLayout);

        void onOpen(SwipeLayout swipeLayout);

        void onStartOpen(SwipeLayout swipeLayout);
    }

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化手势识别器
        mGestureDetector = new GestureDetectorCompat(context, mOnGestureListener);
        mDragHelper = ViewDragHelper.create(this, callback);
        swipeLayout = this;
    }

    private GestureDetectorCompat mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // 当横向移动距离大于等于纵向时，返回true
            return Math.abs(distanceX) >= Math.abs(distanceY) & Math.abs(distanceX) > 20;
        }
    };

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == firstView || child == deleteView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mDeleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            //修正left值为我们需要的
            if (child == firstView) {
                if (left > 0) return 0;
                if (left < -mDeleteWidth) return -mDeleteWidth;
            } else if (child == deleteView) {
                if (left > mWidth) return mWidth;
                if (left < mWidth - mDeleteWidth) return mWidth - mDeleteWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == firstView) {
                deleteView.offsetLeftAndRight(dx);
            } else if (changedView == deleteView) {
                firstView.offsetLeftAndRight(dx);
            }
            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            if (xvel < 0) {
                //open
                open();

            } else if (xvel == 0 && firstView.getLeft() < -mDeleteWidth / 2f) {
                //open
                open();
            } else {
                close();
            }

        }


    };

    public void open() {
        open(true);
    }

    public void close() {
        close(true);
    }

    private void open(boolean isSmooth) {
        int finalLeft = -mDeleteWidth;
        mStatus = Status.onOpen;
        //监听器
        if (listener != null) {
            listener.onStartOpen(swipeLayout);
        }
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(firstView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            layoutContent(true);
        }
        mStatus = Status.open;

        //监听器
        if (listener != null) {
            listener.onOpen(swipeLayout);
        }

    }

    private void close(boolean isSmooth) {
        int finalLeft = 0;
        if (isSmooth) {
            //开始动画
            if (mDragHelper.smoothSlideViewTo(firstView, finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }

        } else {
            layoutContent(false);
        }

        mStatus = Status.close;
        //监听器
        if (listener != null) {
            listener.onColse(swipeLayout);
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        // 决定当前的SwipeLayout是否要把touch事件拦截下来，直接交由自己的onTouchEvent处理
        // 返回true则为拦截
        return mDragHelper.shouldInterceptTouchEvent(ev) & mGestureDetector.onTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 当处理touch事件时，不希望被父类onInterceptTouchEvent的代码所影响。
        // 比如处理向右滑动关闭已打开的条目时，如果进行以下逻辑，则不会在关闭的同时引发左边菜单的打开。

        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getRawX() - mDownX;
                float deltaY = event.getRawY() - mDownY;
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX < 0) requestDisallowInterceptTouchEvent(true);
                    if (mStatus != Status.close) requestDisallowInterceptTouchEvent(true);
                }
                if (deltaX > mDragHelper.getTouchSlop() & mStatus != Status.close) {
                    // 请求父级View不拦截touch事件
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                mDownX = 0;
            default:
                break;
        }

        try {
            mDragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
        }

        return true;
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutContent(false);
    }


    /**
     * layout删除view
     *
     * @param isOpen 删除view显示与否
     */
    protected void layoutContent(boolean isOpen) {

        //摆放firstview
        Rect firstViewRect = compliteFirstViewRect(isOpen);
        firstView.layout(firstViewRect.left, firstViewRect.top, firstViewRect.right, firstViewRect.bottom);
        //摆放deleteView
        Rect deleteViewRect = compliteDeleteViewRect(firstViewRect);
        deleteView.layout(deleteViewRect.left, deleteViewRect.top, deleteViewRect.right, deleteViewRect.bottom);
        //调整顺序
        bringChildToFront(firstView);
    }


    /**
     * 删除view 的布局rect
     *
     * @param
     * @return
     */
    Rect compliteDeleteViewRect(Rect firstRect) {

        return new Rect(firstRect.right - 5, 0, firstRect.right + mDeleteWidth, getMeasuredHeight());
    }

    /**
     * 前view 的布局rect
     *
     * @param
     * @return
     */
    Rect compliteFirstViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -mDeleteWidth;
        }
        return new Rect(left, 0, left + getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        firstView = getChildAt(0);
        deleteView = getChildAt(1);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDeleteWidth = deleteView.getMeasuredWidth();
    }

}
