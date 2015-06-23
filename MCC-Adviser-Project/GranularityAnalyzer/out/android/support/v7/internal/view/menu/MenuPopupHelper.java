package android.support.v7.internal.view.menu;

import android.content.*;
import android.support.v7.internal.widget.*;
import android.support.v7.appcompat.*;
import android.content.res.*;
import android.view.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.util.*;

public class MenuPopupHelper implements AdapterView$OnItemClickListener, View$OnKeyListener, ViewTreeObserver$OnGlobalLayoutListener, PopupWindow$OnDismissListener, MenuPresenter
{
    static final int ITEM_LAYOUT = 0;
    private static final String TAG = "MenuPopupHelper";
    private MenuAdapter mAdapter;
    private View mAnchorView;
    private Context mContext;
    boolean mForceShowIcon;
    private LayoutInflater mInflater;
    private ViewGroup mMeasureParent;
    private MenuBuilder mMenu;
    private boolean mOverflowOnly;
    private ListPopupWindow mPopup;
    private int mPopupMaxWidth;
    private Callback mPresenterCallback;
    private ViewTreeObserver mTreeObserver;
    
    static {
        ITEM_LAYOUT = R.layout.abc_popup_menu_item_layout;
    }
    
    public MenuPopupHelper(final Context context, final MenuBuilder menuBuilder) {
        this(context, menuBuilder, null, false);
    }
    
    public MenuPopupHelper(final Context context, final MenuBuilder menuBuilder, final View view) {
        this(context, menuBuilder, view, false);
    }
    
    public MenuPopupHelper(final Context mContext, final MenuBuilder mMenu, final View mAnchorView, final boolean mOverflowOnly) {
        super();
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mMenu = mMenu;
        this.mOverflowOnly = mOverflowOnly;
        final Resources resources = mContext.getResources();
        this.mPopupMaxWidth = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
        this.mAnchorView = mAnchorView;
        mMenu.addMenuPresenter(this);
    }
    
    private int measureContentWidth(final ListAdapter listAdapter) {
        int max = 0;
        View view = null;
        int n = 0;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(0, 0);
        final int measureSpec2 = View$MeasureSpec.makeMeasureSpec(0, 0);
        for (int count = listAdapter.getCount(), i = 0; i < count; ++i) {
            final int itemViewType = listAdapter.getItemViewType(i);
            if (itemViewType != n) {
                n = itemViewType;
                view = null;
            }
            if (this.mMeasureParent == null) {
                this.mMeasureParent = (ViewGroup)new FrameLayout(this.mContext);
            }
            view = listAdapter.getView(i, view, this.mMeasureParent);
            view.measure(measureSpec, measureSpec2);
            max = Math.max(max, view.getMeasuredWidth());
        }
        return max;
    }
    
    public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    public void dismiss() {
        if (this.isShowing()) {
            this.mPopup.dismiss();
        }
    }
    
    public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
        return false;
    }
    
    public boolean flagActionItems() {
        return false;
    }
    
    public int getId() {
        return 0;
    }
    
    public MenuView getMenuView(final ViewGroup viewGroup) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }
    
    public void initForMenu(final Context context, final MenuBuilder menuBuilder) {
    }
    
    public boolean isShowing() {
        return this.mPopup != null && this.mPopup.isShowing();
    }
    
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        if (menuBuilder == this.mMenu) {
            this.dismiss();
            if (this.mPresenterCallback != null) {
                this.mPresenterCallback.onCloseMenu(menuBuilder, b);
            }
        }
    }
    
    public void onDismiss() {
        this.mPopup = null;
        this.mMenu.close();
        if (this.mTreeObserver != null) {
            if (!this.mTreeObserver.isAlive()) {
                this.mTreeObserver = this.mAnchorView.getViewTreeObserver();
            }
            this.mTreeObserver.removeGlobalOnLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
            this.mTreeObserver = null;
        }
    }
    
    public void onGlobalLayout() {
        if (this.isShowing()) {
            final View mAnchorView = this.mAnchorView;
            if (mAnchorView == null || !mAnchorView.isShown()) {
                this.dismiss();
            }
            else if (this.isShowing()) {
                this.mPopup.show();
            }
        }
    }
    
    public void onItemClick(final AdapterView<?> adapterView, final View view, final int n, final long n2) {
        final MenuAdapter mAdapter = this.mAdapter;
        mAdapter.mAdapterMenu.performItemAction((MenuItem)mAdapter.getItem(n), 0);
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && n == 82) {
            this.dismiss();
            return true;
        }
        return false;
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
    }
    
    public Parcelable onSaveInstanceState() {
        return null;
    }
    
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        final boolean hasVisibleItems = subMenuBuilder.hasVisibleItems();
        boolean b = false;
        if (hasVisibleItems) {
            final MenuPopupHelper menuPopupHelper = new MenuPopupHelper(this.mContext, subMenuBuilder, this.mAnchorView, false);
            menuPopupHelper.setCallback(this.mPresenterCallback);
            final int size = subMenuBuilder.size();
            int n = 0;
            boolean forceShowIcon;
            while (true) {
                forceShowIcon = false;
                if (n >= size) {
                    break;
                }
                final MenuItem item = subMenuBuilder.getItem(n);
                if (item.isVisible() && item.getIcon() != null) {
                    forceShowIcon = true;
                    break;
                }
                ++n;
            }
            menuPopupHelper.setForceShowIcon(forceShowIcon);
            final boolean tryShow = menuPopupHelper.tryShow();
            b = false;
            if (tryShow) {
                if (this.mPresenterCallback != null) {
                    this.mPresenterCallback.onOpenSubMenu(subMenuBuilder);
                }
                b = true;
            }
        }
        return b;
    }
    
    public void setAnchorView(final View mAnchorView) {
        this.mAnchorView = mAnchorView;
    }
    
    public void setCallback(final Callback mPresenterCallback) {
        this.mPresenterCallback = mPresenterCallback;
    }
    
    public void setForceShowIcon(final boolean mForceShowIcon) {
        this.mForceShowIcon = mForceShowIcon;
    }
    
    public void show() {
        if (!this.tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }
    
    public boolean tryShow() {
        (this.mPopup = new ListPopupWindow(this.mContext, null, R.attr.popupMenuStyle)).setOnDismissListener((PopupWindow$OnDismissListener)this);
        this.mPopup.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        this.mAdapter = new MenuAdapter(this.mMenu);
        this.mPopup.setAdapter((ListAdapter)this.mAdapter);
        this.mPopup.setModal(true);
        final View mAnchorView = this.mAnchorView;
        if (mAnchorView != null) {
            final ViewTreeObserver mTreeObserver = this.mTreeObserver;
            boolean b = false;
            if (mTreeObserver == null) {
                b = true;
            }
            this.mTreeObserver = mAnchorView.getViewTreeObserver();
            if (b) {
                this.mTreeObserver.addOnGlobalLayoutListener((ViewTreeObserver$OnGlobalLayoutListener)this);
            }
            this.mPopup.setAnchorView(mAnchorView);
            this.mPopup.setContentWidth(Math.min(this.measureContentWidth((ListAdapter)this.mAdapter), this.mPopupMaxWidth));
            this.mPopup.setInputMethodMode(2);
            this.mPopup.show();
            this.mPopup.getListView().setOnKeyListener((View$OnKeyListener)this);
            return true;
        }
        return false;
    }
    
    public void updateMenuView(final boolean b) {
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }
    
    private class MenuAdapter extends BaseAdapter
    {
        private MenuBuilder mAdapterMenu;
        private int mExpandedIndex;
        
        public MenuAdapter(final MenuBuilder mAdapterMenu) {
            super();
            this.mExpandedIndex = -1;
            this.mAdapterMenu = mAdapterMenu;
            this.findExpandedIndex();
        }
        
        void findExpandedIndex() {
            final MenuItemImpl expandedItem = MenuPopupHelper.this.mMenu.getExpandedItem();
            if (expandedItem != null) {
                final ArrayList<MenuItemImpl> nonActionItems = MenuPopupHelper.this.mMenu.getNonActionItems();
                for (int size = nonActionItems.size(), i = 0; i < size; ++i) {
                    if (nonActionItems.get(i) == expandedItem) {
                        this.mExpandedIndex = i;
                        return;
                    }
                }
            }
            this.mExpandedIndex = -1;
        }
        
        public int getCount() {
            ArrayList<MenuItemImpl> list;
            if (MenuPopupHelper.this.mOverflowOnly) {
                list = this.mAdapterMenu.getNonActionItems();
            }
            else {
                list = this.mAdapterMenu.getVisibleItems();
            }
            if (this.mExpandedIndex < 0) {
                return list.size();
            }
            return -1 + list.size();
        }
        
        public MenuItemImpl getItem(int n) {
            ArrayList<MenuItemImpl> list;
            if (MenuPopupHelper.this.mOverflowOnly) {
                list = this.mAdapterMenu.getNonActionItems();
            }
            else {
                list = this.mAdapterMenu.getVisibleItems();
            }
            if (this.mExpandedIndex >= 0 && n >= this.mExpandedIndex) {
                ++n;
            }
            return list.get(n);
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(final int n, View inflate, final ViewGroup viewGroup) {
            if (inflate == null) {
                inflate = MenuPopupHelper.this.mInflater.inflate(MenuPopupHelper.ITEM_LAYOUT, viewGroup, false);
            }
            final MenuView.ItemView itemView = (MenuView.ItemView)inflate;
            if (MenuPopupHelper.this.mForceShowIcon) {
                ((ListMenuItemView)inflate).setForceShowIcon(true);
            }
            itemView.initialize(this.getItem(n), 0);
            return inflate;
        }
        
        public void notifyDataSetChanged() {
            this.findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }
}
