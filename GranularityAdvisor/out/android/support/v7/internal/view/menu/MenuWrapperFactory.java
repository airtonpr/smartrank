package android.support.v7.internal.view.menu;

import android.os.*;
import android.view.*;
import android.support.v4.internal.view.*;

public final class MenuWrapperFactory
{
    public static MenuItem createMenuItemWrapper(MenuItem menuItem) {
        if (Build$VERSION.SDK_INT >= 16) {
            menuItem = (MenuItem)new MenuItemWrapperJB(menuItem);
        }
        else if (Build$VERSION.SDK_INT >= 14) {
            return (MenuItem)new MenuItemWrapperICS(menuItem);
        }
        return menuItem;
    }
    
    public static Menu createMenuWrapper(Menu menu) {
        if (Build$VERSION.SDK_INT >= 14) {
            menu = (Menu)new MenuWrapperICS(menu);
        }
        return menu;
    }
    
    public static SupportMenuItem createSupportMenuItemWrapper(final MenuItem menuItem) {
        if (Build$VERSION.SDK_INT >= 16) {
            return new MenuItemWrapperJB(menuItem);
        }
        if (Build$VERSION.SDK_INT >= 14) {
            return new MenuItemWrapperICS(menuItem);
        }
        throw new UnsupportedOperationException();
    }
    
    public static SupportMenu createSupportMenuWrapper(final Menu menu) {
        if (Build$VERSION.SDK_INT >= 14) {
            return new MenuWrapperICS(menu);
        }
        throw new UnsupportedOperationException();
    }
    
    public static SupportSubMenu createSupportSubMenuWrapper(final SubMenu subMenu) {
        if (Build$VERSION.SDK_INT >= 14) {
            return new SubMenuWrapperICS(subMenu);
        }
        throw new UnsupportedOperationException();
    }
}
