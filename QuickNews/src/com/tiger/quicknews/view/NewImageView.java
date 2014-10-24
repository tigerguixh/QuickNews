
package com.tiger.quicknews.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.tiger.quicknews.App;
import com.tiger.quicknews.R;
import com.tiger.quicknews.utils.Options;
import com.tiger.quicknews.wedget.ProgressButton;
import com.tiger.quicknews.wedget.photoview.PhotoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EViewGroup(R.layout.item_image)
public class NewImageView extends RelativeLayout implements ImageLoadingListener,
        ImageLoadingProgressListener {

    @ViewById(R.id.current_image)
    protected ImageView currentImage;

    @ViewById(R.id.current_page)
    protected TextView currentPage;

    @ViewById(R.id.progressButton)
    protected ProgressButton progressButton;

    protected CompoundButton.OnCheckedChangeListener checkedChangeListener;

    protected ImageLoader imageLoader = ImageLoader.getInstance();

    protected DisplayImageOptions options;

    protected Context context;

    public NewImageView(Context context) {
        super(context);
        this.context = context;
        options = Options.getListOptions();
        progress(context);
    }

    public void setImage(List<String> imgList, int position) {
        currentPage.setText((position + 1) + "/" + imgList.size());
        imageLoader.displayImage(imgList.get(position), currentImage, options, this,
                this);
    }

    @AfterViews
    public void initView() {
        progressButton.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressButton.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressButton.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        progressButton.setVisibility(View.GONE);
    }

    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {

        int currentpro = (current * 100 / total);

        if (currentpro == 100) {
            progressButton.setVisibility(View.GONE);
        } else {
            progressButton.setVisibility(View.VISIBLE);
        }
        if (currentpro >= 0 && currentpro <= 100) {
            progressButton.setProgress(currentpro);
        }
        updatePinProgressContentDescription(progressButton, context);

    }

    public void progress(final Context context) {
        checkedChangeListener =
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        updatePinProgressContentDescription((ProgressButton) compoundButton,
                                context);
                    }
                };
    }

    /**
     * Helper method to update the progressButton's content description.
     */
    private void updatePinProgressContentDescription(ProgressButton button, Context context) {
        int progress = button.getProgress();
        if (progress <= 0) {
            button.setContentDescription(context.getString(
                    button.isChecked() ? R.string.content_desc_pinned_not_downloaded
                            : R.string.content_desc_unpinned_not_downloaded));
        } else if (progress >= button.getMax()) {
            button.setContentDescription(context.getString(
                    button.isChecked() ? R.string.content_desc_pinned_downloaded
                            : R.string.content_desc_unpinned_downloaded));
        } else {
            button.setContentDescription(context.getString(
                    button.isChecked() ? R.string.content_desc_pinned_downloading
                            : R.string.content_desc_unpinned_downloading));
        }
    }

}
