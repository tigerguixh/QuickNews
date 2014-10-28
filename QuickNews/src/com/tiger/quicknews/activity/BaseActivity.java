
package com.tiger.quicknews.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Xml.Encoding;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.tiger.quicknews.R;
import com.tiger.quicknews.http.HttpUtil;
import com.tiger.quicknews.http.Url;
import com.tiger.quicknews.listener.BackGestureListener;
import com.tiger.quicknews.utils.ACache;
import com.tiger.quicknews.utils.DialogUtil;
import com.tiger.quicknews.utils.StringUtils;
import com.tiger.quicknews.wedget.crouton.Crouton;
import com.tiger.quicknews.wedget.crouton.Style;
import com.tiger.quicknews.wedget.gesture.BaseActivityHelper;
import com.tiger.quicknews.wedget.slideingactivity.IntentUtils;
import com.tiger.quicknews.wedget.slideingactivity.SlidingActivity;

import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BaseActivity extends SlidingActivity {

    /** 手势监听 */
    // private GestureDetector mGestureDetector;
    /** 是否需要监听手势关闭功能 */
    private boolean mNeedBackGesture = false;
    // private BaseActivityHelper baseActivityHelper;
    private Dialog progressDialog;
    public static final int REQUEST_CODE = 1000;

    @Override
    public void onResume() {
        super.onResume();
        // baseActivityHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // baseActivityHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // baseActivityHelper.onDestroy();
    }

    public boolean isSupportSlide() {
        return true;
    }

    public String getUrl(String newId) {
        return Url.NewDetail + newId + Url.endDetailUrl;
    }

    public String getMsgUrl(String index, String itemId) {
        String urlString = Url.CommonUrl + itemId + "/" + index + "-40.html";
        return urlString;
    }

    public String getWeatherUrl(String cityName) throws UnsupportedEncodingException {
        // + Url.WeatherKey
        String urlString = Url.WeatherHost + URLEncoder.encode(cityName, "utf-8");
        return urlString;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        initGestureDetector();
        // baseActivityHelper = new BaseActivityHelper(this, isSupportSlide());
        // baseActivityHelper.onCreate();
    }

    private void initGestureDetector() {
        // if (mGestureDetector == null) {
        // mGestureDetector = new GestureDetector(getApplicationContext(),
        // new BackGestureListener(this));
        // }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mNeedBackGesture) {
            // return mGestureDetector.onTouchEvent(ev) ||
            // super.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /*
     * 设置是否进行手势监听
     */
    public void setNeedBackGesture(boolean mNeedBackGesture) {
        this.mNeedBackGesture = mNeedBackGesture;
    }

    /**
     * 显示dialog
     * 
     * @param msg 显示内容
     */
    public void showProgressDialog() {
        try {

            if (progressDialog == null) {
                progressDialog = DialogUtil.createLoadingDialog(this);

            }
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 隐藏dialog
     */
    public void dismissProgressDialog() {
        try {

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * dialog是否显示
     */
    public boolean isShow() {
        try {

            if (progressDialog != null && progressDialog.isShowing()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更具类打开acitvity
     */
    public void openActivity(Class<?> pClass) {
        openActivity(pClass, null, 0);

    }

    public void openActivityForResult(Class<?> pClass, int requestCode) {
        openActivity(pClass, null, requestCode);
    }

    /**
     * 更具类打开acitvity,并携带参数
     */
    public void openActivity(Class<?> pClass, Bundle pBundle, int requestCode) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        if (requestCode == 0) {
            IntentUtils.startPreviewActivity(this, intent, 0);
            // startActivity(intent);
        } else {
            IntentUtils.startPreviewActivity(this, intent, requestCode);
            // startActivityForResult(intent, requestCode);
        }
        // actityAnim();
    }

    /**
     * 判断是否有网络
     * 
     * @return
     */
    public boolean hasNetWork() {
        return HttpUtil.isNetworkAvailable(this);
    }

    /**
     * 显示LongToast
     */
    public void showShortToast(String pMsg) {
        // ToastUtil.createCustomToast(this, pMsg, Toast.LENGTH_LONG).show();
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示ShortToast
     */
    public void showCustomToast(String pMsg) {
        // ToastUtil.createCustomToast(this, pMsg, Toast.LENGTH_SHORT).show();
        // Crouton.makeText(this, pMsg, Style.ALERT, R.id.toast_conten).show();
        Crouton.makeText(this, pMsg, Style.ALERT, R.id.toast_conten).show();

    }

    /**
     * 设置缓存数据（key,value）
     */
    public void setCacheStr(String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            ACache.get(this).put(key, value);
        }
    }

    /**
     * 获取缓存数据更具key
     */
    public String getCacheStr(String key) {
        return ACache.get(this).getAsString(key);
    }

    /**
     * 带动画效果的关闭
     */
    @Override
    public void finish() {
        super.finish();
        // baseActivityHelper.finish();
        actityAnim();
    }

    /**
     * 系统默认关闭
     */
    public void defaultFinish() {
        super.finish();
        // baseActivityHelper.finish();
    }

    /**
     * 系统默认关闭
     */
    public void defaultFinishNotActivityHelper() {
        super.finish();
    }

    public void actityAnim() {
        // overridePendingTransition(R.anim.slide_in_right,
        // R.anim.slide_right_out);
    }

    /**
     * 返回
     */
    public void doBack(View view) {
        onBackPressed();
    }

}
