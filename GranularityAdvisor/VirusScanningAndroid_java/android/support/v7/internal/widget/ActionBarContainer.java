package android.support.v7.internal.widget;

import android.content.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.content.res.*;
import android.graphics.*;
import android.widget.*;
import android.graphics.drawable.*;
import android.view.*;
import android.support.v7.view.*;

public class ActionBarContainer extends FrameLayout
{
    private ActionBarView mActionBarView;
    private Drawable mBackground;
    private boolean mIsSplit;
    private boolean mIsStacked;
    private boolean mIsTransitioning;
    private Drawable mSplitBackground;
    private Drawable mStackedBackground;
    private View mTabContainer;
    
    public ActionBarContainer(final Context context) {
        this(context, null);
    }
    
    public ActionBarContainer(final Context context, final AttributeSet set) {
        boolean b = true;
        super(context, set);
        this.setBackgroundDrawable((Drawable)null);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ActionBar);
        this.mBackground = obtainStyledAttributes.getDrawable(10);
        this.mStackedBackground = obtainStyledAttributes.getDrawable(11);
        if (this.getId() == R.id.split_action_bar) {
            this.mIsSplit = b;
            this.mSplitBackground = obtainStyledAttributes.getDrawable(12);
        }
        obtainStyledAttributes.recycle();
        if (this.mIsSplit) {
            if (this.mSplitBackground != null) {
                b = false;
            }
        }
        else if (this.mBackground != null || this.mStackedBackground != null) {
            b = false;
        }
        this.setWillNotDraw(b);
    }
    
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mBackground != null && this.mBackground.isStateful()) {
            this.mBackground.setState(this.getDrawableState());
        }
        if (this.mStackedBackground != null && this.mStackedBackground.isStateful()) {
            this.mStackedBackground.setState(this.getDrawableState());
        }
        if (this.mSplitBackground != null && this.mSplitBackground.isStateful()) {
            this.mSplitBackground.setState(this.getDrawableState());
        }
    }
    
    public View getTabContainer() {
        return this.mTabContainer;
    }
    
    public void onDraw(final Canvas canvas) {
        if (this.getWidth() != 0 && this.getHeight() != 0) {
            if (this.mIsSplit) {
                if (this.mSplitBackground != null) {
                    this.mSplitBackground.draw(canvas);
                }
            }
            else {
                if (this.mBackground != null) {
                    this.mBackground.draw(canvas);
                }
                if (this.mStackedBackground != null && this.mIsStacked) {
                    this.mStackedBackground.draw(canvas);
                }
            }
        }
    }
    
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = (ActionBarView)this.findViewById(R.id.action_bar);
    }
    
    public boolean onHoverEvent(final MotionEvent motionEvent) {
        return true;
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent motionEvent) {
        return this.mIsTransitioning || super.onInterceptTouchEvent(motionEvent);
    }
    
    public void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        final boolean b2 = this.mTabContainer != null && this.mTabContainer.getVisibility() != 8;
        if (this.mTabContainer != null && this.mTabContainer.getVisibility() != 8) {
            final int measuredHeight = this.getMeasuredHeight();
            final int measuredHeight2 = this.mTabContainer.getMeasuredHeight();
            if ((0x2 & this.mActionBarView.getDisplayOptions()) == 0x0) {
                for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                    final View child = this.getChildAt(i);
                    if (child != this.mTabContainer && !this.mActionBarView.isCollapsed()) {
                        child.offsetTopAndBottom(measuredHeight2);
                    }
                }
                this.mTabContainer.layout(n, 0, n3, measuredHeight2);
            }
            else {
                this.mTabContainer.layout(n, measuredHeight - measuredHeight2, n3, measuredHeight);
            }
        }
        int n5;
        if (this.mIsSplit) {
            final Drawable mSplitBackground = this.mSplitBackground;
            n5 = 0;
            if (mSplitBackground != null) {
                this.mSplitBackground.setBounds(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
                n5 = 1;
            }
        }
        else {
            final Drawable mBackground = this.mBackground;
            n5 = 0;
            if (mBackground != null) {
                this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                n5 = 1;
            }
            final boolean mIsStacked = b2 && this.mStackedBackground != null;
            this.mIsStacked = mIsStacked;
            if (mIsStacked) {
                this.mStackedBackground.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
                n5 = 1;
            }
        }
        if (n5 != 0) {
            this.invalidate();
        }
    }
    
    public void onMeasure(final int n, final int n2) {
        super.onMeasure(n, n2);
        if (this.mActionBarView != null) {
            final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)this.mActionBarView.getLayoutParams();
            int n3;
            if (this.mActionBarView.isCollapsed()) {
                n3 = 0;
            }
            else {
                n3 = this.mActionBarView.getMeasuredHeight() + frameLayout$LayoutParams.topMargin + frameLayout$LayoutParams.bottomMargin;
            }
            if (this.mTabContainer != null && this.mTabContainer.getVisibility() != 8 && View$MeasureSpec.getMode(n2) == Integer.MIN_VALUE) {
                this.setMeasuredDimension(this.getMeasuredWidth(), Math.min(n3 + this.mTabContainer.getMeasuredHeight(), View$MeasureSpec.getSize(n2)));
            }
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }
    
    public void setPrimaryBackground(final Drawable mBackground) {
        boolean willNotDraw = true;
        if (this.mBackground != null) {
            this.mBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mBackground);
        }
        if ((this.mBackground = mBackground) != null) {
            mBackground.setCallback((Drawable$Callback)this);
        }
        if (this.mIsSplit) {
            if (this.mSplitBackground != null) {
                willNotDraw = false;
            }
        }
        else if (this.mBackground != null || this.mStackedBackground != null) {
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setSplitBackground(final Drawable mSplitBackground) {
        boolean willNotDraw = true;
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mSplitBackground);
        }
        if ((this.mSplitBackground = mSplitBackground) != null) {
            mSplitBackground.setCallback((Drawable$Callback)this);
        }
        if (this.mIsSplit) {
            if (this.mSplitBackground != null) {
                willNotDraw = false;
            }
        }
        else if (this.mBackground != null || this.mStackedBackground != null) {
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setStackedBackground(final Drawable mStackedBackground) {
        boolean willNotDraw = true;
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setCallback((Drawable$Callback)null);
            this.unscheduleDrawable(this.mStackedBackground);
        }
        if ((this.mStackedBackground = mStackedBackground) != null) {
            mStackedBackground.setCallback((Drawable$Callback)this);
        }
        if (this.mIsSplit) {
            if (this.mSplitBackground != null) {
                willNotDraw = false;
            }
        }
        else if (this.mBackground != null || this.mStackedBackground != null) {
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
        this.invalidate();
    }
    
    public void setTabContainer(final ScrollingTabContainerView mTabContainer) {
        if (this.mTabContainer != null) {
            this.removeView(this.mTabContainer);
        }
        if ((this.mTabContainer = (View)mTabContainer) != null) {
            this.addView((View)mTabContainer);
            final ViewGroup$LayoutParams layoutParams = mTabContainer.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -2;
            mTabContainer.setAllowCollapse(false);
        }
    }
    
    public void setTransitioning(final boolean mIsTransitioning) {
        this.mIsTransitioning = mIsTransitioning;
        int descendantFocusability;
        if (mIsTransitioning) {
            descendantFocusability = 393216;
        }
        else {
            descendantFocusability = 262144;
        }
        this.setDescendantFocusability(descendantFocusability);
    }
    
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);
        final boolean b = visibility == 0;
        if (this.mBackground != null) {
            this.mBackground.setVisible(b, false);
        }
        if (this.mStackedBackground != null) {
            this.mStackedBackground.setVisible(b, false);
        }
        if (this.mSplitBackground != null) {
            this.mSplitBackground.setVisible(b, false);
        }
    }
    
    public ActionMode startActionModeForChild(final View view, final ActionMode.Callback callback) {
        return null;
    }
    
    protected boolean verifyDrawable(final Drawable drawable) {
        return (drawable == this.mBackground && !this.mIsSplit) || (drawable == this.mStackedBackground && this.mIsStacked) || (drawable == this.mSplitBackground && this.mIsSplit) || super.verifyDrawable(drawable);
    }
}
