package android.support.v4.view;

import android.view.animation.*;
import android.support.v4.widget.*;
import android.graphics.drawable.*;
import android.widget.*;
import java.lang.reflect.*;
import android.content.*;
import java.util.*;
import android.util.*;
import android.view.accessibility.*;
import android.graphics.*;
import android.database.*;
import android.content.res.*;
import android.support.v4.view.accessibility.*;
import android.view.*;
import android.os.*;
import android.support.v4.os.*;

public class ViewPager extends ViewGroup
{
    private static final int CLOSE_ENOUGH = 2;
    private static final Comparator<ItemInfo> COMPARATOR;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    private static final int[] LAYOUT_ATTRS;
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "ViewPager";
    private static final boolean USE_CACHE;
    private static final Interpolator sInterpolator;
    private static final ViewPositionComparator sPositionComparator;
    private int mActivePointerId;
    private PagerAdapter mAdapter;
    private OnAdapterChangeListener mAdapterChangeListener;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    private int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mIgnoreGutter;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems;
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffectCompat mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffectCompat mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private Method mSetChildrenDrawingOrderEnabled;
    private final ItemInfo mTempItem;
    private final Rect mTempRect;
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    
    static {
        LAYOUT_ATTRS = new int[] { 16842931 };
        COMPARATOR = new Comparator<ItemInfo>() {
            @Override
            public int compare(final ItemInfo itemInfo, final ItemInfo itemInfo2) {
                return itemInfo.position - itemInfo2.position;
            }
        };
        sInterpolator = (Interpolator)new Interpolator() {
            public float getInterpolation(final float n) {
                final float n2 = n - 1.0f;
                return 1.0f + n2 * (n2 * (n2 * (n2 * n2)));
            }
        };
        sPositionComparator = new ViewPositionComparator();
    }
    
    public ViewPager(final Context context) {
        super(context);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    public ViewPager(final Context context, final AttributeSet set) {
        super(context, set);
        this.mItems = new ArrayList<ItemInfo>();
        this.mTempItem = new ItemInfo();
        this.mTempRect = new Rect();
        this.mRestoredCurItem = -1;
        this.mRestoredAdapterState = null;
        this.mRestoredClassLoader = null;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        this.mOffscreenPageLimit = 1;
        this.mActivePointerId = -1;
        this.mFirstLayout = true;
        this.mNeedCalculatePageOffsets = false;
        this.mEndScrollRunnable = new Runnable() {
            @Override
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = 0;
        this.initViewPager();
    }
    
    private void calculatePageOffsets(final ItemInfo itemInfo, final int n, final ItemInfo itemInfo2) {
        final int count = this.mAdapter.getCount();
        final int clientWidth = this.getClientWidth();
        float n2;
        if (clientWidth > 0) {
            n2 = this.mPageMargin / clientWidth;
        }
        else {
            n2 = 0.0f;
        }
        if (itemInfo2 != null) {
            final int position = itemInfo2.position;
            if (position < itemInfo.position) {
                int n3 = 0;
                float offset = n2 + (itemInfo2.offset + itemInfo2.widthFactor);
                for (int i = position + 1; i <= itemInfo.position && n3 < this.mItems.size(); ++i) {
                    ItemInfo itemInfo3;
                    for (itemInfo3 = this.mItems.get(n3); i > itemInfo3.position && n3 < -1 + this.mItems.size(); ++n3, itemInfo3 = this.mItems.get(n3)) {}
                    while (i < itemInfo3.position) {
                        offset += n2 + this.mAdapter.getPageWidth(i);
                        ++i;
                    }
                    itemInfo3.offset = offset;
                    offset += n2 + itemInfo3.widthFactor;
                }
            }
            else if (position > itemInfo.position) {
                int n4 = -1 + this.mItems.size();
                float offset2 = itemInfo2.offset;
                for (int j = position - 1; j >= itemInfo.position && n4 >= 0; --j) {
                    ItemInfo itemInfo4;
                    for (itemInfo4 = this.mItems.get(n4); j < itemInfo4.position && n4 > 0; --n4, itemInfo4 = this.mItems.get(n4)) {}
                    while (j > itemInfo4.position) {
                        offset2 -= n2 + this.mAdapter.getPageWidth(j);
                        --j;
                    }
                    offset2 -= n2 + itemInfo4.widthFactor;
                    itemInfo4.offset = offset2;
                }
            }
        }
        final int size = this.mItems.size();
        float offset3 = itemInfo.offset;
        int k = -1 + itemInfo.position;
        float offset4;
        if (itemInfo.position == 0) {
            offset4 = itemInfo.offset;
        }
        else {
            offset4 = -3.4028235E38f;
        }
        this.mFirstOffset = offset4;
        float mLastOffset;
        if (itemInfo.position == count - 1) {
            mLastOffset = itemInfo.offset + itemInfo.widthFactor - 1.0f;
        }
        else {
            mLastOffset = Float.MAX_VALUE;
        }
        this.mLastOffset = mLastOffset;
        for (int l = n - 1; l >= 0; --l, --k) {
            ItemInfo itemInfo5;
            int n5;
            for (itemInfo5 = this.mItems.get(l); k > itemInfo5.position; k = n5) {
                final PagerAdapter mAdapter = this.mAdapter;
                n5 = k - 1;
                offset3 -= n2 + mAdapter.getPageWidth(k);
            }
            offset3 -= n2 + itemInfo5.widthFactor;
            itemInfo5.offset = offset3;
            if (itemInfo5.position == 0) {
                this.mFirstOffset = offset3;
            }
        }
        float offset5 = n2 + (itemInfo.offset + itemInfo.widthFactor);
        for (int n6 = 1 + itemInfo.position, n7 = n + 1; n7 < size; ++n7, ++n6) {
            ItemInfo itemInfo6;
            int n8;
            for (itemInfo6 = this.mItems.get(n7); n6 < itemInfo6.position; n6 = n8) {
                final PagerAdapter mAdapter2 = this.mAdapter;
                n8 = n6 + 1;
                offset5 += n2 + mAdapter2.getPageWidth(n6);
            }
            if (itemInfo6.position == count - 1) {
                this.mLastOffset = offset5 + itemInfo6.widthFactor - 1.0f;
            }
            itemInfo6.offset = offset5;
            offset5 += n2 + itemInfo6.widthFactor;
        }
        this.mNeedCalculatePageOffsets = false;
    }
    
    private void completeScroll(final boolean b) {
        int n;
        if (this.mScrollState == 2) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            this.setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
            final int scrollX = this.getScrollX();
            final int scrollY = this.getScrollY();
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                this.scrollTo(currX, currY);
            }
        }
        this.mPopulatePending = false;
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.scrolling) {
                n = 1;
                itemInfo.scrolling = false;
            }
        }
        if (n != 0) {
            if (!b) {
                this.mEndScrollRunnable.run();
                return;
            }
            ViewCompat.postOnAnimation((View)this, this.mEndScrollRunnable);
        }
    }
    
    private int determineTargetPage(final int n, final float n2, final int n3, final int n4) {
        int max;
        if (Math.abs(n4) > this.mFlingDistance && Math.abs(n3) > this.mMinimumVelocity) {
            if (n3 > 0) {
                max = n;
            }
            else {
                max = n + 1;
            }
        }
        else {
            float n5;
            if (n >= this.mCurItem) {
                n5 = 0.4f;
            }
            else {
                n5 = 0.6f;
            }
            max = (int)(n5 + (n2 + n));
        }
        if (this.mItems.size() > 0) {
            max = Math.max(this.mItems.get(0).position, Math.min(max, this.mItems.get(-1 + this.mItems.size()).position));
        }
        return max;
    }
    
    private void enableLayers(final boolean b) {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            int n;
            if (b) {
                n = 2;
            }
            else {
                n = 0;
            }
            ViewCompat.setLayerType(this.getChildAt(i), n, null);
        }
    }
    
    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
    
    private Rect getChildRectInPagerCoordinates(Rect rect, final View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view == null) {
            rect.set(0, 0, 0, 0);
        }
        else {
            rect.left = view.getLeft();
            rect.right = view.getRight();
            rect.top = view.getTop();
            rect.bottom = view.getBottom();
            ViewPager viewPager;
            for (ViewParent viewParent = view.getParent(); viewParent instanceof ViewGroup && viewParent != this; viewParent = viewPager.getParent()) {
                viewPager = (ViewPager)viewParent;
                rect.left += viewPager.getLeft();
                rect.right += viewPager.getRight();
                rect.top += viewPager.getTop();
                rect.bottom += viewPager.getBottom();
            }
        }
        return rect;
    }
    
    private int getClientWidth() {
        return this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight();
    }
    
    private ItemInfo infoForCurrentScrollPosition() {
        final int clientWidth = this.getClientWidth();
        float n;
        if (clientWidth > 0) {
            n = this.getScrollX() / clientWidth;
        }
        else {
            n = 0.0f;
        }
        float n2 = 0.0f;
        if (clientWidth > 0) {
            n2 = this.mPageMargin / clientWidth;
        }
        int position = -1;
        float n3 = 0.0f;
        float widthFactor = 0.0f;
        int n4 = 1;
        ItemInfo itemInfo = null;
        for (int i = 0; i < this.mItems.size(); ++i, n4 = 0) {
            ItemInfo mTempItem = this.mItems.get(i);
            if (n4 == 0 && mTempItem.position != position + 1) {
                mTempItem = this.mTempItem;
                mTempItem.offset = n2 + (n3 + widthFactor);
                mTempItem.position = position + 1;
                mTempItem.widthFactor = this.mAdapter.getPageWidth(mTempItem.position);
                --i;
            }
            final float offset = mTempItem.offset;
            final float n5 = n2 + (offset + mTempItem.widthFactor);
            if (n4 == 0 && n < offset) {
                break;
            }
            if (n < n5 || i == -1 + this.mItems.size()) {
                itemInfo = mTempItem;
                break;
            }
            position = mTempItem.position;
            n3 = offset;
            widthFactor = mTempItem.widthFactor;
            itemInfo = mTempItem;
        }
        return itemInfo;
    }
    
    private boolean isGutterDrag(final float n, final float n2) {
        return (n < this.mGutterSize && n2 > 0.0f) || (n > this.getWidth() - this.mGutterSize && n2 < 0.0f);
    }
    
    private void onSecondaryPointerUp(final MotionEvent motionEvent) {
        final int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
        if (MotionEventCompat.getPointerId(motionEvent, actionIndex) == this.mActivePointerId) {
            boolean b;
            if (actionIndex == 0) {
                b = true;
            }
            else {
                b = false;
            }
            this.mLastMotionX = MotionEventCompat.getX(motionEvent, b ? 1 : 0);
            this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, b ? 1 : 0);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }
    
    private boolean pageScrolled(final int n) {
        boolean b;
        if (this.mItems.size() == 0) {
            this.mCalledSuper = false;
            this.onPageScrolled(0, 0.0f, 0);
            final boolean mCalledSuper = this.mCalledSuper;
            b = false;
            if (!mCalledSuper) {
                throw new IllegalStateException("onPageScrolled did not call superclass implementation");
            }
        }
        else {
            final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
            final int clientWidth = this.getClientWidth();
            final int n2 = clientWidth + this.mPageMargin;
            final float n3 = this.mPageMargin / clientWidth;
            final int position = infoForCurrentScrollPosition.position;
            final float n4 = (n / clientWidth - infoForCurrentScrollPosition.offset) / (n3 + infoForCurrentScrollPosition.widthFactor);
            final int n5 = (int)(n4 * n2);
            this.mCalledSuper = false;
            this.onPageScrolled(position, n4, n5);
            if (!this.mCalledSuper) {
                throw new IllegalStateException("onPageScrolled did not call superclass implementation");
            }
            b = true;
        }
        return b;
    }
    
    private boolean performDrag(final float mLastMotionX) {
        final float n = this.mLastMotionX - mLastMotionX;
        this.mLastMotionX = mLastMotionX;
        float n2 = n + this.getScrollX();
        final int clientWidth = this.getClientWidth();
        float n3 = clientWidth * this.mFirstOffset;
        float n4 = clientWidth * this.mLastOffset;
        boolean b = true;
        boolean b2 = true;
        final ItemInfo itemInfo = this.mItems.get(0);
        final ItemInfo itemInfo2 = this.mItems.get(-1 + this.mItems.size());
        if (itemInfo.position != 0) {
            b = false;
            n3 = itemInfo.offset * clientWidth;
        }
        if (itemInfo2.position != -1 + this.mAdapter.getCount()) {
            b2 = false;
            n4 = itemInfo2.offset * clientWidth;
        }
        boolean b3;
        if (n2 < n3) {
            b3 = false;
            if (b) {
                b3 = this.mLeftEdge.onPull(Math.abs(n3 - n2) / clientWidth);
            }
            n2 = n3;
        }
        else {
            final float n5 = fcmpl(n2, n4);
            b3 = false;
            if (n5 > 0) {
                b3 = false;
                if (b2) {
                    b3 = this.mRightEdge.onPull(Math.abs(n2 - n4) / clientWidth);
                }
                n2 = n4;
            }
        }
        this.mLastMotionX += n2 - (int)n2;
        this.scrollTo((int)n2, this.getScrollY());
        this.pageScrolled((int)n2);
        return b3;
    }
    
    private void recomputeScrollPosition(final int n, final int n2, final int n3, final int n4) {
        if (n2 > 0 && !this.mItems.isEmpty()) {
            final int n5 = this.getScrollX() / (n4 + (n2 - this.getPaddingLeft() - this.getPaddingRight())) * (n3 + (n - this.getPaddingLeft() - this.getPaddingRight()));
            this.scrollTo(n5, this.getScrollY());
            if (!this.mScroller.isFinished()) {
                this.mScroller.startScroll(n5, 0, (int)(this.infoForPosition(this.mCurItem).offset * n), 0, this.mScroller.getDuration() - this.mScroller.timePassed());
            }
        }
        else {
            final ItemInfo infoForPosition = this.infoForPosition(this.mCurItem);
            float min;
            if (infoForPosition != null) {
                min = Math.min(infoForPosition.offset, this.mLastOffset);
            }
            else {
                min = 0.0f;
            }
            final int n6 = (int)(min * (n - this.getPaddingLeft() - this.getPaddingRight()));
            if (n6 != this.getScrollX()) {
                this.completeScroll(false);
                this.scrollTo(n6, this.getScrollY());
            }
        }
    }
    
    private void removeNonDecorViews() {
        for (int i = 0; i < this.getChildCount(); ++i) {
            if (!((LayoutParams)this.getChildAt(i).getLayoutParams()).isDecor) {
                this.removeViewAt(i);
                --i;
            }
        }
    }
    
    private void requestParentDisallowInterceptTouchEvent(final boolean b) {
        final ViewParent parent = this.getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(b);
        }
    }
    
    private void scrollToItem(final int n, final boolean b, final int n2, final boolean b2) {
        final ItemInfo infoForPosition = this.infoForPosition(n);
        int n3 = 0;
        if (infoForPosition != null) {
            n3 = (int)(this.getClientWidth() * Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset)));
        }
        if (b) {
            this.smoothScrollTo(n3, 0, n2);
            if (b2 && this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageSelected(n);
            }
            if (b2 && this.mInternalPageChangeListener != null) {
                this.mInternalPageChangeListener.onPageSelected(n);
            }
            return;
        }
        if (b2 && this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageSelected(n);
        }
        if (b2 && this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageSelected(n);
        }
        this.completeScroll(false);
        this.scrollTo(n3, 0);
        this.pageScrolled(n3);
    }
    
    private void setScrollState(final int mScrollState) {
        if (this.mScrollState != mScrollState) {
            this.mScrollState = mScrollState;
            if (this.mPageTransformer != null) {
                this.enableLayers(mScrollState != 0);
            }
            if (this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageScrollStateChanged(mScrollState);
            }
        }
    }
    
    private void setScrollingCacheEnabled(final boolean mScrollingCacheEnabled) {
        if (this.mScrollingCacheEnabled != mScrollingCacheEnabled) {
            this.mScrollingCacheEnabled = mScrollingCacheEnabled;
        }
    }
    
    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            if (this.mDrawingOrderedChildren == null) {
                this.mDrawingOrderedChildren = new ArrayList<View>();
            }
            else {
                this.mDrawingOrderedChildren.clear();
            }
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                this.mDrawingOrderedChildren.add(this.getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, ViewPager.sPositionComparator);
        }
    }
    
    public void addFocusables(final ArrayList<View> list, final int n, final int n2) {
        final int size = list.size();
        final int descendantFocusability = this.getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i = 0; i < this.getChildCount(); ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() == 0) {
                    final ItemInfo infoForChild = this.infoForChild(child);
                    if (infoForChild != null && infoForChild.position == this.mCurItem) {
                        child.addFocusables((ArrayList)list, n, n2);
                    }
                }
            }
        }
        if ((descendantFocusability != 262144 || size == list.size()) && this.isFocusable() && ((n2 & 0x1) != 0x1 || !this.isInTouchMode() || this.isFocusableInTouchMode()) && list != null) {
            list.add((View)this);
        }
    }
    
    ItemInfo addNewItem(final int position, final int n) {
        final ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = position;
        itemInfo.object = this.mAdapter.instantiateItem(this, position);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(position);
        if (n < 0 || n >= this.mItems.size()) {
            this.mItems.add(itemInfo);
            return itemInfo;
        }
        this.mItems.add(n, itemInfo);
        return itemInfo;
    }
    
    public void addTouchables(final ArrayList<View> list) {
        for (int i = 0; i < this.getChildCount(); ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem) {
                    child.addTouchables((ArrayList)list);
                }
            }
        }
    }
    
    public void addView(final View view, final int n, ViewGroup$LayoutParams generateLayoutParams) {
        if (!this.checkLayoutParams(generateLayoutParams)) {
            generateLayoutParams = this.generateLayoutParams(generateLayoutParams);
        }
        final LayoutParams layoutParams = (LayoutParams)generateLayoutParams;
        layoutParams.isDecor |= (view instanceof Decor);
        if (!this.mInLayout) {
            super.addView(view, n, generateLayoutParams);
            return;
        }
        if (layoutParams != null && layoutParams.isDecor) {
            throw new IllegalStateException("Cannot add pager decor view during layout");
        }
        layoutParams.needsMeasure = true;
        this.addViewInLayout(view, n, generateLayoutParams);
    }
    
    public boolean arrowScroll(final int n) {
        Object focus = this.findFocus();
        if (focus == this) {
            focus = null;
        }
        else if (focus != null) {
            ViewParent viewParent = ((View)focus).getParent();
            boolean b2;
            while (true) {
                final boolean b = viewParent instanceof ViewGroup;
                b2 = false;
                if (!b) {
                    break;
                }
                if (viewParent == this) {
                    b2 = true;
                    break;
                }
                viewParent = viewParent.getParent();
            }
            if (!b2) {
                final StringBuilder sb = new StringBuilder();
                sb.append(((View)focus).getClass().getSimpleName());
                for (ViewParent viewParent2 = ((View)focus).getParent(); viewParent2 instanceof ViewGroup; viewParent2 = viewParent2.getParent()) {
                    sb.append(" => ").append(viewParent2.getClass().getSimpleName());
                }
                Log.e("ViewPager", "arrowScroll tried to find focus based on non-child current focused view " + sb.toString());
                focus = null;
            }
        }
        final View nextFocus = FocusFinder.getInstance().findNextFocus((ViewGroup)this, (View)focus, n);
        boolean b3 = false;
        Label_0086: {
            if (nextFocus != null && nextFocus != focus) {
                if (n == 17) {
                    final int left = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                    final int left2 = this.getChildRectInPagerCoordinates(this.mTempRect, (View)focus).left;
                    if (focus != null && left >= left2) {
                        b3 = this.pageLeft();
                    }
                    else {
                        b3 = nextFocus.requestFocus();
                    }
                }
                else {
                    b3 = false;
                    if (n == 66) {
                        final int left3 = this.getChildRectInPagerCoordinates(this.mTempRect, nextFocus).left;
                        final int left4 = this.getChildRectInPagerCoordinates(this.mTempRect, (View)focus).left;
                        if (focus != null && left3 <= left4) {
                            b3 = this.pageRight();
                        }
                        else {
                            b3 = nextFocus.requestFocus();
                        }
                    }
                }
            }
            else if (n == 17 || n == 1) {
                b3 = this.pageLeft();
            }
            else {
                if (n != 66) {
                    b3 = false;
                    if (n != 2) {
                        break Label_0086;
                    }
                }
                b3 = this.pageRight();
            }
        }
        if (b3) {
            this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(n));
        }
        return b3;
    }
    
    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        this.mFakeDragging = true;
        this.setScrollState(1);
        this.mLastMotionX = 0.0f;
        this.mInitialMotionX = 0.0f;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        else {
            this.mVelocityTracker.clear();
        }
        final long uptimeMillis = SystemClock.uptimeMillis();
        final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
        this.mFakeDragBeginTime = uptimeMillis;
        return true;
    }
    
    protected boolean canScroll(final View view, final boolean b, final int n, final int n2, final int n3) {
        if (view instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup)view;
            final int scrollX = view.getScrollX();
            final int scrollY = view.getScrollY();
            for (int i = -1 + viewGroup.getChildCount(); i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);
                if (n2 + scrollX >= child.getLeft() && n2 + scrollX < child.getRight() && n3 + scrollY >= child.getTop() && n3 + scrollY < child.getBottom() && this.canScroll(child, true, n, n2 + scrollX - child.getLeft(), n3 + scrollY - child.getTop())) {
                    return true;
                }
            }
        }
        return b && ViewCompat.canScrollHorizontally(view, -n);
    }
    
    public boolean canScrollHorizontally(final int n) {
        boolean b = true;
        if (this.mAdapter != null) {
            final int clientWidth = this.getClientWidth();
            final int scrollX = this.getScrollX();
            if (n < 0) {
                if (scrollX <= (int)(clientWidth * this.mFirstOffset)) {
                    b = false;
                }
                return b;
            }
            if (n > 0) {
                if (scrollX >= (int)(clientWidth * this.mLastOffset)) {
                    b = false;
                }
                return b;
            }
        }
        return false;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public void computeScroll() {
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            final int scrollX = this.getScrollX();
            final int scrollY = this.getScrollY();
            final int currX = this.mScroller.getCurrX();
            final int currY = this.mScroller.getCurrY();
            if (scrollX != currX || scrollY != currY) {
                this.scrollTo(currX, currY);
                if (!this.pageScrolled(currX)) {
                    this.mScroller.abortAnimation();
                    this.scrollTo(0, currY);
                }
            }
            ViewCompat.postInvalidateOnAnimation((View)this);
            return;
        }
        this.completeScroll(true);
    }
    
    void dataSetChanged() {
        final int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        boolean b = this.mItems.size() < 1 + 2 * this.mOffscreenPageLimit && this.mItems.size() < count;
        int n = this.mCurItem;
        int n2 = 0;
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            final int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            if (itemPosition != -1) {
                if (itemPosition == -2) {
                    this.mItems.remove(i);
                    --i;
                    if (n2 == 0) {
                        this.mAdapter.startUpdate(this);
                        n2 = 1;
                    }
                    this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
                    b = true;
                    if (this.mCurItem == itemInfo.position) {
                        n = Math.max(0, Math.min(this.mCurItem, count - 1));
                        b = true;
                    }
                }
                else if (itemInfo.position != itemPosition) {
                    if (itemInfo.position == this.mCurItem) {
                        n = itemPosition;
                    }
                    itemInfo.position = itemPosition;
                    b = true;
                }
            }
        }
        if (n2 != 0) {
            this.mAdapter.finishUpdate(this);
        }
        Collections.sort(this.mItems, ViewPager.COMPARATOR);
        if (b) {
            for (int childCount = this.getChildCount(), j = 0; j < childCount; ++j) {
                final LayoutParams layoutParams = (LayoutParams)this.getChildAt(j).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            this.setCurrentItemInternal(n, false, true);
            this.requestLayout();
        }
    }
    
    public boolean dispatchKeyEvent(final KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || this.executeKeyEvent(keyEvent);
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem && child.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    float distanceInfluenceForSnapDuration(final float n) {
        return (float)Math.sin((float)(0.4712389167638204 * (n - 0.5f)));
    }
    
    public void draw(final Canvas canvas) {
        super.draw(canvas);
        final int overScrollMode = ViewCompat.getOverScrollMode((View)this);
        boolean b;
        if (overScrollMode == 0 || (overScrollMode == 1 && this.mAdapter != null && this.mAdapter.getCount() > 1)) {
            final boolean finished = this.mLeftEdge.isFinished();
            b = false;
            if (!finished) {
                final int save = canvas.save();
                final int n = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                final int width = this.getWidth();
                canvas.rotate(270.0f);
                canvas.translate((float)(-n + this.getPaddingTop()), this.mFirstOffset * width);
                this.mLeftEdge.setSize(n, width);
                b = (false | this.mLeftEdge.draw(canvas));
                canvas.restoreToCount(save);
            }
            if (!this.mRightEdge.isFinished()) {
                final int save2 = canvas.save();
                final int width2 = this.getWidth();
                final int n2 = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
                canvas.rotate(90.0f);
                canvas.translate((float)(-this.getPaddingTop()), -(1.0f + this.mLastOffset) * width2);
                this.mRightEdge.setSize(n2, width2);
                b |= this.mRightEdge.draw(canvas);
                canvas.restoreToCount(save2);
            }
        }
        else {
            this.mLeftEdge.finish();
            this.mRightEdge.finish();
            b = false;
        }
        if (b) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        final Drawable mMarginDrawable = this.mMarginDrawable;
        if (mMarginDrawable != null && mMarginDrawable.isStateful()) {
            mMarginDrawable.setState(this.getDrawableState());
        }
    }
    
    public void endFakeDrag() {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        final VelocityTracker mVelocityTracker = this.mVelocityTracker;
        mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
        final int n = (int)VelocityTrackerCompat.getXVelocity(mVelocityTracker, this.mActivePointerId);
        this.mPopulatePending = true;
        final int clientWidth = this.getClientWidth();
        final int scrollX = this.getScrollX();
        final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
        this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / clientWidth - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, n, (int)(this.mLastMotionX - this.mInitialMotionX)), true, true, n);
        this.endDrag();
        this.mFakeDragging = false;
    }
    
    public boolean executeKeyEvent(final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            switch (keyEvent.getKeyCode()) {
                case 21: {
                    return this.arrowScroll(17);
                }
                case 22: {
                    return this.arrowScroll(66);
                }
                case 61: {
                    if (Build$VERSION.SDK_INT < 11) {
                        break;
                    }
                    if (KeyEventCompat.hasNoModifiers(keyEvent)) {
                        return this.arrowScroll(2);
                    }
                    if (KeyEventCompat.hasModifiers(keyEvent, 1)) {
                        return this.arrowScroll(1);
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    public void fakeDragBy(final float n) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        }
        this.mLastMotionX += n;
        float n2 = this.getScrollX() - n;
        final int clientWidth = this.getClientWidth();
        float n3 = clientWidth * this.mFirstOffset;
        float n4 = clientWidth * this.mLastOffset;
        final ItemInfo itemInfo = this.mItems.get(0);
        final ItemInfo itemInfo2 = this.mItems.get(-1 + this.mItems.size());
        if (itemInfo.position != 0) {
            n3 = itemInfo.offset * clientWidth;
        }
        if (itemInfo2.position != -1 + this.mAdapter.getCount()) {
            n4 = itemInfo2.offset * clientWidth;
        }
        if (n2 < n3) {
            n2 = n3;
        }
        else if (n2 > n4) {
            n2 = n4;
        }
        this.mLastMotionX += n2 - (int)n2;
        this.scrollTo((int)n2, this.getScrollY());
        this.pageScrolled((int)n2);
        final MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return this.generateDefaultLayoutParams();
    }
    
    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }
    
    protected int getChildDrawingOrder(final int n, final int n2) {
        int n3;
        if (this.mDrawingOrder == 2) {
            n3 = n - 1 - n2;
        }
        else {
            n3 = n2;
        }
        return ((LayoutParams)this.mDrawingOrderedChildren.get(n3).getLayoutParams()).childIndex;
    }
    
    public int getCurrentItem() {
        return this.mCurItem;
    }
    
    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }
    
    public int getPageMargin() {
        return this.mPageMargin;
    }
    
    ItemInfo infoForAnyChild(View view) {
        while (true) {
            final ViewParent parent = view.getParent();
            if (parent == this) {
                return this.infoForChild(view);
            }
            if (parent == null || !(parent instanceof View)) {
                return null;
            }
            view = (View)parent;
        }
    }
    
    ItemInfo infoForChild(final View view) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }
    
    ItemInfo infoForPosition(final int n) {
        for (int i = 0; i < this.mItems.size(); ++i) {
            final ItemInfo itemInfo = this.mItems.get(i);
            if (itemInfo.position == n) {
                return itemInfo;
            }
        }
        return null;
    }
    
    void initViewPager() {
        this.setWillNotDraw(false);
        this.setDescendantFocusability(262144);
        this.setFocusable(true);
        final Context context = this.getContext();
        this.mScroller = new Scroller(context, ViewPager.sInterpolator);
        final ViewConfiguration value = ViewConfiguration.get(context);
        final float density = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(value);
        this.mMinimumVelocity = (int)(400.0f * density);
        this.mMaximumVelocity = value.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffectCompat(context);
        this.mRightEdge = new EdgeEffectCompat(context);
        this.mFlingDistance = (int)(25.0f * density);
        this.mCloseEnough = (int)(2.0f * density);
        this.mDefaultGutterSize = (int)(16.0f * density);
        ViewCompat.setAccessibilityDelegate((View)this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility((View)this) == 0) {
            ViewCompat.setImportantForAccessibility((View)this, 1);
        }
    }
    
    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        this.removeCallbacks(this.mEndScrollRunnable);
        super.onDetachedFromWindow();
    }
    
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPageMargin > 0 && this.mMarginDrawable != null && this.mItems.size() > 0 && this.mAdapter != null) {
            final int scrollX = this.getScrollX();
            final int width = this.getWidth();
            final float n = this.mPageMargin / width;
            int n2 = 0;
            ItemInfo itemInfo = this.mItems.get(0);
            float offset = itemInfo.offset;
            final int size = this.mItems.size();
            final int position = itemInfo.position;
            for (int position2 = this.mItems.get(size - 1).position, i = position; i < position2; ++i) {
                while (i > itemInfo.position && n2 < size) {
                    final ArrayList<ItemInfo> mItems = this.mItems;
                    ++n2;
                    itemInfo = mItems.get(n2);
                }
                float n3;
                if (i == itemInfo.position) {
                    n3 = (itemInfo.offset + itemInfo.widthFactor) * width;
                    offset = n + (itemInfo.offset + itemInfo.widthFactor);
                }
                else {
                    final float pageWidth = this.mAdapter.getPageWidth(i);
                    n3 = (offset + pageWidth) * width;
                    offset += pageWidth + n;
                }
                if (n3 + this.mPageMargin > scrollX) {
                    this.mMarginDrawable.setBounds((int)n3, this.mTopPageBounds, (int)(0.5f + (n3 + this.mPageMargin)), this.mBottomPageBounds);
                    this.mMarginDrawable.draw(canvas);
                }
                if (n3 > scrollX + width) {
                    break;
                }
            }
        }
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int n = 0xFF & motionEvent.getAction();
        if (n == 3 || n == 1) {
            this.mIsBeingDragged = false;
            this.mIsUnableToDrag = false;
            this.mActivePointerId = -1;
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
            return false;
        }
        if (n != 0) {
            if (this.mIsBeingDragged) {
                return true;
            }
            if (this.mIsUnableToDrag) {
                return false;
            }
        }
        switch (n) {
            case 2: {
                final int mActivePointerId = this.mActivePointerId;
                if (mActivePointerId == -1) {
                    break;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                final float n2 = x - this.mLastMotionX;
                final float abs = Math.abs(n2);
                final float y = MotionEventCompat.getY(motionEvent, pointerIndex);
                final float abs2 = Math.abs(y - this.mInitialMotionY);
                if (n2 != 0.0f && !this.isGutterDrag(this.mLastMotionX, n2) && this.canScroll((View)this, false, (int)n2, (int)x, (int)y)) {
                    this.mLastMotionX = x;
                    this.mLastMotionY = y;
                    this.mIsUnableToDrag = true;
                    return false;
                }
                if (abs > this.mTouchSlop && 0.5f * abs > abs2) {
                    this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                    this.setScrollState(1);
                    float mLastMotionX;
                    if (n2 > 0.0f) {
                        mLastMotionX = this.mInitialMotionX + this.mTouchSlop;
                    }
                    else {
                        mLastMotionX = this.mInitialMotionX - this.mTouchSlop;
                    }
                    this.mLastMotionX = mLastMotionX;
                    this.mLastMotionY = y;
                    this.setScrollingCacheEnabled(true);
                }
                else if (abs2 > this.mTouchSlop) {
                    this.mIsUnableToDrag = true;
                }
                if (this.mIsBeingDragged && this.performDrag(x)) {
                    ViewCompat.postInvalidateOnAnimation((View)this);
                    break;
                }
                break;
            }
            case 0: {
                final float x2 = motionEvent.getX();
                this.mInitialMotionX = x2;
                this.mLastMotionX = x2;
                final float y2 = motionEvent.getY();
                this.mInitialMotionY = y2;
                this.mLastMotionY = y2;
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                this.mIsUnableToDrag = false;
                this.mScroller.computeScrollOffset();
                if (this.mScrollState == 2 && Math.abs(this.mScroller.getFinalX() - this.mScroller.getCurrX()) > this.mCloseEnough) {
                    this.mScroller.abortAnimation();
                    this.mPopulatePending = false;
                    this.populate();
                    this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                    this.setScrollState(1);
                    break;
                }
                this.completeScroll(false);
                this.mIsBeingDragged = false;
                break;
            }
            case 6: {
                this.onSecondaryPointerUp(motionEvent);
                break;
            }
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        return this.mIsBeingDragged;
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        final int childCount = this.getChildCount();
        final int n5 = n3 - n;
        final int n6 = n4 - n2;
        int paddingLeft = this.getPaddingLeft();
        int paddingTop = this.getPaddingTop();
        int paddingRight = this.getPaddingRight();
        int paddingBottom = this.getPaddingBottom();
        final int scrollX = this.getScrollX();
        int mDecorChildCount = 0;
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.isDecor) {
                    final int n7 = 0x7 & layoutParams.gravity;
                    final int n8 = 0x70 & layoutParams.gravity;
                    int max = 0;
                    switch (n7) {
                        default: {
                            max = paddingLeft;
                            break;
                        }
                        case 3: {
                            max = paddingLeft;
                            paddingLeft += child.getMeasuredWidth();
                            break;
                        }
                        case 1: {
                            max = Math.max((n5 - child.getMeasuredWidth()) / 2, paddingLeft);
                            break;
                        }
                        case 5: {
                            max = n5 - paddingRight - child.getMeasuredWidth();
                            paddingRight += child.getMeasuredWidth();
                            break;
                        }
                    }
                    int max2 = 0;
                    switch (n8) {
                        default: {
                            max2 = paddingTop;
                            break;
                        }
                        case 48: {
                            max2 = paddingTop;
                            paddingTop += child.getMeasuredHeight();
                            break;
                        }
                        case 16: {
                            max2 = Math.max((n6 - child.getMeasuredHeight()) / 2, paddingTop);
                            break;
                        }
                        case 80: {
                            max2 = n6 - paddingBottom - child.getMeasuredHeight();
                            paddingBottom += child.getMeasuredHeight();
                            break;
                        }
                    }
                    final int n9 = max + scrollX;
                    child.layout(n9, max2, n9 + child.getMeasuredWidth(), max2 + child.getMeasuredHeight());
                    ++mDecorChildCount;
                }
            }
        }
        final int n10 = n5 - paddingLeft - paddingRight;
        for (int j = 0; j < childCount; ++j) {
            final View child2 = this.getChildAt(j);
            if (child2.getVisibility() != 8) {
                final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (!layoutParams2.isDecor) {
                    final ItemInfo infoForChild = this.infoForChild(child2);
                    if (infoForChild != null) {
                        final int n11 = paddingLeft + (int)(n10 * infoForChild.offset);
                        final int n12 = paddingTop;
                        if (layoutParams2.needsMeasure) {
                            layoutParams2.needsMeasure = false;
                            child2.measure(View$MeasureSpec.makeMeasureSpec((int)(n10 * layoutParams2.widthFactor), 1073741824), View$MeasureSpec.makeMeasureSpec(n6 - paddingTop - paddingBottom, 1073741824));
                        }
                        child2.layout(n11, n12, n11 + child2.getMeasuredWidth(), n12 + child2.getMeasuredHeight());
                    }
                }
            }
        }
        this.mTopPageBounds = paddingTop;
        this.mBottomPageBounds = n6 - paddingBottom;
        this.mDecorChildCount = mDecorChildCount;
        if (this.mFirstLayout) {
            this.scrollToItem(this.mCurItem, false, 0, false);
        }
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(final int n, final int n2) {
        this.setMeasuredDimension(getDefaultSize(0, n), getDefaultSize(0, n2));
        final int measuredWidth = this.getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        int n3 = measuredWidth - this.getPaddingLeft() - this.getPaddingRight();
        int n4 = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams != null && layoutParams.isDecor) {
                    final int n5 = 0x7 & layoutParams.gravity;
                    final int n6 = 0x70 & layoutParams.gravity;
                    int n7 = Integer.MIN_VALUE;
                    int n8 = Integer.MIN_VALUE;
                    boolean b;
                    if (n6 == 48 || n6 == 80) {
                        b = true;
                    }
                    else {
                        b = false;
                    }
                    final boolean b2 = n5 == 3 || n5 == 5;
                    if (b) {
                        n7 = 1073741824;
                    }
                    else if (b2) {
                        n8 = 1073741824;
                    }
                    int width = n3;
                    int height = n4;
                    if (layoutParams.width != -2) {
                        n7 = 1073741824;
                        if (layoutParams.width != -1) {
                            width = layoutParams.width;
                        }
                    }
                    if (layoutParams.height != -2) {
                        n8 = 1073741824;
                        if (layoutParams.height != -1) {
                            height = layoutParams.height;
                        }
                    }
                    child.measure(View$MeasureSpec.makeMeasureSpec(width, n7), View$MeasureSpec.makeMeasureSpec(height, n8));
                    if (b) {
                        n4 -= child.getMeasuredHeight();
                    }
                    else if (b2) {
                        n3 -= child.getMeasuredWidth();
                    }
                }
            }
        }
        this.mChildWidthMeasureSpec = View$MeasureSpec.makeMeasureSpec(n3, 1073741824);
        this.mChildHeightMeasureSpec = View$MeasureSpec.makeMeasureSpec(n4, 1073741824);
        this.mInLayout = true;
        this.populate();
        this.mInLayout = false;
        for (int childCount2 = this.getChildCount(), j = 0; j < childCount2; ++j) {
            final View child2 = this.getChildAt(j);
            if (child2.getVisibility() != 8) {
                final LayoutParams layoutParams2 = (LayoutParams)child2.getLayoutParams();
                if (layoutParams2 == null || !layoutParams2.isDecor) {
                    child2.measure(View$MeasureSpec.makeMeasureSpec((int)(n3 * layoutParams2.widthFactor), 1073741824), this.mChildHeightMeasureSpec);
                }
            }
        }
    }
    
    protected void onPageScrolled(final int n, final float n2, final int n3) {
        if (this.mDecorChildCount > 0) {
            final int scrollX = this.getScrollX();
            int paddingLeft = this.getPaddingLeft();
            int paddingRight = this.getPaddingRight();
            final int width = this.getWidth();
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.isDecor) {
                    int max = 0;
                    switch (0x7 & layoutParams.gravity) {
                        default: {
                            max = paddingLeft;
                            break;
                        }
                        case 3: {
                            max = paddingLeft;
                            paddingLeft += child.getWidth();
                            break;
                        }
                        case 1: {
                            max = Math.max((width - child.getMeasuredWidth()) / 2, paddingLeft);
                            break;
                        }
                        case 5: {
                            max = width - paddingRight - child.getMeasuredWidth();
                            paddingRight += child.getMeasuredWidth();
                            break;
                        }
                    }
                    final int n4 = max + scrollX - child.getLeft();
                    if (n4 != 0) {
                        child.offsetLeftAndRight(n4);
                    }
                }
            }
        }
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(n, n2, n3);
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrolled(n, n2, n3);
        }
        if (this.mPageTransformer != null) {
            final int scrollX2 = this.getScrollX();
            for (int childCount2 = this.getChildCount(), j = 0; j < childCount2; ++j) {
                final View child2 = this.getChildAt(j);
                if (!((LayoutParams)child2.getLayoutParams()).isDecor) {
                    this.mPageTransformer.transformPage(child2, (child2.getLeft() - scrollX2) / this.getClientWidth());
                }
            }
        }
        this.mCalledSuper = true;
    }
    
    protected boolean onRequestFocusInDescendants(final int n, final Rect rect) {
        final int childCount = this.getChildCount();
        int n2;
        int n3;
        int n4;
        if ((n & 0x2) != 0x0) {
            n2 = 0;
            n3 = 1;
            n4 = childCount;
        }
        else {
            n2 = childCount - 1;
            n3 = -1;
            n4 = -1;
        }
        for (int i = n2; i != n4; i += n3) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() == 0) {
                final ItemInfo infoForChild = this.infoForChild(child);
                if (infoForChild != null && infoForChild.position == this.mCurItem && child.requestFocus(n, rect)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (this.mAdapter != null) {
            this.mAdapter.restoreState(savedState.adapterState, savedState.loader);
            this.setCurrentItemInternal(savedState.position, false, true);
            return;
        }
        this.mRestoredCurItem = savedState.position;
        this.mRestoredAdapterState = savedState.adapterState;
        this.mRestoredClassLoader = savedState.loader;
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        if (this.mAdapter != null) {
            savedState.adapterState = this.mAdapter.saveState();
        }
        return (Parcelable)savedState;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        super.onSizeChanged(n, n2, n3, n4);
        if (n != n3) {
            this.recomputeScrollPosition(n, n3, this.mPageMargin, this.mPageMargin);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        if (this.mFakeDragging) {
            return true;
        }
        if (motionEvent.getAction() == 0 && motionEvent.getEdgeFlags() != 0) {
            return false;
        }
        if (this.mAdapter == null || this.mAdapter.getCount() == 0) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        final int n = 0xFF & motionEvent.getAction();
        int n2 = 0;
        switch (n) {
            case 0: {
                this.mScroller.abortAnimation();
                this.mPopulatePending = false;
                this.populate();
                final float x = motionEvent.getX();
                this.mInitialMotionX = x;
                this.mLastMotionX = x;
                final float y = motionEvent.getY();
                this.mInitialMotionY = y;
                this.mLastMotionY = y;
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                n2 = 0;
                break;
            }
            case 2: {
                if (!this.mIsBeingDragged) {
                    final int pointerIndex = MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId);
                    final float x2 = MotionEventCompat.getX(motionEvent, pointerIndex);
                    final float abs = Math.abs(x2 - this.mLastMotionX);
                    final float y2 = MotionEventCompat.getY(motionEvent, pointerIndex);
                    final float abs2 = Math.abs(y2 - this.mLastMotionY);
                    if (abs > this.mTouchSlop && abs > abs2) {
                        this.requestParentDisallowInterceptTouchEvent(this.mIsBeingDragged = true);
                        float mLastMotionX;
                        if (x2 - this.mInitialMotionX > 0.0f) {
                            mLastMotionX = this.mInitialMotionX + this.mTouchSlop;
                        }
                        else {
                            mLastMotionX = this.mInitialMotionX - this.mTouchSlop;
                        }
                        this.mLastMotionX = mLastMotionX;
                        this.mLastMotionY = y2;
                        this.setScrollState(1);
                        this.setScrollingCacheEnabled(true);
                        final ViewParent parent = this.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                final boolean mIsBeingDragged = this.mIsBeingDragged;
                n2 = 0;
                if (mIsBeingDragged) {
                    n2 = ((false | this.performDrag(MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId)))) ? 1 : 0);
                    break;
                }
                break;
            }
            case 1: {
                final boolean mIsBeingDragged2 = this.mIsBeingDragged;
                n2 = 0;
                if (mIsBeingDragged2) {
                    final VelocityTracker mVelocityTracker = this.mVelocityTracker;
                    mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                    final int n3 = (int)VelocityTrackerCompat.getXVelocity(mVelocityTracker, this.mActivePointerId);
                    this.mPopulatePending = true;
                    final int clientWidth = this.getClientWidth();
                    final int scrollX = this.getScrollX();
                    final ItemInfo infoForCurrentScrollPosition = this.infoForCurrentScrollPosition();
                    this.setCurrentItemInternal(this.determineTargetPage(infoForCurrentScrollPosition.position, (scrollX / clientWidth - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, n3, (int)(MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId)) - this.mInitialMotionX)), true, true, n3);
                    this.mActivePointerId = -1;
                    this.endDrag();
                    n2 = ((this.mLeftEdge.onRelease() | this.mRightEdge.onRelease()) ? 1 : 0);
                    break;
                }
                break;
            }
            case 3: {
                final boolean mIsBeingDragged3 = this.mIsBeingDragged;
                n2 = 0;
                if (mIsBeingDragged3) {
                    this.scrollToItem(this.mCurItem, true, 0, false);
                    this.mActivePointerId = -1;
                    this.endDrag();
                    n2 = ((this.mLeftEdge.onRelease() | this.mRightEdge.onRelease()) ? 1 : 0);
                    break;
                }
                break;
            }
            case 5: {
                final int actionIndex = MotionEventCompat.getActionIndex(motionEvent);
                this.mLastMotionX = MotionEventCompat.getX(motionEvent, actionIndex);
                this.mActivePointerId = MotionEventCompat.getPointerId(motionEvent, actionIndex);
                n2 = 0;
                break;
            }
            case 6: {
                this.onSecondaryPointerUp(motionEvent);
                this.mLastMotionX = MotionEventCompat.getX(motionEvent, MotionEventCompat.findPointerIndex(motionEvent, this.mActivePointerId));
                n2 = 0;
                break;
            }
        }
        if (n2 != 0) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
        return true;
    }
    
    boolean pageLeft() {
        if (this.mCurItem > 0) {
            this.setCurrentItem(-1 + this.mCurItem, true);
            return true;
        }
        return false;
    }
    
    boolean pageRight() {
        if (this.mAdapter != null && this.mCurItem < -1 + this.mAdapter.getCount()) {
            this.setCurrentItem(1 + this.mCurItem, true);
            return true;
        }
        return false;
    }
    
    void populate() {
        this.populate(this.mCurItem);
    }
    
    void populate(final int mCurItem) {
        int n = 2;
        final int mCurItem2 = this.mCurItem;
        ItemInfo infoForPosition = null;
        if (mCurItem2 != mCurItem) {
            if (this.mCurItem < mCurItem) {
                n = 66;
            }
            else {
                n = 17;
            }
            infoForPosition = this.infoForPosition(this.mCurItem);
            this.mCurItem = mCurItem;
        }
        if (this.mAdapter == null) {
            this.sortChildDrawingOrder();
        }
        else {
            if (this.mPopulatePending) {
                this.sortChildDrawingOrder();
                return;
            }
            if (this.getWindowToken() != null) {
                this.mAdapter.startUpdate(this);
                final int mOffscreenPageLimit = this.mOffscreenPageLimit;
                final int max = Math.max(0, this.mCurItem - mOffscreenPageLimit);
                final int count = this.mAdapter.getCount();
                final int min = Math.min(count - 1, mOffscreenPageLimit + this.mCurItem);
                if (count != this.mExpectedAdapterCount) {
                    try {
                        final String s = this.getResources().getResourceName(this.getId());
                        throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + count + " Pager id: " + s + " Pager class: " + this.getClass() + " Problematic adapter: " + this.mAdapter.getClass());
                    }
                    catch (Resources$NotFoundException ex) {
                        final String s = Integer.toHexString(this.getId());
                        throw new IllegalStateException("The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: " + this.mExpectedAdapterCount + ", found: " + count + " Pager id: " + s + " Pager class: " + this.getClass() + " Problematic adapter: " + this.mAdapter.getClass());
                    }
                }
                int n2 = 0;
                ItemInfo addNewItem;
                while (true) {
                    final int size = this.mItems.size();
                    addNewItem = null;
                    if (n2 >= size) {
                        break;
                    }
                    final ItemInfo itemInfo = this.mItems.get(n2);
                    if (itemInfo.position >= this.mCurItem) {
                        final int position = itemInfo.position;
                        final int mCurItem3 = this.mCurItem;
                        addNewItem = null;
                        if (position == mCurItem3) {
                            addNewItem = itemInfo;
                            break;
                        }
                        break;
                    }
                    else {
                        ++n2;
                    }
                }
                if (addNewItem == null && count > 0) {
                    addNewItem = this.addNewItem(this.mCurItem, n2);
                }
                if (addNewItem != null) {
                    float n3 = 0.0f;
                    int n4 = n2 - 1;
                    ItemInfo itemInfo2;
                    if (n4 >= 0) {
                        itemInfo2 = this.mItems.get(n4);
                    }
                    else {
                        itemInfo2 = null;
                    }
                    final int clientWidth = this.getClientWidth();
                    float n5;
                    if (clientWidth <= 0) {
                        n5 = 0.0f;
                    }
                    else {
                        n5 = 2.0f - addNewItem.widthFactor + this.getPaddingLeft() / clientWidth;
                    }
                    for (int i = -1 + this.mCurItem; i >= 0; --i) {
                        if (n3 >= n5 && i < max) {
                            if (itemInfo2 == null) {
                                break;
                            }
                            if (i == itemInfo2.position && !itemInfo2.scrolling) {
                                this.mItems.remove(n4);
                                this.mAdapter.destroyItem(this, i, itemInfo2.object);
                                --n4;
                                --n2;
                                if (n4 >= 0) {
                                    itemInfo2 = this.mItems.get(n4);
                                }
                                else {
                                    itemInfo2 = null;
                                }
                            }
                        }
                        else if (itemInfo2 != null && i == itemInfo2.position) {
                            n3 += itemInfo2.widthFactor;
                            if (--n4 >= 0) {
                                itemInfo2 = this.mItems.get(n4);
                            }
                            else {
                                itemInfo2 = null;
                            }
                        }
                        else {
                            n3 += this.addNewItem(i, n4 + 1).widthFactor;
                            ++n2;
                            if (n4 >= 0) {
                                itemInfo2 = this.mItems.get(n4);
                            }
                            else {
                                itemInfo2 = null;
                            }
                        }
                    }
                    float widthFactor = addNewItem.widthFactor;
                    int n6 = n2 + 1;
                    if (widthFactor < 2.0f) {
                        ItemInfo itemInfo3;
                        if (n6 < this.mItems.size()) {
                            itemInfo3 = this.mItems.get(n6);
                        }
                        else {
                            itemInfo3 = null;
                        }
                        float n7;
                        if (clientWidth <= 0) {
                            n7 = 0.0f;
                        }
                        else {
                            n7 = 2.0f + this.getPaddingRight() / clientWidth;
                        }
                        for (int j = 1 + this.mCurItem; j < count; ++j) {
                            if (widthFactor >= n7 && j > min) {
                                if (itemInfo3 == null) {
                                    break;
                                }
                                if (j == itemInfo3.position && !itemInfo3.scrolling) {
                                    this.mItems.remove(n6);
                                    this.mAdapter.destroyItem(this, j, itemInfo3.object);
                                    if (n6 < this.mItems.size()) {
                                        itemInfo3 = this.mItems.get(n6);
                                    }
                                    else {
                                        itemInfo3 = null;
                                    }
                                }
                            }
                            else if (itemInfo3 != null && j == itemInfo3.position) {
                                widthFactor += itemInfo3.widthFactor;
                                ++n6;
                                if (n6 < this.mItems.size()) {
                                    itemInfo3 = this.mItems.get(n6);
                                }
                                else {
                                    itemInfo3 = null;
                                }
                            }
                            else {
                                final ItemInfo addNewItem2 = this.addNewItem(j, n6);
                                ++n6;
                                widthFactor += addNewItem2.widthFactor;
                                if (n6 < this.mItems.size()) {
                                    itemInfo3 = this.mItems.get(n6);
                                }
                                else {
                                    itemInfo3 = null;
                                }
                            }
                        }
                    }
                    this.calculatePageOffsets(addNewItem, n2, infoForPosition);
                }
                final PagerAdapter mAdapter = this.mAdapter;
                final int mCurItem4 = this.mCurItem;
                Object object;
                if (addNewItem != null) {
                    object = addNewItem.object;
                }
                else {
                    object = null;
                }
                mAdapter.setPrimaryItem(this, mCurItem4, object);
                this.mAdapter.finishUpdate(this);
                for (int childCount = this.getChildCount(), k = 0; k < childCount; ++k) {
                    final View child = this.getChildAt(k);
                    final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                    layoutParams.childIndex = k;
                    if (!layoutParams.isDecor && layoutParams.widthFactor == 0.0f) {
                        final ItemInfo infoForChild = this.infoForChild(child);
                        if (infoForChild != null) {
                            layoutParams.widthFactor = infoForChild.widthFactor;
                            layoutParams.position = infoForChild.position;
                        }
                    }
                }
                this.sortChildDrawingOrder();
                if (this.hasFocus()) {
                    final View focus = this.findFocus();
                    ItemInfo infoForAnyChild;
                    if (focus != null) {
                        infoForAnyChild = this.infoForAnyChild(focus);
                    }
                    else {
                        infoForAnyChild = null;
                    }
                    if (infoForAnyChild == null || infoForAnyChild.position != this.mCurItem) {
                        for (int l = 0; l < this.getChildCount(); ++l) {
                            final View child2 = this.getChildAt(l);
                            final ItemInfo infoForChild2 = this.infoForChild(child2);
                            if (infoForChild2 != null && infoForChild2.position == this.mCurItem && child2.requestFocus(n)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void removeView(final View view) {
        if (this.mInLayout) {
            this.removeViewInLayout(view);
            return;
        }
        super.removeView(view);
    }
    
    public void setAdapter(final PagerAdapter mAdapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unregisterDataSetObserver(this.mObserver);
            this.mAdapter.startUpdate(this);
            for (int i = 0; i < this.mItems.size(); ++i) {
                final ItemInfo itemInfo = this.mItems.get(i);
                this.mAdapter.destroyItem(this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate(this);
            this.mItems.clear();
            this.removeNonDecorViews();
            this.scrollTo(this.mCurItem = 0, 0);
        }
        final PagerAdapter mAdapter2 = this.mAdapter;
        this.mAdapter = mAdapter;
        this.mExpectedAdapterCount = 0;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.registerDataSetObserver(this.mObserver);
            this.mPopulatePending = false;
            final boolean mFirstLayout = this.mFirstLayout;
            this.mFirstLayout = true;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                this.setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            }
            else if (!mFirstLayout) {
                this.populate();
            }
            else {
                this.requestLayout();
            }
        }
        if (this.mAdapterChangeListener != null && mAdapter2 != mAdapter) {
            this.mAdapterChangeListener.onAdapterChanged(mAdapter2, mAdapter);
        }
    }
    
    void setChildrenDrawingOrderEnabledCompat(final boolean p0) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: bipush          7
        //     5: if_icmplt       71
        //     8: aload_0        
        //     9: getfield        android/support/v4/view/ViewPager.mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;
        //    12: ifnonnull       42
        //    15: iconst_1       
        //    16: anewarray       Ljava/lang/Class;
        //    19: astore          9
        //    21: aload           9
        //    23: iconst_0       
        //    24: getstatic       java/lang/Boolean.TYPE:Ljava/lang/Class;
        //    27: aastore        
        //    28: aload_0        
        //    29: ldc             Landroid/view/ViewGroup;.class
        //    31: ldc_w           "setChildrenDrawingOrderEnabled"
        //    34: aload           9
        //    36: invokevirtual   java/lang/Class.getDeclaredMethod:(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
        //    39: putfield        android/support/v4/view/ViewPager.mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;
        //    42: aload_0        
        //    43: getfield        android/support/v4/view/ViewPager.mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;
        //    46: astore          4
        //    48: iconst_1       
        //    49: anewarray       Ljava/lang/Object;
        //    52: astore          5
        //    54: aload           5
        //    56: iconst_0       
        //    57: iload_1        
        //    58: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
        //    61: aastore        
        //    62: aload           4
        //    64: aload_0        
        //    65: aload           5
        //    67: invokevirtual   java/lang/reflect/Method.invoke:(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
        //    70: pop            
        //    71: return         
        //    72: astore          7
        //    74: ldc             "ViewPager"
        //    76: ldc_w           "Can't find setChildrenDrawingOrderEnabled"
        //    79: aload           7
        //    81: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //    84: pop            
        //    85: goto            42
        //    88: astore_2       
        //    89: ldc             "ViewPager"
        //    91: ldc_w           "Error changing children drawing order"
        //    94: aload_2        
        //    95: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //    98: pop            
        //    99: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                             
        //  -----  -----  -----  -----  ---------------------------------
        //  15     42     72     88     Ljava/lang/NoSuchMethodException;
        //  42     71     88     100    Ljava/lang/Exception;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0042:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2592)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:304)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:225)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:110)
        //     at advisor.main.Main.transformClassesToJava(Main.java:54)
        //     at advisor.main.Main.main(Main.java:21)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void setCurrentItem(final int n) {
        this.mPopulatePending = false;
        this.setCurrentItemInternal(n, !this.mFirstLayout, false);
    }
    
    public void setCurrentItem(final int n, final boolean b) {
        this.setCurrentItemInternal(n, b, this.mPopulatePending = false);
    }
    
    void setCurrentItemInternal(final int n, final boolean b, final boolean b2) {
        this.setCurrentItemInternal(n, b, b2, 0);
    }
    
    void setCurrentItemInternal(int mCurItem, final boolean b, final boolean b2, final int n) {
        boolean scrolling = true;
        if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        if (!b2 && this.mCurItem == mCurItem && this.mItems.size() != 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        if (mCurItem < 0) {
            mCurItem = 0;
        }
        else if (mCurItem >= this.mAdapter.getCount()) {
            mCurItem = -1 + this.mAdapter.getCount();
        }
        final int mOffscreenPageLimit = this.mOffscreenPageLimit;
        if (mCurItem > mOffscreenPageLimit + this.mCurItem || mCurItem < this.mCurItem - mOffscreenPageLimit) {
            for (int i = 0; i < this.mItems.size(); ++i) {
                this.mItems.get(i).scrolling = scrolling;
            }
        }
        if (this.mCurItem == mCurItem) {
            scrolling = false;
        }
        if (this.mFirstLayout) {
            this.mCurItem = mCurItem;
            if (scrolling && this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageSelected(mCurItem);
            }
            if (scrolling && this.mInternalPageChangeListener != null) {
                this.mInternalPageChangeListener.onPageSelected(mCurItem);
            }
            this.requestLayout();
            return;
        }
        this.populate(mCurItem);
        this.scrollToItem(mCurItem, b, n, scrolling);
    }
    
    OnPageChangeListener setInternalPageChangeListener(final OnPageChangeListener mInternalPageChangeListener) {
        final OnPageChangeListener mInternalPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = mInternalPageChangeListener;
        return mInternalPageChangeListener2;
    }
    
    public void setOffscreenPageLimit(int mOffscreenPageLimit) {
        if (mOffscreenPageLimit < 1) {
            Log.w("ViewPager", "Requested offscreen page limit " + mOffscreenPageLimit + " too small; defaulting to " + 1);
            mOffscreenPageLimit = 1;
        }
        if (mOffscreenPageLimit != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = mOffscreenPageLimit;
            this.populate();
        }
    }
    
    void setOnAdapterChangeListener(final OnAdapterChangeListener mAdapterChangeListener) {
        this.mAdapterChangeListener = mAdapterChangeListener;
    }
    
    public void setOnPageChangeListener(final OnPageChangeListener mOnPageChangeListener) {
        this.mOnPageChangeListener = mOnPageChangeListener;
    }
    
    public void setPageMargin(final int mPageMargin) {
        final int mPageMargin2 = this.mPageMargin;
        this.mPageMargin = mPageMargin;
        final int width = this.getWidth();
        this.recomputeScrollPosition(width, width, mPageMargin, mPageMargin2);
        this.requestLayout();
    }
    
    public void setPageMarginDrawable(final int n) {
        this.setPageMarginDrawable(this.getContext().getResources().getDrawable(n));
    }
    
    public void setPageMarginDrawable(final Drawable mMarginDrawable) {
        this.mMarginDrawable = mMarginDrawable;
        if (mMarginDrawable != null) {
            this.refreshDrawableState();
        }
        this.setWillNotDraw(mMarginDrawable == null);
        this.invalidate();
    }
    
    public void setPageTransformer(final boolean b, final PageTransformer mPageTransformer) {
        int mDrawingOrder = 1;
        if (Build$VERSION.SDK_INT >= 11) {
            int childrenDrawingOrderEnabledCompat;
            if (mPageTransformer != null) {
                childrenDrawingOrderEnabledCompat = mDrawingOrder;
            }
            else {
                childrenDrawingOrderEnabledCompat = 0;
            }
            boolean b2;
            if (this.mPageTransformer != null) {
                b2 = (mDrawingOrder != 0);
            }
            else {
                b2 = false;
            }
            int n;
            if (childrenDrawingOrderEnabledCompat != b2) {
                n = mDrawingOrder;
            }
            else {
                n = 0;
            }
            this.mPageTransformer = mPageTransformer;
            this.setChildrenDrawingOrderEnabledCompat(childrenDrawingOrderEnabledCompat != 0);
            if (childrenDrawingOrderEnabledCompat != 0) {
                if (b) {
                    mDrawingOrder = 2;
                }
                this.mDrawingOrder = mDrawingOrder;
            }
            else {
                this.mDrawingOrder = 0;
            }
            if (n != 0) {
                this.populate();
            }
        }
    }
    
    void smoothScrollTo(final int n, final int n2) {
        this.smoothScrollTo(n, n2, 0);
    }
    
    void smoothScrollTo(final int n, final int n2, final int n3) {
        if (this.getChildCount() == 0) {
            this.setScrollingCacheEnabled(false);
            return;
        }
        final int scrollX = this.getScrollX();
        final int scrollY = this.getScrollY();
        final int n4 = n - scrollX;
        final int n5 = n2 - scrollY;
        if (n4 == 0 && n5 == 0) {
            this.completeScroll(false);
            this.populate();
            this.setScrollState(0);
            return;
        }
        this.setScrollingCacheEnabled(true);
        this.setScrollState(2);
        final int clientWidth = this.getClientWidth();
        final int n6 = clientWidth / 2;
        final float n7 = n6 + n6 * this.distanceInfluenceForSnapDuration(Math.min(1.0f, 1.0f * Math.abs(n4) / clientWidth));
        final int abs = Math.abs(n3);
        int n8;
        if (abs > 0) {
            n8 = 4 * Math.round(1000.0f * Math.abs(n7 / abs));
        }
        else {
            n8 = (int)(100.0f * (1.0f + Math.abs(n4) / (clientWidth * this.mAdapter.getPageWidth(this.mCurItem) + this.mPageMargin)));
        }
        this.mScroller.startScroll(scrollX, scrollY, n4, n5, Math.min(n8, 600));
        ViewCompat.postInvalidateOnAnimation((View)this);
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }
    
    interface Decor
    {
    }
    
    static class ItemInfo
    {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;
    }
    
    public static class LayoutParams extends ViewGroup$LayoutParams
    {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor;
        
        public LayoutParams() {
            super(-1, -1);
            this.widthFactor = 0.0f;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.widthFactor = 0.0f;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }
    
    class MyAccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private boolean canScroll() {
            return ViewPager.this.mAdapter != null && ViewPager.this.mAdapter.getCount() > 1;
        }
        
        @Override
        public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName((CharSequence)ViewPager.class.getName());
            final AccessibilityRecordCompat obtain = AccessibilityRecordCompat.obtain();
            obtain.setScrollable(this.canScroll());
            if (accessibilityEvent.getEventType() == 4096 && ViewPager.this.mAdapter != null) {
                obtain.setItemCount(ViewPager.this.mAdapter.getCount());
                obtain.setFromIndex(ViewPager.this.mCurItem);
                obtain.setToIndex(ViewPager.this.mCurItem);
            }
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
            accessibilityNodeInfoCompat.setScrollable(this.canScroll());
            if (ViewPager.this.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }
        
        @Override
        public boolean performAccessibilityAction(final View view, final int n, final Bundle bundle) {
            if (super.performAccessibilityAction(view, n, bundle)) {
                return true;
            }
            switch (n) {
                default: {
                    return false;
                }
                case 4096: {
                    if (ViewPager.this.canScrollHorizontally(1)) {
                        ViewPager.this.setCurrentItem(1 + ViewPager.this.mCurItem);
                        return true;
                    }
                    return false;
                }
                case 8192: {
                    if (ViewPager.this.canScrollHorizontally(-1)) {
                        ViewPager.this.setCurrentItem(-1 + ViewPager.this.mCurItem);
                        return true;
                    }
                    return false;
                }
            }
        }
    }
    
    interface OnAdapterChangeListener
    {
        void onAdapterChanged(PagerAdapter p0, PagerAdapter p1);
    }
    
    public interface OnPageChangeListener
    {
        void onPageScrollStateChanged(int p0);
        
        void onPageScrolled(int p0, float p1, int p2);
        
        void onPageSelected(int p0);
    }
    
    public interface PageTransformer
    {
        void transformPage(View p0, float p1);
    }
    
    private class PagerObserver extends DataSetObserver
    {
        public void onChanged() {
            ViewPager.this.dataSetChanged();
        }
        
        public void onInvalidated() {
            ViewPager.this.dataSetChanged();
        }
    }
    
    public static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        Parcelable adapterState;
        ClassLoader loader;
        int position;
        
        static {
            CREATOR = ParcelableCompat.newCreator((ParcelableCompatCreatorCallbacks<SavedState>)new ParcelableCompatCreatorCallbacks<SavedState>() {
                @Override
                public SavedState createFromParcel(final Parcel parcel, final ClassLoader classLoader) {
                    return new SavedState(parcel, classLoader);
                }
                
                @Override
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            });
        }
        
        SavedState(final Parcel parcel, ClassLoader classLoader) {
            super(parcel);
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, n);
        }
    }
    
    public static class SimpleOnPageChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageScrollStateChanged(final int n) {
        }
        
        @Override
        public void onPageScrolled(final int n, final float n2, final int n3) {
        }
        
        @Override
        public void onPageSelected(final int n) {
        }
    }
    
    static class ViewPositionComparator implements Comparator<View>
    {
        @Override
        public int compare(final View view, final View view2) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            final LayoutParams layoutParams2 = (LayoutParams)view2.getLayoutParams();
            if (layoutParams.isDecor == layoutParams2.isDecor) {
                return layoutParams.position - layoutParams2.position;
            }
            if (layoutParams.isDecor) {
                return 1;
            }
            return -1;
        }
    }
}
