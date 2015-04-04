package android.support.v4.widget;

import android.os.*;
import android.content.*;
import android.graphics.*;

public class EdgeEffectCompat
{
    private static final EdgeEffectImpl IMPL;
    private Object mEdgeEffect;
    
    static {
        if (Build$VERSION.SDK_INT >= 14) {
            IMPL = (EdgeEffectImpl)new EdgeEffectIcsImpl();
            return;
        }
        IMPL = (EdgeEffectImpl)new BaseEdgeEffectImpl();
    }
    
    public EdgeEffectCompat(final Context context) {
        super();
        this.mEdgeEffect = EdgeEffectCompat.IMPL.newEdgeEffect(context);
    }
    
    public boolean draw(final Canvas canvas) {
        return EdgeEffectCompat.IMPL.draw(this.mEdgeEffect, canvas);
    }
    
    public void finish() {
        EdgeEffectCompat.IMPL.finish(this.mEdgeEffect);
    }
    
    public boolean isFinished() {
        return EdgeEffectCompat.IMPL.isFinished(this.mEdgeEffect);
    }
    
    public boolean onAbsorb(final int n) {
        return EdgeEffectCompat.IMPL.onAbsorb(this.mEdgeEffect, n);
    }
    
    public boolean onPull(final float n) {
        return EdgeEffectCompat.IMPL.onPull(this.mEdgeEffect, n);
    }
    
    public boolean onRelease() {
        return EdgeEffectCompat.IMPL.onRelease(this.mEdgeEffect);
    }
    
    public void setSize(final int n, final int n2) {
        EdgeEffectCompat.IMPL.setSize(this.mEdgeEffect, n, n2);
    }
    
    static class BaseEdgeEffectImpl implements EdgeEffectImpl
    {
        @Override
        public boolean draw(final Object o, final Canvas canvas) {
            return false;
        }
        
        @Override
        public void finish(final Object o) {
        }
        
        @Override
        public boolean isFinished(final Object o) {
            return true;
        }
        
        @Override
        public Object newEdgeEffect(final Context context) {
            return null;
        }
        
        @Override
        public boolean onAbsorb(final Object o, final int n) {
            return false;
        }
        
        @Override
        public boolean onPull(final Object o, final float n) {
            return false;
        }
        
        @Override
        public boolean onRelease(final Object o) {
            return false;
        }
        
        @Override
        public void setSize(final Object o, final int n, final int n2) {
        }
    }
    
    static class EdgeEffectIcsImpl implements EdgeEffectImpl
    {
        @Override
        public boolean draw(final Object o, final Canvas canvas) {
            return EdgeEffectCompatIcs.draw(o, canvas);
        }
        
        @Override
        public void finish(final Object o) {
            EdgeEffectCompatIcs.finish(o);
        }
        
        @Override
        public boolean isFinished(final Object o) {
            return EdgeEffectCompatIcs.isFinished(o);
        }
        
        @Override
        public Object newEdgeEffect(final Context context) {
            return EdgeEffectCompatIcs.newEdgeEffect(context);
        }
        
        @Override
        public boolean onAbsorb(final Object o, final int n) {
            return EdgeEffectCompatIcs.onAbsorb(o, n);
        }
        
        @Override
        public boolean onPull(final Object o, final float n) {
            return EdgeEffectCompatIcs.onPull(o, n);
        }
        
        @Override
        public boolean onRelease(final Object o) {
            return EdgeEffectCompatIcs.onRelease(o);
        }
        
        @Override
        public void setSize(final Object o, final int n, final int n2) {
            EdgeEffectCompatIcs.setSize(o, n, n2);
        }
    }
    
    interface EdgeEffectImpl
    {
        boolean draw(Object p0, Canvas p1);
        
        void finish(Object p0);
        
        boolean isFinished(Object p0);
        
        Object newEdgeEffect(Context p0);
        
        boolean onAbsorb(Object p0, int p1);
        
        boolean onPull(Object p0, float p1);
        
        boolean onRelease(Object p0);
        
        void setSize(Object p0, int p1, int p2);
    }
}
