package android.support.v4.view;

import android.os.*;
import android.view.*;

public class ViewConfigurationCompat
{
    static final ViewConfigurationVersionImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT >= 11) {
            IMPL = (ViewConfigurationVersionImpl)new FroyoViewConfigurationVersionImpl();
            return;
        }
        IMPL = (ViewConfigurationVersionImpl)new BaseViewConfigurationVersionImpl();
    }
    
    public static int getScaledPagingTouchSlop(final ViewConfiguration viewConfiguration) {
        return ViewConfigurationCompat.IMPL.getScaledPagingTouchSlop(viewConfiguration);
    }
    
    static class BaseViewConfigurationVersionImpl implements ViewConfigurationVersionImpl
    {
        @Override
        public int getScaledPagingTouchSlop(final ViewConfiguration viewConfiguration) {
            return viewConfiguration.getScaledTouchSlop();
        }
    }
    
    static class FroyoViewConfigurationVersionImpl implements ViewConfigurationVersionImpl
    {
        @Override
        public int getScaledPagingTouchSlop(final ViewConfiguration viewConfiguration) {
            return ViewConfigurationCompatFroyo.getScaledPagingTouchSlop(viewConfiguration);
        }
    }
    
    interface ViewConfigurationVersionImpl
    {
        int getScaledPagingTouchSlop(ViewConfiguration p0);
    }
}
