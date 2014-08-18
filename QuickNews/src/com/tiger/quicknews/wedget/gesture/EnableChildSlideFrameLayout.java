
package com.tiger.quicknews.wedget.gesture;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.tiger.quicknews.R;

public class EnableChildSlideFrameLayout extends FrameLayout {

    private ViewPager vp;

    public EnableChildSlideFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private float lastX;
    private float lastY;
    private boolean isScrolling = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (vp == null) {
            vp = (ViewPager) findViewById(R.id.vPager);
        }
        if (vp == null) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = ev.getX();
                lastY = ev.getY();
                isScrolling = false;
                requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float distanceX = ev.getX() - lastX;
                float distanceY = ev.getY() - lastY;
                if (vp.getCurrentItem() == 0) {
                    if (isScrolling) {
                        break;
                    }
                    if (distanceX > 10 && distanceX > Math.abs(distanceY)) {
                        requestDisallowInterceptTouchEvent(false);
                        return false;
                    } else if (Math.abs(distanceY) > Math.abs(distanceX)
                            && Math.abs(distanceY) > 10) {
                        isScrolling = true;
                        requestDisallowInterceptTouchEvent(true);
                    } else if (distanceX < -10 && Math.abs(distanceX) > Math.abs(distanceY)) {
                        requestDisallowInterceptTouchEvent(true);
                    } else {
                        requestDisallowInterceptTouchEvent(true);
                    }
                }
                else {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                isScrolling = false;
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}
