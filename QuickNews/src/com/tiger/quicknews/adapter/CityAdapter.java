
package com.tiger.quicknews.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tiger.quicknews.R;
import com.tiger.quicknews.wedget.city.ContactItemInterface;
import com.tiger.quicknews.wedget.city.ContactListAdapter;

import java.util.List;

public class CityAdapter extends ContactListAdapter
{

    public CityAdapter(Context _context, int _resource,
            List<ContactItemInterface> _items)
    {
        super(_context, _resource, _items);
    }

    @Override
    public void populateDataForRow(View parentView, ContactItemInterface item,
            int position)
    {
        View infoView = parentView.findViewById(R.id.infoRowContainer);
        TextView nicknameView = (TextView) infoView
                .findViewById(R.id.cityName);

        nicknameView.setText(item.getDisplayInfo());
    }

}
