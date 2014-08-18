
package com.tiger.quicknews.initview;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import com.tiger.quicknews.R;
import com.tiger.quicknews.wedget.slidingmenu.SlidingMenu;
import com.tiger.quicknews.wedget.slidingmenu.SlidingMenu.CanvasTransformer;

public class SlidingMenuView {
    private static SlidingMenuView slidingMenuView;

    public SlidingMenu slidingMenu;

    private CanvasTransformer mTransformer;

    public static SlidingMenuView instance() {
        if (slidingMenuView == null) {
            slidingMenuView = new SlidingMenuView();
        }
        return slidingMenuView;
    }

    public SlidingMenu initSlidingMenuView(Activity activity, View view) {
        // 菜单打开时的动画
        mTransformer = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
            }
        };
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
        // 设置菜单打开动画
        // slidingMenu.setBehindCanvasTransformer(mTransformer);
        return slidingMenu;
    }

    public void setWeatherImage(ImageView mWeatherImage, String weather) {
        if (weather.equals("多云") || weather.equals("多云转阴") || weather.equals("多云转晴")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        } else if (weather.equals("中雨") || weather.equals("中到大雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
        } else if (weather.equals("雷阵雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        } else if (weather.equals("阵雨") || weather.equals("阵雨转多云")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
        } else if (weather.equals("暴雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        } else if (weather.equals("暴雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        } else if (weather.equals("大暴雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        } else if (weather.equals("大雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_daxue);
        } else if (weather.equals("大雨") || weather.equals("大雨转中雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_dayu);
        } else if (weather.equals("雷阵雨冰雹")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        } else if (weather.equals("晴")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_qing);
        } else if (weather.equals("沙尘暴")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        } else if (weather.equals("特大暴雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        } else if (weather.equals("雾") || weather.equals("雾霾")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_wu);
        } else if (weather.equals("小雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
        } else if (weather.equals("小雨")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
        } else if (weather.equals("阴")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_yin);
        } else if (weather.equals("雨夹雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
        } else if (weather.equals("阵雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
        } else if (weather.equals("中雪")) {
            mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
        }
    }

}
