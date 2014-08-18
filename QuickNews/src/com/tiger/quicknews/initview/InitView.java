
package com.tiger.quicknews.initview;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.tiger.quicknews.R;
import com.tiger.quicknews.utils.ScreenUtils;
import com.tiger.quicknews.utils.SettingsManager;
import com.tiger.quicknews.wedget.slidingmenu.SlidingMenu;
import com.tiger.quicknews.wedget.swiptlistview.SwipeListView;

public class InitView {
    private static InitView initView;

    private SlidingMenu slidingMenu;

    public static InitView instance() {
        if (initView == null) {
            initView = new InitView();
        }
        return initView;
    }

    /**
     * 初始化侧滑控件
     * 
     * @param activity
     * @param view
     * @return
     */
    public SlidingMenu initSlidingMenuView(Activity activity, View view) {
        slidingMenu = new SlidingMenu(activity);
        slidingMenu.setMode(SlidingMenu.LEFT);// 设置左右滑菜单
        slidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);// 设置要使菜单滑动，触碰屏幕的范围
        // slidingMenuView.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);//设置了这个会获取不到菜单里面的焦点，所以先注释掉
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);// 设置阴影图片的宽度
        slidingMenu.setShadowDrawable(R.drawable.shadow);// 设置阴影图片
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// SlidingMenu划出时主页面显示的剩余宽度
        slidingMenu.setFadeDegree(0.35F);// SlidingMenu滑动时的渐变程度
        slidingMenu.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);// 使SlidingMenu附加在Activity右边
        // slidingMenuView.setBehindWidthRes(R.dimen.left_drawer_avatar_size);//设置SlidingMenu菜单的宽度
        slidingMenu.setMenu(view);// 设置menu的布局文件
        // localSlidingMenu.toggle();//动态判断自动关闭或开启SlidingMenu
        // slidingMenu.setSecondaryMenu(R.layout.activity_main);
        // slidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
        return slidingMenu;
    }

    /**
     * 设置下拉刷新控件颜色
     * 
     * @param swipeLayout
     */
    public void initSwipeRefreshLayout(SwipeRefreshLayout swipeLayout) {
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * 初始化listview
     * 
     * @param mListView
     * @param context
     */
    public void initListView(SwipeListView mListView, Context context) {
        SettingsManager settings = SettingsManager.getInstance();
        mListView.setSwipeMode(SwipeListView.SWIPE_MODE_NONE);
        mListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        mListView.setSwipeActionRight(settings.getSwipeActionRight());
        mListView.setOffsetLeft(ScreenUtils.convertDpToPixel(context,
                settings.getSwipeOffsetLeft()));
        mListView.setOffsetRight(ScreenUtils.convertDpToPixel(context,
                settings.getSwipeOffsetRight()));
        mListView.setAnimationTime(settings.getSwipeAnimationTime());
        mListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    }

}
