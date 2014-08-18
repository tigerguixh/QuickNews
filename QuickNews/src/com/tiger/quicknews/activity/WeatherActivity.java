
package com.tiger.quicknews.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.MyViewPagerAdapter;
import com.tiger.quicknews.adapter.WeatherAdapter;
import com.tiger.quicknews.bean.WeatherModle;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.json.WeatherListJson;
import com.tiger.quicknews.initview.SlidingMenuView;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_weather)
public class WeatherActivity extends BaseActivity {

    @ViewById(R.id.title)
    protected TextView mTitle;
    @ViewById(R.id.local)
    protected TextView mLocal;
    @ViewById(R.id.layout)
    protected RelativeLayout mLayout;
    @ViewById(R.id.weatherTemp)
    protected TextView mWeatherTemp;
    @ViewById(R.id.weather)
    protected TextView mWeather;
    @ViewById(R.id.wind)
    protected TextView mWind;
    @ViewById(R.id.weatherImage)
    protected ImageView mWeatherImage;
    @ViewById(R.id.week)
    protected TextView mWeek;
    @ViewById(R.id.weather_date)
    protected TextView mWeatherDate;
    @ViewById(R.id.vPager)
    protected ViewPager mViewPager;

    private List<View> views;

    private View weatherGridView1, weatherGridView2;

    private GridView view1, view2;

    @Bean
    protected WeatherAdapter mWeatherAdapter1, mWeatherAdapter2;

    @AfterInject
    public void init() {
        views = new ArrayList<View>();
    }

    @AfterViews
    public void initView() {
        try {
            initViewPager();
            String titleName = getCacheStr("titleName");
            if (StringUtils.isEmpty(titleName)) {
                titleName = "北京";
            }
            mTitle.setText(titleName + "天气");
            mLocal.setVisibility(View.VISIBLE);
            setBack(titleName);
            loadData(getWeatherUrl(titleName));
            mWeatherDate.setText(TimeUtils.getCurrentTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewPager() {
        weatherGridView1 = LayoutInflater.from(this).inflate(R.layout.gridview_weather, null);
        weatherGridView2 = LayoutInflater.from(this).inflate(R.layout.gridview_weather, null);
        view1 = (GridView) weatherGridView1.findViewById(R.id.gridView);
        view2 = (GridView) weatherGridView2.findViewById(R.id.gridView);
        view1.setAdapter(mWeatherAdapter1);
        view2.setAdapter(mWeatherAdapter2);
        views.add(weatherGridView1);
        views.add(weatherGridView2);
        mViewPager.setOffscreenPageLimit(1);
        MyViewPagerAdapter mAdapetr = new MyViewPagerAdapter(views);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setCurrentItem(0);
    }

    public void setBack(String cityName) {
        if (cityName.equals("北京")) {
            mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_beijin_bg);
        } else if (cityName.equals("上海")) {
            mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_shanghai_bg);
        } else if (cityName.equals("广州")) {
            mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_guangzhou_bg);
        } else if (cityName.equals("深圳")) {
            mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_shenzhen_bg);
        } else {
            mLayout.setBackgroundResource(R.drawable.biz_news_local_weather_bg_big);
        }
    }

    private void loadData(String url) {
        if (hasNetWork()) {
            loadNewDetailData(url);
        } else {
            showShortToast(getString(R.string.not_network));
            String result = getCacheStr("WeatherActivity");
            if (!StringUtils.isEmpty(result)) {
                getResult(result);
            }
        }
    }

    @Background
    public void loadNewDetailData(String url) {
        String result;
        try {
            result = HttpUtil.getByHttpClient(this, url);
            getResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void getResult(String result) {
        setCacheStr("WeatherActivity", result);
        List<WeatherModle> weatherModles = WeatherListJson.instance(this).readJsonPhotoListModles(
                result);
        if (weatherModles.size() > 0) {
            setWeather(weatherModles.get(0));
            mWeatherAdapter1.clear();
            mWeatherAdapter2.clear();
            mWeatherAdapter1.appendList(weatherModles.subList(1, 4));
            mWeatherAdapter2.appendList(weatherModles.subList(4, weatherModles.size()));
        } else {
            showShortToast("错误");
        }
    }

    public void setWeather(WeatherModle weatherModle) {
        mWeather.setText(weatherModle.getWeather());
        mWind.setText(weatherModle.getWind());
        mWeatherTemp.setText(weatherModle.getTemperature());
        mWeek.setText(weatherModle.getWeek());
        SlidingMenuView.instance().setWeatherImage(mWeatherImage, weatherModle.getWeather());
    }

    @Click(R.id.local)
    public void chooseCity(View view) {
        ChooseCityActivity_.intent(this).startForResult(REQUEST_CODE);
    }

    @OnActivityResult(REQUEST_CODE)
    void onResult(int resultCode, Intent data) {
        if (data != null) {
            String titleName = data.getStringExtra("cityname");
            setCacheStr("titleName", titleName);
            if (!"".equals(titleName)) {
                mTitle.setText(titleName + "天气");
                setBack(titleName);
                loadData(getWeatherUrl(titleName));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
