
package com.tiger.quicknews.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tiger.quicknews.bean.PicuterDetailModle;
import com.tiger.quicknews.view.PhotoDetailView;
import com.tiger.quicknews.view.PhotoDetailView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class PicuterDetailAdapter extends BaseAdapter {

    public List<PicuterDetailModle> lists = new ArrayList<PicuterDetailModle>();

    @RootContext
    Context context;

    public void appendList(List<PicuterDetailModle> list) {
        if (!lists.containsAll(list) && list != null && list.size() > 0) {
            lists.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        lists.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoDetailView photoItemView;
        if (convertView == null) {
            photoItemView = PhotoDetailView_.build(context);
        } else {
            photoItemView = (PhotoDetailView) convertView;
        }

        PicuterDetailModle picuterDetailModle = lists.get(position);

        photoItemView.setImage(lists.size(), position, picuterDetailModle.getAlt(),
                picuterDetailModle.getTitle(), picuterDetailModle.getPic());

        return photoItemView;
    }

}
