package android.support.v7.widget;

import android.content.*;
import android.support.v7.internal.view.*;
import android.support.v7.internal.view.menu.*;
import android.view.*;

public class PopupMenu implements MenuBuilder.Callback, MenuPresenter.Callback
{
    private View mAnchor;
    private Context mContext;
    private OnDismissListener mDismissListener;
    private MenuBuilder mMenu;
    private OnMenuItemClickListener mMenuItemClickListener;
    private MenuPopupHelper mPopup;
    
    public PopupMenu(final Context mContext, final View mAnchor) {
        super();
        this.mContext = mContext;
        (this.mMenu = new MenuBuilder(mContext)).setCallback((MenuBuilder.Callback)this);
        this.mAnchor = mAnchor;
        (this.mPopup = new MenuPopupHelper(mContext, this.mMenu, mAnchor)).setCallback(this);
    }
    
    public void dismiss() {
        this.mPopup.dismiss();
    }
    
    public Menu getMenu() {
        return (Menu)this.mMenu;
    }
    
    public MenuInflater getMenuInflater() {
        return new SupportMenuInflater(this.mContext);
    }
    
    public void inflate(final int n) {
        this.getMenuInflater().inflate(n, (Menu)this.mMenu);
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        if (this.mDismissListener != null) {
            this.mDismissListener.onDismiss(this);
        }
    }
    
    public void onCloseSubMenu(final SubMenuBuilder subMenuBuilder) {
    }
    
    @Override
    public boolean onMenuItemSelected(final MenuBuilder menuBuilder, final MenuItem menuItem) {
        return this.mMenuItemClickListener != null && this.mMenuItemClickListener.onMenuItemClick(menuItem);
    }
    
    @Override
    public void onMenuModeChange(final MenuBuilder menuBuilder) {
    }
    
    @Override
    public boolean onOpenSubMenu(final MenuBuilder menuBuilder) {
        boolean b = true;
        if (menuBuilder == null) {
            b = false;
        }
        else if (menuBuilder.hasVisibleItems()) {
            new MenuPopupHelper(this.mContext, menuBuilder, this.mAnchor).show();
            return b;
        }
        return b;
    }
    
    public void setOnDismissListener(final OnDismissListener mDismissListener) {
        this.mDismissListener = mDismissListener;
    }
    
    public void setOnMenuItemClickListener(final OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }
    
    public void show() {
        this.mPopup.show();
    }
    
    public interface OnDismissListener
    {
        void onDismiss(PopupMenu p0);
    }
    
    public interface OnMenuItemClickListener
    {
        boolean onMenuItemClick(MenuItem p0);
    }
}
