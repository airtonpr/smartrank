package android.support.v4.view;

import android.os.*;
import android.view.*;
import android.view.accessibility.*;

public class ViewParentCompat
{
    static final ViewParentCompatImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT >= 14) {
            IMPL = (ViewParentCompatImpl)new ViewParentCompatICSImpl();
            return;
        }
        IMPL = (ViewParentCompatImpl)new ViewParentCompatStubImpl();
    }
    
    public static boolean requestSendAccessibilityEvent(final ViewParent viewParent, final View view, final AccessibilityEvent accessibilityEvent) {
        return ViewParentCompat.IMPL.requestSendAccessibilityEvent(viewParent, view, accessibilityEvent);
    }
    
    static class ViewParentCompatICSImpl extends ViewParentCompatStubImpl
    {
        @Override
        public boolean requestSendAccessibilityEvent(final ViewParent viewParent, final View view, final AccessibilityEvent accessibilityEvent) {
            return ViewParentCompatICS.requestSendAccessibilityEvent(viewParent, view, accessibilityEvent);
        }
    }
    
    interface ViewParentCompatImpl
    {
        boolean requestSendAccessibilityEvent(ViewParent p0, View p1, AccessibilityEvent p2);
    }
    
    static class ViewParentCompatStubImpl implements ViewParentCompatImpl
    {
        @Override
        public boolean requestSendAccessibilityEvent(final ViewParent viewParent, final View view, final AccessibilityEvent accessibilityEvent) {
            if (view == null) {
                return false;
            }
            ((AccessibilityManager)view.getContext().getSystemService("accessibility")).sendAccessibilityEvent(accessibilityEvent);
            return true;
        }
    }
}
