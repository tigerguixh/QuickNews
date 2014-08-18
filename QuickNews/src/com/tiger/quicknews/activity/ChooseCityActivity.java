
package com.tiger.quicknews.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.tiger.quicknews.R;
import com.tiger.quicknews.adapter.CityAdapter;
import com.tiger.quicknews.bean.CityItem;
import com.tiger.quicknews.dao.CityData;
import com.tiger.quicknews.wedget.city.ContactItemInterface;
import com.tiger.quicknews.wedget.city.ContactListViewImpl;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_choose_city)
public class ChooseCityActivity extends BaseActivity
{
    private Context context_;

    @ViewById(R.id.listview)
    protected ContactListViewImpl listview;

    @ViewById(R.id.title)
    protected TextView mTitle;

    @ViewById(R.id.input_search_query)
    protected EditText searchBox;
    private String searchString;
    private CityAdapter adapter;

    private Object searchLock;
    boolean inSearchMode = false;

    private final static String TAG = "MainActivity2";

    List<ContactItemInterface> contactList;
    List<ContactItemInterface> filterList;
    private SearchListTask curSearchTask = null;

    @AfterInject
    public void init() {
        context_ = ChooseCityActivity.this;
        searchLock = new Object();
        filterList = new ArrayList<ContactItemInterface>();
        contactList = CityData.getSampleContactList();
        adapter = new CityAdapter(this, R.layout.city_item, contactList);
    }

    @AfterViews
    public void initView() {
        listview.setFastScrollEnabled(true);
        listview.setAdapter(adapter);
        mTitle.setText("选择城市");
    }

    @ItemClick(R.id.listview)
    protected void onItemClick(int position) {
        List<ContactItemInterface> searchList = inSearchMode ? filterList : contactList;
        Intent intent = new Intent();
        intent.putExtra("cityname", searchList.get(position).getDisplayInfo());
        this.setResult(1001, intent);
        this.finish();
    }

    @AfterTextChange(R.id.input_search_query)
    public void afterTextChanged(Editable s)
    {
        searchString = searchBox.getText().toString().trim().toUpperCase();

        if (curSearchTask != null
                && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
        {
            try
            {
                curSearchTask.cancel(true);
            } catch (Exception e)
            {
                Log.i(TAG, "Fail to cancel running search task");
            }

        }
        curSearchTask = new SearchListTask();
        curSearchTask.execute(searchString);
    }

    private class SearchListTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params)
        {
            filterList.clear();

            String keyword = params[0];

            inSearchMode = (keyword.length() > 0);

            if (inSearchMode)
            {
                // get all the items matching this
                for (ContactItemInterface item : contactList)
                {
                    CityItem contact = (CityItem) item;

                    boolean isPinyin = contact.getFullName().toUpperCase()
                            .indexOf(keyword) > -1;
                    boolean isChinese = contact.getNickName().indexOf(keyword) > -1;

                    if (isPinyin || isChinese)
                    {
                        filterList.add(item);
                    }

                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {

            synchronized (searchLock)
            {

                if (inSearchMode)
                {
                    CityAdapter adapter = new CityAdapter(context_,
                            R.layout.city_item, filterList);
                    adapter.setInSearchMode(true);
                    listview.setInSearchMode(true);
                    listview.setAdapter(adapter);
                } else
                {
                    CityAdapter adapter = new CityAdapter(context_,
                            R.layout.city_item, contactList);
                    adapter.setInSearchMode(false);
                    listview.setInSearchMode(false);
                    listview.setAdapter(adapter);
                }
            }

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
