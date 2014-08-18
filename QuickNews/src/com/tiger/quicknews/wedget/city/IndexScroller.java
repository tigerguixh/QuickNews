package com.tiger.quicknews.wedget.city;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

// taken from https://github.com/woozzu/indexableListView IndexScroller
// rewrite some sections to make it more customizable
public class IndexScroller
{

	/**
	 * Add additional properties for customization
	 * 
	 */
	// Hung - store the last touch down eventX and eventY so that we can detect
	// where user click on list view
	// indexscroller will override touch event of listview, so onclicklistener
	// will not work
	private float lastTouchDownEventX = -1;
	private float lastTouchDownEventY = -1;

	// whether to autohide the scroller
	// false will always show the index bar
	// true will hide the scrollbar at first, but slowly show it later
	private boolean autoHide = false;

	// minor optimizations
	private Paint indexbarContainerPaint = new Paint(); // paint for indexbar
														// container
	private Paint indexPaint = new Paint();

	private boolean showIndexContainer = false; // whether to show the outer
												// index container
	private int indexbarContainerBgColor = Color.BLACK;
	private int indexPaintColor = Color.WHITE; // color for section title

	// / end additional properties

	private float mIndexbarWidth;
	private float mIndexbarMargin;
	private float mPreviewPadding;
	private float mDensity;
	private float mScaledDensity;
	private float mAlphaRate;
	private int mState = STATE_HIDDEN;
	private int mListViewWidth;
	private int mListViewHeight;
	private int mCurrentSection = -1;
	private boolean mIsIndexing = false;
	private ListView mListView = null;
	private SectionIndexer mIndexer = null;
	private String[] mSections = null;
	private RectF mIndexbarRect;

	private static final int STATE_HIDDEN = 0;
	private static final int STATE_SHOWING = 1;
	private static final int STATE_SHOWN = 2;
	private static final int STATE_HIDING = 3;

	public IndexScroller(Context context, ListView lv)
	{
		mDensity = context.getResources().getDisplayMetrics().density;
		mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		mListView = lv;

		setAdapter(mListView.getAdapter());

		mIndexbarWidth = 30 * mDensity;
		mIndexbarMargin = 0 * mDensity;
		mPreviewPadding = 5 * mDensity;

		// customization of paint colors
		// outer container
		indexbarContainerPaint.setAntiAlias(true);

		// letter in section
		indexPaint.setAntiAlias(true);
		indexPaint.setTextSize(12 * mScaledDensity);
	}

	public IndexScroller(Context context, ListView lv, SectionIndexer indexer)
	{
		this(context, lv);

		mIndexer = indexer;

	}

	// draw the outer rounded container
	public void drawIndexBarContainer(Canvas canvas)
	{
		indexbarContainerPaint.setColor(indexbarContainerBgColor);
		indexbarContainerPaint.setAlpha((int) (64 * mAlphaRate)); // opacity
		canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity,
				indexbarContainerPaint);
	}

	public void drawSections(Canvas canvas)
	{
		indexPaint.setColor(indexPaintColor);
		indexPaint.setAlpha((int) (255 * mAlphaRate));

		float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin)
				/ mSections.length;
		float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint
				.ascent())) / 2;

		// draw the section letters
		for (int i = 0; i < mSections.length; i++)
		{
			float paddingLeft = (mIndexbarWidth - indexPaint
					.measureText(mSections[i])) / 2;
			canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft,
					mIndexbarRect.top + mIndexbarMargin + sectionHeight * i
							+ paddingTop - indexPaint.ascent(), indexPaint);
		}
	}

	public void drawCurrentSection(Canvas canvas)
	{
		if (mCurrentSection >= 0)
		{

			// Log.i("IndexScroller", "current section: " + mCurrentSection);

			// Preview is shown when mCurrentSection is set
			// mCurrentSection is the letter that is being pressed
			// this will draw the big preview text on top of the listview
			Paint previewPaint = new Paint();
			previewPaint.setColor(Color.BLACK);
			previewPaint.setAlpha(96);
			previewPaint.setAntiAlias(true);
			previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

			Paint previewTextPaint = new Paint();
			previewTextPaint.setColor(Color.WHITE);
			previewTextPaint.setAntiAlias(true);
			previewTextPaint.setTextSize(50 * mScaledDensity);

			float previewTextWidth = previewTextPaint
					.measureText(mSections[mCurrentSection]);
			float previewSize = 2 * mPreviewPadding
					+ previewTextPaint.descent() - previewTextPaint.ascent();
			RectF previewRect = new RectF((mListViewWidth - previewSize) / 2,
					(mListViewHeight - previewSize) / 2,
					(mListViewWidth - previewSize) / 2 + previewSize,
					(mListViewHeight - previewSize) / 2 + previewSize);

			canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity,
					previewPaint);
			canvas.drawText(mSections[mCurrentSection], previewRect.left
					+ (previewSize - previewTextWidth) / 2 - 1, previewRect.top
					+ mPreviewPadding - previewTextPaint.ascent() + 1,
					previewTextPaint);
		}
	}

	public void draw(Canvas canvas)
	{
		if (mState == STATE_HIDDEN)
			return;

		if (showIndexContainer)
			drawIndexBarContainer(canvas);

		if (mSections != null && mSections.length > 0)
		{

			drawCurrentSection(canvas);
			drawSections(canvas);
		}
	}

	public boolean onTouchEvent(MotionEvent ev)
	{
		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			lastTouchDownEventX = ev.getX(); // Hung capture the event for later
												// use in listview item click
			lastTouchDownEventY = ev.getY();

			// If down event occurs inside index bar region, start indexing
			if (mState != STATE_HIDDEN && contains(ev.getX(), ev.getY()))
			{
				setState(STATE_SHOWN);

				// It demonstrates that the motion event started from index bar
				mIsIndexing = true;
				// Determine which section the point is in, and move the list to
				// that section
				mCurrentSection = getSectionByPoint(ev.getY());
				mListView.setSelection(mIndexer
						.getPositionForSection(mCurrentSection));
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsIndexing)
			{
				// If this event moves inside index bar
				if (contains(ev.getX(), ev.getY()))
				{
					// Determine which section the point is in, and move the
					// list to that section
					mCurrentSection = getSectionByPoint(ev.getY());
					mListView.setSelection(mIndexer
							.getPositionForSection(mCurrentSection));
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsIndexing)
			{
				mIsIndexing = false;
				mCurrentSection = -1;
			}
			// only hide if state is auto hiding
			if (autoHide)
			{
				if (mState == STATE_SHOWN)
					setState(STATE_HIDING);
			}
			break;
		}
		return false;
	}

	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		mListViewWidth = w;
		mListViewHeight = h;
		mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth,
				mIndexbarMargin, w - mIndexbarMargin, h - mIndexbarMargin);
	}

	public void show()
	{
		if (mState == STATE_HIDDEN)
			setState(STATE_SHOWING);
		else if (mState == STATE_HIDING)
			setState(STATE_HIDING);
	}

	public void hide()
	{
		if (mState == STATE_SHOWN)
			setState(STATE_HIDING);
	}

	public void setAdapter(Adapter adapter)
	{
		if (adapter instanceof SectionIndexer)
		{
			mIndexer = (SectionIndexer) adapter;
			mSections = (String[]) mIndexer.getSections();
		} else if (adapter instanceof ContactListAdapter)
		{
			ContactListAdapter c = (ContactListAdapter) adapter;
			mIndexer = c.getIndexer();
			mSections = (String[]) mIndexer.getSections();
		}
	}

	private void setState(int state)
	{
		if (state < STATE_HIDDEN || state > STATE_HIDING)
			return;

		mState = state;
		switch (mState)
		{
		case STATE_HIDDEN:
			// Cancel any fade effect
			mHandler.removeMessages(0);
			break;
		case STATE_SHOWING:
			// Start to fade in
			mAlphaRate = 0;
			fade(0);
			break;
		case STATE_SHOWN:
			// Cancel any fade effect
			mHandler.removeMessages(0);
			break;
		case STATE_HIDING:
			// Start to fade out after three seconds
			mAlphaRate = 1;
			fade(3000);
			break;
		}
	}

	private boolean contains(float x, float y)
	{
		// Determine if the point is in index bar region, which includes the
		// right margin of the bar
		return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top
				+ mIndexbarRect.height());
	}

	private int getSectionByPoint(float y)
	{
		if (mSections == null || mSections.length == 0)
			return 0;
		if (y < mIndexbarRect.top + mIndexbarMargin)
			return 0;
		if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
			return mSections.length - 1;
		return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect
				.height() - 2 * mIndexbarMargin) / mSections.length));
	}

	private void fade(long delay)
	{
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
	}

	private Handler mHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);

			switch (mState)
			{
			case STATE_SHOWING:
				// Fade in effect
				mAlphaRate += (1 - mAlphaRate) * 0.2;
				if (mAlphaRate > 0.9)
				{
					mAlphaRate = 1;
					setState(STATE_SHOWN);
				}

				mListView.invalidate();
				fade(10);
				break;
			case STATE_SHOWN:
				// If no action, hide automatically
				// Hung - comment out this to disable hiding
				if (autoHide)
				{
					setState(STATE_HIDING);
				}
				break;
			case STATE_HIDING:
				// Fade out effect
				mAlphaRate -= mAlphaRate * 0.2;
				if (mAlphaRate < 0.1)
				{
					mAlphaRate = 0;
					setState(STATE_HIDDEN);
				}

				mListView.invalidate();
				fade(10);
				break;
			}
		}

	};

	public float getLastTouchDownEventX()
	{
		return lastTouchDownEventX;
	}

	public void setLastTouchDownEventX(float lastTouchDownEventX)
	{
		this.lastTouchDownEventX = lastTouchDownEventX;
	}

	public float getLastTouchDownEventY()
	{
		return lastTouchDownEventY;
	}

	public void setLastTouchDownEventY(float lastTouchDownEventY)
	{
		this.lastTouchDownEventY = lastTouchDownEventY;
	}

	public boolean isAutoHide()
	{
		return autoHide;
	}

	public void setAutoHide(boolean autoHide)
	{
		this.autoHide = autoHide;
	}

	public boolean isShowIndexContainer()
	{
		return showIndexContainer;
	}

	public void setShowIndexContainer(boolean showIndexContainer)
	{
		this.showIndexContainer = showIndexContainer;
	}

	public int getIndexbarContainerBgColor()
	{
		return indexbarContainerBgColor;
	}

	public void setIndexbarContainerBgColor(int indexbarContainerBgColor)
	{
		this.indexbarContainerBgColor = indexbarContainerBgColor;
	}

	public int getIndexPaintColor()
	{
		return indexPaintColor;
	}

	public void setIndexPaintColor(int indexPaintColor)
	{
		this.indexPaintColor = indexPaintColor;
	}

}
