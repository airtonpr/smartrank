package android.support.v7.internal.view.menu;

import android.graphics.drawable.*;

public interface MenuView
{
    int getWindowAnimations();
    
    void initialize(MenuBuilder p0);
    
    public interface ItemView
    {
        MenuItemImpl getItemData();
        
        void initialize(MenuItemImpl p0, int p1);
        
        boolean prefersCondensedTitle();
        
        void setCheckable(boolean p0);
        
        void setChecked(boolean p0);
        
        void setEnabled(boolean p0);
        
        void setIcon(Drawable p0);
        
        void setShortcut(boolean p0, char p1);
        
        void setTitle(CharSequence p0);
        
        boolean showsIcon();
    }
}
