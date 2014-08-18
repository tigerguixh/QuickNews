
package com.tiger.quicknews.wedget.city;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tiger.quicknews.R;

import java.util.Collections;
import java.util.List;

public class ContactListAdapter extends ArrayAdapter<ContactItemInterface>
{

    private final int resource; // store the resource layout id for 1 row
    private boolean inSearchMode = false;

    private ContactsSectionIndexer indexer = null;

    public ContactListAdapter(Context _context, int _resource,
            List<ContactItemInterface> _items)
    {
        super(_context, _resource, _items);
        resource = _resource;

        // need to sort the items array first, then pass it to the indexer
        Collections.sort(_items, new ContactItemComparator());

        setIndexer(new ContactsSectionIndexer(_items));

    }

    // get the section textview from row view
    // the section view will only be shown for the first item
    public TextView getSectionTextView(View rowView)
    {
        TextView sectionTextView = (TextView) rowView
                .findViewById(R.id.sectionTextView);
        return sectionTextView;
    }

    public void showSectionViewIfFirstItem(View rowView,
            ContactItemInterface item, int position)
    {
        TextView sectionTextView = getSectionTextView(rowView);

        // if in search mode then dun show the section header
        if (inSearchMode)
        {
            sectionTextView.setVisibility(View.GONE);
        } else
        {
            // if first item then show the header

            if (indexer.isFirstItemInSection(position))
            {

                String sectionTitle = indexer.getSectionTitle(item
                        .getItemForIndex());
                sectionTextView.setText(sectionTitle);
                sectionTextView.setVisibility(View.VISIBLE);

            } else
                sectionTextView.setVisibility(View.GONE);
        }
    }

    // do all the data population for the row here
    // subclass overwrite this to draw more items
    public void populateDataForRow(View parentView, ContactItemInterface item,
            int position)
    {
        // default just draw the item only
        View infoView = parentView.findViewById(R.id.infoRowContainer);
        TextView nameView = (TextView) infoView.findViewById(R.id.cityName);
        nameView.setText(item.getItemForIndex());
    }

    // this should be override by subclass if necessary
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewGroup parentView;

        ContactItemInterface item = getItem(position);

        if (convertView == null)
        {
            parentView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    inflater);
            vi.inflate(resource, parentView, true);
        } else
        {
            parentView = (LinearLayout) convertView;
        }

        showSectionViewIfFirstItem(parentView, item, position);

        populateDataForRow(parentView, item, position);

        return parentView;

    }

    public boolean isInSearchMode()
    {
        return inSearchMode;
    }

    public void setInSearchMode(boolean inSearchMode)
    {
        this.inSearchMode = inSearchMode;
    }

    public ContactsSectionIndexer getIndexer()
    {
        return indexer;
    }

    public void setIndexer(ContactsSectionIndexer indexer)
    {
        this.indexer = indexer;
    }

}
