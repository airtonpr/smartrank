package android.support.v7.internal.widget;

import android.graphics.drawable.*;
import android.content.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.content.res.*;
import android.graphics.*;
import android.widget.*;
import android.view.*;

public class LinearLayoutICS extends LinearLayout
{
    private static final int SHOW_DIVIDER_BEGINNING = 1;
    private static final int SHOW_DIVIDER_END = 4;
    private static final int SHOW_DIVIDER_MIDDLE = 2;
    private static final int SHOW_DIVIDER_NONE;
    private final Drawable mDivider;
    private final int mDividerHeight;
    private final int mDividerPadding;
    private final int mDividerWidth;
    private final int mShowDividers;
    
    public LinearLayoutICS(final Context context, final AttributeSet set) {
        boolean willNotDraw = true;
        super(context, set);
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R.styleable.LinearLayoutICS);
        this.mDivider = obtainStyledAttributes.getDrawable(0);
        if (this.mDivider != null) {
            this.mDividerWidth = this.mDivider.getIntrinsicWidth();
            this.mDividerHeight = this.mDivider.getIntrinsicHeight();
        }
        else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        this.mShowDividers = obtainStyledAttributes.getInt((int)(willNotDraw ? 1 : 0), 0);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(2, 0);
        obtainStyledAttributes.recycle();
        if (this.mDivider != null) {
            willNotDraw = false;
        }
        this.setWillNotDraw(willNotDraw);
    }
    
    void drawSupportDividersHorizontal(final Canvas canvas) {
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child != null && child.getVisibility() != 8 && this.hasSupportDividerBeforeChildAt(i)) {
                this.drawSupportVerticalDivider(canvas, child.getLeft() - ((LinearLayout$LayoutParams)child.getLayoutParams()).leftMargin);
            }
        }
        if (this.hasSupportDividerBeforeChildAt(childCount)) {
            final View child2 = this.getChildAt(childCount - 1);
            int right;
            if (child2 == null) {
                right = this.getWidth() - this.getPaddingRight() - this.mDividerWidth;
            }
            else {
                right = child2.getRight();
            }
            this.drawSupportVerticalDivider(canvas, right);
        }
    }
    
    void drawSupportDividersVertical(final Canvas canvas) {
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child != null && child.getVisibility() != 8 && this.hasSupportDividerBeforeChildAt(i)) {
                this.drawSupportHorizontalDivider(canvas, child.getTop() - ((LinearLayout$LayoutParams)child.getLayoutParams()).topMargin);
            }
        }
        if (this.hasSupportDividerBeforeChildAt(childCount)) {
            final View child2 = this.getChildAt(childCount - 1);
            int bottom;
            if (child2 == null) {
                bottom = this.getHeight() - this.getPaddingBottom() - this.mDividerHeight;
            }
            else {
                bottom = child2.getBottom();
            }
            this.drawSupportHorizontalDivider(canvas, bottom);
        }
    }
    
    void drawSupportHorizontalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(this.getPaddingLeft() + this.mDividerPadding, n, this.getWidth() - this.getPaddingRight() - this.mDividerPadding, n + this.mDividerHeight);
        this.mDivider.draw(canvas);
    }
    
    void drawSupportVerticalDivider(final Canvas canvas, final int n) {
        this.mDivider.setBounds(n, this.getPaddingTop() + this.mDividerPadding, n + this.mDividerWidth, this.getHeight() - this.getPaddingBottom() - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }
    
    public int getSupportDividerWidth() {
        return this.mDividerWidth;
    }
    
    protected boolean hasSupportDividerBeforeChildAt(final int n) {
        if (n == 0) {
            if ((0x1 & this.mShowDividers) == 0x0) {
                return false;
            }
        }
        else if (n == this.getChildCount()) {
            if ((0x4 & this.mShowDividers) == 0x0) {
                return false;
            }
        }
        else {
            if ((0x2 & this.mShowDividers) != 0x0) {
                int n2 = n - 1;
                boolean b;
                while (true) {
                    b = false;
                    if (n2 < 0) {
                        break;
                    }
                    if (this.getChildAt(n2).getVisibility() != 8) {
                        b = true;
                        break;
                    }
                    --n2;
                }
                return b;
            }
            return false;
        }
        return true;
    }
    
    protected void measureChildWithMargins(final View view, final int n, final int n2, final int n3, final int n4) {
        if (this.mDivider != null) {
            final int indexOfChild = this.indexOfChild(view);
            final int childCount = this.getChildCount();
            final LinearLayout$LayoutParams linearLayout$LayoutParams = (LinearLayout$LayoutParams)view.getLayoutParams();
            if (this.getOrientation() == 1) {
                if (this.hasSupportDividerBeforeChildAt(indexOfChild)) {
                    linearLayout$LayoutParams.topMargin = this.mDividerHeight;
                }
                else if (indexOfChild == childCount - 1 && this.hasSupportDividerBeforeChildAt(childCount)) {
                    linearLayout$LayoutParams.bottomMargin = this.mDividerHeight;
                }
            }
            else if (this.hasSupportDividerBeforeChildAt(indexOfChild)) {
                linearLayout$LayoutParams.leftMargin = this.mDividerWidth;
            }
            else if (indexOfChild == childCount - 1 && this.hasSupportDividerBeforeChildAt(childCount)) {
                linearLayout$LayoutParams.rightMargin = this.mDividerWidth;
            }
        }
        super.measureChildWithMargins(view, n, n2, n3, n4);
    }
    
    protected void onDraw(final Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.getOrientation() == 1) {
            this.drawSupportDividersVertical(canvas);
            return;
        }
        this.drawSupportDividersHorizontal(canvas);
    }
}
