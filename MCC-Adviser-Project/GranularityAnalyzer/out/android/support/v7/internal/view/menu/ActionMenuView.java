package android.support.v7.internal.view.menu;

import android.support.v7.internal.widget.*;
import android.content.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.view.accessibility.*;
import android.widget.*;
import android.content.res.*;
import android.os.*;
import android.view.*;

public class ActionMenuView extends LinearLayoutICS implements ItemInvoker, MenuView
{
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private int mMaxItemHeight;
    private int mMeasuredExtraWidth;
    private MenuBuilder mMenu;
    private int mMinCellSize;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;
    
    public ActionMenuView(final Context context) {
        this(context, null);
    }
    
    public ActionMenuView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setBaselineAligned(false);
        final float density = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int)(56.0f * density);
        this.mGeneratedItemPadding = (int)(4.0f * density);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        this.mMaxItemHeight = obtainStyledAttributes.getDimensionPixelSize(1, 0);
        obtainStyledAttributes.recycle();
    }
    
    static int measureChildForCells(final View view, final int n, final int n2, final int n3, final int n4) {
        final LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(View$MeasureSpec.getSize(n3) - n4, View$MeasureSpec.getMode(n3));
        ActionMenuItemView actionMenuItemView;
        if (view instanceof ActionMenuItemView) {
            actionMenuItemView = (ActionMenuItemView)view;
        }
        else {
            actionMenuItemView = null;
        }
        boolean b;
        if (actionMenuItemView != null && actionMenuItemView.hasText()) {
            b = true;
        }
        else {
            b = false;
        }
        int cellsUsed = 0;
        Label_0124: {
            if (n2 > 0) {
                if (b) {
                    cellsUsed = 0;
                    if (n2 < 2) {
                        break Label_0124;
                    }
                }
                view.measure(View$MeasureSpec.makeMeasureSpec(n * n2, Integer.MIN_VALUE), measureSpec);
                final int measuredWidth = view.getMeasuredWidth();
                cellsUsed = measuredWidth / n;
                if (measuredWidth % n != 0) {
                    ++cellsUsed;
                }
                if (b && cellsUsed < 2) {
                    cellsUsed = 2;
                }
            }
        }
        layoutParams.expandable = (!layoutParams.isOverflowButton && b);
        layoutParams.cellsUsed = cellsUsed;
        view.measure(View$MeasureSpec.makeMeasureSpec(cellsUsed * n, 1073741824), measureSpec);
        return cellsUsed;
    }
    
    private void onMeasureExactFormat(final int n, final int n2) {
        final int mode = View$MeasureSpec.getMode(n2);
        final int size = View$MeasureSpec.getSize(n);
        int size2 = View$MeasureSpec.getSize(n2);
        final int n3 = this.getPaddingLeft() + this.getPaddingRight();
        final int n4 = this.getPaddingTop() + this.getPaddingBottom();
        int n5;
        if (mode == 1073741824) {
            n5 = View$MeasureSpec.makeMeasureSpec(size2 - n4, 1073741824);
        }
        else {
            n5 = View$MeasureSpec.makeMeasureSpec(Math.min(this.mMaxItemHeight, size2 - n4), Integer.MIN_VALUE);
        }
        final int n6 = size - n3;
        final int n7 = n6 / this.mMinCellSize;
        final int n8 = n6 % this.mMinCellSize;
        if (n7 == 0) {
            this.setMeasuredDimension(n6, 0);
            return;
        }
        final int n9 = this.mMinCellSize + n8 / n7;
        int n10 = n7;
        int max = 0;
        int max2 = 0;
        int n11 = 0;
        int n12 = 0;
        boolean b = false;
        long n13 = 0L;
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                final boolean b2 = child instanceof ActionMenuItemView;
                ++n12;
                if (b2) {
                    child.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                }
                final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                layoutParams.expanded = false;
                layoutParams.extraPixels = 0;
                layoutParams.cellsUsed = 0;
                layoutParams.expandable = false;
                layoutParams.leftMargin = 0;
                layoutParams.rightMargin = 0;
                layoutParams.preventEdgeOffset = (b2 && ((ActionMenuItemView)child).hasText());
                int n14;
                if (layoutParams.isOverflowButton) {
                    n14 = 1;
                }
                else {
                    n14 = n10;
                }
                final int measureChildForCells = measureChildForCells(child, n9, n14, n5, n4);
                max2 = Math.max(max2, measureChildForCells);
                if (layoutParams.expandable) {
                    ++n11;
                }
                if (layoutParams.isOverflowButton) {
                    b = true;
                }
                n10 -= measureChildForCells;
                max = Math.max(max, child.getMeasuredHeight());
                if (measureChildForCells == 1) {
                    n13 |= 1 << i;
                }
            }
        }
        final boolean b3 = b && n12 == 2;
        boolean b4 = false;
        while (n11 > 0 && n10 > 0) {
            int cellsUsed = Integer.MAX_VALUE;
            long n15 = 0L;
            int n16 = 0;
            for (int j = 0; j < childCount; ++j) {
                final LayoutParams layoutParams2 = (LayoutParams)this.getChildAt(j).getLayoutParams();
                if (layoutParams2.expandable) {
                    if (layoutParams2.cellsUsed < cellsUsed) {
                        cellsUsed = layoutParams2.cellsUsed;
                        n15 = 1 << j;
                        n16 = 1;
                    }
                    else if (layoutParams2.cellsUsed == cellsUsed) {
                        n15 |= 1 << j;
                        ++n16;
                    }
                }
            }
            n13 |= n15;
            if (n16 > n10) {
                break;
            }
            final int n17 = cellsUsed + 1;
            for (int k = 0; k < childCount; ++k) {
                final View child2 = this.getChildAt(k);
                final LayoutParams layoutParams3 = (LayoutParams)child2.getLayoutParams();
                if ((n15 & 1 << k) == 0x0L) {
                    if (layoutParams3.cellsUsed == n17) {
                        n13 |= 1 << k;
                    }
                }
                else {
                    if (b3 && layoutParams3.preventEdgeOffset && n10 == 1) {
                        child2.setPadding(n9 + this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                    }
                    ++layoutParams3.cellsUsed;
                    layoutParams3.expanded = true;
                    --n10;
                }
            }
            b4 = true;
        }
        boolean b5;
        if (!b && n12 == 1) {
            b5 = true;
        }
        else {
            b5 = false;
        }
        if (n10 > 0 && n13 != 0L && (n10 < n12 - 1 || b5 || max2 > 1)) {
            float n18 = Long.bitCount(n13);
            if (!b5) {
                if ((0x1L & n13) != 0x0L && !((LayoutParams)this.getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                    n18 -= 0.5f;
                }
                if ((n13 & 1 << childCount - 1) != 0x0L && !((LayoutParams)this.getChildAt(childCount - 1).getLayoutParams()).preventEdgeOffset) {
                    n18 -= 0.5f;
                }
            }
            int n19;
            if (n18 > 0.0f) {
                n19 = (int)(n10 * n9 / n18);
            }
            else {
                n19 = 0;
            }
            for (int l = 0; l < childCount; ++l) {
                if ((n13 & 1 << l) != 0x0L) {
                    final View child3 = this.getChildAt(l);
                    final LayoutParams layoutParams4 = (LayoutParams)child3.getLayoutParams();
                    if (child3 instanceof ActionMenuItemView) {
                        layoutParams4.extraPixels = n19;
                        layoutParams4.expanded = true;
                        if (l == 0 && !layoutParams4.preventEdgeOffset) {
                            layoutParams4.leftMargin = -n19 / 2;
                        }
                        b4 = true;
                    }
                    else if (layoutParams4.isOverflowButton) {
                        layoutParams4.extraPixels = n19;
                        layoutParams4.expanded = true;
                        layoutParams4.rightMargin = -n19 / 2;
                        b4 = true;
                    }
                    else {
                        if (l != 0) {
                            layoutParams4.leftMargin = n19 / 2;
                        }
                        if (l != childCount - 1) {
                            layoutParams4.rightMargin = n19 / 2;
                        }
                    }
                }
            }
            n10 = 0;
        }
        if (b4) {
            for (int n20 = 0; n20 < childCount; ++n20) {
                final View child4 = this.getChildAt(n20);
                final LayoutParams layoutParams5 = (LayoutParams)child4.getLayoutParams();
                if (layoutParams5.expanded) {
                    child4.measure(View$MeasureSpec.makeMeasureSpec(n9 * layoutParams5.cellsUsed + layoutParams5.extraPixels, 1073741824), n5);
                }
            }
        }
        if (mode != 1073741824) {
            size2 = max;
        }
        this.setMeasuredDimension(n6, size2);
        this.mMeasuredExtraWidth = n10 * n9;
    }
    
    protected boolean checkLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        return viewGroup$LayoutParams != null && viewGroup$LayoutParams instanceof LayoutParams;
    }
    
    public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        return false;
    }
    
    protected LayoutParams generateDefaultLayoutParams() {
        final LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }
    
    public LayoutParams generateLayoutParams(final AttributeSet set) {
        return new LayoutParams(this.getContext(), set);
    }
    
    protected LayoutParams generateLayoutParams(final ViewGroup$LayoutParams viewGroup$LayoutParams) {
        if (viewGroup$LayoutParams instanceof LayoutParams) {
            final LayoutParams layoutParams = new LayoutParams((LayoutParams)viewGroup$LayoutParams);
            if (layoutParams.gravity <= 0) {
                layoutParams.gravity = 16;
            }
            return layoutParams;
        }
        return this.generateDefaultLayoutParams();
    }
    
    public LayoutParams generateOverflowButtonLayoutParams() {
        final LayoutParams generateDefaultLayoutParams = this.generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }
    
    @Override
    public int getWindowAnimations() {
        return 0;
    }
    
    @Override
    protected boolean hasSupportDividerBeforeChildAt(final int n) {
        final View child = this.getChildAt(n - 1);
        final View child2 = this.getChildAt(n);
        final int childCount = this.getChildCount();
        boolean b = false;
        if (n < childCount) {
            final boolean b2 = child instanceof ActionMenuChildView;
            b = false;
            if (b2) {
                b = (false | ((ActionMenuChildView)child).needsDividerAfter());
            }
        }
        if (n > 0 && child2 instanceof ActionMenuChildView) {
            b |= ((ActionMenuChildView)child2).needsDividerBefore();
        }
        return b;
    }
    
    @Override
    public void initialize(final MenuBuilder mMenu) {
        this.mMenu = mMenu;
    }
    
    @Override
    public boolean invokeItem(final MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction((MenuItem)menuItemImpl, 0);
    }
    
    public boolean isExpandedFormat() {
        return this.mFormatItems;
    }
    
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        if (Build$VERSION.SDK_INT >= 8) {
            super.onConfigurationChanged(configuration);
        }
        this.mPresenter.updateMenuView(false);
        if (this.mPresenter != null && this.mPresenter.isOverflowMenuShowing()) {
            this.mPresenter.hideOverflowMenu();
            this.mPresenter.showOverflowMenu();
        }
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mPresenter.dismissPopupMenus();
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        if (!this.mFormatItems) {
            super.onLayout(b, n, n2, n3, n4);
        }
        else {
            final int childCount = this.getChildCount();
            final int n5 = (n2 + n4) / 2;
            final int supportDividerWidth = this.getSupportDividerWidth();
            int n6 = 0;
            int n7 = 0;
            int n8 = n3 - n - this.getPaddingRight() - this.getPaddingLeft();
            boolean b2 = false;
            for (int i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() != 8) {
                    final LayoutParams layoutParams = (LayoutParams)child.getLayoutParams();
                    if (layoutParams.isOverflowButton) {
                        int measuredWidth = child.getMeasuredWidth();
                        if (this.hasSupportDividerBeforeChildAt(i)) {
                            measuredWidth += supportDividerWidth;
                        }
                        final int measuredHeight = child.getMeasuredHeight();
                        final int n9 = this.getWidth() - this.getPaddingRight() - layoutParams.rightMargin;
                        final int n10 = n9 - measuredWidth;
                        final int n11 = n5 - measuredHeight / 2;
                        child.layout(n10, n11, n9, n11 + measuredHeight);
                        n8 -= measuredWidth;
                        b2 = true;
                    }
                    else {
                        final int n12 = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                        n6 += n12;
                        n8 -= n12;
                        if (this.hasSupportDividerBeforeChildAt(i)) {
                            n6 += supportDividerWidth;
                        }
                        ++n7;
                    }
                }
            }
            if (childCount == 1 && !b2) {
                final View child2 = this.getChildAt(0);
                final int measuredWidth2 = child2.getMeasuredWidth();
                final int measuredHeight2 = child2.getMeasuredHeight();
                final int n13 = (n3 - n) / 2 - measuredWidth2 / 2;
                final int n14 = n5 - measuredHeight2 / 2;
                child2.layout(n13, n14, n13 + measuredWidth2, n14 + measuredHeight2);
                return;
            }
            int n15;
            if (b2) {
                n15 = 0;
            }
            else {
                n15 = 1;
            }
            final int n16 = n7 - n15;
            int n17;
            if (n16 > 0) {
                n17 = n8 / n16;
            }
            else {
                n17 = 0;
            }
            final int max = Math.max(0, n17);
            int paddingLeft = this.getPaddingLeft();
            for (int j = 0; j < childCount; ++j) {
                final View child3 = this.getChildAt(j);
                final LayoutParams layoutParams2 = (LayoutParams)child3.getLayoutParams();
                if (child3.getVisibility() != 8 && !layoutParams2.isOverflowButton) {
                    final int n18 = paddingLeft + layoutParams2.leftMargin;
                    final int measuredWidth3 = child3.getMeasuredWidth();
                    final int measuredHeight3 = child3.getMeasuredHeight();
                    final int n19 = n5 - measuredHeight3 / 2;
                    child3.layout(n18, n19, n18 + measuredWidth3, n19 + measuredHeight3);
                    paddingLeft = n18 + (max + (measuredWidth3 + layoutParams2.rightMargin));
                }
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final boolean mFormatItems = this.mFormatItems;
        this.mFormatItems = (View$MeasureSpec.getMode(n) == 1073741824);
        if (mFormatItems != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        final int mode = View$MeasureSpec.getMode(n);
        if (this.mFormatItems && this.mMenu != null && mode != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = mode;
            this.mMenu.onItemsChanged(true);
        }
        if (this.mFormatItems) {
            this.onMeasureExactFormat(n, n2);
            return;
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final LayoutParams layoutParams = (LayoutParams)this.getChildAt(i).getLayoutParams();
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        super.onMeasure(n, n2);
    }
    
    public void setOverflowReserved(final boolean mReserveOverflow) {
        this.mReserveOverflow = mReserveOverflow;
    }
    
    public void setPresenter(final ActionMenuPresenter mPresenter) {
        this.mPresenter = mPresenter;
    }
    
    public interface ActionMenuChildView
    {
        boolean needsDividerAfter();
        
        boolean needsDividerBefore();
    }
    
    public static class LayoutParams extends LinearLayout$LayoutParams
    {
        @ViewDebug$ExportedProperty
        public int cellsUsed;
        @ViewDebug$ExportedProperty
        public boolean expandable;
        public boolean expanded;
        @ViewDebug$ExportedProperty
        public int extraPixels;
        @ViewDebug$ExportedProperty
        public boolean isOverflowButton;
        @ViewDebug$ExportedProperty
        public boolean preventEdgeOffset;
        
        public LayoutParams(final int n, final int n2) {
            super(n, n2);
            this.isOverflowButton = false;
        }
        
        public LayoutParams(final int n, final int n2, final boolean isOverflowButton) {
            super(n, n2);
            this.isOverflowButton = isOverflowButton;
        }
        
        public LayoutParams(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public LayoutParams(final LayoutParams layoutParams) {
            super((LinearLayout$LayoutParams)layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }
    }
}
