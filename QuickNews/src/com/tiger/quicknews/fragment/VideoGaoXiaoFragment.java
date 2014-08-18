
package com.tiger.quicknews.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.tiger.quicknews.R;
import com.tiger.quicknews.activity.*;
import com.tiger.quicknews.adapter.CardsAnimationAdapter;
import com.tiger.quicknews.adapter.VideoAdapter;
import com.tiger.quicknews.bean.VideoModle;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.Url;
import com.tiger.quicknews.http.json.ViedoListJson;
import com.tiger.quicknews.initview.InitView;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.swiptlistview.SwipeListView;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.activity_main)
public class VideoGaoXiaoFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {
    @ViewById(R.id.swipe_container)
    protected SwipeRefreshLayout swipeLayout;
    @ViewById(R.id.listview)
    protected SwipeListView mListView;
    @ViewById(R.id.progressBar)
    protected ProgressBar mProgressBar;

    @Bean
    protected VideoAdapter videoAdapter;
    protected List<VideoModle> listsModles;
    private int index = 0;
    private boolean isRefresh = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @AfterInject
    protected void init() {
        listsModles = new ArrayList<VideoModle>();
    }

    @AfterViews
    protected void initView() {

        swipeLayout.setOnRefreshListener(this);
        InitView.instance().initSwipeRefreshLayout(swipeLayout);
        InitView.instance().initListView(mListView, getActivity());
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(videoAdapter);
        animationAdapter.setAbsListView(mListView);
        mListView.setAdapter(animationAdapter);
        loadData(getVideoUrl(index + "", Url.VideoGaoXiaoId));

        mListView.setOnBottomListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPagte++;
                index = index + 10;
                loadData(getVideoUrl(index + "", Url.VideoGaoXiaoId));
            }
        });
    }

    private void loadData(String url) {
        if (getMyActivity().hasNetWork()) {
            loadNewList(url);
        } else {
            mListView.onBottomComplete();
            mProgressBar.setVisibility(View.GONE);
            getMyActivity().showShortToast(getString(R.string.not_network));
            String result = getMyActivity().getCacheStr("VideoGaoXiaoFragment" + currentPagte);
            if (!StringUtils.isEmpty(result)) {
                getResult(result);
            }
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentPagte = 1;
                isRefresh = true;
                loadData(getVideoUrl(0 + "", Url.VideoGaoXiaoId));
            }
        }, 2000);
    }

    @ItemClick(R.id.listview)
    protected void onItemClick(int position) {
        VideoModle videoModle = listsModles.get(position);
        enterDetailActivity(videoModle);
    }

    public void enterDetailActivity(VideoModle videoModle) {
        Bundle bundle = new Bundle();
        bundle.putString("playUrl", videoModle.getMp4Hd_url());
        ((BaseActivity) getActivity()).openActivity(VideoPlayActivity_.class, bundle, 0);
    }

    @Background
    void loadNewList(String url) {
        String result;
        try {
            result = HttpUtil.getByHttpClient(getActivity(), url, null);
            getResult(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @UiThread
    public void getResult(String result) {
        getMyActivity().setCacheStr("VideoGaoXiaoFragment" + currentPagte, result);
        if (isRefresh) {
            isRefresh = false;
            videoAdapter.clear();
            listsModles.clear();
        }
        mProgressBar.setVisibility(View.GONE);
        swipeLayout.setRefreshing(false);

        List<VideoModle> list =
                ViedoListJson.instance(getActivity()).readJsonVideoModles(result,
                        Url.VideoGaoXiaoId);
        videoAdapter.appendList(list);
        listsModles.addAll(list);
        mListView.onBottomComplete();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); // 统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }
}
