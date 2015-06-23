package android.support.v7.app;

import android.app.*;

class ActionBarActivityDelegateJB extends ActionBarActivityDelegateICS
{
    ActionBarActivityDelegateJB(final ActionBarActivity actionBarActivity) {
        super(actionBarActivity);
    }
    
    @Override
    public ActionBar createSupportActionBar() {
        return new ActionBarImplJB(this.mActivity, this.mActivity);
    }
}
