
package com.tiger.quicknews.wedget.city;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class ContactListViewImpl extends ContactListView
{

    public ContactListViewImpl(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void createScroller()
    {

        mScroller = new IndexScroller(getContext(), this);

        mScroller.setAutoHide(autoHide);

        // style 1
        // mScroller.setShowIndexContainer(false);
        // mScroller.setIndexPaintColor(Color.argb(255, 49, 64, 91));

        // style 2
        mScroller.setShowIndexContainer(true);
        mScroller.setIndexPaintColor(Color.WHITE);

        if (autoHide)
            mScroller.hide();
        else
            mScroller.show();

    }
}
