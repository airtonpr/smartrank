package android.support.v4.view;

import android.os.*;
import android.view.*;

public class KeyEventCompat
{
    static final KeyEventVersionImpl IMPL;
    
    static {
        if (Build$VERSION.SDK_INT >= 11) {
            IMPL = (KeyEventVersionImpl)new HoneycombKeyEventVersionImpl();
            return;
        }
        IMPL = (KeyEventVersionImpl)new BaseKeyEventVersionImpl();
    }
    
    public static boolean dispatch(final KeyEvent keyEvent, final KeyEvent$Callback keyEvent$Callback, final Object o, final Object o2) {
        return KeyEventCompat.IMPL.dispatch(keyEvent, keyEvent$Callback, o, o2);
    }
    
    public static Object getKeyDispatcherState(final View view) {
        return KeyEventCompat.IMPL.getKeyDispatcherState(view);
    }
    
    public static boolean hasModifiers(final KeyEvent keyEvent, final int n) {
        return KeyEventCompat.IMPL.metaStateHasModifiers(keyEvent.getMetaState(), n);
    }
    
    public static boolean hasNoModifiers(final KeyEvent keyEvent) {
        return KeyEventCompat.IMPL.metaStateHasNoModifiers(keyEvent.getMetaState());
    }
    
    public static boolean isTracking(final KeyEvent keyEvent) {
        return KeyEventCompat.IMPL.isTracking(keyEvent);
    }
    
    public static boolean metaStateHasModifiers(final int n, final int n2) {
        return KeyEventCompat.IMPL.metaStateHasModifiers(n, n2);
    }
    
    public static boolean metaStateHasNoModifiers(final int n) {
        return KeyEventCompat.IMPL.metaStateHasNoModifiers(n);
    }
    
    public static int normalizeMetaState(final int n) {
        return KeyEventCompat.IMPL.normalizeMetaState(n);
    }
    
    public static void startTracking(final KeyEvent keyEvent) {
        KeyEventCompat.IMPL.startTracking(keyEvent);
    }
    
    static class BaseKeyEventVersionImpl implements KeyEventVersionImpl
    {
        private static final int META_ALL_MASK = 247;
        private static final int META_MODIFIER_MASK = 247;
        
        private static int metaStateFilterDirectionalModifiers(int n, final int n2, final int n3, final int n4, final int n5) {
            int n6 = 1;
            int n7;
            if ((n2 & n3) != 0x0) {
                n7 = n6;
            }
            else {
                n7 = 0;
            }
            final int n8 = n4 | n5;
            if ((n2 & n8) == 0x0) {
                n6 = 0;
            }
            if (n7 != 0) {
                if (n6 != 0) {
                    throw new IllegalArgumentException("bad arguments");
                }
                n &= ~n8;
            }
            else if (n6 != 0) {
                return n & ~n3;
            }
            return n;
        }
        
        @Override
        public boolean dispatch(final KeyEvent keyEvent, final KeyEvent$Callback keyEvent$Callback, final Object o, final Object o2) {
            return keyEvent.dispatch(keyEvent$Callback);
        }
        
        @Override
        public Object getKeyDispatcherState(final View view) {
            return null;
        }
        
        @Override
        public boolean isTracking(final KeyEvent keyEvent) {
            return false;
        }
        
        @Override
        public boolean metaStateHasModifiers(final int n, final int n2) {
            return metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(0xF7 & this.normalizeMetaState(n), n2, 1, 64, 128), n2, 2, 16, 32) == n2;
        }
        
        @Override
        public boolean metaStateHasNoModifiers(final int n) {
            return (0xF7 & this.normalizeMetaState(n)) == 0x0;
        }
        
        @Override
        public int normalizeMetaState(int n) {
            if ((n & 0xC0) != 0x0) {
                n |= 0x1;
            }
            if ((n & 0x30) != 0x0) {
                n |= 0x2;
            }
            return n & 0xF7;
        }
        
        @Override
        public void startTracking(final KeyEvent keyEvent) {
        }
    }
    
    static class EclairKeyEventVersionImpl extends BaseKeyEventVersionImpl
    {
        @Override
        public boolean dispatch(final KeyEvent keyEvent, final KeyEvent$Callback keyEvent$Callback, final Object o, final Object o2) {
            return KeyEventCompatEclair.dispatch(keyEvent, keyEvent$Callback, o, o2);
        }
        
        @Override
        public Object getKeyDispatcherState(final View view) {
            return KeyEventCompatEclair.getKeyDispatcherState(view);
        }
        
        @Override
        public boolean isTracking(final KeyEvent keyEvent) {
            return KeyEventCompatEclair.isTracking(keyEvent);
        }
        
        @Override
        public void startTracking(final KeyEvent keyEvent) {
            KeyEventCompatEclair.startTracking(keyEvent);
        }
    }
    
    static class HoneycombKeyEventVersionImpl extends EclairKeyEventVersionImpl
    {
        @Override
        public boolean metaStateHasModifiers(final int n, final int n2) {
            return KeyEventCompatHoneycomb.metaStateHasModifiers(n, n2);
        }
        
        @Override
        public boolean metaStateHasNoModifiers(final int n) {
            return KeyEventCompatHoneycomb.metaStateHasNoModifiers(n);
        }
        
        @Override
        public int normalizeMetaState(final int n) {
            return KeyEventCompatHoneycomb.normalizeMetaState(n);
        }
    }
    
    interface KeyEventVersionImpl
    {
        boolean dispatch(KeyEvent p0, KeyEvent$Callback p1, Object p2, Object p3);
        
        Object getKeyDispatcherState(View p0);
        
        boolean isTracking(KeyEvent p0);
        
        boolean metaStateHasModifiers(int p0, int p1);
        
        boolean metaStateHasNoModifiers(int p0);
        
        int normalizeMetaState(int p0);
        
        void startTracking(KeyEvent p0);
    }
}
