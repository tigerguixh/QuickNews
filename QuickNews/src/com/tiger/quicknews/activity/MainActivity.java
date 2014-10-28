
package com.tiger.quicknews.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiger.quicknews.App;
import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.NewsFragmentPagerAdapter;
import com.tiger.quicknews.bean.ChannelItem;
import com.tiger.quicknews.bean.ChannelManage;
import com.tiger.quicknews.fragment.*;
import com.tiger.quicknews.initview.SlidingMenuView;
import com.tiger.quicknews.utils.BaseTools;
import com.tiger.quicknews.view.*;
import com.tiger.quicknews.wedget.ColumnHorizontalScrollView;
import com.tiger.quicknews.wedget.slidingmenu.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EActivity(R.layout.main)
public class MainActivity extends BaseActivity {
    /** 自定义HorizontalScrollView */
    @ViewById(R.id.mColumnHorizontalScrollView)
    protected ColumnHorizontalScrollView mColumnHorizontalScrollView;
    @ViewById(R.id.mRadioGroup_content)
    protected LinearLayout mRadioGroup_content;
    @ViewById(R.id.ll_more_columns)
    protected LinearLayout ll_more_columns;
    @ViewById(R.id.rl_column)
    protected RelativeLayout rl_column;
    @ViewById(R.id.button_more_columns)
    protected ImageView button_more_columns;
    @ViewById(R.id.mViewPager)
    protected ViewPager mViewPager;
    @ViewById(R.id.shade_left)
    protected ImageView shade_left;
    @ViewById(R.id.shade_right)
    protected ImageView shade_right;
    protected LeftView mLeftView;

    protected SlidingMenu side_drawer;
    /** 屏幕宽度 */
    private int mScreenWidth = 0;
    /** Item宽度 */
    private int mItemWidth = 0;
    /** head 头部 的左侧菜单 按钮 */
    @ViewById(R.id.top_head)
    protected ImageView top_head;
    /** head 头部 的右侧菜单 按钮 */
    @ViewById(R.id.top_more)
    protected ImageView top_more;
    /** 用户选择的新闻分类列表 */
    protected static ArrayList<ChannelItem> userChannelLists;
    /** 请求CODE */
    public final static int CHANNELREQUEST = 1;
    /** 调整返回的RESULTCODE */
    public final static int CHANNELRESULT = 10;
    /** 当前选中的栏目 */
    private int columnSelectIndex = 0;
    private ArrayList<Fragment> fragments;

    private Fragment newfragment;
    private double back_pressed;

    public static boolean isChange = false;

    private NewsFragmentPagerAdapter mAdapetr;

    /** 当前选中的栏目 */

    @Override
    public boolean isSupportSlide() {
        return false;
    }

    @SuppressLint("InlinedApi")
    @AfterInject
    void init() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        UmengUpdateAgent.update(this);
        MobclickAgent.updateOnlineConfig(this);
        mScreenWidth = BaseTools.getWindowsWidth(this);
        mItemWidth = mScreenWidth / 7;// 一个Item宽度为屏幕的1/7
        userChannelLists = new ArrayList<ChannelItem>();
        fragments = new ArrayList<Fragment>();
    }

    @AfterViews
    void initView() {
        try {
            initSlidingMenu();
            initViewPager();
            setChangelView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewPager() {
        mAdapetr = new NewsFragmentPagerAdapter(
                getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mAdapetr);
        mViewPager.setOnPageChangeListener(pageListener);
    }

    protected void initSlidingMenu() {
        mLeftView = LeftView_.build(this);
        side_drawer = SlidingMenuView.instance().initSlidingMenuView(this, mLeftView);
    }

    @Click(R.id.top_head)
    protected void onMenu(View view) {
        if (side_drawer.isMenuShowing()) {
            side_drawer.showContent();
        } else {
            side_drawer.showMenu();
        }
    }

    // @Click(R.id.top_more)
    // protected void onAcount(View view) {
    // if (side_drawer.isSecondaryMenuShowing()) {
    // side_drawer.showContent();
    // } else {
    // side_drawer.showSecondaryMenu();
    // }
    // }

    @Click(R.id.button_more_columns)
    protected void onMoreColumns(View view) {
        openActivityForResult(ChannelActivity_.class, CHANNELREQUEST);
    }

    /**
     * 当栏目项发生变化时候调用
     */
    public void setChangelView() {
        initColumnData();

    }

    /** 获取Column栏目 数据 */
    private void initColumnData() {
        userChannelLists = ((ArrayList<ChannelItem>) ChannelManage.getManage(
                App.getApp().getSQLHelper()).getUserChannel());
        initTabColumn();
        initFragment();
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        mRadioGroup_content.removeAllViews();
        int count = userChannelLists.size();
        mColumnHorizontalScrollView.setParam(this, mScreenWidth, mRadioGroup_content, shade_left,
                shade_right, ll_more_columns, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mItemWidth,
                    LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            // TextView localTextView = (TextView)
            // mInflater.inflate(R.layout.column_radio_item, null);
            TextView columnTextView = new TextView(this);
            columnTextView.setTextAppearance(this, R.style.top_category_scroll_view_item_text);
            // localTextView.setBackground(getResources().getDrawable(R.drawable.top_category_scroll_text_view_bg));
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(5, 5, 5, 5);
            columnTextView.setId(i);
            columnTextView.setText(userChannelLists.get(i).getName());
            columnTextView.setTextColor(getResources().getColorStateList(
                    R.color.top_category_scroll_text_color_day));
            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v)
                            localView.setSelected(false);
                        else {
                            localView.setSelected(true);
                            mViewPager.setCurrentItem(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        fragments.clear();
        int count = userChannelLists.size();
        for (int i = 0; i < count; i++) {
            // Bundle data = new Bundle();
            String nameString = userChannelLists.get(i).getName();
            // data.putString("text", nameString);
            // data.putInt("id", userChannelList.get(i).getId());
            // initFragment(nameString);
            // map.put(nameString, nameString);
            // newfragment.setArguments(data);
            fragments.add(initFragment(nameString));
            // fragments.add(nameString);
        }
        mAdapetr.appendList(fragments);
    }

    public Fragment initFragment(String channelName) {
        if (channelName.equals("头条")) {
            newfragment = new NewsFragment_();
        } else if (channelName.equals("足球")) {
            newfragment = new FoodBallFragment_();
        } else if (channelName.equals("娱乐")) {
            newfragment = new YuLeFragment_();
        } else if (channelName.equals("体育")) {
            newfragment = new TiYuFragment_();
        } else if (channelName.equals("财经")) {
            newfragment = new CaiJingFragment_();
        } else if (channelName.equals("科技")) {
            newfragment = new KeJiFragment_();
        } else if (channelName.equals("电影")) {
            newfragment = new DianYingFragment_();
        } else if (channelName.equals("汽车")) {
            newfragment = new QiCheFragment_();
        } else if (channelName.equals("笑话")) {
            newfragment = new XiaoHuaFragment_();
        } else if (channelName.equals("时尚")) {
            newfragment = new ShiShangFragment_();
        } else if (channelName.equals("北京")) {
            newfragment = new BeiJingFragment_();
        } else if (channelName.equals("军事")) {
            newfragment = new JunShiFragment_();
        } else if (channelName.equals("房产")) {
            newfragment = new FangChanFragment_();
        } else if (channelName.equals("游戏")) {
            newfragment = new YouXiFragment_();
        } else if (channelName.equals("情感")) {
            newfragment = new QinGanFragment_();
        } else if (channelName.equals("精选")) {
            newfragment = new JingXuanFragment_();
        } else if (channelName.equals("电台")) {
            newfragment = new DianTaiFragment_();
        } else if (channelName.equals("图片")) {
            newfragment = new TuPianFragment_();
        } else if (channelName.equals("NBA")) {
            newfragment = new NBAFragment_();
        } else if (channelName.equals("数码")) {
            newfragment = new ShuMaFragment_();
        } else if (channelName.equals("移动")) {
            newfragment = new YiDongFragment_();
        } else if (channelName.equals("彩票")) {
            newfragment = new CaiPiaoFragment_();
        } else if (channelName.equals("教育")) {
            newfragment = new JiaoYuFragment_();
        } else if (channelName.equals("论坛")) {
            newfragment = new LunTanFragment_();
        } else if (channelName.equals("旅游")) {
            newfragment = new LvYouFragment_();
        } else if (channelName.equals("手机")) {
            newfragment = new ShouJiFragment_();
        } else if (channelName.equals("博客")) {
            newfragment = new BoKeFragment_();
        } else if (channelName.equals("社会")) {
            newfragment = new SheHuiFragment_();
        } else if (channelName.equals("家居")) {
            newfragment = new JiaJuFragment_();
        } else if (channelName.equals("暴雪")) {
            newfragment = new BaoXueYouXiFragment_();
        } else if (channelName.equals("亲子")) {
            newfragment = new QinZiFragment_();
        } else if (channelName.equals("CBA")) {
            newfragment = new CBAFragment_();
        }
        return newfragment;
    }

    /**
     * ViewPager切换监听方法
     */
    public OnPageChangeListener pageListener = new OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            mViewPager.setCurrentItem(position);
            selectTab(position);
        }
    };

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {
        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            // rg_nav_content.getParent()).smoothScrollTo(i2, 0);
            mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
            // mColumnHorizontalScrollView.smoothScrollTo((position - 2) *
            // mItemWidth , 0);
        }
        // 判断是否选中
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                ischeck = true;
            } else {
                ischeck = false;
            }
            checkView.setSelected(ischeck);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (isChange) {
                setChangelView();
                // initColumnData();
                // initTabColumn();
                isChange = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击两次返回退出系统
     * 
     * @param view
     */
    @Override
    public void onBackPressed() {
        if (side_drawer.isMenuShowing()) {
            side_drawer.showContent();
        } else {
            System.out.println(isShow());
            if (isShow()) {
                dismissProgressDialog();
            } else {
                if (back_pressed + 3000 > System.currentTimeMillis()) {
                    finish();
                    super.onBackPressed();
                }
                else
                    showCustomToast(getString(R.string.press_again_exit));
                // Toast.makeText(this, getString(R.string.press_again_exit),
                // 1).show();

                back_pressed = System.currentTimeMillis();
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
