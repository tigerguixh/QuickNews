
package com.tiger.quicknews.view;

import android.support.v4.view.ViewPager.PageTransformer;

public class TransformerItem {

    public String title;
    public Class<? extends PageTransformer> clazz;

    public TransformerItem(Class<? extends PageTransformer> clazz) {
        this.clazz = clazz;
        title = clazz.getSimpleName();
    }

    @Override
    public String toString() {
        return title;
    }

}
