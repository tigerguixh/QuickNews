
package com.tiger.quicknews.activity;

import android.view.View;
import android.widget.Button;

import com.tiger.quicknews.R;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.discrollview.DiscrollView;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

@Fullscreen
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity {
    @ViewById(R.id.layout)
    protected DiscrollView mDiscrollView;

    @ViewById(R.id.lastView3)
    protected Button mButton;
    private String cache;

    @AfterViews
    public void initView() {
        String result = getCacheStr("welcome");
        cache = getCacheStr("MoreActivity");
        if (StringUtils.isEmpty(cache)) {
            if (!StringUtils.isEmpty(result)) {
                mDiscrollView.setVisibility(View.GONE);
                MainActivity_.intent(this).start();
                defaultFinishNotActivityHelper();
            }
        } else {
            mDiscrollView.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.lastView3)
    public void startApp(View view) {
        if (StringUtils.isEmpty(cache)) {
            setCacheStr("welcome", "welcome");
            MainActivity_.intent(this).start();
        }
        defaultFinishNotActivityHelper();
        setCacheStr("MoreActivity", "");
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
