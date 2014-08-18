
package com.tiger.quicknews.wedget.gesture;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.lang.ref.WeakReference;

/**
 * 自定义的 ViewGroup，具有监听手势滑动功能； 用于封装 DecorView 中的LinearLayout；可�?Activity
 * 具有滑动关闭效果�?
 * 
 * @author caizenghui
 */
public class GestureViewGroup extends ViewGroup {

    private final Context context;

    private Scroller scroller;

    /**
     * 标记�?ViewGroup 是否处于 fling 状�?�?
     */
    private boolean isFling = false;

    private final Handler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<GestureViewGroup> myGestureViewGroup;

        public MyHandler(GestureViewGroup gestureViewGroup) {
            myGestureViewGroup = new WeakReference<GestureViewGroup>(
                    gestureViewGroup);
        }

        @Override
        public void handleMessage(Message msg) {
            if (myGestureViewGroup.get() != null)
                myGestureViewGroup.get().getGestureViewGroupGoneListener()
                        .onFinish();

        }
    }

    public GestureViewGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initview();
    }

    public GestureViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initview();
    }

    public GestureViewGroup(Context context) {
        super(context);
        this.context = context;
        initview();
    }

    private GestureDetector detector;

    private void initview() {
        scroller = new Scroller(context);

        detector = new GestureDetector(context, new
                GestureDetectorListener());
    }

    class GestureDetectorListener implements OnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            int scrollX = -getScrollX();

            float finger_originX = e2.getX() - e1.getX();

            if (finger_originX < 0 && scrollX < Math.abs(finger_originX) + 120) {
                scrollTo(0, 0);
                return false;
            }

            int scroll_distanceX = (int) (scrollX - distanceX < 0 ? scrollX
                    : -distanceX);
            scrollBy(-scroll_distanceX, 0);

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {

            if (velocityX > 100 && e2.getX() - e1.getX() > 80
                    && Math.abs(velocityX) > Math.abs(velocityY)
                    && Math.abs(e2.getX() - e1.getX()) > Math.abs(e2.getY() - e1.getY())) {// 向右滑动;
                isFling = true;
                int distance = -getScrollX();
                scroller.startScroll(getScrollX(), 0, -(getWidth() - distance),
                        0, 400);
                invalidate();
                Message mes = Message.obtain();
                mHandler.sendMessageDelayed(mes, 400);
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MeasureSpec.getSize(widthMeasureSpec);
        MeasureSpec.getMode(widthMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view != null) {
                view.layout(0, 0, getWidth(), getHeight());
            }
        }
    }

    private float lastX;
    private float lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) ev.getX();
                lastY = (int) ev.getY();
                detector.onTouchEvent(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                int distanceX = (int) Math.abs(ev.getX() - lastX);
                int distanceY = (int) Math.abs(ev.getY() - lastY);

                if (distanceX > distanceY && distanceX > 10) {
                    result = true;
                }

                break;

            default:
                break;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                if (!isFling) {
                    moveToDest();
                }
                isFling = false;
                break;
        }

        return true;
    }

    /**
     * 处理 up 事件发生时，界面进行还原或�?消失�?
     */
    public void moveToDest() {
        int distance = -getScrollX(); // distance<0;
        if (distance < getWidth() * 6 / 10) {
            scroller.startScroll(getScrollX(), 0, distance, 0,
                    Math.abs(distance));
            invalidate();
        } else {
            scroller.startScroll(getScrollX(), 0, -(getWidth() - distance), 0,
                    400);
            invalidate();
            Message mes = Message.obtain();
            mHandler.sendMessageDelayed(mes, 400);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int x = scroller.getCurrX();
            scrollTo(x, 0);
            postInvalidate();
        }
    }

    private GestureViewGroupGoneListener GestureViewGroupGoneListener;

    public GestureViewGroupGoneListener getGestureViewGroupGoneListener() {
        return GestureViewGroupGoneListener;
    }

    public void setGestureViewGroupGoneListener(
            GestureViewGroupGoneListener gestureViewGroupGoneListener) {
        GestureViewGroupGoneListener = gestureViewGroupGoneListener;
    }

    /**
     * 用于定义界面滑动消失后要进行的操作的接口�?
     */
    public interface GestureViewGroupGoneListener {
        /**
         * 用于定义界面滑动消失后要进行的操作；
         */
        void onFinish();
    }

}
