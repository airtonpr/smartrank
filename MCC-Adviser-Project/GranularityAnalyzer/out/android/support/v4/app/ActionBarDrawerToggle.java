package android.support.v4.app;

import android.support.v4.widget.*;
import android.app.*;
import android.os.*;
import android.content.res.*;
import android.view.*;
import android.graphics.drawable.*;
import android.support.v4.graphics.drawable.*;
import android.graphics.*;
import android.support.v4.view.*;

public class ActionBarDrawerToggle implements DrawerListener
{
    private static final int ID_HOME = 16908332;
    private static final ActionBarDrawerToggleImpl IMPL;
    private static final float TOGGLE_DRAWABLE_OFFSET = 0.33333334f;
    private final Activity mActivity;
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    private Drawable mDrawerImage;
    private final int mDrawerImageResource;
    private boolean mDrawerIndicatorEnabled;
    private final DrawerLayout mDrawerLayout;
    private final int mOpenDrawerContentDescRes;
    private Object mSetIndicatorInfo;
    private SlideDrawable mSlider;
    private Drawable mThemeImage;
    
    static {
        if (Build$VERSION.SDK_INT >= 11) {
            IMPL = (ActionBarDrawerToggleImpl)new ActionBarDrawerToggleImplHC();
            return;
        }
        IMPL = (ActionBarDrawerToggleImpl)new ActionBarDrawerToggleImplBase();
    }
    
    public ActionBarDrawerToggle(final Activity mActivity, final DrawerLayout mDrawerLayout, final int mDrawerImageResource, final int mOpenDrawerContentDescRes, final int mCloseDrawerContentDescRes) {
        super();
        this.mDrawerIndicatorEnabled = true;
        this.mActivity = mActivity;
        if (mActivity instanceof DelegateProvider) {
            this.mActivityImpl = ((DelegateProvider)mActivity).getDrawerToggleDelegate();
        }
        else {
            this.mActivityImpl = null;
        }
        this.mDrawerLayout = mDrawerLayout;
        this.mDrawerImageResource = mDrawerImageResource;
        this.mOpenDrawerContentDescRes = mOpenDrawerContentDescRes;
        this.mCloseDrawerContentDescRes = mCloseDrawerContentDescRes;
        this.mThemeImage = this.getThemeUpIndicator();
        this.mDrawerImage = mActivity.getResources().getDrawable(mDrawerImageResource);
        (this.mSlider = new SlideDrawable(this.mDrawerImage)).setOffset(0.33333334f);
    }
    
    Drawable getThemeUpIndicator() {
        if (this.mActivityImpl != null) {
            return this.mActivityImpl.getThemeUpIndicator();
        }
        return ActionBarDrawerToggle.IMPL.getThemeUpIndicator(this.mActivity);
    }
    
    public boolean isDrawerIndicatorEnabled() {
        return this.mDrawerIndicatorEnabled;
    }
    
    public void onConfigurationChanged(final Configuration configuration) {
        this.mThemeImage = this.getThemeUpIndicator();
        this.mDrawerImage = this.mActivity.getResources().getDrawable(this.mDrawerImageResource);
        this.syncState();
    }
    
    @Override
    public void onDrawerClosed(final View view) {
        this.mSlider.setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            this.setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }
    
    @Override
    public void onDrawerOpened(final View view) {
        this.mSlider.setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            this.setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }
    
    @Override
    public void onDrawerSlide(final View view, final float n) {
        final float position = this.mSlider.getPosition();
        float position2;
        if (n > 0.5f) {
            position2 = Math.max(position, 2.0f * Math.max(0.0f, n - 0.5f));
        }
        else {
            position2 = Math.min(position, n * 2.0f);
        }
        this.mSlider.setPosition(position2);
    }
    
    @Override
    public void onDrawerStateChanged(final int n) {
    }
    
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        if (menuItem != null && menuItem.getItemId() == 16908332 && this.mDrawerIndicatorEnabled) {
            if (this.mDrawerLayout.isDrawerVisible(8388611)) {
                this.mDrawerLayout.closeDrawer(8388611);
            }
            else {
                this.mDrawerLayout.openDrawer(8388611);
            }
            return true;
        }
        return false;
    }
    
    void setActionBarDescription(final int actionBarDescription) {
        if (this.mActivityImpl != null) {
            this.mActivityImpl.setActionBarDescription(actionBarDescription);
            return;
        }
        this.mSetIndicatorInfo = ActionBarDrawerToggle.IMPL.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, actionBarDescription);
    }
    
    void setActionBarUpIndicator(final Drawable drawable, final int n) {
        if (this.mActivityImpl != null) {
            this.mActivityImpl.setActionBarUpIndicator(drawable, n);
            return;
        }
        this.mSetIndicatorInfo = ActionBarDrawerToggle.IMPL.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, drawable, n);
    }
    
    public void setDrawerIndicatorEnabled(final boolean mDrawerIndicatorEnabled) {
        if (mDrawerIndicatorEnabled != this.mDrawerIndicatorEnabled) {
            if (mDrawerIndicatorEnabled) {
                final SlideDrawable mSlider = this.mSlider;
                int n;
                if (this.mDrawerLayout.isDrawerOpen(8388611)) {
                    n = this.mCloseDrawerContentDescRes;
                }
                else {
                    n = this.mOpenDrawerContentDescRes;
                }
                this.setActionBarUpIndicator((Drawable)mSlider, n);
            }
            else {
                this.setActionBarUpIndicator(this.mThemeImage, 0);
            }
            this.mDrawerIndicatorEnabled = mDrawerIndicatorEnabled;
        }
    }
    
    public void syncState() {
        if (this.mDrawerLayout.isDrawerOpen(8388611)) {
            this.mSlider.setPosition(1.0f);
        }
        else {
            this.mSlider.setPosition(0.0f);
        }
        if (this.mDrawerIndicatorEnabled) {
            final SlideDrawable mSlider = this.mSlider;
            int n;
            if (this.mDrawerLayout.isDrawerOpen(8388611)) {
                n = this.mCloseDrawerContentDescRes;
            }
            else {
                n = this.mOpenDrawerContentDescRes;
            }
            this.setActionBarUpIndicator((Drawable)mSlider, n);
        }
    }
    
    private interface ActionBarDrawerToggleImpl
    {
        Drawable getThemeUpIndicator(Activity p0);
        
        Object setActionBarDescription(Object p0, Activity p1, int p2);
        
        Object setActionBarUpIndicator(Object p0, Activity p1, Drawable p2, int p3);
    }
    
    private static class ActionBarDrawerToggleImplBase implements ActionBarDrawerToggleImpl
    {
        @Override
        public Drawable getThemeUpIndicator(final Activity activity) {
            return null;
        }
        
        @Override
        public Object setActionBarDescription(final Object o, final Activity activity, final int n) {
            return o;
        }
        
        @Override
        public Object setActionBarUpIndicator(final Object o, final Activity activity, final Drawable drawable, final int n) {
            return o;
        }
    }
    
    private static class ActionBarDrawerToggleImplHC implements ActionBarDrawerToggleImpl
    {
        @Override
        public Drawable getThemeUpIndicator(final Activity activity) {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(activity);
        }
        
        @Override
        public Object setActionBarDescription(final Object o, final Activity activity, final int n) {
            return ActionBarDrawerToggleHoneycomb.setActionBarDescription(o, activity, n);
        }
        
        @Override
        public Object setActionBarUpIndicator(final Object o, final Activity activity, final Drawable drawable, final int n) {
            return ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(o, activity, drawable, n);
        }
    }
    
    public interface Delegate
    {
        Drawable getThemeUpIndicator();
        
        void setActionBarDescription(int p0);
        
        void setActionBarUpIndicator(Drawable p0, int p1);
    }
    
    public interface DelegateProvider
    {
        Delegate getDrawerToggleDelegate();
    }
    
    private class SlideDrawable extends LevelListDrawable implements Drawable$Callback
    {
        private final boolean mHasMirroring;
        private float mOffset;
        private float mPosition;
        private final Rect mTmpRect;
        
        private SlideDrawable(final Drawable drawable) {
            super();
            this.mHasMirroring = (Build$VERSION.SDK_INT > 18);
            this.mTmpRect = new Rect();
            if (DrawableCompat.isAutoMirrored(drawable)) {
                DrawableCompat.setAutoMirrored((Drawable)this, true);
            }
            this.addLevel(0, 0, drawable);
        }
        
        public void draw(final Canvas canvas) {
            int n = 1;
            this.copyBounds(this.mTmpRect);
            canvas.save();
            int n2;
            if (ViewCompat.getLayoutDirection(ActionBarDrawerToggle.this.mActivity.getWindow().getDecorView()) == n) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                n = -1;
            }
            final int width = this.mTmpRect.width();
            canvas.translate(-this.mOffset * width * this.mPosition * n, 0.0f);
            if (n2 != 0 && !this.mHasMirroring) {
                canvas.translate((float)width, 0.0f);
                canvas.scale(-1.0f, 1.0f);
            }
            super.draw(canvas);
            canvas.restore();
        }
        
        public float getPosition() {
            return this.mPosition;
        }
        
        public void setOffset(final float mOffset) {
            this.mOffset = mOffset;
            this.invalidateSelf();
        }
        
        public void setPosition(final float mPosition) {
            this.mPosition = mPosition;
            this.invalidateSelf();
        }
    }
}
