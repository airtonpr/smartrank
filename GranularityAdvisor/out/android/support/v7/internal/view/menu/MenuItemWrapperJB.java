package android.support.v7.internal.view.menu;

import android.support.v4.view.*;
import android.view.*;

class MenuItemWrapperJB extends MenuItemWrapperICS
{
    MenuItemWrapperJB(final MenuItem menuItem) {
        super(menuItem, false);
    }
    
    @Override
    ActionProviderWrapper createActionProviderWrapper(final ActionProvider actionProvider) {
        return new ActionProviderWrapperJB(actionProvider);
    }
    
    class ActionProviderWrapperJB extends ActionProviderWrapper implements VisibilityListener
    {
        ActionProvider$VisibilityListener mListener;
        
        public ActionProviderWrapperJB(final ActionProvider actionProvider) {
            super(actionProvider);
        }
        
        public boolean isVisible() {
            return this.mInner.isVisible();
        }
        
        @Override
        public void onActionProviderVisibilityChanged(final boolean b) {
            if (this.mListener != null) {
                this.mListener.onActionProviderVisibilityChanged(b);
            }
        }
        
        public View onCreateActionView(final MenuItem menuItem) {
            return this.mInner.onCreateActionView(menuItem);
        }
        
        public boolean overridesItemVisibility() {
            return this.mInner.overridesItemVisibility();
        }
        
        public void refreshVisibility() {
            this.mInner.refreshVisibility();
        }
        
        public void setVisibilityListener(final ActionProvider$VisibilityListener mListener) {
            this.mListener = mListener;
            final ActionProvider mInner = this.mInner;
            final ActionProvider.VisibilityListener visibilityListener;
            if (mListener == null) {
                visibilityListener = null;
            }
            mInner.setVisibilityListener(visibilityListener);
        }
    }
}
