package com.example.administrator.swipelayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Administrator on 2017/2/9.
 */

public class SwipeLayout extends FrameLayout {

    private View content;
    private View delete;
    private int contentWidth;
    private int deleteWidth;
    private int height;
    private ViewDragHelper viewDragHelper;

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        content.layout(0, 0, contentWidth, height);
        delete.layout(contentWidth, 0, contentWidth + deleteWidth, height);
    }

    public void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildAt(0);
        delete = getChildAt(1);
    }

    //此方法在onlayout后执行，可以用来获取宽高
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = content.getMeasuredWidth();
        deleteWidth = delete.getMeasuredWidth();
        height = delete.getMeasuredHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = viewDragHelper.shouldInterceptTouchEvent(ev);
        return shouldIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    public ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == content || child == delete;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return contentWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == content) {
                if (left > 0) left = 0;
                if (left < -deleteWidth) left = -deleteWidth;
            } else if (child == delete) {
                if (left > contentWidth) left = contentWidth;
                if (left < contentWidth - deleteWidth) left = contentWidth - deleteWidth;
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == content) {
                delete.layout(delete.getLeft() + dx, delete.getTop() + dy, delete.getRight() + dx, delete.getBottom() + dy);
            } else if (changedView == delete) {
                content.layout(content.getLeft() + dx, content.getTop() + dy, content.getRight() + dx, content.getBottom() + dy);
            }

            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (content.getLeft()<-deleteWidth/2){
                viewDragHelper.smoothSlideViewTo(delete,contentWidth-deleteWidth,0);
                ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
            }else {
                viewDragHelper.smoothSlideViewTo(delete,contentWidth,0);
                ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
            }
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }
}
