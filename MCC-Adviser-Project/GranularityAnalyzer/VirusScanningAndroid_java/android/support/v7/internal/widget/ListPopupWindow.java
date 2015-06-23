package android.support.v7.internal.widget;

import android.content.*;
import android.graphics.drawable.*;
import android.os.*;
import android.database.*;
import android.graphics.*;
import android.support.v7.appcompat.*;
import java.util.*;
import android.util.*;
import android.widget.*;
import android.view.*;

public class ListPopupWindow
{
    private static final boolean DEBUG = false;
    private static final int EXPAND_LIST_TIMEOUT = 250;
    public static final int FILL_PARENT = -1;
    public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
    public static final int INPUT_METHOD_NEEDED = 1;
    public static final int INPUT_METHOD_NOT_NEEDED = 2;
    public static final int POSITION_PROMPT_ABOVE = 0;
    public static final int POSITION_PROMPT_BELOW = 1;
    private static final String TAG = "ListPopupWindow";
    public static final int WRAP_CONTENT = -2;
    private ListAdapter mAdapter;
    private Context mContext;
    private boolean mDropDownAlwaysVisible;
    private View mDropDownAnchorView;
    private int mDropDownHeight;
    private int mDropDownHorizontalOffset;
    private DropDownListView mDropDownList;
    private Drawable mDropDownListHighlight;
    private int mDropDownVerticalOffset;
    private boolean mDropDownVerticalOffsetSet;
    private int mDropDownWidth;
    private boolean mForceIgnoreOutsideTouch;
    private Handler mHandler;
    private final ListSelectorHider mHideSelector;
    private AdapterView$OnItemClickListener mItemClickListener;
    private AdapterView$OnItemSelectedListener mItemSelectedListener;
    private int mLayoutDirection;
    int mListItemExpandMaximum;
    private boolean mModal;
    private DataSetObserver mObserver;
    private PopupWindow mPopup;
    private int mPromptPosition;
    private View mPromptView;
    private final ResizePopupRunnable mResizePopupRunnable;
    private final PopupScrollListener mScrollListener;
    private Runnable mShowDropDownRunnable;
    private Rect mTempRect;
    private final PopupTouchInterceptor mTouchInterceptor;
    
    public ListPopupWindow(final Context context) {
        this(context, null, R.attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(final Context context, final AttributeSet set) {
        this(context, set, R.attr.listPopupWindowStyle);
    }
    
    public ListPopupWindow(final Context mContext, final AttributeSet set, final int n) {
        super();
        this.mDropDownHeight = -2;
        this.mDropDownWidth = -2;
        this.mDropDownAlwaysVisible = false;
        this.mForceIgnoreOutsideTouch = false;
        this.mListItemExpandMaximum = Integer.MAX_VALUE;
        this.mPromptPosition = 0;
        this.mResizePopupRunnable = new ResizePopupRunnable();
        this.mTouchInterceptor = new PopupTouchInterceptor();
        this.mScrollListener = new PopupScrollListener();
        this.mHideSelector = new ListSelectorHider();
        this.mHandler = new Handler();
        this.mTempRect = new Rect();
        this.mContext = mContext;
        (this.mPopup = new PopupWindow(mContext, set, n)).setInputMethodMode(1);
        final Locale locale = this.mContext.getResources().getConfiguration().locale;
    }
    
    private int buildDropDown() {
        int n;
        if (this.mDropDownList == null) {
            final Context mContext = this.mContext;
            this.mShowDropDownRunnable = new Runnable() {
                @Override
                public void run() {
                    final View anchorView = ListPopupWindow.this.getAnchorView();
                    if (anchorView != null && anchorView.getWindowToken() != null) {
                        ListPopupWindow.this.show();
                    }
                }
            };
            this.mDropDownList = new DropDownListView(mContext, !this.mModal);
            if (this.mDropDownListHighlight != null) {
                this.mDropDownList.setSelector(this.mDropDownListHighlight);
            }
            this.mDropDownList.setAdapter(this.mAdapter);
            this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
            this.mDropDownList.setFocusable(true);
            this.mDropDownList.setFocusableInTouchMode(true);
            this.mDropDownList.setOnItemSelectedListener((AdapterView$OnItemSelectedListener)new AdapterView$OnItemSelectedListener() {
                public void onItemSelected(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
                    if (n != -1) {
                        final DropDownListView access$700 = ListPopupWindow.this.mDropDownList;
                        if (access$700 != null) {
                            access$700.mListSelectionHidden = false;
                        }
                    }
                }
                
                public void onNothingSelected(final AdapterView<?> adapterView) {
                }
            });
            this.mDropDownList.setOnScrollListener((AbsListView$OnScrollListener)this.mScrollListener);
            if (this.mItemSelectedListener != null) {
                this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener);
            }
            Object mDropDownList = this.mDropDownList;
            final View mPromptView = this.mPromptView;
            n = 0;
            if (mPromptView != null) {
                final LinearLayout linearLayout = new LinearLayout(mContext);
                linearLayout.setOrientation(1);
                final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(-1, 0, 1.0f);
                switch (this.mPromptPosition) {
                    default: {
                        Log.e("ListPopupWindow", "Invalid hint position " + this.mPromptPosition);
                        break;
                    }
                    case 1: {
                        linearLayout.addView((View)mDropDownList, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                        linearLayout.addView(mPromptView);
                        break;
                    }
                    case 0: {
                        linearLayout.addView(mPromptView);
                        linearLayout.addView((View)mDropDownList, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                        break;
                    }
                }
                mPromptView.measure(View$MeasureSpec.makeMeasureSpec(this.mDropDownWidth, Integer.MIN_VALUE), 0);
                final LinearLayout$LayoutParams linearLayout$LayoutParams2 = (LinearLayout$LayoutParams)mPromptView.getLayoutParams();
                n = mPromptView.getMeasuredHeight() + linearLayout$LayoutParams2.topMargin + linearLayout$LayoutParams2.bottomMargin;
                mDropDownList = linearLayout;
            }
            this.mPopup.setContentView((View)mDropDownList);
        }
        else {
            final ViewGroup viewGroup = (ViewGroup)this.mPopup.getContentView();
            final View mPromptView2 = this.mPromptView;
            n = 0;
            if (mPromptView2 != null) {
                final LinearLayout$LayoutParams linearLayout$LayoutParams3 = (LinearLayout$LayoutParams)mPromptView2.getLayoutParams();
                n = mPromptView2.getMeasuredHeight() + linearLayout$LayoutParams3.topMargin + linearLayout$LayoutParams3.bottomMargin;
            }
        }
        final Drawable background = this.mPopup.getBackground();
        int n2;
        if (background != null) {
            background.getPadding(this.mTempRect);
            n2 = this.mTempRect.top + this.mTempRect.bottom;
            if (!this.mDropDownVerticalOffsetSet) {
                this.mDropDownVerticalOffset = -this.mTempRect.top;
            }
        }
        else {
            this.mTempRect.setEmpty();
            n2 = 0;
        }
        final int maxAvailableHeight = this.getMaxAvailableHeight(this.getAnchorView(), this.mDropDownVerticalOffset, this.mPopup.getInputMethodMode() == 2);
        if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1) {
            return maxAvailableHeight + n2;
        }
        int n3 = 0;
        switch (this.mDropDownWidth) {
            default: {
                n3 = View$MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
                break;
            }
            case -2: {
                n3 = View$MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), Integer.MIN_VALUE);
                break;
            }
            case -1: {
                n3 = View$MeasureSpec.makeMeasureSpec(this.mContext.getResources().getDisplayMetrics().widthPixels - (this.mTempRect.left + this.mTempRect.right), 1073741824);
                break;
            }
        }
        final int measureHeightOfChildrenCompat = this.mDropDownList.measureHeightOfChildrenCompat(n3, 0, -1, maxAvailableHeight - n, -1);
        if (measureHeightOfChildrenCompat > 0) {
            n += n2;
        }
        return measureHeightOfChildrenCompat + n;
    }
    
    private void removePromptView() {
        if (this.mPromptView != null) {
            final ViewParent parent = this.mPromptView.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(this.mPromptView);
            }
        }
    }
    
    public void clearListSelection() {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (mDropDownList != null) {
            mDropDownList.mListSelectionHidden = true;
            mDropDownList.requestLayout();
        }
    }
    
    public void dismiss() {
        this.mPopup.dismiss();
        this.removePromptView();
        this.mPopup.setContentView((View)null);
        this.mDropDownList = null;
        this.mHandler.removeCallbacks((Runnable)this.mResizePopupRunnable);
    }
    
    public View getAnchorView() {
        return this.mDropDownAnchorView;
    }
    
    public int getAnimationStyle() {
        return this.mPopup.getAnimationStyle();
    }
    
    public Drawable getBackground() {
        return this.mPopup.getBackground();
    }
    
    public int getHeight() {
        return this.mDropDownHeight;
    }
    
    public int getHorizontalOffset() {
        return this.mDropDownHorizontalOffset;
    }
    
    public int getInputMethodMode() {
        return this.mPopup.getInputMethodMode();
    }
    
    public ListView getListView() {
        return this.mDropDownList;
    }
    
    public int getMaxAvailableHeight(final View view, final int n, final boolean b) {
        final Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        final int[] array = new int[2];
        view.getLocationOnScreen(array);
        int n2 = rect.bottom;
        if (b) {
            n2 = view.getContext().getResources().getDisplayMetrics().heightPixels;
        }
        int max = Math.max(n2 - (array[1] + view.getHeight()) - n, n + (array[1] - rect.top));
        if (this.mPopup.getBackground() != null) {
            this.mPopup.getBackground().getPadding(this.mTempRect);
            max -= this.mTempRect.top + this.mTempRect.bottom;
        }
        return max;
    }
    
    public int getPromptPosition() {
        return this.mPromptPosition;
    }
    
    public Object getSelectedItem() {
        if (!this.isShowing()) {
            return null;
        }
        return this.mDropDownList.getSelectedItem();
    }
    
    public long getSelectedItemId() {
        if (!this.isShowing()) {
            return Long.MIN_VALUE;
        }
        return this.mDropDownList.getSelectedItemId();
    }
    
    public int getSelectedItemPosition() {
        if (!this.isShowing()) {
            return -1;
        }
        return this.mDropDownList.getSelectedItemPosition();
    }
    
    public View getSelectedView() {
        if (!this.isShowing()) {
            return null;
        }
        return this.mDropDownList.getSelectedView();
    }
    
    public int getSoftInputMode() {
        return this.mPopup.getSoftInputMode();
    }
    
    public int getVerticalOffset() {
        if (!this.mDropDownVerticalOffsetSet) {
            return 0;
        }
        return this.mDropDownVerticalOffset;
    }
    
    public int getWidth() {
        return this.mDropDownWidth;
    }
    
    public boolean isDropDownAlwaysVisible() {
        return this.mDropDownAlwaysVisible;
    }
    
    public boolean isInputMethodNotNeeded() {
        return this.mPopup.getInputMethodMode() == 2;
    }
    
    public boolean isModal() {
        return this.mModal;
    }
    
    public boolean isShowing() {
        return this.mPopup.isShowing();
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (this.isShowing() && n != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || (n != 66 && n != 23))) {
            final int selectedItemPosition = this.mDropDownList.getSelectedItemPosition();
            boolean b;
            if (!this.mPopup.isAboveAnchor()) {
                b = true;
            }
            else {
                b = false;
            }
            final ListAdapter mAdapter = this.mAdapter;
            int access$600 = Integer.MAX_VALUE;
            int access$2 = Integer.MIN_VALUE;
            if (mAdapter != null) {
                final boolean allItemsEnabled = mAdapter.areAllItemsEnabled();
                if (allItemsEnabled) {
                    access$600 = 0;
                }
                else {
                    access$600 = this.mDropDownList.lookForSelectablePosition(0, true);
                }
                if (allItemsEnabled) {
                    access$2 = -1 + mAdapter.getCount();
                }
                else {
                    access$2 = this.mDropDownList.lookForSelectablePosition(-1 + mAdapter.getCount(), false);
                }
            }
            if ((b && n == 19 && selectedItemPosition <= access$600) || (!b && n == 20 && selectedItemPosition >= access$2)) {
                this.clearListSelection();
                this.mPopup.setInputMethodMode(1);
                this.show();
            }
            else {
                this.mDropDownList.mListSelectionHidden = false;
                if (this.mDropDownList.onKeyDown(n, keyEvent)) {
                    this.mPopup.setInputMethodMode(2);
                    this.mDropDownList.requestFocusFromTouch();
                    this.show();
                    switch (n) {
                        case 19:
                        case 20:
                        case 23:
                        case 66: {
                            break;
                        }
                        default: {
                            return false;
                        }
                    }
                }
                else if (b && n == 20) {
                    if (selectedItemPosition == access$2) {
                        return true;
                    }
                    return false;
                }
                else {
                    if (!b && n == 19 && selectedItemPosition == access$600) {
                        return true;
                    }
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (this.isShowing() && this.mDropDownList.getSelectedItemPosition() >= 0) {
            final boolean onKeyUp = this.mDropDownList.onKeyUp(n, keyEvent);
            if (onKeyUp) {
                switch (n) {
                    case 23:
                    case 66: {
                        this.dismiss();
                        return onKeyUp;
                    }
                }
            }
            return onKeyUp;
        }
        return false;
    }
    
    public boolean performItemClick(final int n) {
        if (this.isShowing()) {
            if (this.mItemClickListener != null) {
                final DropDownListView mDropDownList = this.mDropDownList;
                this.mItemClickListener.onItemClick((AdapterView)mDropDownList, mDropDownList.getChildAt(n - mDropDownList.getFirstVisiblePosition()), n, mDropDownList.getAdapter().getItemId(n));
            }
            return true;
        }
        return false;
    }
    
    public void postShow() {
        this.mHandler.post(this.mShowDropDownRunnable);
    }
    
    public void setAdapter(final ListAdapter mAdapter) {
        if (this.mObserver == null) {
            this.mObserver = new PopupDataSetObserver();
        }
        else if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
        }
        this.mAdapter = mAdapter;
        if (this.mAdapter != null) {
            mAdapter.registerDataSetObserver(this.mObserver);
        }
        if (this.mDropDownList != null) {
            this.mDropDownList.setAdapter(this.mAdapter);
        }
    }
    
    public void setAnchorView(final View mDropDownAnchorView) {
        this.mDropDownAnchorView = mDropDownAnchorView;
    }
    
    public void setAnimationStyle(final int animationStyle) {
        this.mPopup.setAnimationStyle(animationStyle);
    }
    
    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        this.mPopup.setBackgroundDrawable(backgroundDrawable);
    }
    
    public void setContentWidth(final int width) {
        final Drawable background = this.mPopup.getBackground();
        if (background != null) {
            background.getPadding(this.mTempRect);
            this.mDropDownWidth = width + (this.mTempRect.left + this.mTempRect.right);
            return;
        }
        this.setWidth(width);
    }
    
    public void setDropDownAlwaysVisible(final boolean mDropDownAlwaysVisible) {
        this.mDropDownAlwaysVisible = mDropDownAlwaysVisible;
    }
    
    public void setForceIgnoreOutsideTouch(final boolean mForceIgnoreOutsideTouch) {
        this.mForceIgnoreOutsideTouch = mForceIgnoreOutsideTouch;
    }
    
    public void setHeight(final int mDropDownHeight) {
        this.mDropDownHeight = mDropDownHeight;
    }
    
    public void setHorizontalOffset(final int mDropDownHorizontalOffset) {
        this.mDropDownHorizontalOffset = mDropDownHorizontalOffset;
    }
    
    public void setInputMethodMode(final int inputMethodMode) {
        this.mPopup.setInputMethodMode(inputMethodMode);
    }
    
    void setListItemExpandMax(final int mListItemExpandMaximum) {
        this.mListItemExpandMaximum = mListItemExpandMaximum;
    }
    
    public void setListSelector(final Drawable mDropDownListHighlight) {
        this.mDropDownListHighlight = mDropDownListHighlight;
    }
    
    public void setModal(final boolean focusable) {
        this.mModal = true;
        this.mPopup.setFocusable(focusable);
    }
    
    public void setOnDismissListener(final PopupWindow$OnDismissListener onDismissListener) {
        this.mPopup.setOnDismissListener(onDismissListener);
    }
    
    public void setOnItemClickListener(final AdapterView$OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    
    public void setOnItemSelectedListener(final AdapterView$OnItemSelectedListener mItemSelectedListener) {
        this.mItemSelectedListener = mItemSelectedListener;
    }
    
    public void setPromptPosition(final int mPromptPosition) {
        this.mPromptPosition = mPromptPosition;
    }
    
    public void setPromptView(final View mPromptView) {
        final boolean showing = this.isShowing();
        if (showing) {
            this.removePromptView();
        }
        this.mPromptView = mPromptView;
        if (showing) {
            this.show();
        }
    }
    
    public void setSelection(final int selection) {
        final DropDownListView mDropDownList = this.mDropDownList;
        if (this.isShowing() && mDropDownList != null) {
            mDropDownList.mListSelectionHidden = false;
            mDropDownList.setSelection(selection);
            if (mDropDownList.getChoiceMode() != 0) {
                mDropDownList.setItemChecked(selection, true);
            }
        }
    }
    
    public void setSoftInputMode(final int softInputMode) {
        this.mPopup.setSoftInputMode(softInputMode);
    }
    
    public void setVerticalOffset(final int mDropDownVerticalOffset) {
        this.mDropDownVerticalOffset = mDropDownVerticalOffset;
        this.mDropDownVerticalOffsetSet = true;
    }
    
    public void setWidth(final int mDropDownWidth) {
        this.mDropDownWidth = mDropDownWidth;
    }
    
    public void show() {
        boolean outsideTouchable = true;
        int selection = -1;
        final int buildDropDown = this.buildDropDown();
        final boolean inputMethodNotNeeded = this.isInputMethodNotNeeded();
        if (this.mPopup.isShowing()) {
            int n;
            if (this.mDropDownWidth == selection) {
                n = -1;
            }
            else if (this.mDropDownWidth == -2) {
                n = this.getAnchorView().getWidth();
            }
            else {
                n = this.mDropDownWidth;
            }
            int mDropDownHeight;
            if (this.mDropDownHeight == selection) {
                if (inputMethodNotNeeded) {
                    mDropDownHeight = buildDropDown;
                }
                else {
                    mDropDownHeight = selection;
                }
                if (inputMethodNotNeeded) {
                    final PopupWindow mPopup = this.mPopup;
                    if (this.mDropDownWidth != selection) {
                        selection = 0;
                    }
                    mPopup.setWindowLayoutMode(selection, 0);
                }
                else {
                    final PopupWindow mPopup2 = this.mPopup;
                    int n2;
                    if (this.mDropDownWidth == selection) {
                        n2 = selection;
                    }
                    else {
                        n2 = 0;
                    }
                    mPopup2.setWindowLayoutMode(n2, selection);
                }
            }
            else if (this.mDropDownHeight == -2) {
                mDropDownHeight = buildDropDown;
            }
            else {
                mDropDownHeight = this.mDropDownHeight;
            }
            final PopupWindow mPopup3 = this.mPopup;
            final boolean mForceIgnoreOutsideTouch = this.mForceIgnoreOutsideTouch;
            boolean outsideTouchable2 = false;
            if (!mForceIgnoreOutsideTouch) {
                final boolean mDropDownAlwaysVisible = this.mDropDownAlwaysVisible;
                outsideTouchable2 = false;
                if (!mDropDownAlwaysVisible) {
                    outsideTouchable2 = outsideTouchable;
                }
            }
            mPopup3.setOutsideTouchable(outsideTouchable2);
            this.mPopup.update(this.getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, n, mDropDownHeight);
        }
        else {
            int n3;
            if (this.mDropDownWidth == selection) {
                n3 = -1;
            }
            else if (this.mDropDownWidth == -2) {
                this.mPopup.setWidth(this.getAnchorView().getWidth());
                n3 = 0;
            }
            else {
                this.mPopup.setWidth(this.mDropDownWidth);
                n3 = 0;
            }
            int n4;
            if (this.mDropDownHeight == selection) {
                n4 = -1;
            }
            else if (this.mDropDownHeight == -2) {
                this.mPopup.setHeight(buildDropDown);
                n4 = 0;
            }
            else {
                this.mPopup.setHeight(this.mDropDownHeight);
                n4 = 0;
            }
            this.mPopup.setWindowLayoutMode(n3, n4);
            final PopupWindow mPopup4 = this.mPopup;
            if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible) {
                outsideTouchable = false;
            }
            mPopup4.setOutsideTouchable(outsideTouchable);
            this.mPopup.setTouchInterceptor((View$OnTouchListener)this.mTouchInterceptor);
            this.mPopup.showAsDropDown(this.getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset);
            this.mDropDownList.setSelection(selection);
            if (!this.mModal || this.mDropDownList.isInTouchMode()) {
                this.clearListSelection();
            }
            if (!this.mModal) {
                this.mHandler.post((Runnable)this.mHideSelector);
            }
        }
    }
    
    private static class DropDownListView extends ListView
    {
        public static final int INVALID_POSITION = -1;
        static final int NO_POSITION = -1;
        private static final String TAG = "ListPopupWindow.DropDownListView";
        private boolean mHijackFocus;
        private boolean mListSelectionHidden;
        
        public DropDownListView(final Context context, final boolean mHijackFocus) {
            super(context, (AttributeSet)null, R.attr.dropDownListViewStyle);
            this.mHijackFocus = mHijackFocus;
            this.setCacheColorHint(0);
        }
        
        private int lookForSelectablePosition(final int n, final boolean b) {
            final ListAdapter adapter = this.getAdapter();
            if (adapter != null && !this.isInTouchMode()) {
                final int count = adapter.getCount();
                if (!this.getAdapter().areAllItemsEnabled()) {
                    int n2;
                    if (b) {
                        for (n2 = Math.max(0, n); n2 < count && !adapter.isEnabled(n2); ++n2) {}
                    }
                    else {
                        for (n2 = Math.min(n, count - 1); n2 >= 0 && !adapter.isEnabled(n2); --n2) {}
                    }
                    if (n2 >= 0 && n2 < count) {
                        return n2;
                    }
                }
                else if (n >= 0 && n < count) {
                    return n;
                }
            }
            return -1;
        }
        
        public boolean hasFocus() {
            return this.mHijackFocus || super.hasFocus();
        }
        
        public boolean hasWindowFocus() {
            return this.mHijackFocus || super.hasWindowFocus();
        }
        
        public boolean isFocused() {
            return this.mHijackFocus || super.isFocused();
        }
        
        public boolean isInTouchMode() {
            return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
        }
        
        final int measureHeightOfChildrenCompat(final int n, final int n2, final int n3, final int n4, final int n5) {
            final int listPaddingTop = this.getListPaddingTop();
            final int listPaddingBottom = this.getListPaddingBottom();
            this.getListPaddingLeft();
            this.getListPaddingRight();
            final int dividerHeight = this.getDividerHeight();
            final Drawable divider = this.getDivider();
            final ListAdapter adapter = this.getAdapter();
            if (adapter != null) {
                int n6 = listPaddingTop + listPaddingBottom;
                int n7;
                if (dividerHeight > 0 && divider != null) {
                    n7 = dividerHeight;
                }
                else {
                    n7 = 0;
                }
                int n8 = 0;
                View view = null;
                int n9 = 0;
                final int count = adapter.getCount();
                int i = 0;
                while (i < count) {
                    final int itemViewType = adapter.getItemViewType(i);
                    if (itemViewType != n9) {
                        view = null;
                        n9 = itemViewType;
                    }
                    view = adapter.getView(i, view, (ViewGroup)this);
                    final int height = view.getLayoutParams().height;
                    int n10;
                    if (height > 0) {
                        n10 = View$MeasureSpec.makeMeasureSpec(height, 1073741824);
                    }
                    else {
                        n10 = View$MeasureSpec.makeMeasureSpec(0, 0);
                    }
                    view.measure(n, n10);
                    if (i > 0) {
                        n6 += n7;
                    }
                    n6 += view.getMeasuredHeight();
                    if (n6 >= n4) {
                        if (n5 < 0 || i <= n5 || n8 <= 0 || n6 == n4) {
                            return n4;
                        }
                        return n8;
                    }
                    else {
                        if (n5 >= 0 && i >= n5) {
                            n8 = n6;
                        }
                        ++i;
                    }
                }
                return n6;
            }
            return listPaddingTop + listPaddingBottom;
        }
    }
    
    private class ListSelectorHider implements Runnable
    {
        @Override
        public void run() {
            ListPopupWindow.this.clearListSelection();
        }
    }
    
    private class PopupDataSetObserver extends DataSetObserver
    {
        public void onChanged() {
            if (ListPopupWindow.this.isShowing()) {
                ListPopupWindow.this.show();
            }
        }
        
        public void onInvalidated() {
            ListPopupWindow.this.dismiss();
        }
    }
    
    private class PopupScrollListener implements AbsListView$OnScrollListener
    {
        public void onScroll(final AbsListView absListView, final int n, final int n2, final int n3) {
        }
        
        public void onScrollStateChanged(final AbsListView absListView, final int n) {
            if (n == 1 && !ListPopupWindow.this.isInputMethodNotNeeded() && ListPopupWindow.this.mPopup.getContentView() != null) {
                ListPopupWindow.this.mHandler.removeCallbacks((Runnable)ListPopupWindow.this.mResizePopupRunnable);
                ListPopupWindow.this.mResizePopupRunnable.run();
            }
        }
    }
    
    private class PopupTouchInterceptor implements View$OnTouchListener
    {
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            final int action = motionEvent.getAction();
            final int n = (int)motionEvent.getX();
            final int n2 = (int)motionEvent.getY();
            if (action == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && n >= 0 && n < ListPopupWindow.this.mPopup.getWidth() && n2 >= 0 && n2 < ListPopupWindow.this.mPopup.getHeight()) {
                ListPopupWindow.this.mHandler.postDelayed((Runnable)ListPopupWindow.this.mResizePopupRunnable, 250L);
            }
            else if (action == 1) {
                ListPopupWindow.this.mHandler.removeCallbacks((Runnable)ListPopupWindow.this.mResizePopupRunnable);
            }
            return false;
        }
    }
    
    private class ResizePopupRunnable implements Runnable
    {
        @Override
        public void run() {
            if (ListPopupWindow.this.mDropDownList != null && ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount() && ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum) {
                ListPopupWindow.this.mPopup.setInputMethodMode(2);
                ListPopupWindow.this.show();
            }
        }
    }
}
