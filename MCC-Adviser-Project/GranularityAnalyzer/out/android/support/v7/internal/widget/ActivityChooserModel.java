package android.support.v7.internal.widget;

import android.database.*;
import android.text.*;
import android.content.pm.*;
import android.os.*;
import java.util.*;
import android.content.*;
import java.math.*;

public class ActivityChooserModel extends DataSetObservable
{
    private static final String ATTRIBUTE_ACTIVITY = "activity";
    private static final String ATTRIBUTE_TIME = "time";
    private static final String ATTRIBUTE_WEIGHT = "weight";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_ACTIVITY_INFLATION = 5;
    private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0f;
    public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
    public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
    private static final String HISTORY_FILE_EXTENSION = ".xml";
    private static final int INVALID_INDEX = -1;
    private static final String LOG_TAG;
    private static final String TAG_HISTORICAL_RECORD = "historical-record";
    private static final String TAG_HISTORICAL_RECORDS = "historical-records";
    private static final Map<String, ActivityChooserModel> sDataModelRegistry;
    private static final Object sRegistryLock;
    private final List<ActivityResolveInfo> mActivities;
    private OnChooseActivityListener mActivityChoserModelPolicy;
    private ActivitySorter mActivitySorter;
    private boolean mCanReadHistoricalData;
    private final Context mContext;
    private final List<HistoricalRecord> mHistoricalRecords;
    private boolean mHistoricalRecordsChanged;
    private final String mHistoryFileName;
    private int mHistoryMaxSize;
    private final Object mInstanceLock;
    private Intent mIntent;
    private boolean mReadShareHistoryCalled;
    private boolean mReloadActivities;
    
    static {
        LOG_TAG = ActivityChooserModel.class.getSimpleName();
        sRegistryLock = new Object();
        sDataModelRegistry = new HashMap<String, ActivityChooserModel>();
    }
    
    private ActivityChooserModel(final Context context, final String mHistoryFileName) {
        super();
        this.mInstanceLock = new Object();
        this.mActivities = new ArrayList<ActivityResolveInfo>();
        this.mHistoricalRecords = new ArrayList<HistoricalRecord>();
        this.mActivitySorter = (ActivitySorter)new DefaultSorter();
        this.mHistoryMaxSize = 50;
        this.mCanReadHistoricalData = true;
        this.mReadShareHistoryCalled = false;
        this.mHistoricalRecordsChanged = true;
        this.mReloadActivities = false;
        this.mContext = context.getApplicationContext();
        if (!TextUtils.isEmpty((CharSequence)mHistoryFileName) && !mHistoryFileName.endsWith(".xml")) {
            this.mHistoryFileName = mHistoryFileName + ".xml";
            return;
        }
        this.mHistoryFileName = mHistoryFileName;
    }
    
    private boolean addHisoricalRecord(final HistoricalRecord historicalRecord) {
        final boolean add = this.mHistoricalRecords.add(historicalRecord);
        if (add) {
            this.mHistoricalRecordsChanged = true;
            this.pruneExcessiveHistoricalRecordsIfNeeded();
            this.persistHistoricalDataIfNeeded();
            this.sortActivitiesIfNeeded();
            this.notifyChanged();
        }
        return add;
    }
    
    private void ensureConsistentState() {
        final boolean b = this.loadActivitiesIfNeeded() | this.readHistoricalDataIfNeeded();
        this.pruneExcessiveHistoricalRecordsIfNeeded();
        if (b) {
            this.sortActivitiesIfNeeded();
            this.notifyChanged();
        }
    }
    
    private void executePersistHistoryAsyncTaskBase() {
        new PersistHistoryAsyncTask().execute(new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName });
    }
    
    private void executePersistHistoryAsyncTaskSDK11() {
        new PersistHistoryAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName });
    }
    
    public static ActivityChooserModel get(final Context context, final String s) {
        synchronized (ActivityChooserModel.sRegistryLock) {
            ActivityChooserModel activityChooserModel = ActivityChooserModel.sDataModelRegistry.get(s);
            if (activityChooserModel == null) {
                activityChooserModel = new ActivityChooserModel(context, s);
                ActivityChooserModel.sDataModelRegistry.put(s, activityChooserModel);
            }
            return activityChooserModel;
        }
    }
    
    private boolean loadActivitiesIfNeeded() {
        final boolean mReloadActivities = this.mReloadActivities;
        boolean b = false;
        if (mReloadActivities) {
            final Intent mIntent = this.mIntent;
            b = false;
            if (mIntent != null) {
                this.mReloadActivities = false;
                this.mActivities.clear();
                final List queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
                for (int size = queryIntentActivities.size(), i = 0; i < size; ++i) {
                    this.mActivities.add(new ActivityResolveInfo(queryIntentActivities.get(i)));
                }
                b = true;
            }
        }
        return b;
    }
    
    private void persistHistoricalDataIfNeeded() {
        if (!this.mReadShareHistoryCalled) {
            throw new IllegalStateException("No preceding call to #readHistoricalData");
        }
        if (this.mHistoricalRecordsChanged) {
            this.mHistoricalRecordsChanged = false;
            if (!TextUtils.isEmpty((CharSequence)this.mHistoryFileName)) {
                if (Build$VERSION.SDK_INT >= 11) {
                    this.executePersistHistoryAsyncTaskSDK11();
                    return;
                }
                this.executePersistHistoryAsyncTaskBase();
            }
        }
    }
    
    private void pruneExcessiveHistoricalRecordsIfNeeded() {
        final int n = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
        if (n > 0) {
            this.mHistoricalRecordsChanged = true;
            for (int i = 0; i < n; ++i) {
                final HistoricalRecord historicalRecord = this.mHistoricalRecords.remove(0);
            }
        }
    }
    
    private boolean readHistoricalDataIfNeeded() {
        if (this.mCanReadHistoricalData && this.mHistoricalRecordsChanged && !TextUtils.isEmpty((CharSequence)this.mHistoryFileName)) {
            this.mCanReadHistoricalData = false;
            this.mReadShareHistoryCalled = true;
            this.readHistoricalDataImpl();
            return true;
        }
        return false;
    }
    
    private void readHistoricalDataImpl() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0        
        //     1: getfield        android/support/v7/internal/widget/ActivityChooserModel.mContext:Landroid/content/Context;
        //     4: aload_0        
        //     5: getfield        android/support/v7/internal/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //     8: invokevirtual   android/content/Context.openFileInput:(Ljava/lang/String;)Ljava/io/FileInputStream;
        //    11: astore_2       
        //    12: invokestatic    android/util/Xml.newPullParser:()Lorg/xmlpull/v1/XmlPullParser;
        //    15: astore          11
        //    17: aload           11
        //    19: aload_2        
        //    20: aconst_null    
        //    21: invokeinterface org/xmlpull/v1/XmlPullParser.setInput:(Ljava/io/InputStream;Ljava/lang/String;)V
        //    26: iconst_0       
        //    27: istore          12
        //    29: iload           12
        //    31: iconst_1       
        //    32: if_icmpeq       53
        //    35: iload           12
        //    37: iconst_2       
        //    38: if_icmpeq       53
        //    41: aload           11
        //    43: invokeinterface org/xmlpull/v1/XmlPullParser.next:()I
        //    48: istore          12
        //    50: goto            29
        //    53: ldc             "historical-records"
        //    55: aload           11
        //    57: invokeinterface org/xmlpull/v1/XmlPullParser.getName:()Ljava/lang/String;
        //    62: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //    65: ifne            125
        //    68: new             Lorg/xmlpull/v1/XmlPullParserException;
        //    71: dup            
        //    72: ldc_w           "Share records file does not start with historical-records tag."
        //    75: invokespecial   org/xmlpull/v1/XmlPullParserException.<init>:(Ljava/lang/String;)V
        //    78: athrow         
        //    79: astore          8
        //    81: getstatic       android/support/v7/internal/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
        //    84: new             Ljava/lang/StringBuilder;
        //    87: dup            
        //    88: invokespecial   java/lang/StringBuilder.<init>:()V
        //    91: ldc_w           "Error reading historical recrod file: "
        //    94: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //    97: aload_0        
        //    98: getfield        android/support/v7/internal/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //   101: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   104: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   107: aload           8
        //   109: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   112: pop            
        //   113: aload_2        
        //   114: ifnull          320
        //   117: aload_2        
        //   118: invokevirtual   java/io/FileInputStream.close:()V
        //   121: return         
        //   122: astore          10
        //   124: return         
        //   125: aload_0        
        //   126: getfield        android/support/v7/internal/widget/ActivityChooserModel.mHistoricalRecords:Ljava/util/List;
        //   129: astore          13
        //   131: aload           13
        //   133: invokeinterface java/util/List.clear:()V
        //   138: aload           11
        //   140: invokeinterface org/xmlpull/v1/XmlPullParser.next:()I
        //   145: istore          14
        //   147: iload           14
        //   149: iconst_1       
        //   150: if_icmpne       165
        //   153: aload_2        
        //   154: ifnull          320
        //   157: aload_2        
        //   158: invokevirtual   java/io/FileInputStream.close:()V
        //   161: return         
        //   162: astore          16
        //   164: return         
        //   165: iload           14
        //   167: iconst_3       
        //   168: if_icmpeq       138
        //   171: iload           14
        //   173: iconst_4       
        //   174: if_icmpeq       138
        //   177: ldc             "historical-record"
        //   179: aload           11
        //   181: invokeinterface org/xmlpull/v1/XmlPullParser.getName:()Ljava/lang/String;
        //   186: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   189: ifne            249
        //   192: new             Lorg/xmlpull/v1/XmlPullParserException;
        //   195: dup            
        //   196: ldc_w           "Share records file not well-formed."
        //   199: invokespecial   org/xmlpull/v1/XmlPullParserException.<init>:(Ljava/lang/String;)V
        //   202: athrow         
        //   203: astore          5
        //   205: getstatic       android/support/v7/internal/widget/ActivityChooserModel.LOG_TAG:Ljava/lang/String;
        //   208: new             Ljava/lang/StringBuilder;
        //   211: dup            
        //   212: invokespecial   java/lang/StringBuilder.<init>:()V
        //   215: ldc_w           "Error reading historical recrod file: "
        //   218: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   221: aload_0        
        //   222: getfield        android/support/v7/internal/widget/ActivityChooserModel.mHistoryFileName:Ljava/lang/String;
        //   225: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   228: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
        //   231: aload           5
        //   233: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   236: pop            
        //   237: aload_2        
        //   238: ifnull          320
        //   241: aload_2        
        //   242: invokevirtual   java/io/FileInputStream.close:()V
        //   245: return         
        //   246: astore          7
        //   248: return         
        //   249: aload           13
        //   251: new             Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
        //   254: dup            
        //   255: aload           11
        //   257: aconst_null    
        //   258: ldc             "activity"
        //   260: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   265: aload           11
        //   267: aconst_null    
        //   268: ldc             "time"
        //   270: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   275: invokestatic    java/lang/Long.parseLong:(Ljava/lang/String;)J
        //   278: aload           11
        //   280: aconst_null    
        //   281: ldc             "weight"
        //   283: invokeinterface org/xmlpull/v1/XmlPullParser.getAttributeValue:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
        //   288: invokestatic    java/lang/Float.parseFloat:(Ljava/lang/String;)F
        //   291: invokespecial   android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord.<init>:(Ljava/lang/String;JF)V
        //   294: invokeinterface java/util/List.add:(Ljava/lang/Object;)Z
        //   299: pop            
        //   300: goto            138
        //   303: astore_3       
        //   304: aload_2        
        //   305: ifnull          312
        //   308: aload_2        
        //   309: invokevirtual   java/io/FileInputStream.close:()V
        //   312: aload_3        
        //   313: athrow         
        //   314: astore          4
        //   316: goto            312
        //   319: astore_1       
        //   320: return         
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type                                   
        //  -----  -----  -----  -----  ---------------------------------------
        //  0      12     319    320    Ljava/io/FileNotFoundException;
        //  12     26     79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  12     26     203    249    Ljava/io/IOException;
        //  12     26     303    319    Any
        //  41     50     79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  41     50     203    249    Ljava/io/IOException;
        //  41     50     303    319    Any
        //  53     79     79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  53     79     203    249    Ljava/io/IOException;
        //  53     79     303    319    Any
        //  81     113    303    319    Any
        //  117    121    122    125    Ljava/io/IOException;
        //  125    138    79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  125    138    203    249    Ljava/io/IOException;
        //  125    138    303    319    Any
        //  138    147    79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  138    147    203    249    Ljava/io/IOException;
        //  138    147    303    319    Any
        //  157    161    162    165    Ljava/io/IOException;
        //  177    203    79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  177    203    203    249    Ljava/io/IOException;
        //  177    203    303    319    Any
        //  205    237    303    319    Any
        //  241    245    246    249    Ljava/io/IOException;
        //  249    300    79     125    Lorg/xmlpull/v1/XmlPullParserException;
        //  249    300    203    249    Ljava/io/IOException;
        //  249    300    303    319    Any
        //  308    312    314    319    Ljava/io/IOException;
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalStateException: Expression is linked from several locations: Label_0125:
        //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
        //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2592)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
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
    
    private boolean sortActivitiesIfNeeded() {
        if (this.mActivitySorter != null && this.mIntent != null && !this.mActivities.isEmpty() && !this.mHistoricalRecords.isEmpty()) {
            this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList((List<? extends HistoricalRecord>)this.mHistoricalRecords));
            return true;
        }
        return false;
    }
    
    public Intent chooseActivity(final int n) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == null) {
                return null;
            }
            this.ensureConsistentState();
            final ActivityResolveInfo activityResolveInfo = this.mActivities.get(n);
            final ComponentName component = new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name);
            final Intent intent = new Intent(this.mIntent);
            intent.setComponent(component);
            if (this.mActivityChoserModelPolicy != null && this.mActivityChoserModelPolicy.onChooseActivity(this, new Intent(intent))) {
                return null;
            }
            this.addHisoricalRecord(new HistoricalRecord(component, System.currentTimeMillis(), 1.0f));
            return intent;
        }
    }
    
    public ResolveInfo getActivity(final int n) {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mActivities.get(n).resolveInfo;
        }
    }
    
    public int getActivityCount() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mActivities.size();
        }
    }
    
    public int getActivityIndex(final ResolveInfo resolveInfo) {
        while (true) {
            while (true) {
                int n;
                synchronized (this.mInstanceLock) {
                    this.ensureConsistentState();
                    final List<ActivityResolveInfo> mActivities = this.mActivities;
                    final int size = mActivities.size();
                    n = 0;
                    if (n >= size) {
                        return -1;
                    }
                    if (mActivities.get(n).resolveInfo == resolveInfo) {
                        return n;
                    }
                }
                ++n;
                continue;
            }
        }
    }
    
    public ResolveInfo getDefaultActivity() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            if (!this.mActivities.isEmpty()) {
                return this.mActivities.get(0).resolveInfo;
            }
            return null;
        }
    }
    
    public int getHistoryMaxSize() {
        synchronized (this.mInstanceLock) {
            return this.mHistoryMaxSize;
        }
    }
    
    public int getHistorySize() {
        synchronized (this.mInstanceLock) {
            this.ensureConsistentState();
            return this.mHistoricalRecords.size();
        }
    }
    
    public Intent getIntent() {
        synchronized (this.mInstanceLock) {
            return this.mIntent;
        }
    }
    
    public void setActivitySorter(final ActivitySorter mActivitySorter) {
        synchronized (this.mInstanceLock) {
            if (this.mActivitySorter == mActivitySorter) {
                return;
            }
            this.mActivitySorter = mActivitySorter;
            if (this.sortActivitiesIfNeeded()) {
                this.notifyChanged();
            }
        }
    }
    
    public void setDefaultActivity(final int n) {
        while (true) {
            while (true) {
                synchronized (this.mInstanceLock) {
                    this.ensureConsistentState();
                    final ActivityResolveInfo activityResolveInfo = this.mActivities.get(n);
                    final ActivityResolveInfo activityResolveInfo2 = this.mActivities.get(0);
                    if (activityResolveInfo2 != null) {
                        final float n2 = 5.0f + (activityResolveInfo2.weight - activityResolveInfo.weight);
                        this.addHisoricalRecord(new HistoricalRecord(new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name), System.currentTimeMillis(), n2));
                        return;
                    }
                }
                final float n2 = 1.0f;
                continue;
            }
        }
    }
    
    public void setHistoryMaxSize(final int mHistoryMaxSize) {
        synchronized (this.mInstanceLock) {
            if (this.mHistoryMaxSize == mHistoryMaxSize) {
                return;
            }
            this.mHistoryMaxSize = mHistoryMaxSize;
            this.pruneExcessiveHistoricalRecordsIfNeeded();
            if (this.sortActivitiesIfNeeded()) {
                this.notifyChanged();
            }
        }
    }
    
    public void setIntent(final Intent mIntent) {
        synchronized (this.mInstanceLock) {
            if (this.mIntent == mIntent) {
                return;
            }
            this.mIntent = mIntent;
            this.mReloadActivities = true;
            this.ensureConsistentState();
        }
    }
    
    public void setOnChooseActivityListener(final OnChooseActivityListener mActivityChoserModelPolicy) {
        synchronized (this.mInstanceLock) {
            this.mActivityChoserModelPolicy = mActivityChoserModelPolicy;
        }
    }
    
    public interface ActivityChooserModelClient
    {
        void setActivityChooserModel(ActivityChooserModel p0);
    }
    
    public final class ActivityResolveInfo implements Comparable<ActivityResolveInfo>
    {
        public final ResolveInfo resolveInfo;
        public float weight;
        
        public ActivityResolveInfo(final ResolveInfo resolveInfo) {
            super();
            this.resolveInfo = resolveInfo;
        }
        
        @Override
        public int compareTo(final ActivityResolveInfo activityResolveInfo) {
            return Float.floatToIntBits(activityResolveInfo.weight) - Float.floatToIntBits(this.weight);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o == null) {
                    return false;
                }
                if (this.getClass() != o.getClass()) {
                    return false;
                }
                if (Float.floatToIntBits(this.weight) != Float.floatToIntBits(((ActivityResolveInfo)o).weight)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            return 31 + Float.floatToIntBits(this.weight);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("resolveInfo:").append(this.resolveInfo.toString());
            sb.append("; weight:").append(new BigDecimal(this.weight));
            sb.append("]");
            return sb.toString();
        }
    }
    
    public interface ActivitySorter
    {
        void sort(Intent p0, List<ActivityResolveInfo> p1, List<HistoricalRecord> p2);
    }
    
    private final class DefaultSorter implements ActivitySorter
    {
        private static final float WEIGHT_DECAY_COEFFICIENT = 0.95f;
        private final Map<String, ActivityResolveInfo> mPackageNameToActivityMap;
        
        private DefaultSorter() {
            super();
            this.mPackageNameToActivityMap = new HashMap<String, ActivityResolveInfo>();
        }
        
        @Override
        public void sort(final Intent intent, final List<ActivityResolveInfo> list, final List<HistoricalRecord> list2) {
            final Map<String, ActivityResolveInfo> mPackageNameToActivityMap = this.mPackageNameToActivityMap;
            mPackageNameToActivityMap.clear();
            for (int size = list.size(), i = 0; i < size; ++i) {
                final ActivityResolveInfo activityResolveInfo = list.get(i);
                activityResolveInfo.weight = 0.0f;
                mPackageNameToActivityMap.put(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo);
            }
            final int n = -1 + list2.size();
            float n2 = 1.0f;
            for (int j = n; j >= 0; --j) {
                final HistoricalRecord historicalRecord = list2.get(j);
                final ActivityResolveInfo activityResolveInfo2 = mPackageNameToActivityMap.get(historicalRecord.activity.getPackageName());
                if (activityResolveInfo2 != null) {
                    activityResolveInfo2.weight += n2 * historicalRecord.weight;
                    n2 *= 0.95f;
                }
            }
            Collections.sort((List<Comparable>)list);
        }
    }
    
    public static final class HistoricalRecord
    {
        public final ComponentName activity;
        public final long time;
        public final float weight;
        
        public HistoricalRecord(final ComponentName activity, final long time, final float weight) {
            super();
            this.activity = activity;
            this.time = time;
            this.weight = weight;
        }
        
        public HistoricalRecord(final String s, final long n, final float n2) {
            this(ComponentName.unflattenFromString(s), n, n2);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this != o) {
                if (o == null) {
                    return false;
                }
                if (this.getClass() != o.getClass()) {
                    return false;
                }
                final HistoricalRecord historicalRecord = (HistoricalRecord)o;
                if (this.activity == null) {
                    if (historicalRecord.activity != null) {
                        return false;
                    }
                }
                else if (!this.activity.equals((Object)historicalRecord.activity)) {
                    return false;
                }
                if (this.time != historicalRecord.time) {
                    return false;
                }
                if (Float.floatToIntBits(this.weight) != Float.floatToIntBits(historicalRecord.weight)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            int hashCode;
            if (this.activity == null) {
                hashCode = 0;
            }
            else {
                hashCode = this.activity.hashCode();
            }
            return 31 * (31 * (hashCode + 31) + (int)(this.time ^ this.time >>> 32)) + Float.floatToIntBits(this.weight);
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append("; activity:").append(this.activity);
            sb.append("; time:").append(this.time);
            sb.append("; weight:").append(new BigDecimal(this.weight));
            sb.append("]");
            return sb.toString();
        }
    }
    
    public interface OnChooseActivityListener
    {
        boolean onChooseActivity(ActivityChooserModel p0, Intent p1);
    }
    
    private final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void>
    {
        public Void doInBackground(final Object... p0) {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     0: aload_1        
            //     1: iconst_0       
            //     2: aaload         
            //     3: checkcast       Ljava/util/List;
            //     6: astore_2       
            //     7: aload_1        
            //     8: iconst_1       
            //     9: aaload         
            //    10: checkcast       Ljava/lang/String;
            //    13: astore_3       
            //    14: aload_0        
            //    15: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //    18: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$200:(Landroid/support/v7/internal/widget/ActivityChooserModel;)Landroid/content/Context;
            //    21: aload_3        
            //    22: iconst_0       
            //    23: invokevirtual   android/content/Context.openFileOutput:(Ljava/lang/String;I)Ljava/io/FileOutputStream;
            //    26: astore          6
            //    28: invokestatic    android/util/Xml.newSerializer:()Lorg/xmlpull/v1/XmlSerializer;
            //    31: astore          7
            //    33: aload           7
            //    35: aload           6
            //    37: aconst_null    
            //    38: invokeinterface org/xmlpull/v1/XmlSerializer.setOutput:(Ljava/io/OutputStream;Ljava/lang/String;)V
            //    43: aload           7
            //    45: ldc             "UTF-8"
            //    47: iconst_1       
            //    48: invokestatic    java/lang/Boolean.valueOf:(Z)Ljava/lang/Boolean;
            //    51: invokeinterface org/xmlpull/v1/XmlSerializer.startDocument:(Ljava/lang/String;Ljava/lang/Boolean;)V
            //    56: aload           7
            //    58: aconst_null    
            //    59: ldc             "historical-records"
            //    61: invokeinterface org/xmlpull/v1/XmlSerializer.startTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //    66: pop            
            //    67: aload_2        
            //    68: invokeinterface java/util/List.size:()I
            //    73: istore          24
            //    75: iconst_0       
            //    76: istore          25
            //    78: iload           25
            //    80: iload           24
            //    82: if_icmpge       214
            //    85: aload_2        
            //    86: iconst_0       
            //    87: invokeinterface java/util/List.remove:(I)Ljava/lang/Object;
            //    92: checkcast       Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
            //    95: astore          26
            //    97: aload           7
            //    99: aconst_null    
            //   100: ldc             "historical-record"
            //   102: invokeinterface org/xmlpull/v1/XmlSerializer.startTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   107: pop            
            //   108: aload           7
            //   110: aconst_null    
            //   111: ldc             "activity"
            //   113: aload           26
            //   115: getfield        android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord.activity:Landroid/content/ComponentName;
            //   118: invokevirtual   android/content/ComponentName.flattenToString:()Ljava/lang/String;
            //   121: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   126: pop            
            //   127: aload           7
            //   129: aconst_null    
            //   130: ldc             "time"
            //   132: aload           26
            //   134: getfield        android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord.time:J
            //   137: invokestatic    java/lang/String.valueOf:(J)Ljava/lang/String;
            //   140: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   145: pop            
            //   146: aload           7
            //   148: aconst_null    
            //   149: ldc             "weight"
            //   151: aload           26
            //   153: getfield        android/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord.weight:F
            //   156: invokestatic    java/lang/String.valueOf:(F)Ljava/lang/String;
            //   159: invokeinterface org/xmlpull/v1/XmlSerializer.attribute:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   164: pop            
            //   165: aload           7
            //   167: aconst_null    
            //   168: ldc             "historical-record"
            //   170: invokeinterface org/xmlpull/v1/XmlSerializer.endTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   175: pop            
            //   176: iinc            25, 1
            //   179: goto            78
            //   182: astore          4
            //   184: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$300:()Ljava/lang/String;
            //   187: new             Ljava/lang/StringBuilder;
            //   190: dup            
            //   191: invokespecial   java/lang/StringBuilder.<init>:()V
            //   194: ldc             "Error writing historical recrod file: "
            //   196: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   199: aload_3        
            //   200: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   203: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   206: aload           4
            //   208: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   211: pop            
            //   212: aconst_null    
            //   213: areturn        
            //   214: aload           7
            //   216: aconst_null    
            //   217: ldc             "historical-records"
            //   219: invokeinterface org/xmlpull/v1/XmlSerializer.endTag:(Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
            //   224: pop            
            //   225: aload           7
            //   227: invokeinterface org/xmlpull/v1/XmlSerializer.endDocument:()V
            //   232: aload_0        
            //   233: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   236: iconst_1       
            //   237: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$502:(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
            //   240: pop            
            //   241: aload           6
            //   243: ifnull          251
            //   246: aload           6
            //   248: invokevirtual   java/io/FileOutputStream.close:()V
            //   251: aconst_null    
            //   252: areturn        
            //   253: astore          19
            //   255: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$300:()Ljava/lang/String;
            //   258: new             Ljava/lang/StringBuilder;
            //   261: dup            
            //   262: invokespecial   java/lang/StringBuilder.<init>:()V
            //   265: ldc             "Error writing historical recrod file: "
            //   267: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   270: aload_0        
            //   271: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   274: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$400:(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
            //   277: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   280: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   283: aload           19
            //   285: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   288: pop            
            //   289: aload_0        
            //   290: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   293: iconst_1       
            //   294: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$502:(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
            //   297: pop            
            //   298: aload           6
            //   300: ifnull          251
            //   303: aload           6
            //   305: invokevirtual   java/io/FileOutputStream.close:()V
            //   308: goto            251
            //   311: astore          22
            //   313: goto            251
            //   316: astore          15
            //   318: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$300:()Ljava/lang/String;
            //   321: new             Ljava/lang/StringBuilder;
            //   324: dup            
            //   325: invokespecial   java/lang/StringBuilder.<init>:()V
            //   328: ldc             "Error writing historical recrod file: "
            //   330: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   333: aload_0        
            //   334: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   337: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$400:(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
            //   340: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   343: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   346: aload           15
            //   348: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   351: pop            
            //   352: aload_0        
            //   353: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   356: iconst_1       
            //   357: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$502:(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
            //   360: pop            
            //   361: aload           6
            //   363: ifnull          251
            //   366: aload           6
            //   368: invokevirtual   java/io/FileOutputStream.close:()V
            //   371: goto            251
            //   374: astore          18
            //   376: goto            251
            //   379: astore          11
            //   381: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$300:()Ljava/lang/String;
            //   384: new             Ljava/lang/StringBuilder;
            //   387: dup            
            //   388: invokespecial   java/lang/StringBuilder.<init>:()V
            //   391: ldc             "Error writing historical recrod file: "
            //   393: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   396: aload_0        
            //   397: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   400: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$400:(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
            //   403: invokevirtual   java/lang/StringBuilder.append:(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   406: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
            //   409: aload           11
            //   411: invokestatic    android/util/Log.e:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
            //   414: pop            
            //   415: aload_0        
            //   416: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   419: iconst_1       
            //   420: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$502:(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
            //   423: pop            
            //   424: aload           6
            //   426: ifnull          251
            //   429: aload           6
            //   431: invokevirtual   java/io/FileOutputStream.close:()V
            //   434: goto            251
            //   437: astore          14
            //   439: goto            251
            //   442: astore          8
            //   444: aload_0        
            //   445: getfield        android/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask.this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;
            //   448: iconst_1       
            //   449: invokestatic    android/support/v7/internal/widget/ActivityChooserModel.access$502:(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
            //   452: pop            
            //   453: aload           6
            //   455: ifnull          463
            //   458: aload           6
            //   460: invokevirtual   java/io/FileOutputStream.close:()V
            //   463: aload           8
            //   465: athrow         
            //   466: astore          34
            //   468: goto            251
            //   471: astore          10
            //   473: goto            463
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                
            //  -----  -----  -----  -----  ------------------------------------
            //  14     28     182    214    Ljava/io/FileNotFoundException;
            //  33     75     253    316    Ljava/lang/IllegalArgumentException;
            //  33     75     316    379    Ljava/lang/IllegalStateException;
            //  33     75     379    442    Ljava/io/IOException;
            //  33     75     442    466    Any
            //  85     176    253    316    Ljava/lang/IllegalArgumentException;
            //  85     176    316    379    Ljava/lang/IllegalStateException;
            //  85     176    379    442    Ljava/io/IOException;
            //  85     176    442    466    Any
            //  214    232    253    316    Ljava/lang/IllegalArgumentException;
            //  214    232    316    379    Ljava/lang/IllegalStateException;
            //  214    232    379    442    Ljava/io/IOException;
            //  214    232    442    466    Any
            //  246    251    466    471    Ljava/io/IOException;
            //  255    289    442    466    Any
            //  303    308    311    316    Ljava/io/IOException;
            //  318    352    442    466    Any
            //  366    371    374    379    Ljava/io/IOException;
            //  381    415    442    466    Any
            //  429    434    437    442    Ljava/io/IOException;
            //  458    463    471    476    Ljava/io/IOException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IndexOutOfBoundsException: Index: 212, Size: 212
            //     at java.util.ArrayList.rangeCheck(Unknown Source)
            //     at java.util.ArrayList.get(Unknown Source)
            //     at com.strobel.decompiler.ast.AstBuilder.convertToAst(AstBuilder.java:3305)
            //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:114)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:210)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:556)
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
    }
}
