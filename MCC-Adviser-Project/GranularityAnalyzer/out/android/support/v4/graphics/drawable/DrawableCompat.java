package android.support.v4.graphics.drawable;

import android.os.*;
import android.graphics.drawable.*;

public class DrawableCompat
{
    static final DrawableImpl IMPL;
    
    static {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19) {
            IMPL = (DrawableImpl)new KitKatDrawableImpl();
            return;
        }
        if (sdk_INT >= 11) {
            IMPL = (DrawableImpl)new HoneycombDrawableImpl();
            return;
        }
        IMPL = (DrawableImpl)new BaseDrawableImpl();
    }
    
    public static boolean isAutoMirrored(final Drawable drawable) {
        return DrawableCompat.IMPL.isAutoMirrored(drawable);
    }
    
    public static void jumpToCurrentState(final Drawable drawable) {
        DrawableCompat.IMPL.jumpToCurrentState(drawable);
    }
    
    public static void setAutoMirrored(final Drawable drawable, final boolean b) {
        DrawableCompat.IMPL.setAutoMirrored(drawable, b);
    }
    
    static class BaseDrawableImpl implements DrawableImpl
    {
        @Override
        public boolean isAutoMirrored(final Drawable drawable) {
            return false;
        }
        
        @Override
        public void jumpToCurrentState(final Drawable drawable) {
        }
        
        @Override
        public void setAutoMirrored(final Drawable drawable, final boolean b) {
        }
    }
    
    interface DrawableImpl
    {
        boolean isAutoMirrored(Drawable p0);
        
        void jumpToCurrentState(Drawable p0);
        
        void setAutoMirrored(Drawable p0, boolean p1);
    }
    
    static class HoneycombDrawableImpl extends BaseDrawableImpl
    {
        @Override
        public void jumpToCurrentState(final Drawable drawable) {
            DrawableCompatHoneycomb.jumpToCurrentState(drawable);
        }
    }
    
    static class KitKatDrawableImpl extends HoneycombDrawableImpl
    {
        @Override
        public boolean isAutoMirrored(final Drawable drawable) {
            return DrawableCompatKitKat.isAutoMirrored(drawable);
        }
        
        @Override
        public void setAutoMirrored(final Drawable drawable, final boolean b) {
            DrawableCompatKitKat.setAutoMirrored(drawable, b);
        }
    }
}
