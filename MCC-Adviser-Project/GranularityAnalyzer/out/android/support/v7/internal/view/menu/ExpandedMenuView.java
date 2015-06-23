package android.support.v7.internal.view.menu;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;

public final class ExpandedMenuView extends ListView implements ItemInvoker, MenuView, AdapterView$OnItemClickListener
{
    private int mAnimations;
    private MenuBuilder mMenu;
    
    public ExpandedMenuView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setOnItemClickListener((AdapterView$OnItemClickListener)this);
    }
    
    public int getWindowAnimations() {
        return this.mAnimations;
    }
    
    public void initialize(final MenuBuilder mMenu) {
        this.mMenu = mMenu;
    }
    
    public boolean invokeItem(final MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction((MenuItem)menuItemImpl, 0);
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.setChildrenDrawingCacheEnabled(false);
    }
    
    public void onItemClick(final AdapterView adapterView, final View view, final int n, final long n2) {
        this.invokeItem((MenuItemImpl)this.getAdapter().getItem(n));
    }
}
