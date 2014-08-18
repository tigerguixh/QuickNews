/*
 * Copyright (C) 2013 47 Degrees, LLC
 * http://47deg.com
 * hello@47deg.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tiger.quicknews.wedget.swiptlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tiger.quicknews.R;

import java.util.List;

/**
 * ListView subclass that provides the swipe functionality
 */
public class SwipeListView extends ListView implements OnScrollListener {

    private boolean isDropDownStyle = true;
    private boolean isOnBottomStyle = true;
    private boolean isAutoLoadOnBottom = false;

    private String headerDefaultText;
    private String headerPullText;
    private String headerReleaseText;
    private String headerLoadingText;
    private String footerDefaultText;
    private String footerLoadingText;
    private String footerNoMoreText;

    private final Context context;

    /** header layout view **/
    private RelativeLayout headerLayout;
    private ImageView headerImage;
    private ProgressBar headerProgressBar;
    private TextView headerText;
    private TextView headerSecondText;

    /** footer layout view **/
    private RelativeLayout footerLayout;
    private ProgressBar footerProgressBar;
    private Button footerButton;

    private OnDropDownListener onDropDownListener;
    private OnScrollListener onScrollListener;

    /** rate about drop down distance and header padding top when drop down **/
    private float headerPaddingTopRate = 1.5f;
    /** min distance which header can release to loading **/
    private int headerReleaseMinDistance;

    /** whether bottom listener has more **/
    private boolean hasMore = true;
    /** whether show footer loading progress bar when loading **/
    private boolean isShowFooterProgressBar = true;

    private int currentScrollState;
    private int currentHeaderStatus;

    /** whether reached top, when has reached top, don't show header layout **/
    private boolean hasReachedTop = false;

    /** image flip animation **/
    private RotateAnimation flipAnimation;
    /** image reverse flip animation **/
    private RotateAnimation reverseFlipAnimation;

    /** header layout original height **/
    private int headerOriginalHeight;
    /** header layout original padding top **/
    private int headerOriginalTopPadding;
    /** y of point which user touch down **/
    private float actionDownPointY;

    /** whether is on bottom loading **/
    private boolean isOnBottomLoading = false;

    /**
     * Used when user want change swipe list mode on some rows
     */
    public final static int SWIPE_MODE_DEFAULT = -1;

    /**
     * Disables all swipes
     */
    public final static int SWIPE_MODE_NONE = 0;

    /**
     * Enables both left and right swipe
     */
    public final static int SWIPE_MODE_BOTH = 1;

    /**
     * Enables right swipe
     */
    public final static int SWIPE_MODE_RIGHT = 2;

    /**
     * Enables left swipe
     */
    public final static int SWIPE_MODE_LEFT = 3;

    /**
     * Binds the swipe gesture to reveal a view behind the row (Drawer style)
     */
    public final static int SWIPE_ACTION_REVEAL = 0;

    /**
     * Dismisses the cell when swiped over
     */
    public final static int SWIPE_ACTION_DISMISS = 1;

    /**
     * Marks the cell as checked when swiped and release
     */
    public final static int SWIPE_ACTION_CHOICE = 2;

    /**
     * No action when swiped
     */
    public final static int SWIPE_ACTION_NONE = 3;

    /**
     * Default ids for front view
     */
    public final static String SWIPE_DEFAULT_FRONT_VIEW = "swipelist_frontview";

    /**
     * Default id for back view
     */
    public final static String SWIPE_DEFAULT_BACK_VIEW = "swipelist_backview";

    /**
     * Indicates no movement
     */
    private final static int TOUCH_STATE_REST = 0;

    /**
     * State scrolling x position
     */
    private final static int TOUCH_STATE_SCROLLING_X = 1;

    /**
     * State scrolling y position
     */
    private final static int TOUCH_STATE_SCROLLING_Y = 2;

    private int touchState = TOUCH_STATE_REST;

    private float lastMotionX;
    private float lastMotionY;
    private int touchSlop;

    int swipeFrontView = 0;
    int swipeBackView = 0;

    /**
     * Internal listener for common swipe events
     */
    private SwipeListViewListener swipeListViewListener;

    /**
     * Internal touch listener
     */
    private SwipeListViewTouchListener touchListener;

    /**
     * If you create a View programmatically you need send back and front
     * identifier
     * 
     * @param context Context
     * @param swipeBackView Back Identifier
     * @param swipeFrontView Front Identifier
     */
    public SwipeListView(Context context, int swipeBackView, int swipeFrontView) {
        super(context);
        this.context = context;
        this.swipeFrontView = swipeFrontView;
        this.swipeBackView = swipeBackView;
        init(null);
    }

    /**
     * @see android.widget.ListView#ListView(android.content.Context,
     *      android.util.AttributeSet)
     */
    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    /**
     * @see android.widget.ListView#ListView(android.content.Context,
     *      android.util.AttributeSet, int)
     */
    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init(attrs);
    }

    /**
     * Init ListView
     * 
     * @param attrs AttributeSet
     */
    private void init(AttributeSet attrs) {

        int swipeMode = SWIPE_MODE_BOTH;
        boolean swipeOpenOnLongPress = true;
        boolean swipeCloseAllItemsWhenMoveList = true;
        long swipeAnimationTime = 0;
        float swipeOffsetLeft = 0;
        float swipeOffsetRight = 0;
        int swipeDrawableChecked = 0;
        int swipeDrawableUnchecked = 0;

        int swipeActionLeft = SWIPE_ACTION_REVEAL;
        int swipeActionRight = SWIPE_ACTION_REVEAL;

        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs,
                    R.styleable.SwipeListView);
            swipeMode = styled.getInt(R.styleable.SwipeListView_swipeMode, SWIPE_MODE_BOTH);
            swipeActionLeft = styled.getInt(R.styleable.SwipeListView_swipeActionLeft,
                    SWIPE_ACTION_REVEAL);
            swipeActionRight = styled.getInt(R.styleable.SwipeListView_swipeActionRight,
                    SWIPE_ACTION_REVEAL);
            swipeOffsetLeft = styled.getDimension(R.styleable.SwipeListView_swipeOffsetLeft, 0);
            swipeOffsetRight = styled.getDimension(R.styleable.SwipeListView_swipeOffsetRight, 0);
            swipeOpenOnLongPress = styled.getBoolean(
                    R.styleable.SwipeListView_swipeOpenOnLongPress, true);
            swipeAnimationTime = styled.getInteger(R.styleable.SwipeListView_swipeAnimationTime, 0);
            swipeCloseAllItemsWhenMoveList = styled.getBoolean(
                    R.styleable.SwipeListView_swipeCloseAllItemsWhenMoveList, true);
            swipeDrawableChecked = styled.getResourceId(
                    R.styleable.SwipeListView_swipeDrawableChecked, 0);
            swipeDrawableUnchecked = styled.getResourceId(
                    R.styleable.SwipeListView_swipeDrawableUnchecked, 0);
            swipeFrontView = styled.getResourceId(R.styleable.SwipeListView_swipeFrontView, 0);
            swipeBackView = styled.getResourceId(R.styleable.SwipeListView_swipeBackView, 0);

            isDropDownStyle = styled.getBoolean(R.styleable.SwipeListView_swipeIsDropDownStyle,
                    false);
            isOnBottomStyle = styled.getBoolean(R.styleable.SwipeListView_swipeIsOnBottomStyle,
                    false);
            isAutoLoadOnBottom = styled.getBoolean(
                    R.styleable.SwipeListView_swipeIsAutoLoadOnBottom,
                    false);

        }

        initOnBottomStyle();

        // should set, to run onScroll method and so on
        super.setOnScrollListener(this);

        if (swipeFrontView == 0 || swipeBackView == 0) {
            swipeFrontView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_FRONT_VIEW,
                    "id", getContext().getPackageName());
            swipeBackView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_BACK_VIEW,
                    "id", getContext().getPackageName());

            if (swipeFrontView == 0 || swipeBackView == 0) {
                throw new RuntimeException(
                        String.format(
                                "You forgot the attributes swipeFrontView or swipeBackView. You can add this attributes or use '%s' and '%s' identifiers",
                                SWIPE_DEFAULT_FRONT_VIEW, SWIPE_DEFAULT_BACK_VIEW));
            }
        }

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        touchListener = new SwipeListViewTouchListener(this, swipeFrontView, swipeBackView);
        if (swipeAnimationTime > 0) {
            touchListener.setAnimationTime(swipeAnimationTime);
        }
        touchListener.setRightOffset(swipeOffsetRight);
        touchListener.setLeftOffset(swipeOffsetLeft);
        touchListener.setSwipeActionLeft(swipeActionLeft);
        touchListener.setSwipeActionRight(swipeActionRight);
        touchListener.setSwipeMode(swipeMode);
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
        touchListener.setSwipeDrawableChecked(swipeDrawableChecked);
        touchListener.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
        setOnTouchListener(touchListener);
        setOnScrollListener(touchListener.makeScrollListener());
    }

    /**
     * init on bottom style, only init once
     */
    private void initOnBottomStyle() {
        if (footerLayout != null) {
            if (isOnBottomStyle) {
                addFooterView(footerLayout);
            } else {
                removeFooterView(footerLayout);
            }
            return;
        }
        if (!isOnBottomStyle) {
            return;
        }

        footerDefaultText = context.getString(R.string.drop_down_list_footer_default_text);
        footerLoadingText = context.getString(R.string.drop_down_list_footer_loading_text);
        footerNoMoreText = context.getString(R.string.drop_down_list_footer_no_more_text);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerLayout = (RelativeLayout) inflater.inflate(R.layout.drop_down_list_footer, this,
                false);
        footerButton = (Button) footerLayout.findViewById(R.id.drop_down_list_footer_button);
        footerButton.setDrawingCacheBackgroundColor(0);
        footerButton.setEnabled(true);

        footerProgressBar = (ProgressBar) footerLayout
                .findViewById(R.id.drop_down_list_footer_progress_bar);
        addFooterView(footerLayout);
    }

    /**
     * @return isDropDownStyle
     */
    public boolean isDropDownStyle() {
        return isDropDownStyle;
    }

    /**
     * @param isDropDownStyle
     */
    public void setDropDownStyle(boolean isDropDownStyle) {
        if (this.isDropDownStyle != isDropDownStyle) {
            this.isDropDownStyle = isDropDownStyle;
        }
    }

    /**
     * @return isOnBottomStyle
     */
    public boolean isOnBottomStyle() {
        return isOnBottomStyle;
    }

    /**
     * @param isOnBottomStyle
     */
    public void setOnBottomStyle(boolean isOnBottomStyle) {
        if (this.isOnBottomStyle != isOnBottomStyle) {
            this.isOnBottomStyle = isOnBottomStyle;
            initOnBottomStyle();
        }
    }

    /**
     * @return isAutoLoadOnBottom
     */
    public boolean isAutoLoadOnBottom() {
        return isAutoLoadOnBottom;
    }

    /**
     * set whether auto load when on bottom
     * 
     * @param isAutoLoadOnBottom
     */
    public void setAutoLoadOnBottom(boolean isAutoLoadOnBottom) {
        this.isAutoLoadOnBottom = isAutoLoadOnBottom;
    }

    /**
     * get whether show footer loading progress bar when loading
     * 
     * @return the isShowFooterProgressBar
     */
    public boolean isShowFooterProgressBar() {
        return isShowFooterProgressBar;
    }

    /**
     * set whether show footer loading progress bar when loading
     * 
     * @param isShowFooterProgressBar
     */
    public void setShowFooterProgressBar(boolean isShowFooterProgressBar) {
        this.isShowFooterProgressBar = isShowFooterProgressBar;
    }

    /**
     * get footer button
     * 
     * @return
     */
    public Button getFooterButton() {
        return footerButton;
    }

    /**
     * Recycle cell. This method should be called from getView in Adapter when
     * use SWIPE_ACTION_CHOICE
     * 
     * @param convertView parent view
     * @param position position in list
     */
    public void recycle(View convertView, int position) {
        touchListener.reloadChoiceStateInView(convertView.findViewById(swipeFrontView), position);
    }

    /**
     * Get if item is selected
     * 
     * @param position position in list
     * @return
     */
    public boolean isChecked(int position) {
        return touchListener.isChecked(position);
    }

    /**
     * Get positions selected
     * 
     * @return
     */
    public List<Integer> getPositionsSelected() {
        return touchListener.getPositionsSelected();
    }

    /**
     * Count selected
     * 
     * @return
     */
    public int getCountSelected() {
        return touchListener.getCountSelected();
    }

    /**
     * Unselected choice state in item
     */
    public void unselectedChoiceStates() {
        touchListener.unselectedChoiceStates();
    }

    /**
     * @param onDropDownListener
     */
    public void setOnDropDownListener(OnDropDownListener onDropDownListener) {
        this.onDropDownListener = onDropDownListener;
    }

    /**
     * @param onBottomListener
     */
    public void setOnBottomListener(OnClickListener onBottomListener) {
        footerButton.setOnClickListener(onBottomListener);
    }

    /**
     * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (isDropDownStyle) {
            setSecondPositionVisible();
        }
        touchListener.resetItems();
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                onListChanged();
                touchListener.resetItems();
            }
        });
    }

    /**
     * Dismiss item
     * 
     * @param position Position that you want open
     */
    public void dismiss(int position) {
        int height = touchListener.dismiss(position);
        if (height > 0) {
            touchListener.handlerPendingDismisses(height);
        } else {
            int[] dismissPositions = new int[1];
            dismissPositions[0] = position;
            onDismiss(dismissPositions);
            touchListener.resetPendingDismisses();
        }
    }

    /**
     * Dismiss items selected
     */
    public void dismissSelected() {
        List<Integer> list = touchListener.getPositionsSelected();
        int[] dismissPositions = new int[list.size()];
        int height = 0;
        for (int i = 0; i < list.size(); i++) {
            int position = list.get(i);
            dismissPositions[i] = position;
            int auxHeight = touchListener.dismiss(position);
            if (auxHeight > 0) {
                height = auxHeight;
            }
        }
        if (height > 0) {
            touchListener.handlerPendingDismisses(height);
        } else {
            onDismiss(dismissPositions);
            touchListener.resetPendingDismisses();
        }
        touchListener.returnOldActions();
    }

    /**
     * Open ListView's item
     * 
     * @param position Position that you want open
     */
    public void openAnimate(int position) {
        touchListener.openAnimate(position);
    }

    /**
     * Close ListView's item
     * 
     * @param position Position that you want open
     */
    public void closeAnimate(int position) {
        touchListener.closeAnimate(position);
    }

    /**
     * Notifies onDismiss
     * 
     * @param reverseSortedPositions All dismissed positions
     */
    protected void onDismiss(int[] reverseSortedPositions) {
        if (swipeListViewListener != null) {
            swipeListViewListener.onDismiss(reverseSortedPositions);
        }
    }

    /**
     * Start open item
     * 
     * @param position list item
     * @param action current action
     * @param right to right
     */
    protected void onStartOpen(int position, int action, boolean right) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onStartOpen(position, action, right);
        }
    }

    /**
     * Start close item
     * 
     * @param position list item
     * @param right
     */
    protected void onStartClose(int position, boolean right) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onStartClose(position, right);
        }
    }

    /**
     * Notifies onClickFrontView
     * 
     * @param position item clicked
     */
    protected void onClickFrontView(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClickFrontView(position);
        }
    }

    /**
     * Notifies onClickBackView
     * 
     * @param position back item clicked
     */
    protected void onClickBackView(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClickBackView(position);
        }
    }

    /**
     * Notifies onOpened
     * 
     * @param position Item opened
     * @param toRight If should be opened toward the right
     */
    protected void onOpened(int position, boolean toRight) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onOpened(position, toRight);
        }
    }

    /**
     * Notifies onClosed
     * 
     * @param position Item closed
     * @param fromRight If open from right
     */
    protected void onClosed(int position, boolean fromRight) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClosed(position, fromRight);
        }
    }

    /**
     * Notifies onChoiceChanged
     * 
     * @param position position that choice
     * @param selected if item is selected or not
     */
    protected void onChoiceChanged(int position, boolean selected) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onChoiceChanged(position, selected);
        }
    }

    /**
     * User start choice items
     */
    protected void onChoiceStarted() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceStarted();
        }
    }

    /**
     * User end choice items
     */
    protected void onChoiceEnded() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceEnded();
        }
    }

    /**
     * User is in first item of list
     */
    protected void onFirstListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onFirstListItem();
        }
    }

    /**
     * User is in last item of list
     */
    protected void onLastListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onLastListItem();
        }
    }

    /**
     * Notifies onListChanged
     */
    protected void onListChanged() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onListChanged();
        }
    }

    /**
     * Notifies onMove
     * 
     * @param position Item moving
     * @param x Current position
     */
    protected void onMove(int position, float x) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onMove(position, x);
        }
    }

    protected int changeSwipeMode(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            return swipeListViewListener.onChangeSwipeMode(position);
        }
        return SWIPE_MODE_DEFAULT;
    }

    /**
     * Sets the Listener
     * 
     * @param swipeListViewListener Listener
     */
    public void setSwipeListViewListener(SwipeListViewListener swipeListViewListener) {
        this.swipeListViewListener = swipeListViewListener;
    }

    /**
     * Resets scrolling
     */
    public void resetScrolling() {
        touchState = TOUCH_STATE_REST;
    }

    /**
     * Set offset on right
     * 
     * @param offsetRight Offset
     */
    public void setOffsetRight(float offsetRight) {
        touchListener.setRightOffset(offsetRight);
    }

    /**
     * Set offset on left
     * 
     * @param offsetLeft Offset
     */
    public void setOffsetLeft(float offsetLeft) {
        touchListener.setLeftOffset(offsetLeft);
    }

    /**
     * Set if all items opened will be closed when the user moves the ListView
     * 
     * @param swipeCloseAllItemsWhenMoveList
     */
    public void setSwipeCloseAllItemsWhenMoveList(boolean swipeCloseAllItemsWhenMoveList) {
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
    }

    /**
     * Sets if the user can open an item with long pressing on cell
     * 
     * @param swipeOpenOnLongPress
     */
    public void setSwipeOpenOnLongPress(boolean swipeOpenOnLongPress) {
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
    }

    /**
     * Set swipe mode
     * 
     * @param swipeMode
     */
    public void setSwipeMode(int swipeMode) {
        touchListener.setSwipeMode(swipeMode);
    }

    /**
     * Return action on left
     * 
     * @return Action
     */
    public int getSwipeActionLeft() {
        return touchListener.getSwipeActionLeft();
    }

    /**
     * Set action on left
     * 
     * @param swipeActionLeft Action
     */
    public void setSwipeActionLeft(int swipeActionLeft) {
        touchListener.setSwipeActionLeft(swipeActionLeft);
    }

    /**
     * Return action on right
     * 
     * @return Action
     */
    public int getSwipeActionRight() {
        return touchListener.getSwipeActionRight();
    }

    /**
     * Set action on right
     * 
     * @param swipeActionRight Action
     */
    public void setSwipeActionRight(int swipeActionRight) {
        touchListener.setSwipeActionRight(swipeActionRight);
    }

    /**
     * Sets animation time when user drops cell
     * 
     * @param animationTime milliseconds
     */
    public void setAnimationTime(long animationTime) {
        touchListener.setAnimationTime(animationTime);
    }

    /**
     * @see android.widget.ListView#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        final float x = ev.getX();
        final float y = ev.getY();

        if (isEnabled() && touchListener.isSwipeEnabled()) {

            if (touchState == TOUCH_STATE_SCROLLING_X) {
                return touchListener.onTouch(this, ev);
            }

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    checkInMoving(x, y);
                    return touchState == TOUCH_STATE_SCROLLING_Y;
                case MotionEvent.ACTION_DOWN:
                    touchListener.onTouch(this, ev);
                    touchState = TOUCH_STATE_REST;
                    lastMotionX = x;
                    lastMotionY = y;
                    return false;
                case MotionEvent.ACTION_CANCEL:
                    touchState = TOUCH_STATE_REST;
                    break;
                case MotionEvent.ACTION_UP:
                    touchListener.onTouch(this, ev);
                    return touchState == TOUCH_STATE_SCROLLING_Y;
                default:
                    break;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Check if the user is moving the cell
     * 
     * @param x Position X
     * @param y Position Y
     */
    private void checkInMoving(float x, float y) {
        final int xDiff = (int) Math.abs(x - lastMotionX);
        final int yDiff = (int) Math.abs(y - lastMotionY);

        final int touchSlop = this.touchSlop;
        boolean xMoved = xDiff > touchSlop;
        boolean yMoved = yDiff > touchSlop;

        if (xMoved) {
            touchState = TOUCH_STATE_SCROLLING_X;
            lastMotionX = x;
            lastMotionY = y;
        }

        if (yMoved) {
            touchState = TOUCH_STATE_SCROLLING_Y;
            lastMotionX = x;
            lastMotionY = y;
        }
    }

    /**
     * Close all opened items
     */
    public void closeOpenedItems() {
        touchListener.closeOpenedItems();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (isDropDownStyle) {
            currentScrollState = scrollState;

            if (currentScrollState == SCROLL_STATE_IDLE) {
                hasReachedTop = false;
            }
        }

        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (isDropDownStyle) {
            if (currentScrollState == SCROLL_STATE_TOUCH_SCROLL
                    && currentHeaderStatus != HEADER_STATUS_LOADING) {
                /**
                 * when state of ListView is SCROLL_STATE_TOUCH_SCROLL(ListView
                 * is scrolling and finger is on screen) and header status is
                 * not HEADER_STATUS_LOADING
                 * <ul>
                 * if header layout is visiable,
                 * <li>if height of header is higher than a fixed value, then
                 * set header status to HEADER_STATUS_RELEASE_TO_LOAD.</li>
                 * <li>else set header status to
                 * HEADER_STATUS_DROP_DOWN_TO_LOAD.</li>
                 * </ul>
                 * <ul>
                 * if header layout is not visiable,
                 * <li>set header status to HEADER_STATUS_CLICK_TO_LOAD.</li>
                 * </ul>
                 */
                if (firstVisibleItem == 0) {
                    headerImage.setVisibility(View.VISIBLE);
                    int pointBottom = headerOriginalHeight + headerReleaseMinDistance;
                    if (headerLayout.getBottom() >= pointBottom) {
                        setHeaderStatusReleaseToLoad();
                    } else if (headerLayout.getBottom() < pointBottom) {
                        setHeaderStatusDropDownToLoad();
                    }
                } else {
                    setHeaderStatusClickToLoad();
                }
            } else if (currentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0
                    && currentHeaderStatus != HEADER_STATUS_LOADING) {
                /**
                 * when state of ListView is SCROLL_STATE_FLING(ListView is
                 * scrolling but finger has leave screen) and first item(header
                 * layout) is visible and header status is not
                 * HEADER_STATUS_LOADING, then hide first item, set second item
                 * visible and set hasReachedTop true.
                 */
                setSecondPositionVisible();
                hasReachedTop = true;
            } else if (currentScrollState == SCROLL_STATE_FLING && hasReachedTop) {
                /**
                 * when state of ListView is SCROLL_STATE_FLING(ListView is
                 * scrolling but finger has leave screen) and hasReachedTop is
                 * true(it's because flick back), then hide first item, set
                 * second item visible.
                 */
                setSecondPositionVisible();
            }
        }

        // if isOnBottomStyle and isAutoLoadOnBottom and hasMore, then call
        // onBottom function auto
        if (isOnBottomStyle && isAutoLoadOnBottom && hasMore) {
            if (firstVisibleItem > 0 && totalItemCount > 0
                    && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                onBottom();
            }
        }
        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        // TODO Auto-generated method stub
        onScrollListener = l;
    }

    /**
     * drop down begin, adjust view status
     */
    private void onDropDownBegin() {
        if (isDropDownStyle) {
            setHeaderStatusLoading();
        }
    }

    /**
     * on drop down loading, you can call it by manual, but you should manual
     * call onBottomComplete at the same time.
     */
    public void onDropDown() {
        if (currentHeaderStatus != HEADER_STATUS_LOADING && isDropDownStyle
                && onDropDownListener != null) {
            onDropDownBegin();
            onDropDownListener.onDropDown();
        }
    }

    /**
     * drop down complete, restore view status
     * 
     * @param secondText display below header text, if null, not display
     */
    public void onDropDownComplete(CharSequence secondText) {
        if (isDropDownStyle) {
            setHeaderSecondText(secondText);
            onDropDownComplete();
        }
    }

    /**
     * set header second text
     * 
     * @param secondText secondText display below header text, if null, not
     *            display
     */
    public void setHeaderSecondText(CharSequence secondText) {
        if (isDropDownStyle) {
            if (secondText == null) {
                headerSecondText.setVisibility(View.GONE);
            } else {
                headerSecondText.setVisibility(View.VISIBLE);
                headerSecondText.setText(secondText);
            }
        }
    }

    /**
     * drop down complete, restore view status
     */
    public void onDropDownComplete() {
        if (isDropDownStyle) {
            setHeaderStatusClickToLoad();

            if (headerLayout.getBottom() > 0) {
                invalidateViews();
                setSecondPositionVisible();
            }
        }
    }

    /**
     * on bottom begin, adjust view status
     */
    private void onBottomBegin() {
        if (isOnBottomStyle) {
            if (isShowFooterProgressBar) {
                footerProgressBar.setVisibility(View.VISIBLE);
            }
            footerButton.setText(footerLoadingText);
            footerButton.setEnabled(false);
        }
    }

    /**
     * on bottom loading, you can call it by manual, but you should manual call
     * onBottomComplete at the same time.
     */
    public void onBottom() {
        if (isOnBottomStyle && !isOnBottomLoading) {
            isOnBottomLoading = true;
            onBottomBegin();
            footerButton.performClick();
        }
    }

    /**
     * on bottom load complete, restore view status
     */
    public void onBottomComplete() {
        if (isOnBottomStyle) {
            if (isShowFooterProgressBar) {
                footerProgressBar.setVisibility(View.GONE);
            }
            footerButton.setEnabled(true);
            if (!hasMore) {
                footerButton.setText(footerNoMoreText);
            } else {
                footerButton.setText(footerDefaultText);
            }
            isOnBottomLoading = false;
        }
    }

    /**
     * OnDropDownListener, called when header released
     * 
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
     *         2012-5-31
     */
    public interface OnDropDownListener {

        /**
         * called when header released
         */
        public void onDropDown();
    }

    /**
     * set second position visible(index is 1), because first position is header
     * layout
     */
    public void setSecondPositionVisible() {
        if (getAdapter() != null && getAdapter().getCount() > 0 && getFirstVisiblePosition() == 0) {
            setSelection(1);
        }
    }

    /**
     * set whether has more. if hasMore is false, onBottm will not be called
     * when listView scroll to bottom
     * 
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * get whether has more
     * 
     * @return
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * get header layout view
     * 
     * @return
     */
    public RelativeLayout getHeaderLayout() {
        return headerLayout;
    }

    /**
     * get footer layout view
     * 
     * @return
     */
    public RelativeLayout getFooterLayout() {
        return footerLayout;
    }

    /**
     * get rate about drop down distance and header padding top when drop down
     * 
     * @return headerPaddingTopRate
     */
    public float getHeaderPaddingTopRate() {
        return headerPaddingTopRate;
    }

    /**
     * set rate about drop down distance and header padding top when drop down
     * 
     * @param headerPaddingTopRate
     */
    public void setHeaderPaddingTopRate(float headerPaddingTopRate) {
        this.headerPaddingTopRate = headerPaddingTopRate;
    }

    /**
     * get min distance which header can release to loading
     * 
     * @return headerReleaseMinDistance
     */
    public int getHeaderReleaseMinDistance() {
        return headerReleaseMinDistance;
    }

    /**
     * set min distance which header can release to loading
     * 
     * @param headerReleaseMinDistance
     */
    public void setHeaderReleaseMinDistance(int headerReleaseMinDistance) {
        this.headerReleaseMinDistance = headerReleaseMinDistance;
    }

    /***
     * get header default text, default is
     * R.string.drop_down_list_header_default_text
     * 
     * @return
     */
    public String getHeaderDefaultText() {
        return headerDefaultText;
    }

    /**
     * set header default text, default is
     * R.string.drop_down_list_header_default_text
     * 
     * @param headerDefaultText
     */
    public void setHeaderDefaultText(String headerDefaultText) {
        this.headerDefaultText = headerDefaultText;
        if (headerText != null && currentHeaderStatus == HEADER_STATUS_CLICK_TO_LOAD) {
            headerText.setText(headerDefaultText);
        }
    }

    /**
     * get header pull text, default is R.string.drop_down_list_header_pull_text
     * 
     * @return
     */
    public String getHeaderPullText() {
        return headerPullText;
    }

    /**
     * set header pull text, default is R.string.drop_down_list_header_pull_text
     * 
     * @param headerPullText
     */
    public void setHeaderPullText(String headerPullText) {
        this.headerPullText = headerPullText;
    }

    /**
     * get header release text, default is
     * R.string.drop_down_list_header_release_text
     * 
     * @return
     */
    public String getHeaderReleaseText() {
        return headerReleaseText;
    }

    /**
     * set header release text, default is
     * R.string.drop_down_list_header_release_text
     * 
     * @param headerReleaseText
     */
    public void setHeaderReleaseText(String headerReleaseText) {
        this.headerReleaseText = headerReleaseText;
    }

    /**
     * get header loading text, default is
     * R.string.drop_down_list_header_loading_text
     * 
     * @return
     */
    public String getHeaderLoadingText() {
        return headerLoadingText;
    }

    /**
     * set header loading text, default is
     * R.string.drop_down_list_header_loading_text
     * 
     * @param headerLoadingText
     */
    public void setHeaderLoadingText(String headerLoadingText) {
        this.headerLoadingText = headerLoadingText;
    }

    /**
     * get footer default text, default is
     * R.string.drop_down_list_footer_default_text
     * 
     * @return
     */
    public String getFooterDefaultText() {
        return footerDefaultText;
    }

    /**
     * set footer default text, default is
     * R.string.drop_down_list_footer_default_text
     * 
     * @param footerDefaultText
     */
    public void setFooterDefaultText(String footerDefaultText) {
        this.footerDefaultText = footerDefaultText;
        if (footerButton != null && footerButton.isEnabled()) {
            footerButton.setText(footerDefaultText);
        }
    }

    /**
     * get footer loading text, default is
     * R.string.drop_down_list_footer_loading_text
     * 
     * @return
     */
    public String getFooterLoadingText() {
        return footerLoadingText;
    }

    /**
     * set footer loading text, default is
     * R.string.drop_down_list_footer_loading_text
     * 
     * @param footerLoadingText
     */
    public void setFooterLoadingText(String footerLoadingText) {
        this.footerLoadingText = footerLoadingText;
    }

    /**
     * get footer no more text, default is
     * R.string.drop_down_list_footer_no_more_text
     * 
     * @return
     */
    public String getFooterNoMoreText() {
        return footerNoMoreText;
    }

    /**
     * set footer no more text, default is
     * R.string.drop_down_list_footer_no_more_text
     * 
     * @param footerNoMoreText
     */
    public void setFooterNoMoreText(String footerNoMoreText) {
        this.footerNoMoreText = footerNoMoreText;
    }

    /** status which you can click to load, init satus **/
    public static final int HEADER_STATUS_CLICK_TO_LOAD = 1;
    /**
     * status which you can drop down and then release to excute
     * onDropDownListener, when height of header layout lower than a value
     **/
    public static final int HEADER_STATUS_DROP_DOWN_TO_LOAD = 2;
    /**
     * status which you can release to excute onDropDownListener, when height of
     * header layout higher than a value
     **/
    public static final int HEADER_STATUS_RELEASE_TO_LOAD = 3;
    /** status which is loading **/
    public static final int HEADER_STATUS_LOADING = 4;

    /**
     * set header status to {@link #HEADER_STATUS_CLICK_TO_LOAD}
     */
    private void setHeaderStatusClickToLoad() {
        if (currentHeaderStatus != HEADER_STATUS_CLICK_TO_LOAD) {
            resetHeaderPadding();

            headerImage.clearAnimation();
            headerImage.setVisibility(View.GONE);
            headerProgressBar.setVisibility(View.GONE);
            headerText.setText(headerDefaultText);

            currentHeaderStatus = HEADER_STATUS_CLICK_TO_LOAD;
        }
    }

    /**
     * set header status to {@link #HEADER_STATUS_DROP_DOWN_TO_LOAD}
     */
    private void setHeaderStatusDropDownToLoad() {
        if (currentHeaderStatus != HEADER_STATUS_DROP_DOWN_TO_LOAD) {
            headerImage.setVisibility(View.VISIBLE);
            if (currentHeaderStatus != HEADER_STATUS_CLICK_TO_LOAD) {
                headerImage.clearAnimation();
                headerImage.startAnimation(reverseFlipAnimation);
            }
            headerProgressBar.setVisibility(View.GONE);
            headerText.setText(headerPullText);

            if (isVerticalFadingEdgeEnabled()) {
                setVerticalScrollBarEnabled(false);
            }

            currentHeaderStatus = HEADER_STATUS_DROP_DOWN_TO_LOAD;
        }
    }

    /**
     * set header status to {@link #HEADER_STATUS_RELEASE_TO_LOAD}
     */
    private void setHeaderStatusReleaseToLoad() {
        if (currentHeaderStatus != HEADER_STATUS_RELEASE_TO_LOAD) {
            headerImage.setVisibility(View.VISIBLE);
            headerImage.clearAnimation();
            headerImage.startAnimation(flipAnimation);
            headerProgressBar.setVisibility(View.GONE);
            headerText.setText(headerReleaseText);

            currentHeaderStatus = HEADER_STATUS_RELEASE_TO_LOAD;
        }
    }

    /**
     * set header status to {@link #HEADER_STATUS_LOADING}
     */
    private void setHeaderStatusLoading() {
        if (currentHeaderStatus != HEADER_STATUS_LOADING) {
            resetHeaderPadding();

            headerImage.setVisibility(View.GONE);
            headerImage.clearAnimation();
            headerProgressBar.setVisibility(View.VISIBLE);
            headerText.setText(headerLoadingText);

            currentHeaderStatus = HEADER_STATUS_LOADING;
            setSelection(0);
        }
    }

    /**
     * adjust header padding according to motion event
     * 
     * @param ev
     */
    private void adjustHeaderPadding(MotionEvent ev) {
        // adjust header padding according to motion event history
        int pointerCount = ev.getHistorySize();
        if (isVerticalFadingEdgeEnabled()) {
            setVerticalScrollBarEnabled(false);
        }
        for (int i = 0; i < pointerCount; i++) {
            if (currentHeaderStatus == HEADER_STATUS_DROP_DOWN_TO_LOAD
                    || currentHeaderStatus == HEADER_STATUS_RELEASE_TO_LOAD) {
                headerLayout
                        .setPadding(
                                headerLayout.getPaddingLeft(),
                                (int) (((ev.getHistoricalY(i) - actionDownPointY) - headerOriginalHeight) / headerPaddingTopRate),
                                headerLayout.getPaddingRight(), headerLayout.getPaddingBottom());
            }
        }
    }

    /**
     * reset header padding
     */
    private void resetHeaderPadding() {
        headerLayout.setPadding(headerLayout.getPaddingLeft(), headerOriginalTopPadding,
                headerLayout.getPaddingRight(), headerLayout.getPaddingBottom());
    }

    /**
     * measure header layout
     * 
     * @param child
     */
    private void measureHeaderLayout(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDropDownStyle) {
            return super.onTouchEvent(event);
        }

        hasReachedTop = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                actionDownPointY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                adjustHeaderPadding(event);
                break;
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                /**
                 * set status when finger leave screen if first item visible and
                 * header status is not HEADER_STATUS_LOADING
                 * <ul>
                 * <li>if current header status is
                 * HEADER_STATUS_RELEASE_TO_LOAD, call onDropDown.</li>
                 * <li>if current header status is
                 * HEADER_STATUS_DROP_DOWN_TO_LOAD, then set header status to
                 * HEADER_STATUS_CLICK_TO_LOAD and hide header layout.</li>
                 * </ul>
                 */
                if (getFirstVisiblePosition() == 0 && currentHeaderStatus != HEADER_STATUS_LOADING) {
                    switch (currentHeaderStatus) {
                        case HEADER_STATUS_RELEASE_TO_LOAD:
                            onDropDown();
                            break;
                        case HEADER_STATUS_DROP_DOWN_TO_LOAD:
                            setHeaderStatusClickToLoad();
                            setSecondPositionVisible();
                            break;
                        case HEADER_STATUS_CLICK_TO_LOAD:
                        default:
                            break;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
