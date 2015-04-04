package android.support.v7.internal.widget;

import android.support.v7.app.*;
import android.view.*;
import android.graphics.*;
import android.support.v7.appcompat.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import android.content.res.*;

public class ActionBarOverlayLayout extends FrameLayout
{
    static final int[] mActionBarSizeAttr;
    private ActionBar mActionBar;
    private View mActionBarBottom;
    private int mActionBarHeight;
    private View mActionBarTop;
    private ActionBarView mActionView;
    private ActionBarContainer mContainerView;
    private View mContent;
    private final Rect mZeroRect;
    
    static {
        mActionBarSizeAttr = new int[] { R.attr.actionBarSize };
    }
    
    public ActionBarOverlayLayout(final Context context) {
        super(context);
        this.mZeroRect = new Rect(0, 0, 0, 0);
        this.init(context);
    }
    
    public ActionBarOverlayLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.mZeroRect = new Rect(0, 0, 0, 0);
        this.init(context);
    }
    
    private boolean applyInsets(final View view, final Rect rect, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)view.getLayoutParams();
        boolean b5 = false;
        if (b) {
            final int leftMargin = frameLayout$LayoutParams.leftMargin;
            final int left = rect.left;
            b5 = false;
            if (leftMargin != left) {
                b5 = true;
                frameLayout$LayoutParams.leftMargin = rect.left;
            }
        }
        if (b2 && frameLayout$LayoutParams.topMargin != rect.top) {
            b5 = true;
            frameLayout$LayoutParams.topMargin = rect.top;
        }
        if (b4 && frameLayout$LayoutParams.rightMargin != rect.right) {
            b5 = true;
            frameLayout$LayoutParams.rightMargin = rect.right;
        }
        if (b3 && frameLayout$LayoutParams.bottomMargin != rect.bottom) {
            b5 = true;
            frameLayout$LayoutParams.bottomMargin = rect.bottom;
        }
        return b5;
    }
    
    private void init(final Context context) {
        final TypedArray obtainStyledAttributes = this.getContext().getTheme().obtainStyledAttributes(ActionBarOverlayLayout.mActionBarSizeAttr);
        this.mActionBarHeight = obtainStyledAttributes.getDimensionPixelSize(0, 0);
        obtainStyledAttributes.recycle();
    }
    
    void pullChildren() {
        if (this.mContent == null) {
            this.mContent = this.findViewById(R.id.action_bar_activity_content);
            if (this.mContent == null) {
                this.mContent = this.findViewById(16908290);
            }
            this.mActionBarTop = this.findViewById(R.id.top_action_bar);
            this.mContainerView = (ActionBarContainer)this.findViewById(R.id.action_bar_container);
            this.mActionView = (ActionBarView)this.findViewById(R.id.action_bar);
            this.mActionBarBottom = this.findViewById(R.id.split_action_bar);
        }
    }
    
    public void setActionBar(final ActionBar mActionBar) {
        this.mActionBar = mActionBar;
    }
}
