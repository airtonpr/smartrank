package android.support.v7.internal.widget;

import android.graphics.drawable.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.content.res.*;
import android.text.*;
import android.support.v7.view.*;
import android.support.v7.internal.view.menu.*;
import android.view.*;

public class ActionBarContextView extends AbsActionBarView
{
    private static final String TAG = "ActionBarContextView";
    private View mClose;
    private View mCustomView;
    private Drawable mSplitBackground;
    private CharSequence mSubtitle;
    private int mSubtitleStyleRes;
    private TextView mSubtitleView;
    private CharSequence mTitle;
    private LinearLayout mTitleLayout;
    private boolean mTitleOptional;
    private int mTitleStyleRes;
    private TextView mTitleView;
    
    public ActionBarContextView(final Context context) {
        this(context, null);
    }
    
    public ActionBarContextView(final Context context, final AttributeSet set) {
        this(context, set, R.attr.actionModeStyle);
    }
    
    public ActionBarContextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ActionMode, n, 0);
        this.setBackgroundDrawable(obtainStyledAttributes.getDrawable(3));
        this.mTitleStyleRes = obtainStyledAttributes.getResourceId(1, 0);
        this.mSubtitleStyleRes = obtainStyledAttributes.getResourceId(2, 0);
        this.mContentHeight = obtainStyledAttributes.getLayoutDimension(0, 0);
        this.mSplitBackground = obtainStyledAttributes.getDrawable(4);
        obtainStyledAttributes.recycle();
    }
    
    private void initTitle() {
        int visibility = 8;
        if (this.mTitleLayout == null) {
            LayoutInflater.from(this.getContext()).inflate(R.layout.abc_action_bar_title_item, (ViewGroup)this);
            this.mTitleLayout = (LinearLayout)this.getChildAt(-1 + this.getChildCount());
            this.mTitleView = (TextView)this.mTitleLayout.findViewById(R.id.action_bar_title);
            this.mSubtitleView = (TextView)this.mTitleLayout.findViewById(R.id.action_bar_subtitle);
            if (this.mTitleStyleRes != 0) {
                this.mTitleView.setTextAppearance(this.getContext(), this.mTitleStyleRes);
            }
            if (this.mSubtitleStyleRes != 0) {
                this.mSubtitleView.setTextAppearance(this.getContext(), this.mSubtitleStyleRes);
            }
        }
        this.mTitleView.setText(this.mTitle);
        this.mSubtitleView.setText(this.mSubtitle);
        boolean b;
        if (!TextUtils.isEmpty(this.mTitle)) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (!TextUtils.isEmpty(this.mSubtitle)) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        final TextView mSubtitleView = this.mSubtitleView;
        int visibility2;
        if (b2) {
            visibility2 = 0;
        }
        else {
            visibility2 = visibility;
        }
        mSubtitleView.setVisibility(visibility2);
        final LinearLayout mTitleLayout = this.mTitleLayout;
        if (b || b2) {
            visibility = 0;
        }
        mTitleLayout.setVisibility(visibility);
        if (this.mTitleLayout.getParent() == null) {
            this.addView((View)this.mTitleLayout);
        }
    }
    
    public void closeMode() {
        if (this.mClose == null) {
            this.killMode();
        }
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new ViewGroup$MarginLayoutParams(-1, -2);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new ViewGroup$MarginLayoutParams(this.getContext(), set);
    }
    
    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    @Override
    public boolean hideOverflowMenu() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.hideOverflowMenu();
    }
    
    public void initForMode(final ActionMode actionMode) {
        if (this.mClose == null) {
            this.addView(this.mClose = LayoutInflater.from(this.getContext()).inflate(R.layout.abc_action_mode_close_item, (ViewGroup)this, false));
        }
        else if (this.mClose.getParent() == null) {
            this.addView(this.mClose);
        }
        this.mClose.findViewById(R.id.action_mode_close_button).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                actionMode.finish();
            }
        });
        final MenuBuilder menuBuilder = (MenuBuilder)actionMode.getMenu();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.dismissPopupMenus();
        }
        (this.mActionMenuPresenter = new ActionMenuPresenter(this.getContext())).setReserveOverflow(true);
        final ViewGroup$LayoutParams viewGroup$LayoutParams = new ViewGroup$LayoutParams(-2, -1);
        if (!this.mSplitActionBar) {
            menuBuilder.addMenuPresenter(this.mActionMenuPresenter);
            (this.mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this)).setBackgroundDrawable((Drawable)null);
            this.addView((View)this.mMenuView, viewGroup$LayoutParams);
            return;
        }
        this.mActionMenuPresenter.setWidthLimit(this.getContext().getResources().getDisplayMetrics().widthPixels, true);
        this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
        viewGroup$LayoutParams.width = -1;
        viewGroup$LayoutParams.height = this.mContentHeight;
        menuBuilder.addMenuPresenter(this.mActionMenuPresenter);
        (this.mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this)).setBackgroundDrawable(this.mSplitBackground);
        this.mSplitView.addView((View)this.mMenuView, viewGroup$LayoutParams);
    }
    
    @Override
    public boolean isOverflowMenuShowing() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.isOverflowMenuShowing();
    }
    
    public boolean isTitleOptional() {
        return this.mTitleOptional;
    }
    
    public void killMode() {
        this.removeAllViews();
        if (this.mSplitView != null) {
            this.mSplitView.removeView((View)this.mMenuView);
        }
        this.mCustomView = null;
        this.mMenuView = null;
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu();
            this.mActionMenuPresenter.hideSubMenus();
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        final int n5 = n4 - n2 - this.getPaddingTop() - this.getPaddingBottom();
        if (this.mClose != null && this.mClose.getVisibility() != 8) {
            final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)this.mClose.getLayoutParams();
            final int n6 = paddingLeft + viewGroup$MarginLayoutParams.leftMargin;
            paddingLeft = n6 + this.positionChild(this.mClose, n6, paddingTop, n5) + viewGroup$MarginLayoutParams.rightMargin;
        }
        if (this.mTitleLayout != null && this.mCustomView == null && this.mTitleLayout.getVisibility() != 8) {
            paddingLeft += this.positionChild((View)this.mTitleLayout, paddingLeft, paddingTop, n5);
        }
        if (this.mCustomView != null) {
            final int n7 = paddingLeft + this.positionChild(this.mCustomView, paddingLeft, paddingTop, n5);
        }
        final int n8 = n3 - n - this.getPaddingRight();
        if (this.mMenuView != null) {
            final int n9 = n8 - this.positionChildInverse((View)this.mMenuView, n8, paddingTop, n5);
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        if (View$MeasureSpec.getMode(n) != 1073741824) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used " + "with android:layout_width=\"FILL_PARENT\" (or fill_parent)");
        }
        if (View$MeasureSpec.getMode(n2) == 0) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used " + "with android:layout_height=\"wrap_content\"");
        }
        final int size = View$MeasureSpec.getSize(n);
        int n3;
        if (this.mContentHeight > 0) {
            n3 = this.mContentHeight;
        }
        else {
            n3 = View$MeasureSpec.getSize(n2);
        }
        final int n4 = this.getPaddingTop() + this.getPaddingBottom();
        int n5 = size - this.getPaddingLeft() - this.getPaddingRight();
        final int n6 = n3 - n4;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(n6, Integer.MIN_VALUE);
        if (this.mClose != null) {
            final int measureChildView = this.measureChildView(this.mClose, n5, measureSpec, 0);
            final ViewGroup$MarginLayoutParams viewGroup$MarginLayoutParams = (ViewGroup$MarginLayoutParams)this.mClose.getLayoutParams();
            n5 = measureChildView - (viewGroup$MarginLayoutParams.leftMargin + viewGroup$MarginLayoutParams.rightMargin);
        }
        if (this.mMenuView != null && this.mMenuView.getParent() == this) {
            n5 = this.measureChildView((View)this.mMenuView, n5, measureSpec, 0);
        }
        if (this.mTitleLayout != null && this.mCustomView == null) {
            if (this.mTitleOptional) {
                this.mTitleLayout.measure(View$MeasureSpec.makeMeasureSpec(0, 0), measureSpec);
                final int measuredWidth = this.mTitleLayout.getMeasuredWidth();
                boolean b;
                if (measuredWidth <= n5) {
                    b = true;
                }
                else {
                    b = false;
                }
                if (b) {
                    n5 -= measuredWidth;
                }
                final LinearLayout mTitleLayout = this.mTitleLayout;
                int visibility;
                if (b) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                mTitleLayout.setVisibility(visibility);
            }
            else {
                n5 = this.measureChildView((View)this.mTitleLayout, n5, measureSpec, 0);
            }
        }
        if (this.mCustomView != null) {
            final ViewGroup$LayoutParams layoutParams = this.mCustomView.getLayoutParams();
            int n7;
            if (layoutParams.width != -2) {
                n7 = 1073741824;
            }
            else {
                n7 = Integer.MIN_VALUE;
            }
            int min;
            if (layoutParams.width >= 0) {
                min = Math.min(layoutParams.width, n5);
            }
            else {
                min = n5;
            }
            int n8;
            if (layoutParams.height != -2) {
                n8 = 1073741824;
            }
            else {
                n8 = Integer.MIN_VALUE;
            }
            int min2;
            if (layoutParams.height >= 0) {
                min2 = Math.min(layoutParams.height, n6);
            }
            else {
                min2 = n6;
            }
            this.mCustomView.measure(View$MeasureSpec.makeMeasureSpec(min, n7), View$MeasureSpec.makeMeasureSpec(min2, n8));
        }
        if (this.mContentHeight <= 0) {
            int n9 = 0;
            for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
                final int n10 = n4 + this.getChildAt(i).getMeasuredHeight();
                if (n10 > n9) {
                    n9 = n10;
                }
            }
            this.setMeasuredDimension(size, n9);
            return;
        }
        this.setMeasuredDimension(size, n3);
    }
    
    @Override
    public void setContentHeight(final int mContentHeight) {
        this.mContentHeight = mContentHeight;
    }
    
    public void setCustomView(final View mCustomView) {
        if (this.mCustomView != null) {
            this.removeView(this.mCustomView);
        }
        this.mCustomView = mCustomView;
        if (this.mTitleLayout != null) {
            this.removeView((View)this.mTitleLayout);
            this.mTitleLayout = null;
        }
        if (mCustomView != null) {
            this.addView(mCustomView);
        }
        this.requestLayout();
    }
    
    @Override
    public void setSplitActionBar(final boolean splitActionBar) {
        if (this.mSplitActionBar != splitActionBar) {
            if (this.mActionMenuPresenter != null) {
                final ViewGroup$LayoutParams viewGroup$LayoutParams = new ViewGroup$LayoutParams(-2, -1);
                if (!splitActionBar) {
                    (this.mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this)).setBackgroundDrawable((Drawable)null);
                    final ViewGroup viewGroup = (ViewGroup)this.mMenuView.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView((View)this.mMenuView);
                    }
                    this.addView((View)this.mMenuView, viewGroup$LayoutParams);
                }
                else {
                    this.mActionMenuPresenter.setWidthLimit(this.getContext().getResources().getDisplayMetrics().widthPixels, true);
                    this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
                    viewGroup$LayoutParams.width = -1;
                    viewGroup$LayoutParams.height = this.mContentHeight;
                    (this.mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this)).setBackgroundDrawable(this.mSplitBackground);
                    final ViewGroup viewGroup2 = (ViewGroup)this.mMenuView.getParent();
                    if (viewGroup2 != null) {
                        viewGroup2.removeView((View)this.mMenuView);
                    }
                    this.mSplitView.addView((View)this.mMenuView, viewGroup$LayoutParams);
                }
            }
            super.setSplitActionBar(splitActionBar);
        }
    }
    
    public void setSubtitle(final CharSequence mSubtitle) {
        this.mSubtitle = mSubtitle;
        this.initTitle();
    }
    
    public void setTitle(final CharSequence mTitle) {
        this.mTitle = mTitle;
        this.initTitle();
    }
    
    public void setTitleOptional(final boolean mTitleOptional) {
        if (mTitleOptional != this.mTitleOptional) {
            this.requestLayout();
        }
        this.mTitleOptional = mTitleOptional;
    }
    
    @Override
    public boolean showOverflowMenu() {
        return this.mActionMenuPresenter != null && this.mActionMenuPresenter.showOverflowMenu();
    }
}
