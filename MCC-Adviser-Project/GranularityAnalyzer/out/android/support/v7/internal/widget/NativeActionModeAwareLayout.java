package android.support.v7.internal.widget;

import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;

public class NativeActionModeAwareLayout extends LinearLayout
{
    private OnActionModeForChildListener mActionModeForChildListener;
    
    public NativeActionModeAwareLayout(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public void setActionModeForChildListener(final OnActionModeForChildListener mActionModeForChildListener) {
        this.mActionModeForChildListener = mActionModeForChildListener;
    }
    
    public ActionMode startActionModeForChild(final View view, ActionMode$Callback onActionModeForChild) {
        if (this.mActionModeForChildListener != null) {
            onActionModeForChild = this.mActionModeForChildListener.onActionModeForChild(onActionModeForChild);
        }
        return super.startActionModeForChild(view, onActionModeForChild);
    }
    
    public interface OnActionModeForChildListener
    {
        ActionMode$Callback onActionModeForChild(ActionMode$Callback p0);
    }
}
