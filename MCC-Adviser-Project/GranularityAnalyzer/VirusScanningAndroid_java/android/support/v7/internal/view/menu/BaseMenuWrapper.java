package android.support.v7.internal.view.menu;

import android.support.v4.internal.view.*;
import android.view.*;
import java.util.*;

abstract class BaseMenuWrapper<T> extends BaseWrapper<T>
{
    private HashMap<MenuItem, SupportMenuItem> mMenuItems;
    private HashMap<SubMenu, SubMenu> mSubMenus;
    
    BaseMenuWrapper(final T t) {
        super(t);
    }
    
    final SupportMenuItem getMenuItemWrapper(final MenuItem menuItem) {
        if (menuItem != null) {
            if (this.mMenuItems == null) {
                this.mMenuItems = new HashMap<MenuItem, SupportMenuItem>();
            }
            SupportMenuItem supportMenuItemWrapper = this.mMenuItems.get(menuItem);
            if (supportMenuItemWrapper == null) {
                supportMenuItemWrapper = MenuWrapperFactory.createSupportMenuItemWrapper(menuItem);
                this.mMenuItems.put(menuItem, supportMenuItemWrapper);
            }
            return supportMenuItemWrapper;
        }
        return null;
    }
    
    final SubMenu getSubMenuWrapper(final SubMenu subMenu) {
        if (subMenu != null) {
            if (this.mSubMenus == null) {
                this.mSubMenus = new HashMap<SubMenu, SubMenu>();
            }
            Object supportSubMenuWrapper = this.mSubMenus.get(subMenu);
            if (supportSubMenuWrapper == null) {
                supportSubMenuWrapper = MenuWrapperFactory.createSupportSubMenuWrapper(subMenu);
                this.mSubMenus.put(subMenu, (SubMenu)supportSubMenuWrapper);
            }
            return (SubMenu)supportSubMenuWrapper;
        }
        return null;
    }
    
    final void internalClear() {
        if (this.mMenuItems != null) {
            this.mMenuItems.clear();
        }
        if (this.mSubMenus != null) {
            this.mSubMenus.clear();
        }
    }
    
    final void internalRemoveGroup(final int n) {
        if (this.mMenuItems != null) {
            final Iterator<MenuItem> iterator = this.mMenuItems.keySet().iterator();
            while (iterator.hasNext()) {
                if (n == iterator.next().getGroupId()) {
                    iterator.remove();
                }
            }
        }
    }
    
    final void internalRemoveItem(final int n) {
        if (this.mMenuItems != null) {
            final Iterator<MenuItem> iterator = this.mMenuItems.keySet().iterator();
            while (iterator.hasNext()) {
                if (n == iterator.next().getItemId()) {
                    iterator.remove();
                }
            }
        }
    }
}
