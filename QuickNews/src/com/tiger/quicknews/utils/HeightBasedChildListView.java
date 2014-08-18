
package com.tiger.quicknews.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 此ListView是固定高度的
 * 
 * @author wanpg
 */
public class HeightBasedChildListView extends ListView {

    public HeightBasedChildListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HeightBasedChildListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightBasedChildListView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
