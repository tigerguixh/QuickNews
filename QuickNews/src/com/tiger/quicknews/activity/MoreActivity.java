
package com.tiger.quicknews.activity;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiger.quicknews.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_more)
public class MoreActivity extends BaseActivity {

    @ViewById(R.id.title)
    protected TextView mTitle;

    /**
     * 检查更新进度框
     */
    @ViewById(R.id.update_progress)
    public ProgressBar mProgressBar;
    /**
     * 最新版本提示
     */
    @ViewById(R.id.newest)
    public TextView mTextViewNewest;

    @AfterViews
    public void initView() {
        mTitle.setText("更多");
    }

    @Click
    void checkUpdateClicked() {
        mTextViewNewest.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        checkUpdate();
    }

    /**
     * 手动检查更新
     */
    private void checkUpdate() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // 发现最新版
                        UmengUpdateAgent.showUpdateDialog(MoreActivity.this, updateInfo);
                        break;
                    case UpdateStatus.No: // 已为最新版
                        mTextViewNewest.setVisibility(View.VISIBLE);
                        break;
                    case UpdateStatus.NoneWifi: // 仅在wifi下更新
                        showShortToast("没有wifi连接");
                        break;
                    case UpdateStatus.Timeout: // 连接超时
                        showShortToast("连接超时");
                        break;
                }
                mProgressBar.setVisibility(View.GONE);
            }
        });
        UmengUpdateAgent.forceUpdate(this);
    }

    @Click(R.id.restart)
    public void restart(View view) {
        setCacheStr("MoreActivity", "MoreActivity");
        WelcomeActivity_.intent(this).start();
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
