
package com.tiger.quicknews.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ImageContainer extends GridView {

    public ImageContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ImageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageContainer(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
