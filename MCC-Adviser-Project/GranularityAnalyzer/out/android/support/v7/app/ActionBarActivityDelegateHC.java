package android.support.v7.app;

import android.os.*;

class ActionBarActivityDelegateHC extends ActionBarActivityDelegateBase
{
    ActionBarActivityDelegateHC(final ActionBarActivity actionBarActivity) {
        super(actionBarActivity);
    }
    
    @Override
    public ActionBar createSupportActionBar() {
        this.ensureSubDecor();
        return new ActionBarImplHC(this.mActivity, this.mActivity);
    }
    
    @Override
    void onCreate(final Bundle bundle) {
        this.mActivity.getWindow().requestFeature(10);
        super.onCreate(bundle);
    }
}
