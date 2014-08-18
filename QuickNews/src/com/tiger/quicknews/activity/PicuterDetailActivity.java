
package com.tiger.quicknews.activity;

import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.PicuterDetailAdapter;
import com.tiger.quicknews.bean.PicuterDetailModle;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.Url;
import com.tiger.quicknews.http.json.PicuterSinaJson;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.flipview.FlipView;
import com.tiger.quicknews.wedget.flipview.FlipView.OnFlipListener;
import com.tiger.quicknews.wedget.flipview.FlipView.OnOverFlipListener;
import com.tiger.quicknews.wedget.flipview.OverFlipMode;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_photo)
public class PicuterDetailActivity extends BaseActivity implements OnFlipListener,
        OnOverFlipListener {
    @ViewById(R.id.flip_view)
    protected FlipView mFlipView;

    @Bean
    protected PicuterDetailAdapter picuterDetailAdapter;

    private String imgUrl;

    @AfterInject
    public void init() {
        try {
            if (getIntent().getExtras().getString("pic_id") != null) {
                imgUrl = getIntent().getExtras().getString("pic_id");
                showProgressDialog();
                loadData(Url.JINGXUANDETAIL_ID + imgUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    public void initView() {
        // imageAdapter.appendList(imgList);
        try {
            mFlipView.setOnFlipListener(this);
            mFlipView.setAdapter(picuterDetailAdapter);
            mFlipView.peakNext(false);
            mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
            mFlipView.setOnOverFlipListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadData(String url) {
        if (hasNetWork()) {
            loadPhotoList(url);
        } else {
            dismissProgressDialog();
            showShortToast(getString(R.string.not_network));
            String result = getCacheStr(imgUrl);
            if (!StringUtils.isEmpty(result)) {
                getResult(result);
            }
        }
    }

    @Background
    void loadPhotoList(String url) {
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
        setCacheStr(imgUrl, result);
        dismissProgressDialog();
        try {
            List<PicuterDetailModle> list = PicuterSinaJson.instance(this).readJsonPicuterModle(
                    result);
            picuterDetailAdapter.appendList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOverFlip(FlipView v, OverFlipMode mode, boolean overFlippingPrevious,
            float overFlipDistance, float flipDistancePerPage) {

    }

    @Override
    public void onFlippedToPage(FlipView v, int position, long id) {

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
