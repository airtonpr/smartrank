package android.support.v7.internal.view.menu;

import android.view.*;
import android.content.*;
import android.os.*;

public interface MenuPresenter
{
    boolean collapseItemActionView(MenuBuilder p0, MenuItemImpl p1);
    
    boolean expandItemActionView(MenuBuilder p0, MenuItemImpl p1);
    
    boolean flagActionItems();
    
    int getId();
    
    MenuView getMenuView(ViewGroup p0);
    
    void initForMenu(Context p0, MenuBuilder p1);
    
    void onCloseMenu(MenuBuilder p0, boolean p1);
    
    void onRestoreInstanceState(Parcelable p0);
    
    Parcelable onSaveInstanceState();
    
    boolean onSubMenuSelected(SubMenuBuilder p0);
    
    void setCallback(Callback p0);
    
    void updateMenuView(boolean p0);
    
    public interface Callback
    {
        void onCloseMenu(MenuBuilder p0, boolean p1);
        
        boolean onOpenSubMenu(MenuBuilder p0);
    }
}
