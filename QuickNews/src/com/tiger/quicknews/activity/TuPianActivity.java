
package com.tiger.quicknews.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioButton;

import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.MyOnClickListener;
import com.tiger.quicknews.adapter.NewsFragmentPagerAdapter;
import com.tiger.quicknews.fragment.*;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.activity_tupian)
public class TuPianActivity extends BaseActivity {
    @ViewById(R.id.vPager)
    protected ViewPager mViewPager;
    @ViewById(R.id.redian)
    protected RadioButton mReDian;
    @ViewById(R.id.dujia)
    protected RadioButton mDuJia;
    @ViewById(R.id.mingxing)
    protected RadioButton mMingXing;
    @ViewById(R.id.titan)
    protected RadioButton mTiTan;
    @ViewById(R.id.meitu)
    protected RadioButton mMeiTu;

    private ArrayList<Fragment> fragments;

    @AfterInject
    public void init() {
        fragments = new ArrayList<Fragment>();
        fragments.add(new TuPianReDianFragment_());
        fragments.add(new TuPianDuJiaFragment_());
        fragments.add(new TuPianMingXingFragment_());
        fragments.add(new TuPianTiTanFragment_());
        fragments.add(new TuPianMeiTuFragment_());
    }

    @AfterViews
    public void initView() {
        try {
            initPager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPager() {
        mViewPager.setOffscreenPageLimit(2);
        // NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(
        // getSupportFragmentManager(), fragments);
        // mViewPager.setAdapter(mAdapetr);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mReDian.setOnClickListener(new MyOnClickListener(0, mViewPager));
        mDuJia.setOnClickListener(new MyOnClickListener(1, mViewPager));
        mMingXing.setOnClickListener(new MyOnClickListener(2, mViewPager));
        mTiTan.setOnClickListener(new MyOnClickListener(3, mViewPager));
        mMeiTu.setOnClickListener(new MyOnClickListener(4, mViewPager));
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            switch (position) {
                case 0:
                    setRadioButtonCheck(true, false, false, false, false);
                    break;
                case 1:
                    setRadioButtonCheck(false, true, false, false, false);
                    break;
                case 2:
                    setRadioButtonCheck(false, false, true, false, false);
                    break;
                case 3:
                    setRadioButtonCheck(false, false, false, true, false);
                    break;
                case 4:
                    setRadioButtonCheck(false, false, false, false, true);
                    break;
            }
        }

    }

    private void setRadioButtonCheck(boolean b, boolean c, boolean d, boolean e, boolean f) {
        mReDian.setChecked(b);
        mDuJia.setChecked(c);
        mMingXing.setChecked(d);
        mTiTan.setChecked(e);
        mMeiTu.setChecked(f);
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
