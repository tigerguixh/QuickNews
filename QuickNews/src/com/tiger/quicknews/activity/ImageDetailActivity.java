
package com.tiger.quicknews.activity;

import android.widget.TextView;

import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.ImageAdapter;
import com.tiger.quicknews.bean.NewDetailModle;
import com.tiger.quicknews.bean.NewModle;
import com.tiger.quicknews.wedget.flipview.FlipView;
import com.tiger.quicknews.wedget.flipview.FlipView.OnFlipListener;
import com.tiger.quicknews.wedget.flipview.FlipView.OnOverFlipListener;
import com.tiger.quicknews.wedget.flipview.OverFlipMode;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_image)
public class ImageDetailActivity extends BaseActivity implements OnFlipListener,
        OnOverFlipListener {
    @ViewById(R.id.flip_view)
    protected FlipView mFlipView;
    @ViewById(R.id.new_title)
    protected TextView newTitle;
    private NewModle newModle;
    @Bean
    protected ImageAdapter imageAdapter;
    private List<String> imgList;
    private NewDetailModle newDetailModle;
    private String titleString;

    @AfterInject
    public void init() {
        try {
            if (getIntent().getExtras().getSerializable("newDetailModle") != null) {
                newDetailModle = (NewDetailModle) getIntent().getExtras().getSerializable(
                        "newDetailModle");
                imgList = newDetailModle.getImgList();
                titleString = newDetailModle.getTitle();
            } else {
                newModle = (NewModle) getIntent().getExtras().getSerializable("newModle");
                imgList = newModle.getImagesModle().getImgList();
                titleString = newModle.getTitle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterViews
    public void initView() {
        try {
            newTitle.setText(titleString);
            imageAdapter.appendList(imgList);
            mFlipView.setOnFlipListener(this);
            mFlipView.setAdapter(imageAdapter);
            // mFlipView.peakNext(false);
            mFlipView.setOverFlipMode(OverFlipMode.RUBBER_BAND);
            mFlipView.setOnOverFlipListener(this);
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
