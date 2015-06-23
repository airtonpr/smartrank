package android.support.v4.widget;

import android.widget.*;
import android.view.*;

public class ListViewAutoScrollHelper extends AutoScrollHelper
{
    private final ListView mTarget;
    
    public ListViewAutoScrollHelper(final ListView mTarget) {
        super((View)mTarget);
        this.mTarget = mTarget;
    }
    
    @Override
    public boolean canTargetScrollHorizontally(final int n) {
        return false;
    }
    
    @Override
    public boolean canTargetScrollVertically(final int n) {
        final ListView mTarget = this.mTarget;
        final int count = mTarget.getCount();
        final int childCount = mTarget.getChildCount();
        final int firstVisiblePosition = mTarget.getFirstVisiblePosition();
        final int n2 = firstVisiblePosition + childCount;
        if (n > 0) {
            if (n2 < count || mTarget.getChildAt(childCount - 1).getBottom() > mTarget.getHeight()) {
                return true;
            }
        }
        else if (n < 0 && (firstVisiblePosition > 0 || mTarget.getChildAt(0).getTop() < 0)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void scrollTargetBy(final int n, final int n2) {
        final ListView mTarget = this.mTarget;
        final int firstVisiblePosition = mTarget.getFirstVisiblePosition();
        if (firstVisiblePosition != -1) {
            final View child = mTarget.getChildAt(0);
            if (child != null) {
                mTarget.setSelectionFromTop(firstVisiblePosition, child.getTop() - n2);
            }
        }
    }
}
