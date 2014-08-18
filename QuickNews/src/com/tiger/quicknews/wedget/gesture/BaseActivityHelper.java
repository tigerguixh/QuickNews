
package com.tiger.quicknews.wedget.gesture;

import android.app.Activity;

public class BaseActivityHelper {
    private final Activity activity;

    private boolean isSupportSlide = true;
    private SlideActivityHelper slideActivityHelper;

    public BaseActivityHelper(Activity activity, boolean isSupportSlide) {
        this.activity = activity;
        this.isSupportSlide = isSupportSlide;
        if (isSupportSlide) {
            slideActivityHelper = new SlideActivityHelper(activity);
        }
    }

    public void onCreate() {
        ActivityStack.add(activity);
        if (isSupportSlide) {
            slideActivityHelper.onCreate();
        }
    }

    public void onResume() {
        if (isSupportSlide) {
            slideActivityHelper.onResume();
        }
    }

    public void onPause() {
    }

    public void onDestroy() {
        ActivityStack.remove(activity);
    }

    public void finish() {
        if (isSupportSlide) {
            slideActivityHelper.finish();
        }
    }
}
