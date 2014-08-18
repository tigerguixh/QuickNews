package com.tiger.quicknews.wedget.city;

import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.SectionIndexer;

public class ContactsSectionIndexer implements SectionIndexer
{

	private static String OTHER = "#";
	private static String[] mSections = { OTHER, "A", "B", "C", "D", "E", "F",
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
			"T", "U", "V", "W", "X", "Y", "Z" };

	private static int OTHER_INDEX = 0; // index of other in the mSections array

	private int[] mPositions; // store the list of starting position index for
								// each section
								// e.g. A start at index 0, B start at index 20,
								// C start at index 41 and so on

	private int mCount; // this is the count for total number of contacts

	// Assumption: the contacts array has been sorted
	public ContactsSectionIndexer(List<ContactItemInterface> contacts)
	{
		mCount = contacts.size();

		initPositions(contacts);

	}

	public String getSectionTitle(String indexableItem)
	{
		int sectionIndex = getSectionIndex(indexableItem);
		return mSections[sectionIndex];
	}

	// return which section this item belong to
	public int getSectionIndex(String indexableItem)
	{
		if (indexableItem == null)
		{
			return OTHER_INDEX;
		}

		indexableItem = indexableItem.trim();
		String firstLetter = OTHER;

		if (indexableItem.length() == 0)
		{
			return OTHER_INDEX;
		} else
		{
			// get the first letter
			firstLetter = String.valueOf(indexableItem.charAt(0)).toUpperCase();
		}

		int sectionCount = mSections.length;
		for (int i = 0; i < sectionCount; i++)
		{
			if (mSections[i].equals(firstLetter))
			{
				return i;
			}
		}

		return OTHER_INDEX;

	}

	// initialize the position index
	public void initPositions(List<ContactItemInterface> contacts)
	{

		int sectionCount = mSections.length;
		mPositions = new int[sectionCount];

		Arrays.fill(mPositions, -1); // initialize everything to -1

		// Assumption: list of items have already been sorted by the prefer
		// names
		int itemIndex = 0;

		for (ContactItemInterface contact : contacts)
		{

			String indexableItem = contact.getItemForIndex();
			int sectionIndex = getSectionIndex(indexableItem); // find out which
																// section this
																// item belong
																// to

			if (mPositions[sectionIndex] == -1) // if not set before, then do
												// this, otherwise just ignore
				mPositions[sectionIndex] = itemIndex;

			itemIndex++;

		}

		int lastPos = -1;

		// now loop through, for all the ones not found, set position to the one
		// before them
		// this is to make sure the array is sorted for binary search to work
		for (int i = 0; i < sectionCount; i++)
		{
			if (mPositions[i] == -1)
				mPositions[i] = lastPos;

			lastPos = mPositions[i];

		}

	}

	@Override
	public int getPositionForSection(int section)
	{
		if (section < 0 || section >= mSections.length)
		{
			return -1;
		}

		return mPositions[section];
	}

	@Override
	public int getSectionForPosition(int position)
	{
		if (position < 0 || position >= mCount)
		{
			return -1;
		}

		int index = Arrays.binarySearch(mPositions, position);

		/*
		 * Consider this example: section positions are 0, 3, 5; the supplied
		 * position is 4. The section corresponding to position 4 starts at
		 * position 3, so the expected return value is 1. Binary search will not
		 * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
		 * To get from that number to the expected value of 1 we need to negate
		 * and subtract 2.
		 */
		return index >= 0 ? index : -index - 2;
	}

	@Override
	public Object[] getSections()
	{
		return mSections;
	}

	// if first item in section, then return the section
	// otherwise return -1
	public boolean isFirstItemInSection(int position)
	{ // check whether this item is the first item in section
		int section = Arrays.binarySearch(mPositions, position);
		return (section > -1);

	}

}
