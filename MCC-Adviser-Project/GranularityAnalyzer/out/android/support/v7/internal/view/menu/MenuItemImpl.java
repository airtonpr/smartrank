package android.support.v7.internal.view.menu;

import android.support.v4.internal.view.*;
import android.graphics.drawable.*;
import android.support.v4.view.*;
import android.util.*;
import android.widget.*;
import android.content.*;
import android.view.*;

public final class MenuItemImpl implements SupportMenuItem
{
    private static final int CHECKABLE = 1;
    private static final int CHECKED = 2;
    private static final int ENABLED = 16;
    private static final int EXCLUSIVE = 4;
    private static final int HIDDEN = 8;
    private static final int IS_ACTION = 32;
    static final int NO_ICON = 0;
    private static final int SHOW_AS_ACTION_MASK = 3;
    private static final String TAG = "MenuItemImpl";
    private static String sDeleteShortcutLabel;
    private static String sEnterShortcutLabel;
    private static String sPrependShortcutLabel;
    private static String sSpaceShortcutLabel;
    private ActionProvider mActionProvider;
    private View mActionView;
    private final int mCategoryOrder;
    private MenuItem$OnMenuItemClickListener mClickListener;
    private int mFlags;
    private final int mGroup;
    private Drawable mIconDrawable;
    private int mIconResId;
    private final int mId;
    private Intent mIntent;
    private boolean mIsActionViewExpanded;
    private Runnable mItemCallback;
    private MenuBuilder mMenu;
    private ContextMenu$ContextMenuInfo mMenuInfo;
    private MenuItemCompat.OnActionExpandListener mOnActionExpandListener;
    private final int mOrdering;
    private char mShortcutAlphabeticChar;
    private char mShortcutNumericChar;
    private int mShowAsAction;
    private SubMenuBuilder mSubMenu;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    
    MenuItemImpl(final MenuBuilder mMenu, final int mGroup, final int mId, final int mCategoryOrder, final int mOrdering, final CharSequence mTitle, final int mShowAsAction) {
        super();
        this.mIconResId = 0;
        this.mFlags = 16;
        this.mShowAsAction = 0;
        this.mIsActionViewExpanded = false;
        this.mMenu = mMenu;
        this.mId = mId;
        this.mGroup = mGroup;
        this.mCategoryOrder = mCategoryOrder;
        this.mOrdering = mOrdering;
        this.mTitle = mTitle;
        this.mShowAsAction = mShowAsAction;
    }
    
    public void actionFormatChanged() {
        this.mMenu.onItemActionRequestChanged(this);
    }
    
    @Override
    public boolean collapseActionView() {
        if ((0x8 & this.mShowAsAction) != 0x0) {
            if (this.mActionView == null) {
                return true;
            }
            if (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionCollapse((MenuItem)this)) {
                return this.mMenu.collapseItemActionView(this);
            }
        }
        return false;
    }
    
    @Override
    public boolean expandActionView() {
        return (0x8 & this.mShowAsAction) != 0x0 && this.mActionView != null && (this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionExpand((MenuItem)this)) && this.mMenu.expandItemActionView(this);
    }
    
    public android.view.ActionProvider getActionProvider() {
        throw new UnsupportedOperationException("Implementation should use getSupportActionProvider!");
    }
    
    @Override
    public View getActionView() {
        if (this.mActionView != null) {
            return this.mActionView;
        }
        if (this.mActionProvider != null) {
            return this.mActionView = this.mActionProvider.onCreateActionView((MenuItem)this);
        }
        return null;
    }
    
    public char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }
    
    Runnable getCallback() {
        return this.mItemCallback;
    }
    
    public int getGroupId() {
        return this.mGroup;
    }
    
    public Drawable getIcon() {
        if (this.mIconDrawable != null) {
            return this.mIconDrawable;
        }
        if (this.mIconResId != 0) {
            final Drawable drawable = this.mMenu.getResources().getDrawable(this.mIconResId);
            this.mIconResId = 0;
            return this.mIconDrawable = drawable;
        }
        return null;
    }
    
    public Intent getIntent() {
        return this.mIntent;
    }
    
    @ViewDebug$CapturedViewProperty
    public int getItemId() {
        return this.mId;
    }
    
    public ContextMenu$ContextMenuInfo getMenuInfo() {
        return this.mMenuInfo;
    }
    
    public char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }
    
    public int getOrder() {
        return this.mCategoryOrder;
    }
    
    public int getOrdering() {
        return this.mOrdering;
    }
    
    char getShortcut() {
        return this.mShortcutAlphabeticChar;
    }
    
    String getShortcutLabel() {
        final char shortcut = this.getShortcut();
        if (shortcut == '\0') {
            return "";
        }
        final StringBuilder sb = new StringBuilder(MenuItemImpl.sPrependShortcutLabel);
        switch (shortcut) {
            default: {
                sb.append(shortcut);
                break;
            }
            case 10: {
                sb.append(MenuItemImpl.sEnterShortcutLabel);
                break;
            }
            case 8: {
                sb.append(MenuItemImpl.sDeleteShortcutLabel);
                break;
            }
            case 32: {
                sb.append(MenuItemImpl.sSpaceShortcutLabel);
                break;
            }
        }
        return sb.toString();
    }
    
    public SubMenu getSubMenu() {
        return (SubMenu)this.mSubMenu;
    }
    
    @Override
    public ActionProvider getSupportActionProvider() {
        return this.mActionProvider;
    }
    
    @ViewDebug$CapturedViewProperty
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public CharSequence getTitleCondensed() {
        if (this.mTitleCondensed != null) {
            return this.mTitleCondensed;
        }
        return this.mTitle;
    }
    
    CharSequence getTitleForItemView(final MenuView.ItemView itemView) {
        if (itemView != null && itemView.prefersCondensedTitle()) {
            return this.getTitleCondensed();
        }
        return this.getTitle();
    }
    
    public boolean hasCollapsibleActionView() {
        return (0x8 & this.mShowAsAction) != 0x0 && this.mActionView != null;
    }
    
    public boolean hasSubMenu() {
        return this.mSubMenu != null;
    }
    
    public boolean invoke() {
        if ((this.mClickListener == null || !this.mClickListener.onMenuItemClick((MenuItem)this)) && !this.mMenu.dispatchMenuItemSelected(this.mMenu.getRootMenu(), (MenuItem)this)) {
            if (this.mItemCallback != null) {
                this.mItemCallback.run();
                return true;
            }
            if (this.mIntent != null) {
                try {
                    this.mMenu.getContext().startActivity(this.mIntent);
                    return true;
                }
                catch (ActivityNotFoundException ex) {
                    Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", (Throwable)ex);
                }
            }
            if (this.mActionProvider == null || !this.mActionProvider.onPerformDefaultAction()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isActionButton() {
        return (0x20 & this.mFlags) == 0x20;
    }
    
    @Override
    public boolean isActionViewExpanded() {
        return this.mIsActionViewExpanded;
    }
    
    public boolean isCheckable() {
        return (0x1 & this.mFlags) == 0x1;
    }
    
    public boolean isChecked() {
        return (0x2 & this.mFlags) == 0x2;
    }
    
    public boolean isEnabled() {
        return (0x10 & this.mFlags) != 0x0;
    }
    
    public boolean isExclusiveCheckable() {
        return (0x4 & this.mFlags) != 0x0;
    }
    
    public boolean isVisible() {
        if (this.mActionProvider != null && this.mActionProvider.overridesItemVisibility()) {
            if ((0x8 & this.mFlags) != 0x0 || !this.mActionProvider.isVisible()) {
                return false;
            }
        }
        else if ((0x8 & this.mFlags) != 0x0) {
            return false;
        }
        return true;
    }
    
    public boolean requestsActionButton() {
        return (0x1 & this.mShowAsAction) == 0x1;
    }
    
    public boolean requiresActionButton() {
        return (0x2 & this.mShowAsAction) == 0x2;
    }
    
    public MenuItem setActionProvider(final android.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException("Implementation should use setSupportActionProvider!");
    }
    
    public SupportMenuItem setActionView(final int n) {
        final Context context = this.mMenu.getContext();
        this.setActionView(LayoutInflater.from(context).inflate(n, (ViewGroup)new LinearLayout(context), false));
        return this;
    }
    
    public SupportMenuItem setActionView(final View mActionView) {
        this.mActionView = mActionView;
        this.mActionProvider = null;
        if (mActionView != null && mActionView.getId() == -1 && this.mId > 0) {
            mActionView.setId(this.mId);
        }
        this.mMenu.onItemActionRequestChanged(this);
        return this;
    }
    
    public void setActionViewExpanded(final boolean mIsActionViewExpanded) {
        this.mIsActionViewExpanded = mIsActionViewExpanded;
        this.mMenu.onItemsChanged(false);
    }
    
    public MenuItem setAlphabeticShortcut(final char c) {
        if (this.mShortcutAlphabeticChar == c) {
            return (MenuItem)this;
        }
        this.mShortcutAlphabeticChar = Character.toLowerCase(c);
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public MenuItem setCallback(final Runnable mItemCallback) {
        this.mItemCallback = mItemCallback;
        return (MenuItem)this;
    }
    
    public MenuItem setCheckable(final boolean b) {
        final int mFlags = this.mFlags;
        final int n = 0xFFFFFFFE & this.mFlags;
        boolean b2;
        if (b) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        this.mFlags = ((b2 ? 1 : 0) | n);
        if (mFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
        return (MenuItem)this;
    }
    
    public MenuItem setChecked(final boolean checkedInt) {
        if ((0x4 & this.mFlags) != 0x0) {
            this.mMenu.setExclusiveItemChecked((MenuItem)this);
            return (MenuItem)this;
        }
        this.setCheckedInt(checkedInt);
        return (MenuItem)this;
    }
    
    void setCheckedInt(final boolean b) {
        final int mFlags = this.mFlags;
        final int n = 0xFFFFFFFD & this.mFlags;
        int n2;
        if (b) {
            n2 = 2;
        }
        else {
            n2 = 0;
        }
        this.mFlags = (n2 | n);
        if (mFlags != this.mFlags) {
            this.mMenu.onItemsChanged(false);
        }
    }
    
    public MenuItem setEnabled(final boolean b) {
        if (b) {
            this.mFlags |= 0x10;
        }
        else {
            this.mFlags &= 0xFFFFFFEF;
        }
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public void setExclusiveCheckable(final boolean b) {
        final int n = 0xFFFFFFFB & this.mFlags;
        int n2;
        if (b) {
            n2 = 4;
        }
        else {
            n2 = 0;
        }
        this.mFlags = (n2 | n);
    }
    
    public MenuItem setIcon(final int mIconResId) {
        this.mIconDrawable = null;
        this.mIconResId = mIconResId;
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public MenuItem setIcon(final Drawable mIconDrawable) {
        this.mIconResId = 0;
        this.mIconDrawable = mIconDrawable;
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public MenuItem setIntent(final Intent mIntent) {
        this.mIntent = mIntent;
        return (MenuItem)this;
    }
    
    public void setIsActionButton(final boolean b) {
        if (b) {
            this.mFlags |= 0x20;
            return;
        }
        this.mFlags &= 0xFFFFFFDF;
    }
    
    void setMenuInfo(final ContextMenu$ContextMenuInfo mMenuInfo) {
        this.mMenuInfo = mMenuInfo;
    }
    
    public MenuItem setNumericShortcut(final char mShortcutNumericChar) {
        if (this.mShortcutNumericChar == mShortcutNumericChar) {
            return (MenuItem)this;
        }
        this.mShortcutNumericChar = mShortcutNumericChar;
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public MenuItem setOnActionExpandListener(final MenuItem$OnActionExpandListener menuItem$OnActionExpandListener) {
        throw new UnsupportedOperationException("Implementation should use setSupportOnActionExpandListener!");
    }
    
    public MenuItem setOnMenuItemClickListener(final MenuItem$OnMenuItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
        return (MenuItem)this;
    }
    
    public MenuItem setShortcut(final char mShortcutNumericChar, final char c) {
        this.mShortcutNumericChar = mShortcutNumericChar;
        this.mShortcutAlphabeticChar = Character.toLowerCase(c);
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    @Override
    public void setShowAsAction(final int mShowAsAction) {
        switch (mShowAsAction & 0x3) {
            default: {
                throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
            }
            case 0:
            case 1:
            case 2: {
                this.mShowAsAction = mShowAsAction;
                this.mMenu.onItemActionRequestChanged(this);
            }
        }
    }
    
    public SupportMenuItem setShowAsActionFlags(final int showAsAction) {
        this.setShowAsAction(showAsAction);
        return this;
    }
    
    void setSubMenu(final SubMenuBuilder mSubMenu) {
        (this.mSubMenu = mSubMenu).setHeaderTitle(this.getTitle());
    }
    
    @Override
    public SupportMenuItem setSupportActionProvider(final ActionProvider mActionProvider) {
        if (this.mActionProvider != mActionProvider) {
            this.mActionView = null;
            if (this.mActionProvider != null) {
                this.mActionProvider.setVisibilityListener(null);
            }
            this.mActionProvider = mActionProvider;
            this.mMenu.onItemsChanged(true);
            if (mActionProvider != null) {
                mActionProvider.setVisibilityListener((ActionProvider.VisibilityListener)new ActionProvider.VisibilityListener() {
                    @Override
                    public void onActionProviderVisibilityChanged(final boolean b) {
                        MenuItemImpl.this.mMenu.onItemVisibleChanged(MenuItemImpl.this);
                    }
                });
                return this;
            }
        }
        return this;
    }
    
    @Override
    public SupportMenuItem setSupportOnActionExpandListener(final MenuItemCompat.OnActionExpandListener mOnActionExpandListener) {
        this.mOnActionExpandListener = mOnActionExpandListener;
        return this;
    }
    
    public MenuItem setTitle(final int n) {
        return this.setTitle(this.mMenu.getContext().getString(n));
    }
    
    public MenuItem setTitle(final CharSequence charSequence) {
        this.mTitle = charSequence;
        this.mMenu.onItemsChanged(false);
        if (this.mSubMenu != null) {
            this.mSubMenu.setHeaderTitle(charSequence);
        }
        return (MenuItem)this;
    }
    
    public MenuItem setTitleCondensed(final CharSequence mTitleCondensed) {
        this.mTitleCondensed = mTitleCondensed;
        if (mTitleCondensed == null) {
            final CharSequence mTitle = this.mTitle;
        }
        this.mMenu.onItemsChanged(false);
        return (MenuItem)this;
    }
    
    public MenuItem setVisible(final boolean visibleInt) {
        if (this.setVisibleInt(visibleInt)) {
            this.mMenu.onItemVisibleChanged(this);
        }
        return (MenuItem)this;
    }
    
    boolean setVisibleInt(final boolean b) {
        final int mFlags = this.mFlags;
        final int n = 0xFFFFFFF7 & this.mFlags;
        int n2;
        if (b) {
            n2 = 0;
        }
        else {
            n2 = 8;
        }
        this.mFlags = (n2 | n);
        final int mFlags2 = this.mFlags;
        boolean b2 = false;
        if (mFlags != mFlags2) {
            b2 = true;
        }
        return b2;
    }
    
    public boolean shouldShowIcon() {
        return this.mMenu.getOptionalIconsVisible();
    }
    
    boolean shouldShowShortcut() {
        return this.mMenu.isShortcutsVisible() && this.getShortcut() != '\0';
    }
    
    public boolean showsTextAsAction() {
        return (0x4 & this.mShowAsAction) == 0x4;
    }
    
    @Override
    public String toString() {
        return this.mTitle.toString();
    }
}
