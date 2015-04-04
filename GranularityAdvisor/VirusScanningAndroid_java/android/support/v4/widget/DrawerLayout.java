package android.support.v4.widget;

import android.graphics.drawable.*;
import android.content.*;
import android.util.*;
import android.support.v4.view.*;
import android.graphics.*;
import android.support.v4.view.accessibility.*;
import android.view.accessibility.*;
import android.content.res.*;
import android.view.*;
import android.os.*;

public class DrawerLayout extends ViewGroup
{
    private static final boolean ALLOW_EDGE_LOCK = false;
    private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
    private static final int DEFAULT_SCRIM_COLOR = -1728053248;
    private static final int[] LAYOUT_ATTRS;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MIN_DRAWER_MARGIN = 64;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final int PEEK_DELAY = 160;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "DrawerLayout";
    private static final float TOUCH_SLOP_SENSITIVITY = 1.0f;
    private boolean mChildrenCanceledTouch;
    private boolean mDisallowInterceptRequested;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    private DrawerListener mListener;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mMinDrawerMargin;
    private final ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowLeft;
    private Drawable mShadowRight;
    
    static {
        LAYOUT_ATTRS = new int[] { 16842931 };
    }
    
    public DrawerLayout(final Context context) {
        this(context, null);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public DrawerLayout(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mScrimColor = -1728053248;
        this.mScrimPaint = new Paint();
        this.mFirstLayout = true;
        final float density = this.getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int)(0.5f + 64.0f * density);
        final float n2 = 400.0f * density;
        this.mLeftCallback = new ViewDragCallback(3);
        this.mRightCallback = new ViewDragCallback(5);
        (this.mLeftDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mLeftCallback)).setEdgeTrackingEnabled(1);
        this.mLeftDragger.setMinVelocity(n2);
        this.mLeftCallback.setDragger(this.mLeftDragger);
        (this.mRightDragger = ViewDragHelper.create(this, 1.0f, (ViewDragHelper.Callback)this.mRightCallback)).setEdgeTrackingEnabled(2);
        this.mRightDragger.setMinVelocity(n2);
        this.mRightCallback.setDragger(this.mRightDragger);
        this.setFocusableInTouchMode(true);
        ViewCompat.setAccessibilityDelegate((View)this, new AccessibilityDelegate());
        ViewGroupCompat.setMotionEventSplittingEnabled(this, false);
    }
    
    private View findVisibleDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child) && this.isDrawerVisible(child)) {
                return child;
            }
        }
        return null;
    }
    
    static String gravityToString(final int n) {
        if ((n & 0x3) == 0x3) {
            return "LEFT";
        }
        if ((n & 0x5) == 0x5) {
            return "RIGHT";
        }
        return Integer.toHexString(n);
    }
    
    private static boolean hasOpaqueBackground(final View view) {
        final Drawable background = view.getBackground();
        boolean b = false;
        if (background != null) {
            final int opacity = background.getOpacity();
            b = false;
            if (opacity == -1) {
                b = true;
            }
        }
        return b;
    }
    
    private boolean hasPeekingDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            if (((LayoutParams)this.getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return false;
    }
    
    private boolean hasVisibleDrawer() {
        return this.findVisibleDrawer() != null;
    }
    
    void cancelChildViewTouch() {
        if (!this.mChildrenCanceledTouch) {
            final long uptimeMillis = SystemClock.uptimeMillis();
            final MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                this.getChildAt(i).dispatchTouchEvent(obtain);
            }
            obtain.recycle();
            this.mChildrenCanceledTouch = true;
        }
    }
    
    boolean checkDrawerViewAbsoluteGravity(final View view, final int n) {
        return (n & this.getDrawerViewAbsoluteGravity(view)) == n;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams instanceof LayoutParams && super.checkLayoutParams(viewGroup$LayoutParams);
    }
    
    public void closeDrawer(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
        }
        this.closeDrawer(drawerWithGravity);
    }
    
    public void closeDrawer(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        if (this.mFirstLayout) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            layoutParams.onScreen = 0.0f;
            layoutParams.knownOpen = false;
        }
        else if (this.checkDrawerViewAbsoluteGravity(view, 3)) {
            this.mLeftDragger.smoothSlideViewTo(view, -view.getWidth(), view.getTop());
        }
        else {
            this.mRightDragger.smoothSlideViewTo(view, this.getWidth(), view.getTop());
        }
        this.invalidate();
    }
    
    public void closeDrawers() {
        this.closeDrawers(false);
    }
    
    void closeDrawers(final boolean b) {
        boolean b2 = false;
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
            if (this.isDrawerView(child) && (!b || layoutParams.isPeeking)) {
                final int width = child.getWidth();
                if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                    b2 |= this.mLeftDragger.smoothSlideViewTo(child, -width, child.getTop());
                }
                else {
                    b2 |= this.mRightDragger.smoothSlideViewTo(child, this.getWidth(), child.getTop());
                }
                layoutParams.isPeeking = false;
            }
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (b2) {
            this.invalidate();
        }
    }
    
    public void computeScroll() {
        final int childCount = this.getChildCount();
        float max = 0.0f;
        for (int i = 0; i < childCount; ++i) {
            max = Math.max(max, ((LayoutParams)this.getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = max;
        if (this.mLeftDragger.continueSettling(true) | this.mRightDragger.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation((View)this);
        }
    }
    
    void dispatchOnDrawerClosed(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.knownOpen) {
            layoutParams.knownOpen = false;
            if (this.mListener != null) {
                this.mListener.onDrawerClosed(view);
            }
            this.sendAccessibilityEvent(32);
        }
    }
    
    void dispatchOnDrawerOpened(final View view) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (!layoutParams.knownOpen) {
            layoutParams.knownOpen = true;
            if (this.mListener != null) {
                this.mListener.onDrawerOpened(view);
            }
            view.sendAccessibilityEvent(32);
        }
    }
    
    void dispatchOnDrawerSlide(final View view, final float n) {
        if (this.mListener != null) {
            this.mListener.onDrawerSlide(view, n);
        }
    }
    
    protected boolean drawChild(final Canvas canvas, final View view, final long n) {
        final int height = this.getHeight();
        final boolean contentView = this.isContentView(view);
        int width = this.getWidth();
        final int save = canvas.save();
        int n2 = 0;
        if (contentView) {
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child != view && child.getVisibility() == 0 && hasOpaqueBackground(child) && this.isDrawerView(child) && child.getHeight() >= height) {
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        final int right = child.getRight();
                        if (right > n2) {
                            n2 = right;
                        }
                    }
                    else {
                        final int left = child.getLeft();
                        if (left < width) {
                            width = left;
                        }
                    }
                }
            }
            canvas.clipRect(n2, 0, width, this.getHeight());
        }
        final boolean drawChild = super.drawChild(canvas, view, n);
        canvas.restoreToCount(save);
        if (this.mScrimOpacity > 0.0f && contentView) {
            this.mScrimPaint.setColor((int)(((0xFF000000 & this.mScrimColor) >>> 24) * this.mScrimOpacity) << 24 | (0xFFFFFF & this.mScrimColor));
            canvas.drawRect((float)n2, 0.0f, (float)width, (float)this.getHeight(), this.mScrimPaint);
        }
        else {
            if (this.mShadowLeft != null && this.checkDrawerViewAbsoluteGravity(view, 3)) {
                final int intrinsicWidth = this.mShadowLeft.getIntrinsicWidth();
                final int right2 = view.getRight();
                final float max = Math.max(0.0f, Math.min(right2 / this.mLeftDragger.getEdgeSize(), 1.0f));
                this.mShadowLeft.setBounds(right2, view.getTop(), right2 + intrinsicWidth, view.getBottom());
                this.mShadowLeft.setAlpha((int)(255.0f * max));
                this.mShadowLeft.draw(canvas);
                return drawChild;
            }
            if (this.mShadowRight != null && this.checkDrawerViewAbsoluteGravity(view, 5)) {
                final int intrinsicWidth2 = this.mShadowRight.getIntrinsicWidth();
                final int left2 = view.getLeft();
                final float max2 = Math.max(0.0f, Math.min((this.getWidth() - left2) / this.mRightDragger.getEdgeSize(), 1.0f));
                this.mShadowRight.setBounds(left2 - intrinsicWidth2, view.getTop(), left2, view.getBottom());
                this.mShadowRight.setAlpha((int)(255.0f * max2));
                this.mShadowRight.draw(canvas);
                return drawChild;
            }
        }
        return drawChild;
    }
    
    View findDrawerWithGravity(final int n) {
        final int n2 = 0x7 & GravityCompat.getAbsoluteGravity(n, ViewCompat.getLayoutDirection((View)this));
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if ((0x7 & this.getDrawerViewAbsoluteGravity(child)) == n2) {
                return child;
            }
        }
        return null;
    }
    
    View findOpenDrawer() {
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (((LayoutParams)child.getLayoutParams()).knownOpen) {
                return child;
            }
        }
        return null;
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new LayoutParams(-1, -1);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new LayoutParams(this.getContext(), set);
    }
    
    protected ViewGroup$LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            return (ViewGroup$LayoutParams)new LayoutParams((LayoutParams)viewGroup$LayoutParams);
        }
        if (viewGroup$LayoutParams instanceof ViewGroup$MarginLayoutParams) {
            return (ViewGroup$LayoutParams)new LayoutParams((ViewGroup$MarginLayoutParams)viewGroup$LayoutParams);
        }
        return (ViewGroup$LayoutParams)new LayoutParams(viewGroup$LayoutParams);
    }
    
    public int getDrawerLockMode(final int n) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            return this.mLockModeLeft;
        }
        if (absoluteGravity == 5) {
            return this.mLockModeRight;
        }
        return 0;
    }
    
    public int getDrawerLockMode(final View view) {
        final int drawerViewAbsoluteGravity = this.getDrawerViewAbsoluteGravity(view);
        if (drawerViewAbsoluteGravity == 3) {
            return this.mLockModeLeft;
        }
        if (drawerViewAbsoluteGravity == 5) {
            return this.mLockModeRight;
        }
        return 0;
    }
    
    int getDrawerViewAbsoluteGravity(final View view) {
        return GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection((View)this));
    }
    
    float getDrawerViewOffset(final View view) {
        return ((LayoutParams)view.getLayoutParams()).onScreen;
    }
    
    boolean isContentView(final View view) {
        return ((LayoutParams)view.getLayoutParams()).gravity == 0;
    }
    
    public boolean isDrawerOpen(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerOpen(drawerWithGravity);
    }
    
    public boolean isDrawerOpen(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return ((LayoutParams)view.getLayoutParams()).knownOpen;
    }
    
    boolean isDrawerView(final View view) {
        return (0x7 & GravityCompat.getAbsoluteGravity(((LayoutParams)view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view))) != 0x0;
    }
    
    public boolean isDrawerVisible(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        return drawerWithGravity != null && this.isDrawerVisible(drawerWithGravity);
    }
    
    public boolean isDrawerVisible(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a drawer");
        }
        return ((LayoutParams)view.getLayoutParams()).onScreen > 0.0f;
    }
    
    void moveDrawerToOffset(final View view, final float n) {
        final float drawerViewOffset = this.getDrawerViewOffset(view);
        final int width = view.getWidth();
        int n2 = (int)(n * width) - (int)(drawerViewOffset * width);
        if (!this.checkDrawerViewAbsoluteGravity(view, 3)) {
            n2 = -n2;
        }
        view.offsetLeftAndRight(n2);
        this.setDrawerViewOffset(view, n);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        final int actionMasked = MotionEventCompat.getActionMasked(motionEvent);
        final boolean b = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent) | this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        boolean b2 = false;
        switch (actionMasked) {
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                final float n = fcmpl(this.mScrimOpacity, 0.0f);
                b2 = false;
                if (n > 0) {
                    final boolean contentView = this.isContentView(this.mLeftDragger.findTopChildUnder((int)x, (int)y));
                    b2 = false;
                    if (contentView) {
                        b2 = true;
                    }
                }
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                break;
            }
            case 2: {
                final boolean checkTouchSlop = this.mLeftDragger.checkTouchSlop(3);
                b2 = false;
                if (checkTouchSlop) {
                    this.mLeftCallback.removeCallbacks();
                    this.mRightCallback.removeCallbacks();
                    b2 = false;
                    break;
                }
                break;
            }
            case 1:
            case 3: {
                this.closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                b2 = false;
                break;
            }
        }
        if (!b && !b2 && !this.hasPeekingDrawer()) {
            final boolean mChildrenCanceledTouch = this.mChildrenCanceledTouch;
            final boolean b3 = false;
            if (!mChildrenCanceledTouch) {
                return b3;
            }
        }
        return true;
    }
    
    public boolean onKeyDown(final int n, final KeyEvent keyEvent) {
        if (n == 4 && this.hasVisibleDrawer()) {
            KeyEventCompat.startTracking(keyEvent);
            return true;
        }
        return super.onKeyDown(n, keyEvent);
    }
    
    public boolean onKeyUp(final int n, final KeyEvent keyEvent) {
        if (n == 4) {
            final View visibleDrawer = this.findVisibleDrawer();
            if (visibleDrawer != null && this.getDrawerLockMode(visibleDrawer) == 0) {
                this.closeDrawers();
            }
            return visibleDrawer != null;
        }
        return super.onKeyUp(n, keyEvent);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        this.mInLayout = true;
        final int n5 = n3 - n;
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (this.isContentView(child)) {
                    child.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + child.getMeasuredWidth(), layoutParams.topMargin + child.getMeasuredHeight());
                }
                else {
                    final int measuredWidth = child.getMeasuredWidth();
                    final int measuredHeight = child.getMeasuredHeight();
                    int n6;
                    float n7;
                    if (this.checkDrawerViewAbsoluteGravity(child, 3)) {
                        n6 = -measuredWidth + (int)(measuredWidth * layoutParams.onScreen);
                        n7 = (measuredWidth + n6) / measuredWidth;
                    }
                    else {
                        n6 = n5 - (int)(measuredWidth * layoutParams.onScreen);
                        n7 = (n5 - n6) / measuredWidth;
                    }
                    int n8;
                    if (n7 != layoutParams.onScreen) {
                        n8 = 1;
                    }
                    else {
                        n8 = 0;
                    }
                    switch (0x70 & layoutParams.gravity) {
                        default: {
                            child.layout(n6, layoutParams.topMargin, n6 + measuredWidth, measuredHeight + layoutParams.topMargin);
                            break;
                        }
                        case 80: {
                            final int n9 = n4 - n2;
                            child.layout(n6, n9 - layoutParams.bottomMargin - child.getMeasuredHeight(), n6 + measuredWidth, n9 - layoutParams.bottomMargin);
                            break;
                        }
                        case 16: {
                            final int n10 = n4 - n2;
                            int topMargin = (n10 - measuredHeight) / 2;
                            if (topMargin < layoutParams.topMargin) {
                                topMargin = layoutParams.topMargin;
                            }
                            else if (topMargin + measuredHeight > n10 - layoutParams.bottomMargin) {
                                topMargin = n10 - layoutParams.bottomMargin - measuredHeight;
                            }
                            child.layout(n6, topMargin, n6 + measuredWidth, topMargin + measuredHeight);
                            break;
                        }
                    }
                    if (n8 != 0) {
                        this.setDrawerViewOffset(child, n7);
                    }
                    int visibility;
                    if (layoutParams.onScreen > 0.0f) {
                        visibility = 0;
                    }
                    else {
                        visibility = 4;
                    }
                    if (child.getVisibility() != visibility) {
                        child.setVisibility(visibility);
                    }
                }
            }
        }
        this.mInLayout = false;
        this.mFirstLayout = false;
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int mode = View$MeasureSpec.getMode(n);
        final int mode2 = View$MeasureSpec.getMode(n2);
        int size = View$MeasureSpec.getSize(n);
        int size2 = View$MeasureSpec.getSize(n2);
        if (mode != 1073741824 || mode2 != 1073741824) {
            if (!this.isInEditMode()) {
                throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
            if (mode != Integer.MIN_VALUE && mode == 0) {
                size = 300;
            }
            if (mode2 != Integer.MIN_VALUE && mode2 == 0) {
                size2 = 300;
            }
        }
        this.setMeasuredDimension(size, size2);
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (this.isContentView(child)) {
                    child.measure(View$MeasureSpec.makeMeasureSpec(size - layoutParams.leftMargin - layoutParams.rightMargin, 1073741824), View$MeasureSpec.makeMeasureSpec(size2 - layoutParams.topMargin - layoutParams.bottomMargin, 1073741824));
                }
                else {
                    if (!this.isDrawerView(child)) {
                        throw new IllegalStateException("Child " + child + " at index " + i + " does not have a valid layout_gravity - must be Gravity.LEFT, " + "Gravity.RIGHT or Gravity.NO_GRAVITY");
                    }
                    final int n3 = 0x7 & this.getDrawerViewAbsoluteGravity(child);
                    if ((0x0 & n3) != 0x0) {
                        throw new IllegalStateException("Child drawer has absolute gravity " + gravityToString(n3) + " but this " + "DrawerLayout" + " already has a " + "drawer view along that edge");
                    }
                    child.measure(getChildMeasureSpec(n, this.mMinDrawerMargin + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(n2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                }
            }
        }
    }
    
    protected void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.openDrawerGravity != 0) {
            final View drawerWithGravity = this.findDrawerWithGravity(savedState.openDrawerGravity);
            if (drawerWithGravity != null) {
                this.openDrawer(drawerWithGravity);
            }
        }
        this.setDrawerLockMode(savedState.lockModeLeft, 3);
        this.setDrawerLockMode(savedState.lockModeRight, 5);
    }
    
    protected Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (this.isDrawerView(child)) {
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                if (layoutParams.knownOpen) {
                    savedState.openDrawerGravity = layoutParams.gravity;
                    break;
                }
            }
        }
        savedState.lockModeLeft = this.mLockModeLeft;
        savedState.lockModeRight = this.mLockModeRight;
        return (Parcelable)savedState;
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        switch (0xFF & motionEvent.getAction()) {
            default: {
                return true;
            }
            case 0: {
                final float x = motionEvent.getX();
                final float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
            }
            case 1: {
                final float x2 = motionEvent.getX();
                final float y2 = motionEvent.getY();
                boolean b = true;
                final View topChildUnder = this.mLeftDragger.findTopChildUnder((int)x2, (int)y2);
                if (topChildUnder != null && this.isContentView(topChildUnder)) {
                    final float n = x2 - this.mInitialMotionX;
                    final float n2 = y2 - this.mInitialMotionY;
                    final int touchSlop = this.mLeftDragger.getTouchSlop();
                    if (n * n + n2 * n2 < touchSlop * touchSlop) {
                        final View openDrawer = this.findOpenDrawer();
                        if (openDrawer != null) {
                            b = (this.getDrawerLockMode(openDrawer) == 2);
                        }
                    }
                }
                this.closeDrawers(b);
                this.mDisallowInterceptRequested = false;
                return true;
            }
            case 3: {
                this.closeDrawers(true);
                this.mDisallowInterceptRequested = false;
                this.mChildrenCanceledTouch = false;
                return true;
            }
        }
    }
    
    public void openDrawer(final int n) {
        final View drawerWithGravity = this.findDrawerWithGravity(n);
        if (drawerWithGravity == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " + gravityToString(n));
        }
        this.openDrawer(drawerWithGravity);
    }
    
    public void openDrawer(final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a sliding drawer");
        }
        if (this.mFirstLayout) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            layoutParams.onScreen = 1.0f;
            layoutParams.knownOpen = true;
        }
        else if (this.checkDrawerViewAbsoluteGravity(view, 3)) {
            this.mLeftDragger.smoothSlideViewTo(view, 0, view.getTop());
        }
        else {
            this.mRightDragger.smoothSlideViewTo(view, this.getWidth() - view.getWidth(), view.getTop());
        }
        this.invalidate();
    }
    
    public void requestDisallowInterceptTouchEvent(final boolean mDisallowInterceptRequested) {
        super.requestDisallowInterceptTouchEvent(mDisallowInterceptRequested);
        this.mDisallowInterceptRequested = mDisallowInterceptRequested;
        if (mDisallowInterceptRequested) {
            this.closeDrawers(true);
        }
    }
    
    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }
    
    public void setDrawerListener(final DrawerListener mListener) {
        this.mListener = mListener;
    }
    
    public void setDrawerLockMode(final int n) {
        this.setDrawerLockMode(n, 3);
        this.setDrawerLockMode(n, 5);
    }
    
    public void setDrawerLockMode(final int n, final int n2) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n2, ViewCompat.getLayoutDirection((View)this));
        if (absoluteGravity == 3) {
            this.mLockModeLeft = n;
        }
        else if (absoluteGravity == 5) {
            this.mLockModeRight = n;
        }
        if (n != 0) {
            ViewDragHelper viewDragHelper;
            if (absoluteGravity == 3) {
                viewDragHelper = this.mLeftDragger;
            }
            else {
                viewDragHelper = this.mRightDragger;
            }
            viewDragHelper.cancel();
        }
        switch (n) {
            case 2: {
                final View drawerWithGravity = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity != null) {
                    this.openDrawer(drawerWithGravity);
                    return;
                }
                break;
            }
            case 1: {
                final View drawerWithGravity2 = this.findDrawerWithGravity(absoluteGravity);
                if (drawerWithGravity2 != null) {
                    this.closeDrawer(drawerWithGravity2);
                    return;
                }
                break;
            }
        }
    }
    
    public void setDrawerLockMode(final int n, final View view) {
        if (!this.isDrawerView(view)) {
            throw new IllegalArgumentException("View " + view + " is not a " + "drawer with appropriate layout_gravity");
        }
        this.setDrawerLockMode(n, ((LayoutParams)view.getLayoutParams()).gravity);
    }
    
    public void setDrawerShadow(final int n, final int n2) {
        this.setDrawerShadow(this.getResources().getDrawable(n), n2);
    }
    
    public void setDrawerShadow(final Drawable drawable, final int n) {
        final int absoluteGravity = GravityCompat.getAbsoluteGravity(n, ViewCompat.getLayoutDirection((View)this));
        if ((absoluteGravity & 0x3) == 0x3) {
            this.mShadowLeft = drawable;
            this.invalidate();
        }
        if ((absoluteGravity & 0x5) == 0x5) {
            this.mShadowRight = drawable;
            this.invalidate();
        }
    }
    
    void setDrawerViewOffset(final View view, final float onScreen) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (onScreen == layoutParams.onScreen) {
            return;
        }
        this.dispatchOnDrawerSlide(view, layoutParams.onScreen = onScreen);
    }
    
    public void setScrimColor(final int mScrimColor) {
        this.mScrimColor = mScrimColor;
        this.invalidate();
    }
    
    void updateDrawerState(final int n, final int n2, final View view) {
        final int viewDragState = this.mLeftDragger.getViewDragState();
        final int viewDragState2 = this.mRightDragger.getViewDragState();
        int mDrawerState;
        if (viewDragState == 1 || viewDragState2 == 1) {
            mDrawerState = 1;
        }
        else if (viewDragState == 2 || viewDragState2 == 2) {
            mDrawerState = 2;
        }
        else {
            mDrawerState = 0;
        }
        if (view != null && n2 == 0) {
            final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (layoutParams.onScreen == 0.0f) {
                this.dispatchOnDrawerClosed(view);
            }
            else if (layoutParams.onScreen == 1.0f) {
                this.dispatchOnDrawerOpened(view);
            }
        }
        if (mDrawerState != this.mDrawerState) {
            this.mDrawerState = mDrawerState;
            if (this.mListener != null) {
                this.mListener.onDrawerStateChanged(mDrawerState);
            }
        }
    }
    
    class AccessibilityDelegate extends AccessibilityDelegateCompat
    {
        private final Rect mTmpRect;
        
        AccessibilityDelegate() {
            super();
            this.mTmpRect = new Rect();
        }
        
        private void addChildrenForAccessibility(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final ViewGroup viewGroup) {
            for (int childCount = viewGroup.getChildCount(), i = 0; i < childCount; ++i) {
                final View child = viewGroup.getChildAt(i);
                if (!this.filter(child)) {
                    switch (ViewCompat.getImportantForAccessibility(child)) {
                        case 0: {
                            ViewCompat.setImportantForAccessibility(child, 1);
                        }
                        case 1: {
                            accessibilityNodeInfoCompat.addChild(child);
                        }
                        case 2: {
                            if (child instanceof ViewGroup) {
                                this.addChildrenForAccessibility(accessibilityNodeInfoCompat, (ViewGroup)child);
                                break;
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        private void copyNodeInfoNoChildren(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2) {
            final Rect mTmpRect = this.mTmpRect;
            accessibilityNodeInfoCompat2.getBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInParent(mTmpRect);
            accessibilityNodeInfoCompat2.getBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setBoundsInScreen(mTmpRect);
            accessibilityNodeInfoCompat.setVisibleToUser(accessibilityNodeInfoCompat2.isVisibleToUser());
            accessibilityNodeInfoCompat.setPackageName(accessibilityNodeInfoCompat2.getPackageName());
            accessibilityNodeInfoCompat.setClassName(accessibilityNodeInfoCompat2.getClassName());
            accessibilityNodeInfoCompat.setContentDescription(accessibilityNodeInfoCompat2.getContentDescription());
            accessibilityNodeInfoCompat.setEnabled(accessibilityNodeInfoCompat2.isEnabled());
            accessibilityNodeInfoCompat.setClickable(accessibilityNodeInfoCompat2.isClickable());
            accessibilityNodeInfoCompat.setFocusable(accessibilityNodeInfoCompat2.isFocusable());
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.setLongClickable(accessibilityNodeInfoCompat2.isLongClickable());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
        }
        
        public boolean filter(final View view) {
            final View openDrawer = DrawerLayout.this.findOpenDrawer();
            return openDrawer != null && openDrawer != view;
        }
        
        @Override
        public void onInitializeAccessibilityNodeInfo(final View source, final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            final AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
            super.onInitializeAccessibilityNodeInfo(source, obtain);
            accessibilityNodeInfoCompat.setSource(source);
            final ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(source);
            if (parentForAccessibility instanceof View) {
                accessibilityNodeInfoCompat.setParent((View)parentForAccessibility);
            }
            this.copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
            obtain.recycle();
            this.addChildrenForAccessibility(accessibilityNodeInfoCompat, (ViewGroup)source);
        }
        
        @Override
        public boolean onRequestSendAccessibilityEvent(final ViewGroup viewGroup, final View view, final AccessibilityEvent accessibilityEvent) {
            return !this.filter(view) && super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
        }
    }
    
    public interface DrawerListener
    {
        void onDrawerClosed(View p0);
        
        void onDrawerOpened(View p0);
        
        void onDrawerSlide(View p0, float p1);
        
        void onDrawerStateChanged(int p0);
    }
    
    public static class LayoutParams extends ViewGroup$MarginLayoutParams
    {
        public int gravity;
        boolean isPeeking;
        boolean knownOpen;
        float onScreen;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.gravity = 0;
        }
        
        public LayoutParams(final int n, final int n2, final int gravity) {
            this(n, n2);
            this.gravity = gravity;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
            this.gravity = 0;
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, DrawerLayout.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInt(0, 0);
            obtainStyledAttributes.recycle();
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((ViewGroup$MarginLayoutParams)layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }
        
        public LayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
            super(viewGroup$LayoutParams);
            this.gravity = 0;
        }
        
        public LayoutParams(final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams) {
            super(viewGroup$MarginLayoutParams);
            this.gravity = 0;
        }
    }
    
    protected static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int lockModeLeft;
        int lockModeRight;
        int openDrawerGravity;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        public SavedState(final Parcel parcel) {
            super(parcel);
            this.openDrawerGravity = 0;
            this.lockModeLeft = 0;
            this.lockModeRight = 0;
            this.openDrawerGravity = parcel.readInt();
        }
        
        public SavedState(final Parcelable parcelable) {
            super(parcelable);
            this.openDrawerGravity = 0;
            this.lockModeLeft = 0;
            this.lockModeRight = 0;
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.openDrawerGravity);
        }
    }
    
    public abstract static class SimpleDrawerListener implements DrawerListener
    {
        @Override
        public void onDrawerClosed(final View view) {
        }
        
        @Override
        public void onDrawerOpened(final View view) {
        }
        
        @Override
        public void onDrawerSlide(final View view, final float n) {
        }
        
        @Override
        public void onDrawerStateChanged(final int n) {
        }
    }
    
    private class ViewDragCallback extends Callback
    {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;
        private final Runnable mPeekRunnable;
        
        public ViewDragCallback(final int mAbsGravity) {
            super();
            this.mPeekRunnable = new Runnable() {
                @Override
                public void run() {
                    ViewDragCallback.this.peekDrawer();
                }
            };
            this.mAbsGravity = mAbsGravity;
        }
        
        private void closeOtherDrawer() {
            int n = 3;
            if (this.mAbsGravity == n) {
                n = 5;
            }
            final View drawerWithGravity = DrawerLayout.this.findDrawerWithGravity(n);
            if (drawerWithGravity != null) {
                DrawerLayout.this.closeDrawer(drawerWithGravity);
            }
        }
        
        private void peekDrawer() {
            final int edgeSize = this.mDragger.getEdgeSize();
            boolean b;
            if (this.mAbsGravity == 3) {
                b = true;
            }
            else {
                b = false;
            }
            View view;
            int n2;
            if (b) {
                view = DrawerLayout.this.findDrawerWithGravity(3);
                int n = 0;
                if (view != null) {
                    n = -view.getWidth();
                }
                n2 = n + edgeSize;
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(5);
                n2 = DrawerLayout.this.getWidth() - edgeSize;
            }
            if (view != null && ((b && view.getLeft() < n2) || (!b && view.getLeft() > n2)) && DrawerLayout.this.getDrawerLockMode(view) == 0) {
                final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
                this.mDragger.smoothSlideViewTo(view, n2, view.getTop());
                layoutParams.isPeeking = true;
                DrawerLayout.this.invalidate();
                this.closeOtherDrawer();
                DrawerLayout.this.cancelChildViewTouch();
            }
        }
        
        @Override
        public int clampViewPositionHorizontal(final View view, final int n, final int n2) {
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                return Math.max(-view.getWidth(), Math.min(n, 0));
            }
            final int width = DrawerLayout.this.getWidth();
            return Math.max(width - view.getWidth(), Math.min(n, width));
        }
        
        @Override
        public int clampViewPositionVertical(final View view, final int n, final int n2) {
            return view.getTop();
        }
        
        @Override
        public int getViewHorizontalDragRange(final View view) {
            return view.getWidth();
        }
        
        @Override
        public void onEdgeDragStarted(final int n, final int n2) {
            View view;
            if ((n & 0x1) == 0x1) {
                view = DrawerLayout.this.findDrawerWithGravity(3);
            }
            else {
                view = DrawerLayout.this.findDrawerWithGravity(5);
            }
            if (view != null && DrawerLayout.this.getDrawerLockMode(view) == 0) {
                this.mDragger.captureChildView(view, n2);
            }
        }
        
        @Override
        public boolean onEdgeLock(final int n) {
            return false;
        }
        
        @Override
        public void onEdgeTouched(final int n, final int n2) {
            DrawerLayout.this.postDelayed(this.mPeekRunnable, 160L);
        }
        
        @Override
        public void onViewCaptured(final View view, final int n) {
            ((LayoutParams)view.getLayoutParams()).isPeeking = false;
            this.closeOtherDrawer();
        }
        
        @Override
        public void onViewDragStateChanged(final int n) {
            DrawerLayout.this.updateDrawerState(this.mAbsGravity, n, this.mDragger.getCapturedView());
        }
        
        @Override
        public void onViewPositionChanged(final View view, final int n, final int n2, final int n3, final int n4) {
            final int width = view.getWidth();
            float n5;
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                n5 = (width + n) / width;
            }
            else {
                n5 = (DrawerLayout.this.getWidth() - n) / width;
            }
            DrawerLayout.this.setDrawerViewOffset(view, n5);
            int visibility;
            if (n5 == 0.0f) {
                visibility = 4;
            }
            else {
                visibility = 0;
            }
            view.setVisibility(visibility);
            DrawerLayout.this.invalidate();
        }
        
        @Override
        public void onViewReleased(final View view, final float n, final float n2) {
            final float drawerViewOffset = DrawerLayout.this.getDrawerViewOffset(view);
            final int width = view.getWidth();
            int n3;
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                if (n > 0.0f || (n == 0.0f && drawerViewOffset > 0.5f)) {
                    n3 = 0;
                }
                else {
                    n3 = -width;
                }
            }
            else {
                final int width2 = DrawerLayout.this.getWidth();
                if (n < 0.0f || (n == 0.0f && drawerViewOffset > 0.5f)) {
                    n3 = width2 - width;
                }
                else {
                    n3 = width2;
                }
            }
            this.mDragger.settleCapturedViewAt(n3, view.getTop());
            DrawerLayout.this.invalidate();
        }
        
        public void removeCallbacks() {
            DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
        }
        
        public void setDragger(final ViewDragHelper mDragger) {
            this.mDragger = mDragger;
        }
        
        @Override
        public boolean tryCaptureView(final View view, final int n) {
            return DrawerLayout.this.isDrawerView(view) && DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, this.mAbsGravity) && DrawerLayout.this.getDrawerLockMode(view) == 0;
        }
    }
}
