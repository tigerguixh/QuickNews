
package com.tiger.quicknews.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.tiger.quicknews.R;
import com.tiger.quicknews.bean.NewDetailModle;
import com.tiger.quicknews.bean.NewModle;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.json.NewDetailJson;
import com.tiger.quicknews.utils.Options;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.ProgressPieView;
import com.tiger.quicknews.wedget.htmltextview.HtmlTextView;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_details)
public class DetailsActivity extends BaseActivity implements ImageLoadingListener,
        ImageLoadingProgressListener {

    @ViewById(R.id.new_title)
    protected TextView newTitle;
    @ViewById(R.id.new_time)
    protected TextView newTime;
    @ViewById(R.id.wb_details)
    protected HtmlTextView webView;
    // @ViewById(R.id.progressBar)
    // protected ProgressBar progressBar;
    @ViewById(R.id.progressPieView)
    protected ProgressPieView mProgressPieView;
    @ViewById(R.id.new_img)
    protected ImageView newImg;
    @ViewById(R.id.img_count)
    protected TextView imgCount;
    @ViewById(R.id.play)
    protected ImageView mPlay;
    private String newUrl;
    private NewModle newModle;
    private String newID;
    protected ImageLoader imageLoader;
    private String imgCountString;

    protected DisplayImageOptions options;

    private NewDetailModle newDetailModle;

    @AfterInject
    public void init() {
        try {
            newModle = (NewModle) getIntent().getExtras().getSerializable("newModle");
            newID = newModle.getDocid();
            newUrl = getUrl(newID);
            imageLoader = ImageLoader.getInstance();
            options = Options.getListOptions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("JavascriptInterface")
    @AfterViews
    public void initWebView() {
        try {
            mProgressPieView.setShowText(true);
            mProgressPieView.setShowImage(false);
            // WebSettings settings = webView.getSettings();
            // settings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
            // settings.setDefaultFontSize(16);
            // settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
            // settings.setSupportZoom(false);// 用于设置webview放大
            // settings.setBuiltInZoomControls(false);
            // webView.setBackgroundResource(R.color.transparent);
            // webView.setWebViewClient(new MyWebViewClient());
            showProgressDialog();
            loadData(newUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData(String url) {
        if (hasNetWork()) {
            loadNewDetailData(url);
        } else {
            dismissProgressDialog();
            showShortToast(getString(R.string.not_network));
            String result = getCacheStr(newUrl);
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
        newDetailModle = NewDetailJson.instance(this).readJsonNewModles(result,
                newID);
        if (newDetailModle == null)
            return;
        setCacheStr(newUrl, result);
        if (!"".equals(newDetailModle.getUrl_mp4())) {
            imageLoader.displayImage(newDetailModle.getCover(), newImg, options, this,
                    this);
            newImg.setVisibility(View.VISIBLE);
        } else {
            if (newDetailModle.getImgList().size() > 0) {
                imgCountString = "共" + newDetailModle.getImgList().size() + "张";
                imageLoader.displayImage(newDetailModle.getImgList().get(0), newImg, options, this,
                        this);
                newImg.setVisibility(View.VISIBLE);
            }
        }
        newTitle.setText(newDetailModle.getTitle());
        newTime.setText("来源：" + newDetailModle.getSource() + " " + newDetailModle.getPtime());
        String content = newDetailModle.getBody();
        content = content.replace("<!--VIDEO#1--></p><p>", "");
        content = content.replace("<!--VIDEO#2--></p><p>", "");
        content = content.replace("<!--VIDEO#3--></p><p>", "");
        content = content.replace("<!--VIDEO#4--></p><p>", "");
        content = content.replace("<!--REWARD#0--></p><p>", "");
        webView.setHtmlFromString(content, false);
        dismissProgressDialog();
        // webView.loadDataWithBaseURL(null, content, "text/html", "utf-8",
        // null);
    }

    @Click(R.id.new_img)
    public void imageMore(View view) {
        try {
            Bundle bundle = new Bundle();
            bundle.putSerializable("newDetailModle", newDetailModle);
            if (!"".equals(newDetailModle.getUrl_mp4())) {
                bundle.putString("playUrl", newDetailModle.getUrl_mp4());
                openActivity(VideoPlayActivity_.class, bundle, 0);
            } else {
                openActivity(ImageDetailActivity_.class, bundle, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            // progressBar.setVisibility(View.GONE);
            dismissProgressDialog();
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                String description, String failingUrl) {
            // progressBar.setVisibility(View.GONE);
            dismissProgressDialog();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        mProgressPieView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        mProgressPieView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        if (!"".equals(newDetailModle.getUrl_mp4())) {
            mPlay.setVisibility(View.VISIBLE);
        } else {
            imgCount.setVisibility(View.VISIBLE);
            imgCount.setText(imgCountString);
        }
        mProgressPieView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        mProgressPieView.setVisibility(View.GONE);
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        int currentpro = (current * 100 / total);
        if (currentpro == 100) {
            mProgressPieView.setVisibility(View.GONE);
            mProgressPieView.setShowText(false);
        } else {
            mProgressPieView.setVisibility(View.VISIBLE);
            mProgressPieView.setProgress(currentpro);
            mProgressPieView.setText(currentpro + "%");
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
