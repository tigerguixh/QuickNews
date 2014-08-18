
package com.tiger.quicknews.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.tiger.quicknews.activity.BaseActivity;
import com.tiger.quicknews.http.Url;
import com.tiger.quicknews.utils.StringUtils;

public class BaseFragment extends Fragment {
    public View mView;
    /**
     * 当前页
     */
    public int currentPagte = 1;

    public BaseActivity getMyActivity() {
        return (BaseActivity) getActivity();
    }

    public String getNewUrl(String index) {
        String urlString = Url.TopUrl + Url.TopId + "/" + index + Url.endUrl;
        return urlString;
    }

    public String getCommonUrl(String index, String itemId) {
        String urlString = Url.CommonUrl + itemId + "/" + index + Url.endUrl;
        return urlString;
    }

    public String getLocalUrl(String index, String itemId) {
        String urlString = Url.Local + itemId + "/" + index + Url.endUrl;
        return urlString;
    }

    public String getFangUrl(String index, String itemId) {
        String urlString = Url.FangChan + itemId + "/" + index + Url.endUrl;
        return urlString;
    }

    public String getPhotosUrl(String index) {
        String urlString = Url.TuJi + index + Url.TuJiEnd;
        return urlString;
    }

    public String getReDianPicsUrl(String index) {
        String urlString = Url.TuPianReDian + index + Url.TuJiEnd;
        return urlString;
    }

    public String getDuJiaPicsUrl(String index) {
        String urlString = Url.TuPianDuJia + index + Url.TuJiEnd;
        return urlString;
    }

    public String getMingXingPicsUrl(String index) {
        String urlString = Url.TuPianMingXing + index + Url.TuJiEnd;
        return urlString;
    }

    public String getTiTanPicsUrl(String index) {
        String urlString = Url.TuPianTiTan + index + Url.TuJiEnd;
        return urlString;
    }

    public String getMeiTuPicsUrl(String index) {
        String urlString = Url.TuPianMeiTu + index + Url.TuJiEnd;
        return urlString;
    }

    public String getSinaJingXuan(String index) {
        String urlString = Url.JINGXUAN_ID + index;
        return urlString;
    }

    public String getSinaQuTu(String index) {
        String urlString = Url.QUTU_ID + index;
        return urlString;
    }

    public String getSinaMeiTu(String index) {
        String urlString = Url.MEITU_ID + index;
        return urlString;
    }

    public String getSinaGuShi(String index) {
        String urlString = Url.GUSHI_ID + index;
        return urlString;
    }

    // ��Ƶ http://c.3g.163.com/nc/video/list/V9LG4B3A0/n/10-10.html
    public String getVideoUrl(String index, String videoId) {
        String urlString = Url.Video + videoId + Url.VideoCenter + index + Url.videoEndUrl;
        return urlString;
    }

    public boolean isNullString(String imgUrl) {

        if (StringUtils.isEmpty(imgUrl)) {
            return true;
        }
        return false;
    }
}
