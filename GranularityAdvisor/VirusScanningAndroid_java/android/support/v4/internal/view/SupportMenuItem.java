package android.support.v4.internal.view;

import android.view.*;
import android.support.v4.view.*;

public interface SupportMenuItem extends MenuItem
{
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    
    boolean collapseActionView();
    
    boolean expandActionView();
    
    View getActionView();
    
    ActionProvider getSupportActionProvider();
    
    boolean isActionViewExpanded();
    
    MenuItem setActionView(int p0);
    
    MenuItem setActionView(View p0);
    
    void setShowAsAction(int p0);
    
    MenuItem setShowAsActionFlags(int p0);
    
    SupportMenuItem setSupportActionProvider(ActionProvider p0);
    
    SupportMenuItem setSupportOnActionExpandListener(MenuItemCompat.OnActionExpandListener p0);
}
