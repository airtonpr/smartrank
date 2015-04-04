package android.support.v7.internal.widget;

import android.support.v7.app.*;
import android.content.*;
import android.graphics.drawable.*;
import android.util.*;
import android.support.v7.appcompat.*;
import android.text.*;
import android.content.res.*;
import android.support.v4.internal.view.*;
import android.support.v7.view.*;
import android.support.v7.internal.view.menu.*;
import android.view.accessibility.*;
import android.widget.*;
import android.view.*;
import android.os.*;

public class ActionBarView extends AbsActionBarView
{
    private static final int DEFAULT_CUSTOM_GRAVITY = 19;
    public static final int DISPLAY_DEFAULT = 0;
    private static final int DISPLAY_RELAYOUT_MASK = 31;
    private static final String TAG = "ActionBarView";
    private ActionBar.OnNavigationListener mCallback;
    private Context mContext;
    private ActionBarContextView mContextView;
    private View mCustomNavView;
    private int mDisplayOptions;
    View mExpandedActionView;
    private final View$OnClickListener mExpandedActionViewUpListener;
    private HomeView mExpandedHomeLayout;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private HomeView mHomeLayout;
    private Drawable mIcon;
    private boolean mIncludeTabs;
    private int mIndeterminateProgressStyle;
    private ProgressBarICS mIndeterminateProgressView;
    private boolean mIsCollapsable;
    private boolean mIsCollapsed;
    private int mItemPadding;
    private LinearLayout mListNavLayout;
    private Drawable mLogo;
    private ActionMenuItem mLogoNavItem;
    private final AdapterViewICS.OnItemSelectedListener mNavItemSelectedListener;
    private int mNavigationMode;
    private MenuBuilder mOptionsMenu;
    private int mProgressBarPadding;
    private int mProgressStyle;
    private ProgressBarICS mProgressView;
    private SpinnerICS mSpinner;
    private SpinnerAdapter mSpinnerAdapter;
    private CharSequence mSubtitle;
    private int mSubtitleStyleRes;
    private TextView mSubtitleView;
    private ScrollingTabContainerView mTabScrollView;
    private Runnable mTabSelector;
    private CharSequence mTitle;
    private LinearLayout mTitleLayout;
    private int mTitleStyleRes;
    private View mTitleUpView;
    private TextView mTitleView;
    private final View$OnClickListener mUpClickListener;
    private boolean mUserTitle;
    Window$Callback mWindowCallback;
    
    public ActionBarView(final Context p0, final AttributeSet p1) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0        
        //     1: aload_1        
        //     2: aload_2        
        //     3: invokespecial   android/support/v7/internal/widget/AbsActionBarView.<init>:(Landroid/content/Context;Landroid/util/AttributeSet;)V
        //     6: aload_0        
        //     7: iconst_m1      
        //     8: putfield        android/support/v7/internal/widget/ActionBarView.mDisplayOptions:I
        //    11: aload_0        
        //    12: new             Landroid/support/v7/internal/widget/ActionBarView$1;
        //    15: dup            
        //    16: aload_0        
        //    17: invokespecial   android/support/v7/internal/widget/ActionBarView$1.<init>:(Landroid/support/v7/internal/widget/ActionBarView;)V
        //    20: putfield        android/support/v7/internal/widget/ActionBarView.mNavItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewICS$OnItemSelectedListener;
        //    23: aload_0        
        //    24: new             Landroid/support/v7/internal/widget/ActionBarView$2;
        //    27: dup            
        //    28: aload_0        
        //    29: invokespecial   android/support/v7/internal/widget/ActionBarView$2.<init>:(Landroid/support/v7/internal/widget/ActionBarView;)V
        //    32: putfield        android/support/v7/internal/widget/ActionBarView.mExpandedActionViewUpListener:Landroid/view/View$OnClickListener;
        //    35: aload_0        
        //    36: new             Landroid/support/v7/internal/widget/ActionBarView$3;
        //    39: dup            
        //    40: aload_0        
        //    41: invokespecial   android/support/v7/internal/widget/ActionBarView$3.<init>:(Landroid/support/v7/internal/widget/ActionBarView;)V
        //    44: putfield        android/support/v7/internal/widget/ActionBarView.mUpClickListener:Landroid/view/View$OnClickListener;
        //    47: aload_0        
        //    48: aload_1        
        //    49: putfield        android/support/v7/internal/widget/ActionBarView.mContext:Landroid/content/Context;
        //    52: aload_0        
        //    53: iconst_0       
        //    54: invokevirtual   android/support/v7/internal/widget/ActionBarView.setBackgroundResource:(I)V
        //    57: aload_1        
        //    58: aload_2        
        //    59: getstatic       android/support/v7/appcompat/R$styleable.ActionBar:[I
        //    62: getstatic       android/support/v7/appcompat/R$attr.actionBarStyle:I
        //    65: iconst_0       
        //    66: invokevirtual   android/content/Context.obtainStyledAttributes:(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;
        //    69: astore_3       
        //    70: aload_1        
        //    71: invokevirtual   android/content/Context.getApplicationInfo:()Landroid/content/pm/ApplicationInfo;
        //    74: astore          4
        //    76: aload_1        
        //    77: invokevirtual   android/content/Context.getPackageManager:()Landroid/content/pm/PackageManager;
        //    80: astore          5
        //    82: aload_0        
        //    83: aload_3        
        //    84: iconst_2       
        //    85: iconst_0       
        //    86: invokevirtual   android/content/res/TypedArray.getInt:(II)I
        //    89: putfield        android/support/v7/internal/widget/ActionBarView.mNavigationMode:I
        //    92: aload_0        
        //    93: aload_3        
        //    94: iconst_0       
        //    95: invokevirtual   android/content/res/TypedArray.getText:(I)Ljava/lang/CharSequence;
        //    98: putfield        android/support/v7/internal/widget/ActionBarView.mTitle:Ljava/lang/CharSequence;
        //   101: aload_0        
        //   102: aload_3        
        //   103: iconst_4       
        //   104: invokevirtual   android/content/res/TypedArray.getText:(I)Ljava/lang/CharSequence;
        //   107: putfield        android/support/v7/internal/widget/ActionBarView.mSubtitle:Ljava/lang/CharSequence;
        //   110: aload_0        
        //   111: aload_3        
        //   112: bipush          8
        //   114: invokevirtual   android/content/res/TypedArray.getDrawable:(I)Landroid/graphics/drawable/Drawable;
        //   117: putfield        android/support/v7/internal/widget/ActionBarView.mLogo:Landroid/graphics/drawable/Drawable;
        //   120: aload_0        
        //   121: getfield        android/support/v7/internal/widget/ActionBarView.mLogo:Landroid/graphics/drawable/Drawable;
        //   124: ifnonnull       176
        //   127: getstatic       android/os/Build$VERSION.SDK_INT:I
        //   130: bipush          9
        //   132: if_icmplt       176
        //   135: aload_1        
        //   136: instanceof      Landroid/app/Activity;
        //   139: ifeq            158
        //   142: aload_0        
        //   143: aload           5
        //   145: aload_1        
        //   146: checkcast       Landroid/app/Activity;
        //   149: invokevirtual   android/app/Activity.getComponentName:()Landroid/content/ComponentName;
        //   152: invokevirtual   android/content/pm/PackageManager.getActivityLogo:(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;
        //   155: putfield        android/support/v7/internal/widget/ActionBarView.mLogo:Landroid/graphics/drawable/Drawable;
        //   158: aload_0        
        //   159: getfield        android/support/v7/internal/widget/ActionBarView.mLogo:Landroid/graphics/drawable/Drawable;
        //   162: ifnonnull       176
        //   165: aload_0        
        //   166: aload           4
        //   168: aload           5
        //   170: invokevirtual   android/content/pm/ApplicationInfo.loadLogo:(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;
        //   173: putfield        android/support/v7/internal/widget/ActionBarView.mLogo:Landroid/graphics/drawable/Drawable;
        //   176: aload_0        
        //   177: aload_3        
        //   178: bipush          7
        //   180: invokevirtual   android/content/res/TypedArray.getDrawable:(I)Landroid/graphics/drawable/Drawable;
        //   183: putfield        android/support/v7/internal/widget/ActionBarView.mIcon:Landroid/graphics/drawable/Drawable;
        //   186: aload_0        
        //   187: getfield        android/support/v7/internal/widget/ActionBarView.mIcon:Landroid/graphics/drawable/Drawable;
        //   190: ifnonnull       234
        //   193: aload_1        
        //   194: instanceof      Landroid/app/Activity;
        //   197: ifeq            216
        //   200: aload_0        
        //   201: aload           5
        //   203: aload_1        
        //   204: checkcast       Landroid/app/Activity;
        //   207: invokevirtual   android/app/Activity.getComponentName:()Landroid/content/ComponentName;
        //   210: invokevirtual   android/content/pm/PackageManager.getActivityIcon:(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;
        //   213: putfield        android/support/v7/internal/widget/ActionBarView.mIcon:Landroid/graphics/drawable/Drawable;
        //   216: aload_0        
        //   217: getfield        android/support/v7/internal/widget/ActionBarView.mIcon:Landroid/graphics/drawable/Drawable;
        //   220: ifnonnull       234
        //   223: aload_0        
        //   224: aload           4
        //   226: aload           5
        //   228: invokevirtual   android/content/pm/ApplicationInfo.loadIcon:(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;
        //   231: putfield        android/support/v7/internal/widget/ActionBarView.mIcon:Landroid/graphics/drawable/Drawable;
        //   234: aload_1        
        //   235: invokestatic    android/view/LayoutInflater.from:(Landroid/content/Context;)Landroid/view/LayoutInflater;
        //   238: astore          6
        //   240: aload_3        
        //   241: bipush          14
        //   243: getstatic       android/support/v7/appcompat/R$layout.abc_action_bar_home:I
        //   246: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   249: istore          7
        //   251: aload_0        
        //   252: aload           6
        //   254: iload           7
        //   256: aload_0        
        //   257: iconst_0       
        //   258: invokevirtual   android/view/LayoutInflater.inflate:(ILandroid/view/ViewGroup;Z)Landroid/view/View;
        //   261: checkcast       Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   264: putfield        android/support/v7/internal/widget/ActionBarView.mHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   267: aload_0        
        //   268: aload           6
        //   270: iload           7
        //   272: aload_0        
        //   273: iconst_0       
        //   274: invokevirtual   android/view/LayoutInflater.inflate:(ILandroid/view/ViewGroup;Z)Landroid/view/View;
        //   277: checkcast       Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   280: putfield        android/support/v7/internal/widget/ActionBarView.mExpandedHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   283: aload_0        
        //   284: getfield        android/support/v7/internal/widget/ActionBarView.mExpandedHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   287: iconst_1       
        //   288: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setUp:(Z)V
        //   291: aload_0        
        //   292: getfield        android/support/v7/internal/widget/ActionBarView.mExpandedHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   295: aload_0        
        //   296: getfield        android/support/v7/internal/widget/ActionBarView.mExpandedActionViewUpListener:Landroid/view/View$OnClickListener;
        //   299: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setOnClickListener:(Landroid/view/View$OnClickListener;)V
        //   302: aload_0        
        //   303: getfield        android/support/v7/internal/widget/ActionBarView.mExpandedHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   306: aload_0        
        //   307: invokevirtual   android/support/v7/internal/widget/ActionBarView.getResources:()Landroid/content/res/Resources;
        //   310: getstatic       android/support/v7/appcompat/R$string.abc_action_bar_up_description:I
        //   313: invokevirtual   android/content/res/Resources.getText:(I)Ljava/lang/CharSequence;
        //   316: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setContentDescription:(Ljava/lang/CharSequence;)V
        //   319: aload_0        
        //   320: aload_3        
        //   321: iconst_5       
        //   322: iconst_0       
        //   323: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   326: putfield        android/support/v7/internal/widget/ActionBarView.mTitleStyleRes:I
        //   329: aload_0        
        //   330: aload_3        
        //   331: bipush          6
        //   333: iconst_0       
        //   334: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   337: putfield        android/support/v7/internal/widget/ActionBarView.mSubtitleStyleRes:I
        //   340: aload_0        
        //   341: aload_3        
        //   342: bipush          15
        //   344: iconst_0       
        //   345: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   348: putfield        android/support/v7/internal/widget/ActionBarView.mProgressStyle:I
        //   351: aload_0        
        //   352: aload_3        
        //   353: bipush          16
        //   355: iconst_0       
        //   356: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   359: putfield        android/support/v7/internal/widget/ActionBarView.mIndeterminateProgressStyle:I
        //   362: aload_0        
        //   363: aload_3        
        //   364: bipush          17
        //   366: iconst_0       
        //   367: invokevirtual   android/content/res/TypedArray.getDimensionPixelOffset:(II)I
        //   370: putfield        android/support/v7/internal/widget/ActionBarView.mProgressBarPadding:I
        //   373: aload_0        
        //   374: aload_3        
        //   375: bipush          18
        //   377: iconst_0       
        //   378: invokevirtual   android/content/res/TypedArray.getDimensionPixelOffset:(II)I
        //   381: putfield        android/support/v7/internal/widget/ActionBarView.mItemPadding:I
        //   384: aload_0        
        //   385: aload_3        
        //   386: iconst_3       
        //   387: iconst_0       
        //   388: invokevirtual   android/content/res/TypedArray.getInt:(II)I
        //   391: invokevirtual   android/support/v7/internal/widget/ActionBarView.setDisplayOptions:(I)V
        //   394: aload_3        
        //   395: bipush          13
        //   397: iconst_0       
        //   398: invokevirtual   android/content/res/TypedArray.getResourceId:(II)I
        //   401: istore          8
        //   403: iload           8
        //   405: ifeq            437
        //   408: aload_0        
        //   409: aload           6
        //   411: iload           8
        //   413: aload_0        
        //   414: iconst_0       
        //   415: invokevirtual   android/view/LayoutInflater.inflate:(ILandroid/view/ViewGroup;Z)Landroid/view/View;
        //   418: putfield        android/support/v7/internal/widget/ActionBarView.mCustomNavView:Landroid/view/View;
        //   421: aload_0        
        //   422: iconst_0       
        //   423: putfield        android/support/v7/internal/widget/ActionBarView.mNavigationMode:I
        //   426: aload_0        
        //   427: bipush          16
        //   429: aload_0        
        //   430: getfield        android/support/v7/internal/widget/ActionBarView.mDisplayOptions:I
        //   433: ior            
        //   434: invokevirtual   android/support/v7/internal/widget/ActionBarView.setDisplayOptions:(I)V
        //   437: aload_0        
        //   438: aload_3        
        //   439: iconst_1       
        //   440: iconst_0       
        //   441: invokevirtual   android/content/res/TypedArray.getLayoutDimension:(II)I
        //   444: putfield        android/support/v7/internal/widget/ActionBarView.mContentHeight:I
        //   447: aload_3        
        //   448: invokevirtual   android/content/res/TypedArray.recycle:()V
        //   451: aload_0        
        //   452: new             Landroid/support/v7/internal/view/menu/ActionMenuItem;
        //   455: dup            
        //   456: aload_1        
        //   457: iconst_0       
        //   458: ldc_w           16908332
        //   461: iconst_0       
        //   462: iconst_0       
        //   463: aload_0        
        //   464: getfield        android/support/v7/internal/widget/ActionBarView.mTitle:Ljava/lang/CharSequence;
        //   467: invokespecial   android/support/v7/internal/view/menu/ActionMenuItem.<init>:(Landroid/content/Context;IIIILjava/lang/CharSequence;)V
        //   470: putfield        android/support/v7/internal/widget/ActionBarView.mLogoNavItem:Landroid/support/v7/internal/view/menu/ActionMenuItem;
        //   473: aload_0        
        //   474: getfield        android/support/v7/internal/widget/ActionBarView.mHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   477: aload_0        
        //   478: getfield        android/support/v7/internal/widget/ActionBarView.mUpClickListener:Landroid/view/View$OnClickListener;
        //   481: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setOnClickListener:(Landroid/view/View$OnClickListener;)V
        //   484: aload_0        
        //   485: getfield        android/support/v7/internal/widget/ActionBarView.mHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   488: iconst_1       
        //   489: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setClickable:(Z)V
        //   492: aload_0        
        //   493: getfield        android/support/v7/internal/widget/ActionBarView.mHomeLayout:Landroid/support/v7/internal/widget/ActionBarView$HomeView;
        //   496: iconst_1       
        //   497: invokevirtual   android/support/v7/internal/widget/ActionBarView$HomeView.setFocusable:(Z)V
        //   500: return         
        //   501: astore          11
        //   503: ldc             "ActionBarView"
        //   505: ldc_w           "Activity component name not found!"
        //   508: aload           11
        //   510: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   513: pop            
        //   514: goto            158
        //   517: astore          9
        //   519: ldc             "ActionBarView"
        //   521: ldc_w           "Activity component name not found!"
        //   524: aload           9
        //   526: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   529: pop            
        //   530: goto            216
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                                     
        //  -----  -----  -----  -----  ---------------------------------------------------------
        //  142    158    501    517    Landroid/content/pm/PackageManager$NameNotFoundException;
        //  200    216    517    533    Landroid/content/pm/PackageManager$NameNotFoundException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0216:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2592)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:692)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:529)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:304)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:225)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:110)
        //     at advisor.main.Main.transformClassesToJava(Main.java:54)
        //     at advisor.main.Main.main(Main.java:21)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private void configPresenters(final MenuBuilder menuBuilder) {
        if (menuBuilder != null) {
            menuBuilder.addMenuPresenter(this.mActionMenuPresenter);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter);
        }
        else {
            this.mActionMenuPresenter.initForMenu(this.mContext, null);
            this.mExpandedMenuPresenter.initForMenu(this.mContext, null);
        }
        this.mActionMenuPresenter.updateMenuView(true);
        this.mExpandedMenuPresenter.updateMenuView(true);
    }
    
    private void initTitle() {
        boolean enabled = true;
        if (this.mTitleLayout == null) {
            this.mTitleLayout = (LinearLayout)LayoutInflater.from(this.getContext()).inflate(R.layout.abc_action_bar_title_item, (ViewGroup)this, false);
            this.mTitleView = (TextView)this.mTitleLayout.findViewById(R.id.action_bar_title);
            this.mSubtitleView = (TextView)this.mTitleLayout.findViewById(R.id.action_bar_subtitle);
            this.mTitleUpView = this.mTitleLayout.findViewById(R.id.up);
            this.mTitleLayout.setOnClickListener(this.mUpClickListener);
            if (this.mTitleStyleRes != 0) {
                this.mTitleView.setTextAppearance(this.mContext, this.mTitleStyleRes);
            }
            if (this.mTitle != null) {
                this.mTitleView.setText(this.mTitle);
            }
            if (this.mSubtitleStyleRes != 0) {
                this.mSubtitleView.setTextAppearance(this.mContext, this.mSubtitleStyleRes);
            }
            if (this.mSubtitle != null) {
                this.mSubtitleView.setText(this.mSubtitle);
                this.mSubtitleView.setVisibility(0);
            }
            final boolean b = (0x4 & this.mDisplayOptions) != 0x0 && enabled;
            final boolean b2 = (0x2 & this.mDisplayOptions) != 0x0 && enabled;
            final View mTitleUpView = this.mTitleUpView;
            int visibility;
            if (!b2) {
                if (b) {
                    visibility = 0;
                }
                else {
                    visibility = 4;
                }
            }
            else {
                visibility = 8;
            }
            mTitleUpView.setVisibility(visibility);
            final LinearLayout mTitleLayout = this.mTitleLayout;
            if (!b || b2) {
                enabled = false;
            }
            mTitleLayout.setEnabled(enabled);
        }
        this.addView((View)this.mTitleLayout);
        if (this.mExpandedActionView != null || (TextUtils.isEmpty(this.mTitle) && TextUtils.isEmpty(this.mSubtitle))) {
            this.mTitleLayout.setVisibility(8);
        }
    }
    
    private void setTitleImpl(final CharSequence title) {
        this.mTitle = title;
        if (this.mTitleView != null) {
            this.mTitleView.setText(title);
            int n;
            if (this.mExpandedActionView == null && (0x8 & this.mDisplayOptions) != 0x0 && (!TextUtils.isEmpty(this.mTitle) || !TextUtils.isEmpty(this.mSubtitle))) {
                n = 1;
            }
            else {
                n = 0;
            }
            final LinearLayout mTitleLayout = this.mTitleLayout;
            int visibility = 0;
            if (n == 0) {
                visibility = 8;
            }
            mTitleLayout.setVisibility(visibility);
        }
        if (this.mLogoNavItem != null) {
            this.mLogoNavItem.setTitle(title);
        }
    }
    
    public void collapseActionView() {
        MenuItemImpl mCurrentExpandedItem;
        if (this.mExpandedMenuPresenter == null) {
            mCurrentExpandedItem = null;
        }
        else {
            mCurrentExpandedItem = this.mExpandedMenuPresenter.mCurrentExpandedItem;
        }
        if (mCurrentExpandedItem != null) {
            mCurrentExpandedItem.collapseActionView();
        }
    }
    
    protected ViewGroup$LayoutParams generateDefaultLayoutParams() {
        return (ViewGroup$LayoutParams)new ActionBar.LayoutParams(19);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(final AttributeSet set) {
        return (ViewGroup$LayoutParams)new ActionBar.LayoutParams(this.getContext(), set);
    }
    
    public ViewGroup$LayoutParams generateLayoutParams(ViewGroup$LayoutParams generateDefaultLayoutParams) {
        if (generateDefaultLayoutParams == null) {
            generateDefaultLayoutParams = this.generateDefaultLayoutParams();
        }
        return generateDefaultLayoutParams;
    }
    
    public View getCustomNavigationView() {
        return this.mCustomNavView;
    }
    
    public int getDisplayOptions() {
        return this.mDisplayOptions;
    }
    
    public SpinnerAdapter getDropdownAdapter() {
        return this.mSpinnerAdapter;
    }
    
    public int getDropdownSelectedPosition() {
        return this.mSpinner.getSelectedItemPosition();
    }
    
    public int getNavigationMode() {
        return this.mNavigationMode;
    }
    
    public CharSequence getSubtitle() {
        return this.mSubtitle;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public boolean hasEmbeddedTabs() {
        return this.mIncludeTabs;
    }
    
    public boolean hasExpandedActionView() {
        return this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null;
    }
    
    public void initIndeterminateProgress() {
        (this.mIndeterminateProgressView = new ProgressBarICS(this.mContext, null, 0, this.mIndeterminateProgressStyle)).setId(R.id.progress_circular);
        this.mIndeterminateProgressView.setVisibility(8);
        this.addView((View)this.mIndeterminateProgressView);
    }
    
    public void initProgress() {
        (this.mProgressView = new ProgressBarICS(this.mContext, null, 0, this.mProgressStyle)).setId(R.id.progress_horizontal);
        this.mProgressView.setMax(10000);
        this.mProgressView.setVisibility(8);
        this.addView((View)this.mProgressView);
    }
    
    public boolean isCollapsed() {
        return this.mIsCollapsed;
    }
    
    public boolean isSplitActionBar() {
        return this.mSplitActionBar;
    }
    
    @Override
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mTitleView = null;
        this.mSubtitleView = null;
        this.mTitleUpView = null;
        if (this.mTitleLayout != null && this.mTitleLayout.getParent() == this) {
            this.removeView((View)this.mTitleLayout);
        }
        this.mTitleLayout = null;
        if ((0x8 & this.mDisplayOptions) != 0x0) {
            this.initTitle();
        }
        if (this.mTabScrollView != null && this.mIncludeTabs) {
            final ViewGroup$LayoutParams layoutParams = this.mTabScrollView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = -2;
                layoutParams.height = -1;
            }
            this.mTabScrollView.setAllowCollapse(true);
        }
        if (this.mProgressView != null) {
            this.removeView((View)this.mProgressView);
            this.initProgress();
        }
        if (this.mIndeterminateProgressView != null) {
            this.removeView((View)this.mIndeterminateProgressView);
            this.initIndeterminateProgress();
        }
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks(this.mTabSelector);
        if (this.mActionMenuPresenter != null) {
            this.mActionMenuPresenter.hideOverflowMenu();
            this.mActionMenuPresenter.hideSubMenus();
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.addView((View)this.mHomeLayout);
        if (this.mCustomNavView != null && (0x10 & this.mDisplayOptions) != 0x0) {
            final ViewParent parent = this.mCustomNavView.getParent();
            if (parent != this) {
                if (parent instanceof ViewGroup) {
                    ((ActionBarView)parent).removeView(this.mCustomNavView);
                }
                this.addView(this.mCustomNavView);
            }
        }
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        int paddingLeft = this.getPaddingLeft();
        final int paddingTop = this.getPaddingTop();
        final int n5 = n4 - n2 - this.getPaddingTop() - this.getPaddingBottom();
        if (n5 > 0) {
            HomeView homeView;
            if (this.mExpandedActionView != null) {
                homeView = this.mExpandedHomeLayout;
            }
            else {
                homeView = this.mHomeLayout;
            }
            if (homeView.getVisibility() != 8) {
                final int leftOffset = homeView.getLeftOffset();
                paddingLeft += leftOffset + this.positionChild((View)homeView, paddingLeft + leftOffset, paddingTop, n5);
            }
            if (this.mExpandedActionView == null) {
                boolean b2;
                if (this.mTitleLayout != null && this.mTitleLayout.getVisibility() != 8 && (0x8 & this.mDisplayOptions) != 0x0) {
                    b2 = true;
                }
                else {
                    b2 = false;
                }
                if (b2) {
                    paddingLeft += this.positionChild((View)this.mTitleLayout, paddingLeft, paddingTop, n5);
                }
                switch (this.mNavigationMode) {
                    case 1: {
                        if (this.mListNavLayout != null) {
                            if (b2) {
                                paddingLeft += this.mItemPadding;
                            }
                            paddingLeft += this.positionChild((View)this.mListNavLayout, paddingLeft, paddingTop, n5) + this.mItemPadding;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (this.mTabScrollView != null) {
                            if (b2) {
                                paddingLeft += this.mItemPadding;
                            }
                            paddingLeft += this.positionChild((View)this.mTabScrollView, paddingLeft, paddingTop, n5) + this.mItemPadding;
                            break;
                        }
                        break;
                    }
                }
            }
            int n6 = n3 - n - this.getPaddingRight();
            if (this.mMenuView != null && this.mMenuView.getParent() == this) {
                this.positionChildInverse((View)this.mMenuView, n6, paddingTop, n5);
                n6 -= this.mMenuView.getMeasuredWidth();
            }
            if (this.mIndeterminateProgressView != null && this.mIndeterminateProgressView.getVisibility() != 8) {
                this.positionChildInverse(this.mIndeterminateProgressView, n6, paddingTop, n5);
                n6 -= this.mIndeterminateProgressView.getMeasuredWidth();
            }
            View view;
            if (this.mExpandedActionView != null) {
                view = this.mExpandedActionView;
            }
            else {
                final int n7 = 0x10 & this.mDisplayOptions;
                view = null;
                if (n7 != 0) {
                    final View mCustomNavView = this.mCustomNavView;
                    view = null;
                    if (mCustomNavView != null) {
                        view = this.mCustomNavView;
                    }
                }
            }
            if (view != null) {
                final ViewGroup$LayoutParams layoutParams = view.getLayoutParams();
                ActionBar.LayoutParams layoutParams2;
                if (layoutParams instanceof ActionBar.LayoutParams) {
                    layoutParams2 = (ActionBar.LayoutParams)layoutParams;
                }
                else {
                    layoutParams2 = null;
                }
                int gravity;
                if (layoutParams2 != null) {
                    gravity = layoutParams2.gravity;
                }
                else {
                    gravity = 19;
                }
                final int measuredWidth = view.getMeasuredWidth();
                int bottomMargin = 0;
                int topMargin = 0;
                if (layoutParams2 != null) {
                    paddingLeft += layoutParams2.leftMargin;
                    n6 -= layoutParams2.rightMargin;
                    topMargin = layoutParams2.topMargin;
                    bottomMargin = layoutParams2.bottomMargin;
                }
                int n8 = gravity & 0x7;
                if (n8 == 1) {
                    final int n9 = (this.getWidth() - measuredWidth) / 2;
                    if (n9 < paddingLeft) {
                        n8 = 3;
                    }
                    else if (n9 + measuredWidth > n6) {
                        n8 = 5;
                    }
                }
                else if (gravity == -1) {
                    n8 = 3;
                }
                int n10 = 0;
                switch (n8) {
                    case 1: {
                        n10 = (this.getWidth() - measuredWidth) / 2;
                        break;
                    }
                    case 3: {
                        n10 = paddingLeft;
                        break;
                    }
                    case 5: {
                        n10 = n6 - measuredWidth;
                        break;
                    }
                }
                int n11 = gravity & 0x70;
                if (gravity == -1) {
                    n11 = 16;
                }
                int n12 = 0;
                switch (n11) {
                    case 16: {
                        n12 = (this.getHeight() - this.getPaddingBottom() - this.getPaddingTop() - view.getMeasuredHeight()) / 2;
                        break;
                    }
                    case 48: {
                        n12 = topMargin + this.getPaddingTop();
                        break;
                    }
                    case 80: {
                        n12 = this.getHeight() - this.getPaddingBottom() - view.getMeasuredHeight() - bottomMargin;
                        break;
                    }
                }
                view.layout(n10, n12, n10 + view.getMeasuredWidth(), n12 + view.getMeasuredHeight());
            }
            if (this.mProgressView != null) {
                this.mProgressView.bringToFront();
                final int n13 = this.mProgressView.getMeasuredHeight() / 2;
                this.mProgressView.layout(this.mProgressBarPadding, -n13, this.mProgressBarPadding + this.mProgressView.getMeasuredWidth(), n13);
            }
        }
    }
    
    protected void onMeasure(final int n, final int n2) {
        final int childCount = this.getChildCount();
        Label_0087: {
            if (!this.mIsCollapsable) {
                break Label_0087;
            }
            int n3 = 0;
            for (int i = 0; i < childCount; ++i) {
                final View child = this.getChildAt(i);
                if (child.getVisibility() != 8 && (child != this.mMenuView || this.mMenuView.getChildCount() != 0)) {
                    ++n3;
                }
            }
            if (n3 != 0) {
                break Label_0087;
            }
            this.setMeasuredDimension(0, 0);
            this.mIsCollapsed = true;
            return;
        }
        this.mIsCollapsed = false;
        if (View$MeasureSpec.getMode(n) != 1073741824) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used " + "with android:layout_width=\"MATCH_PARENT\" (or fill_parent)");
        }
        if (View$MeasureSpec.getMode(n2) != Integer.MIN_VALUE) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " can only be used " + "with android:layout_height=\"wrap_content\"");
        }
        final int size = View$MeasureSpec.getSize(n);
        int n4;
        if (this.mContentHeight > 0) {
            n4 = this.mContentHeight;
        }
        else {
            n4 = View$MeasureSpec.getSize(n2);
        }
        final int n5 = this.getPaddingTop() + this.getPaddingBottom();
        final int paddingLeft = this.getPaddingLeft();
        final int paddingRight = this.getPaddingRight();
        int min = n4 - n5;
        final int measureSpec = View$MeasureSpec.makeMeasureSpec(min, Integer.MIN_VALUE);
        int n6 = size - paddingLeft - paddingRight;
        int n8;
        int n7 = n8 = n6 / 2;
        HomeView homeView;
        if (this.mExpandedActionView != null) {
            homeView = this.mExpandedHomeLayout;
        }
        else {
            homeView = this.mHomeLayout;
        }
        if (homeView.getVisibility() != 8) {
            final ViewGroup$LayoutParams layoutParams = homeView.getLayoutParams();
            int n9;
            if (layoutParams.width < 0) {
                n9 = View$MeasureSpec.makeMeasureSpec(n6, Integer.MIN_VALUE);
            }
            else {
                n9 = View$MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
            }
            homeView.measure(n9, View$MeasureSpec.makeMeasureSpec(min, 1073741824));
            final int n10 = homeView.getMeasuredWidth() + homeView.getLeftOffset();
            n6 = Math.max(0, n6 - n10);
            n7 = Math.max(0, n6 - n10);
        }
        if (this.mMenuView != null && this.mMenuView.getParent() == this) {
            n6 = this.measureChildView((View)this.mMenuView, n6, measureSpec, 0);
            n8 = Math.max(0, n8 - this.mMenuView.getMeasuredWidth());
        }
        if (this.mIndeterminateProgressView != null && this.mIndeterminateProgressView.getVisibility() != 8) {
            n6 = this.measureChildView(this.mIndeterminateProgressView, n6, measureSpec, 0);
            n8 = Math.max(0, n8 - this.mIndeterminateProgressView.getMeasuredWidth());
        }
        boolean b;
        if (this.mTitleLayout != null && this.mTitleLayout.getVisibility() != 8 && (0x8 & this.mDisplayOptions) != 0x0) {
            b = true;
        }
        else {
            b = false;
        }
        if (this.mExpandedActionView == null) {
            switch (this.mNavigationMode) {
                case 1: {
                    if (this.mListNavLayout != null) {
                        int mItemPadding;
                        if (b) {
                            mItemPadding = 2 * this.mItemPadding;
                        }
                        else {
                            mItemPadding = this.mItemPadding;
                        }
                        final int max = Math.max(0, n6 - mItemPadding);
                        final int max2 = Math.max(0, n7 - mItemPadding);
                        this.mListNavLayout.measure(View$MeasureSpec.makeMeasureSpec(max, Integer.MIN_VALUE), View$MeasureSpec.makeMeasureSpec(min, 1073741824));
                        final int measuredWidth = this.mListNavLayout.getMeasuredWidth();
                        n6 = Math.max(0, max - measuredWidth);
                        n7 = Math.max(0, max2 - measuredWidth);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (this.mTabScrollView != null) {
                        int mItemPadding2;
                        if (b) {
                            mItemPadding2 = 2 * this.mItemPadding;
                        }
                        else {
                            mItemPadding2 = this.mItemPadding;
                        }
                        final int max3 = Math.max(0, n6 - mItemPadding2);
                        final int max4 = Math.max(0, n7 - mItemPadding2);
                        this.mTabScrollView.measure(View$MeasureSpec.makeMeasureSpec(max3, Integer.MIN_VALUE), View$MeasureSpec.makeMeasureSpec(min, 1073741824));
                        final int measuredWidth2 = this.mTabScrollView.getMeasuredWidth();
                        n6 = Math.max(0, max3 - measuredWidth2);
                        n7 = Math.max(0, max4 - measuredWidth2);
                        break;
                    }
                    break;
                }
            }
        }
        View view;
        if (this.mExpandedActionView != null) {
            view = this.mExpandedActionView;
        }
        else {
            final int n11 = 0x10 & this.mDisplayOptions;
            view = null;
            if (n11 != 0) {
                final View mCustomNavView = this.mCustomNavView;
                view = null;
                if (mCustomNavView != null) {
                    view = this.mCustomNavView;
                }
            }
        }
        if (view != null) {
            final ViewGroup$LayoutParams generateLayoutParams = this.generateLayoutParams(view.getLayoutParams());
            ActionBar.LayoutParams layoutParams2;
            if (generateLayoutParams instanceof ActionBar.LayoutParams) {
                layoutParams2 = (ActionBar.LayoutParams)generateLayoutParams;
            }
            else {
                layoutParams2 = null;
            }
            int n12 = 0;
            int n13 = 0;
            if (layoutParams2 != null) {
                n12 = layoutParams2.leftMargin + layoutParams2.rightMargin;
                n13 = layoutParams2.topMargin + layoutParams2.bottomMargin;
            }
            int n14;
            if (this.mContentHeight <= 0) {
                n14 = Integer.MIN_VALUE;
            }
            else if (generateLayoutParams.height != -2) {
                n14 = 1073741824;
            }
            else {
                n14 = Integer.MIN_VALUE;
            }
            if (generateLayoutParams.height >= 0) {
                min = Math.min(generateLayoutParams.height, min);
            }
            final int max5 = Math.max(0, min - n13);
            int n15;
            if (generateLayoutParams.width != -2) {
                n15 = 1073741824;
            }
            else {
                n15 = Integer.MIN_VALUE;
            }
            int min2;
            if (generateLayoutParams.width >= 0) {
                min2 = Math.min(generateLayoutParams.width, n6);
            }
            else {
                min2 = n6;
            }
            int max6 = Math.max(0, min2 - n12);
            int gravity;
            if (layoutParams2 != null) {
                gravity = layoutParams2.gravity;
            }
            else {
                gravity = 19;
            }
            if ((gravity & 0x7) == 0x1 && generateLayoutParams.width == -1) {
                max6 = 2 * Math.min(n7, n8);
            }
            view.measure(View$MeasureSpec.makeMeasureSpec(max6, n15), View$MeasureSpec.makeMeasureSpec(max5, n14));
            n6 -= n12 + view.getMeasuredWidth();
        }
        if (this.mExpandedActionView == null && b) {
            this.measureChildView((View)this.mTitleLayout, n6, View$MeasureSpec.makeMeasureSpec(this.mContentHeight, 1073741824), 0);
            Math.max(0, n7 - this.mTitleLayout.getMeasuredWidth());
        }
        if (this.mContentHeight <= 0) {
            int n16 = 0;
            for (int j = 0; j < childCount; ++j) {
                final int n17 = n5 + this.getChildAt(j).getMeasuredHeight();
                if (n17 > n16) {
                    n16 = n17;
                }
            }
            this.setMeasuredDimension(size, n16);
        }
        else {
            this.setMeasuredDimension(size, n4);
        }
        if (this.mContextView != null) {
            this.mContextView.setContentHeight(this.getMeasuredHeight());
        }
        if (this.mProgressView != null && this.mProgressView.getVisibility() != 8) {
            this.mProgressView.measure(View$MeasureSpec.makeMeasureSpec(size - 2 * this.mProgressBarPadding, 1073741824), View$MeasureSpec.makeMeasureSpec(this.getMeasuredHeight(), Integer.MIN_VALUE));
        }
    }
    
    public void onRestoreInstanceState(final Parcelable parcelable) {
        final SavedState savedState = (SavedState)parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.expandedMenuItemId != 0 && this.mExpandedMenuPresenter != null && this.mOptionsMenu != null) {
            final SupportMenuItem supportMenuItem = (SupportMenuItem)this.mOptionsMenu.findItem(savedState.expandedMenuItemId);
            if (supportMenuItem != null) {
                supportMenuItem.expandActionView();
            }
        }
        if (savedState.isOverflowOpen) {
            this.postShowOverflowMenu();
        }
    }
    
    public Parcelable onSaveInstanceState() {
        final SavedState savedState = new SavedState(super.onSaveInstanceState());
        if (this.mExpandedMenuPresenter != null && this.mExpandedMenuPresenter.mCurrentExpandedItem != null) {
            savedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        savedState.isOverflowOpen = this.isOverflowMenuShowing();
        return (Parcelable)savedState;
    }
    
    public void setCallback(final ActionBar.OnNavigationListener mCallback) {
        this.mCallback = mCallback;
    }
    
    public void setCollapsable(final boolean mIsCollapsable) {
        this.mIsCollapsable = mIsCollapsable;
    }
    
    public void setContextView(final ActionBarContextView mContextView) {
        this.mContextView = mContextView;
    }
    
    public void setCustomNavigationView(final View mCustomNavView) {
        boolean b;
        if ((0x10 & this.mDisplayOptions) != 0x0) {
            b = true;
        }
        else {
            b = false;
        }
        if (this.mCustomNavView != null && b) {
            this.removeView(this.mCustomNavView);
        }
        this.mCustomNavView = mCustomNavView;
        if (this.mCustomNavView != null && b) {
            this.addView(this.mCustomNavView);
        }
    }
    
    public void setDisplayOptions(final int mDisplayOptions) {
        int visibility = 8;
        int n = -1;
        boolean b = true;
        if (this.mDisplayOptions != n) {
            n = (mDisplayOptions ^ this.mDisplayOptions);
        }
        this.mDisplayOptions = mDisplayOptions;
        if ((n & 0x1F) != 0x0) {
            final boolean b2 = (mDisplayOptions & 0x2) != 0x0 && b;
            int visibility2;
            if (b2 && this.mExpandedActionView == null) {
                visibility2 = 0;
            }
            else {
                visibility2 = visibility;
            }
            this.mHomeLayout.setVisibility(visibility2);
            if ((n & 0x4) != 0x0) {
                final boolean up = (mDisplayOptions & 0x4) != 0x0 && b;
                this.mHomeLayout.setUp(up);
                if (up) {
                    this.setHomeButtonEnabled(b);
                }
            }
            if ((n & 0x1) != 0x0) {
                final boolean b3 = this.mLogo != null && (mDisplayOptions & 0x1) != 0x0 && b;
                final HomeView mHomeLayout = this.mHomeLayout;
                Drawable icon;
                if (b3) {
                    icon = this.mLogo;
                }
                else {
                    icon = this.mIcon;
                }
                mHomeLayout.setIcon(icon);
            }
            if ((n & 0x8) != 0x0) {
                if ((mDisplayOptions & 0x8) != 0x0) {
                    this.initTitle();
                }
                else {
                    this.removeView((View)this.mTitleLayout);
                }
            }
            if (this.mTitleLayout != null && (n & 0x6) != 0x0) {
                final boolean b4 = (0x4 & this.mDisplayOptions) != 0x0 && b;
                final View mTitleUpView = this.mTitleUpView;
                if (!b2) {
                    if (b4) {
                        visibility = 0;
                    }
                    else {
                        visibility = 4;
                    }
                }
                mTitleUpView.setVisibility(visibility);
                final LinearLayout mTitleLayout = this.mTitleLayout;
                if (b2 || !b4) {
                    b = false;
                }
                mTitleLayout.setEnabled(b);
            }
            if ((n & 0x10) != 0x0 && this.mCustomNavView != null) {
                if ((mDisplayOptions & 0x10) != 0x0) {
                    this.addView(this.mCustomNavView);
                }
                else {
                    this.removeView(this.mCustomNavView);
                }
            }
            this.requestLayout();
        }
        else {
            this.invalidate();
        }
        if (!this.mHomeLayout.isEnabled()) {
            this.mHomeLayout.setContentDescription((CharSequence)null);
            return;
        }
        if ((mDisplayOptions & 0x4) != 0x0) {
            this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.abc_action_bar_up_description));
            return;
        }
        this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.abc_action_bar_home_description));
    }
    
    public void setDropdownAdapter(final SpinnerAdapter spinnerAdapter) {
        this.mSpinnerAdapter = spinnerAdapter;
        if (this.mSpinner != null) {
            this.mSpinner.setAdapter(spinnerAdapter);
        }
    }
    
    public void setDropdownSelectedPosition(final int selection) {
        this.mSpinner.setSelection(selection);
    }
    
    public void setEmbeddedTabView(final ScrollingTabContainerView mTabScrollView) {
        if (this.mTabScrollView != null) {
            this.removeView((View)this.mTabScrollView);
        }
        this.mTabScrollView = mTabScrollView;
        this.mIncludeTabs = (mTabScrollView != null);
        if (this.mIncludeTabs && this.mNavigationMode == 2) {
            this.addView((View)this.mTabScrollView);
            final ViewGroup$LayoutParams layoutParams = this.mTabScrollView.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -1;
            mTabScrollView.setAllowCollapse(true);
        }
    }
    
    public void setHomeAsUpIndicator(final int upIndicator) {
        this.mHomeLayout.setUpIndicator(upIndicator);
    }
    
    public void setHomeAsUpIndicator(final Drawable upIndicator) {
        this.mHomeLayout.setUpIndicator(upIndicator);
    }
    
    public void setHomeButtonEnabled(final boolean b) {
        this.mHomeLayout.setEnabled(b);
        this.mHomeLayout.setFocusable(b);
        if (!b) {
            this.mHomeLayout.setContentDescription((CharSequence)null);
            return;
        }
        if ((0x4 & this.mDisplayOptions) != 0x0) {
            this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.abc_action_bar_up_description));
            return;
        }
        this.mHomeLayout.setContentDescription(this.mContext.getResources().getText(R.string.abc_action_bar_home_description));
    }
    
    public void setIcon(final int n) {
        this.setIcon(this.mContext.getResources().getDrawable(n));
    }
    
    public void setIcon(final Drawable drawable) {
        this.mIcon = drawable;
        if (drawable != null && ((0x1 & this.mDisplayOptions) == 0x0 || this.mLogo == null)) {
            this.mHomeLayout.setIcon(drawable);
        }
        if (this.mExpandedActionView != null) {
            this.mExpandedHomeLayout.setIcon(this.mIcon.getConstantState().newDrawable(this.getResources()));
        }
    }
    
    public void setLogo(final int n) {
        this.setLogo(this.mContext.getResources().getDrawable(n));
    }
    
    public void setLogo(final Drawable drawable) {
        this.mLogo = drawable;
        if (drawable != null && (0x1 & this.mDisplayOptions) != 0x0) {
            this.mHomeLayout.setIcon(drawable);
        }
    }
    
    public void setMenu(final SupportMenu supportMenu, final MenuPresenter.Callback callback) {
        if (supportMenu == this.mOptionsMenu) {
            return;
        }
        if (this.mOptionsMenu != null) {
            this.mOptionsMenu.removeMenuPresenter(this.mActionMenuPresenter);
            this.mOptionsMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
        }
        final MenuBuilder mOptionsMenu = (MenuBuilder)supportMenu;
        this.mOptionsMenu = mOptionsMenu;
        if (this.mMenuView != null) {
            final ViewGroup viewGroup = (ViewGroup)this.mMenuView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView((View)this.mMenuView);
            }
        }
        if (this.mActionMenuPresenter == null) {
            (this.mActionMenuPresenter = new ActionMenuPresenter(this.mContext)).setCallback(callback);
            this.mActionMenuPresenter.setId(R.id.action_menu_presenter);
            this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
        }
        final ViewGroup$LayoutParams layoutParams = new ViewGroup$LayoutParams(-2, -1);
        ActionMenuView mMenuView;
        if (!this.mSplitActionBar) {
            this.mActionMenuPresenter.setExpandedActionViewsExclusive(this.getResources().getBoolean(R.bool.abc_action_bar_expanded_action_views_exclusive));
            this.configPresenters(mOptionsMenu);
            mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this);
            mMenuView.initialize(mOptionsMenu);
            final ViewGroup viewGroup2 = (ViewGroup)mMenuView.getParent();
            if (viewGroup2 != null && viewGroup2 != this) {
                viewGroup2.removeView((View)mMenuView);
            }
            this.addView((View)mMenuView, layoutParams);
        }
        else {
            this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
            this.mActionMenuPresenter.setWidthLimit(this.getContext().getResources().getDisplayMetrics().widthPixels, true);
            this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
            layoutParams.width = -1;
            this.configPresenters(mOptionsMenu);
            mMenuView = (ActionMenuView)this.mActionMenuPresenter.getMenuView(this);
            if (this.mSplitView != null) {
                final ViewGroup viewGroup3 = (ViewGroup)mMenuView.getParent();
                if (viewGroup3 != null && viewGroup3 != this.mSplitView) {
                    viewGroup3.removeView((View)mMenuView);
                }
                mMenuView.setVisibility(this.getAnimatedVisibility());
                this.mSplitView.addView((View)mMenuView, layoutParams);
            }
            else {
                mMenuView.setLayoutParams(layoutParams);
            }
        }
        this.mMenuView = mMenuView;
    }
    
    public void setNavigationMode(final int mNavigationMode) {
        final int mNavigationMode2 = this.mNavigationMode;
        if (mNavigationMode != mNavigationMode2) {
            switch (mNavigationMode2) {
                case 1: {
                    if (this.mListNavLayout != null) {
                        this.removeView((View)this.mListNavLayout);
                        break;
                    }
                    break;
                }
                case 2: {
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        this.removeView((View)this.mTabScrollView);
                        break;
                    }
                    break;
                }
            }
            switch (mNavigationMode) {
                case 1: {
                    if (this.mSpinner == null) {
                        this.mSpinner = new SpinnerICS(this.mContext, null, R.attr.actionDropDownStyle);
                        this.mListNavLayout = (LinearLayout)LayoutInflater.from(this.mContext).inflate(R.layout.abc_action_bar_view_list_nav_layout, (ViewGroup)null);
                        final LinearLayout$LayoutParams linearLayout$LayoutParams = new LinearLayout$LayoutParams(-2, -1);
                        linearLayout$LayoutParams.gravity = 17;
                        this.mListNavLayout.addView((View)this.mSpinner, (ViewGroup$LayoutParams)linearLayout$LayoutParams);
                    }
                    if (this.mSpinner.getAdapter() != this.mSpinnerAdapter) {
                        this.mSpinner.setAdapter(this.mSpinnerAdapter);
                    }
                    this.mSpinner.setOnItemSelectedListener(this.mNavItemSelectedListener);
                    this.addView((View)this.mListNavLayout);
                    break;
                }
                case 2: {
                    if (this.mTabScrollView != null && this.mIncludeTabs) {
                        this.addView((View)this.mTabScrollView);
                        break;
                    }
                    break;
                }
            }
            this.mNavigationMode = mNavigationMode;
            this.requestLayout();
        }
    }
    
    @Override
    public void setSplitActionBar(final boolean splitActionBar) {
        if (this.mSplitActionBar != splitActionBar) {
            if (this.mMenuView != null) {
                final ViewGroup viewGroup = (ViewGroup)this.mMenuView.getParent();
                if (viewGroup != null) {
                    viewGroup.removeView((View)this.mMenuView);
                }
                if (splitActionBar) {
                    if (this.mSplitView != null) {
                        this.mSplitView.addView((View)this.mMenuView);
                    }
                    this.mMenuView.getLayoutParams().width = -1;
                }
                else {
                    this.addView((View)this.mMenuView);
                    this.mMenuView.getLayoutParams().width = -2;
                }
                this.mMenuView.requestLayout();
            }
            if (this.mSplitView != null) {
                final ActionBarContainer mSplitView = this.mSplitView;
                int visibility;
                if (splitActionBar) {
                    visibility = 0;
                }
                else {
                    visibility = 8;
                }
                mSplitView.setVisibility(visibility);
            }
            if (this.mActionMenuPresenter != null) {
                if (!splitActionBar) {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(this.getResources().getBoolean(R.bool.abc_action_bar_expanded_action_views_exclusive));
                }
                else {
                    this.mActionMenuPresenter.setExpandedActionViewsExclusive(false);
                    this.mActionMenuPresenter.setWidthLimit(this.getContext().getResources().getDisplayMetrics().widthPixels, true);
                    this.mActionMenuPresenter.setItemLimit(Integer.MAX_VALUE);
                }
            }
            super.setSplitActionBar(splitActionBar);
        }
    }
    
    public void setSubtitle(final CharSequence charSequence) {
        this.mSubtitle = charSequence;
        if (this.mSubtitleView != null) {
            this.mSubtitleView.setText(charSequence);
            final TextView mSubtitleView = this.mSubtitleView;
            int visibility;
            if (charSequence != null) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mSubtitleView.setVisibility(visibility);
            int n;
            if (this.mExpandedActionView == null && (0x8 & this.mDisplayOptions) != 0x0 && (!TextUtils.isEmpty(this.mTitle) || !TextUtils.isEmpty(this.mSubtitle))) {
                n = 1;
            }
            else {
                n = 0;
            }
            final LinearLayout mTitleLayout = this.mTitleLayout;
            int visibility2 = 0;
            if (n == 0) {
                visibility2 = 8;
            }
            mTitleLayout.setVisibility(visibility2);
        }
    }
    
    public void setTitle(final CharSequence titleImpl) {
        this.mUserTitle = true;
        this.setTitleImpl(titleImpl);
    }
    
    public void setWindowCallback(final Window$Callback mWindowCallback) {
        this.mWindowCallback = mWindowCallback;
    }
    
    public void setWindowTitle(final CharSequence titleImpl) {
        if (!this.mUserTitle) {
            this.setTitleImpl(titleImpl);
        }
    }
    
    public boolean shouldDelayChildPressedState() {
        return false;
    }
    
    private class ExpandedActionViewMenuPresenter implements MenuPresenter
    {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;
        
        @Override
        public boolean collapseItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl menuItemImpl) {
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView)ActionBarView.this.mExpandedActionView).onActionViewCollapsed();
            }
            ActionBarView.this.removeView(ActionBarView.this.mExpandedActionView);
            ActionBarView.this.removeView((View)ActionBarView.this.mExpandedHomeLayout);
            ActionBarView.this.mExpandedActionView = null;
            if ((0x2 & ActionBarView.this.mDisplayOptions) != 0x0) {
                ActionBarView.this.mHomeLayout.setVisibility(0);
            }
            if ((0x8 & ActionBarView.this.mDisplayOptions) != 0x0) {
                if (ActionBarView.this.mTitleLayout == null) {
                    ActionBarView.this.initTitle();
                }
                else {
                    ActionBarView.this.mTitleLayout.setVisibility(0);
                }
            }
            if (ActionBarView.this.mTabScrollView != null && ActionBarView.this.mNavigationMode == 2) {
                ActionBarView.this.mTabScrollView.setVisibility(0);
            }
            if (ActionBarView.this.mSpinner != null && ActionBarView.this.mNavigationMode == 1) {
                ActionBarView.this.mSpinner.setVisibility(0);
            }
            if (ActionBarView.this.mCustomNavView != null && (0x10 & ActionBarView.this.mDisplayOptions) != 0x0) {
                ActionBarView.this.mCustomNavView.setVisibility(0);
            }
            ActionBarView.this.mExpandedHomeLayout.setIcon(null);
            this.mCurrentExpandedItem = null;
            ActionBarView.this.requestLayout();
            menuItemImpl.setActionViewExpanded(false);
            return true;
        }
        
        @Override
        public boolean expandItemActionView(final MenuBuilder menuBuilder, final MenuItemImpl mCurrentExpandedItem) {
            ActionBarView.this.mExpandedActionView = mCurrentExpandedItem.getActionView();
            ActionBarView.this.mExpandedHomeLayout.setIcon(ActionBarView.this.mIcon.getConstantState().newDrawable(ActionBarView.this.getResources()));
            this.mCurrentExpandedItem = mCurrentExpandedItem;
            if (ActionBarView.this.mExpandedActionView.getParent() != ActionBarView.this) {
                ActionBarView.this.addView(ActionBarView.this.mExpandedActionView);
            }
            if (ActionBarView.this.mExpandedHomeLayout.getParent() != ActionBarView.this) {
                ActionBarView.this.addView((View)ActionBarView.this.mExpandedHomeLayout);
            }
            ActionBarView.this.mHomeLayout.setVisibility(8);
            if (ActionBarView.this.mTitleLayout != null) {
                ActionBarView.this.mTitleLayout.setVisibility(8);
            }
            if (ActionBarView.this.mTabScrollView != null) {
                ActionBarView.this.mTabScrollView.setVisibility(8);
            }
            if (ActionBarView.this.mSpinner != null) {
                ActionBarView.this.mSpinner.setVisibility(8);
            }
            if (ActionBarView.this.mCustomNavView != null) {
                ActionBarView.this.mCustomNavView.setVisibility(8);
            }
            ActionBarView.this.requestLayout();
            mCurrentExpandedItem.setActionViewExpanded(true);
            if (ActionBarView.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView)ActionBarView.this.mExpandedActionView).onActionViewExpanded();
            }
            return true;
        }
        
        @Override
        public boolean flagActionItems() {
            return false;
        }
        
        @Override
        public int getId() {
            return 0;
        }
        
        @Override
        public MenuView getMenuView(final ViewGroup viewGroup) {
            return null;
        }
        
        @Override
        public void initForMenu(final Context context, final MenuBuilder mMenu) {
            if (this.mMenu != null && this.mCurrentExpandedItem != null) {
                this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
            }
            this.mMenu = mMenu;
        }
        
        @Override
        public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        }
        
        @Override
        public void onRestoreInstanceState(final Parcelable parcelable) {
        }
        
        @Override
        public Parcelable onSaveInstanceState() {
            return null;
        }
        
        @Override
        public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
            return false;
        }
        
        @Override
        public void setCallback(final Callback callback) {
        }
        
        @Override
        public void updateMenuView(final boolean b) {
            if (this.mCurrentExpandedItem != null) {
                final MenuBuilder mMenu = this.mMenu;
                boolean b2 = false;
                if (mMenu != null) {
                    final int size = this.mMenu.size();
                    int n = 0;
                    while (true) {
                        b2 = false;
                        if (n >= size) {
                            break;
                        }
                        if ((SupportMenuItem)this.mMenu.getItem(n) == this.mCurrentExpandedItem) {
                            b2 = true;
                            break;
                        }
                        ++n;
                    }
                }
                if (!b2) {
                    this.collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
                }
            }
        }
    }
    
    private static class HomeView extends FrameLayout
    {
        private Drawable mDefaultUpIndicator;
        private ImageView mIconView;
        private int mUpIndicatorRes;
        private ImageView mUpView;
        private int mUpWidth;
        
        public HomeView(final Context context) {
            this(context, null);
        }
        
        public HomeView(final Context context, final AttributeSet set) {
            super(context, set);
        }
        
        public boolean dispatchPopulateAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
            final CharSequence contentDescription = this.getContentDescription();
            if (!TextUtils.isEmpty(contentDescription)) {
                accessibilityEvent.getText().add(contentDescription);
            }
            return true;
        }
        
        public int getLeftOffset() {
            if (this.mUpView.getVisibility() == 8) {
                return this.mUpWidth;
            }
            return 0;
        }
        
        protected void onConfigurationChanged(final Configuration configuration) {
            super.onConfigurationChanged(configuration);
            if (this.mUpIndicatorRes != 0) {
                this.setUpIndicator(this.mUpIndicatorRes);
            }
        }
        
        protected void onFinishInflate() {
            this.mUpView = (ImageView)this.findViewById(R.id.up);
            this.mIconView = (ImageView)this.findViewById(R.id.home);
            this.mDefaultUpIndicator = this.mUpView.getDrawable();
        }
        
        protected void onLayout(final boolean b, int n, final int n2, final int n3, final int n4) {
            final int n5 = (n4 - n2) / 2;
            final int visibility = this.mUpView.getVisibility();
            int n6 = 0;
            if (visibility != 8) {
                final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)this.mUpView.getLayoutParams();
                final int measuredHeight = this.mUpView.getMeasuredHeight();
                final int measuredWidth = this.mUpView.getMeasuredWidth();
                final int n7 = n5 - measuredHeight / 2;
                this.mUpView.layout(0, n7, measuredWidth, n7 + measuredHeight);
                n6 = measuredWidth + frameLayout$LayoutParams.leftMargin + frameLayout$LayoutParams.rightMargin;
                n += n6;
            }
            final FrameLayout$LayoutParams frameLayout$LayoutParams2 = (FrameLayout$LayoutParams)this.mIconView.getLayoutParams();
            final int measuredHeight2 = this.mIconView.getMeasuredHeight();
            final int measuredWidth2 = this.mIconView.getMeasuredWidth();
            final int n8 = n6 + Math.max(frameLayout$LayoutParams2.leftMargin, (n3 - n) / 2 - measuredWidth2 / 2);
            final int max = Math.max(frameLayout$LayoutParams2.topMargin, n5 - measuredHeight2 / 2);
            this.mIconView.layout(n8, max, n8 + measuredWidth2, max + measuredHeight2);
        }
        
        protected void onMeasure(final int n, final int n2) {
            this.measureChildWithMargins((View)this.mUpView, n, 0, n2, 0);
            final FrameLayout$LayoutParams frameLayout$LayoutParams = (FrameLayout$LayoutParams)this.mUpView.getLayoutParams();
            this.mUpWidth = frameLayout$LayoutParams.leftMargin + this.mUpView.getMeasuredWidth() + frameLayout$LayoutParams.rightMargin;
            int mUpWidth;
            if (this.mUpView.getVisibility() == 8) {
                mUpWidth = 0;
            }
            else {
                mUpWidth = this.mUpWidth;
            }
            final int n3 = frameLayout$LayoutParams.topMargin + this.mUpView.getMeasuredHeight() + frameLayout$LayoutParams.bottomMargin;
            this.measureChildWithMargins((View)this.mIconView, n, mUpWidth, n2, 0);
            final FrameLayout$LayoutParams frameLayout$LayoutParams2 = (FrameLayout$LayoutParams)this.mIconView.getLayoutParams();
            int min = mUpWidth + (frameLayout$LayoutParams2.leftMargin + this.mIconView.getMeasuredWidth() + frameLayout$LayoutParams2.rightMargin);
            int n4 = Math.max(n3, frameLayout$LayoutParams2.topMargin + this.mIconView.getMeasuredHeight() + frameLayout$LayoutParams2.bottomMargin);
            final int mode = View$MeasureSpec.getMode(n);
            final int mode2 = View$MeasureSpec.getMode(n2);
            final int size = View$MeasureSpec.getSize(n);
            final int size2 = View$MeasureSpec.getSize(n2);
            switch (mode) {
                case Integer.MIN_VALUE: {
                    min = Math.min(min, size);
                    break;
                }
                case 1073741824: {
                    min = size;
                    break;
                }
            }
            switch (mode2) {
                case Integer.MIN_VALUE: {
                    n4 = Math.min(n4, size2);
                    break;
                }
                case 1073741824: {
                    n4 = size2;
                    break;
                }
            }
            this.setMeasuredDimension(min, n4);
        }
        
        public void setIcon(final Drawable imageDrawable) {
            this.mIconView.setImageDrawable(imageDrawable);
        }
        
        public void setUp(final boolean b) {
            final ImageView mUpView = this.mUpView;
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            mUpView.setVisibility(visibility);
        }
        
        public void setUpIndicator(final int mUpIndicatorRes) {
            this.mUpIndicatorRes = mUpIndicatorRes;
            final ImageView mUpView = this.mUpView;
            Drawable drawable;
            if (mUpIndicatorRes != 0) {
                drawable = this.getResources().getDrawable(mUpIndicatorRes);
            }
            else {
                drawable = null;
            }
            mUpView.setImageDrawable(drawable);
        }
        
        public void setUpIndicator(Drawable mDefaultUpIndicator) {
            final ImageView mUpView = this.mUpView;
            if (mDefaultUpIndicator == null) {
                mDefaultUpIndicator = this.mDefaultUpIndicator;
            }
            mUpView.setImageDrawable(mDefaultUpIndicator);
            this.mUpIndicatorRes = 0;
        }
    }
    
    static class SavedState extends View$BaseSavedState
    {
        public static final Parcelable$Creator<SavedState> CREATOR;
        int expandedMenuItemId;
        boolean isOverflowOpen;
        
        static {
            CREATOR = (Parcelable$Creator)new Parcelable$Creator<SavedState>() {
                public SavedState createFromParcel(final Parcel parcel) {
                    return new SavedState(parcel);
                }
                
                public SavedState[] newArray(final int n) {
                    return new SavedState[n];
                }
            };
        }
        
        private SavedState(final Parcel parcel) {
            super(parcel);
            this.expandedMenuItemId = parcel.readInt();
            this.isOverflowOpen = (parcel.readInt() != 0);
        }
        
        SavedState(final Parcelable parcelable) {
            super(parcelable);
        }
        
        public void writeToParcel(final Parcel parcel, final int n) {
            super.writeToParcel(parcel, n);
            parcel.writeInt(this.expandedMenuItemId);
            int n2;
            if (this.isOverflowOpen) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            parcel.writeInt(n2);
        }
    }
}
