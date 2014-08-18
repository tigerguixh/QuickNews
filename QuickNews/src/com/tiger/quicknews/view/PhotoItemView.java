
package com.tiger.quicknews.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tiger.quicknews.App;
import com.tiger.quicknews.R;
import com.tiger.quicknews.bean.PhotoModle;
import com.tiger.quicknews.utils.Options;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.item_photo)
public class PhotoItemView extends RelativeLayout {

    @ViewById(R.id.photo_img)
    protected ImageView photoImg;

    @ViewById(R.id.photo_title)
    protected TextView photoTitle;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    protected DisplayImageOptions options;

    public PhotoItemView(Context context) {
        super(context);
        options = Options.getListOptions();
    }

    public void setData(String title, String picUrl) {

        picUrl = picUrl.replace("auto", "854x480x75x0x0x3");

        photoTitle.setText(title);
        imageLoader.displayImage(picUrl, photoImg, options);
    }

}
