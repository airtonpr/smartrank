package android.support.v4.content;

import android.os.*;
import android.content.*;
import android.util.*;
import android.net.*;
import java.util.*;

public class LocalBroadcastManager
{
    private static final boolean DEBUG = false;
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private static final String TAG = "LocalBroadcastManager";
    private static LocalBroadcastManager mInstance;
    private static final Object mLock;
    private final HashMap<String, ArrayList<ReceiverRecord>> mActions;
    private final Context mAppContext;
    private final Handler mHandler;
    private final ArrayList<BroadcastRecord> mPendingBroadcasts;
    private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers;
    
    static {
        mLock = new Object();
    }
    
    private LocalBroadcastManager(final Context mAppContext) {
        super();
        this.mReceivers = new HashMap<BroadcastReceiver, ArrayList<IntentFilter>>();
        this.mActions = new HashMap<String, ArrayList<ReceiverRecord>>();
        this.mPendingBroadcasts = new ArrayList<BroadcastRecord>();
        this.mAppContext = mAppContext;
        this.mHandler = new Handler(mAppContext.getMainLooper()) {
            public void handleMessage(final Message message) {
                switch (message.what) {
                    default: {
                        super.handleMessage(message);
                    }
                    case 1: {
                        LocalBroadcastManager.this.executePendingBroadcasts();
                    }
                }
            }
        };
    }
    
    private void executePendingBroadcasts() {
    Label_0050_Outer:
        while (true) {
            while (true) {
                int n;
                synchronized (this.mReceivers) {
                    final int size = this.mPendingBroadcasts.size();
                    if (size <= 0) {
                        return;
                    }
                    final BroadcastRecord[] array = new BroadcastRecord[size];
                    this.mPendingBroadcasts.toArray(array);
                    this.mPendingBroadcasts.clear();
                    // monitorexit(this.mReceivers)
                    n = 0;
                    if (n >= array.length) {
                        continue Label_0050_Outer;
                    }
                    final BroadcastRecord broadcastRecord = array[n];
                    for (int i = 0; i < broadcastRecord.receivers.size(); ++i) {
                        ((ReceiverRecord)broadcastRecord.receivers.get(i)).receiver.onReceive(this.mAppContext, broadcastRecord.intent);
                    }
                }
                ++n;
                continue;
            }
        }
    }
    
    public static LocalBroadcastManager getInstance(final Context context) {
        synchronized (LocalBroadcastManager.mLock) {
            if (LocalBroadcastManager.mInstance == null) {
                LocalBroadcastManager.mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }
            return LocalBroadcastManager.mInstance;
        }
    }
    
    public void registerReceiver(final BroadcastReceiver broadcastReceiver, final IntentFilter intentFilter) {
        synchronized (this.mReceivers) {
            final ReceiverRecord receiverRecord = new ReceiverRecord(intentFilter, broadcastReceiver);
            ArrayList<IntentFilter> list = this.mReceivers.get(broadcastReceiver);
            if (list == null) {
                list = new ArrayList<IntentFilter>(1);
                this.mReceivers.put(broadcastReceiver, list);
            }
            list.add(intentFilter);
            for (int i = 0; i < intentFilter.countActions(); ++i) {
                final String action = intentFilter.getAction(i);
                ArrayList<ReceiverRecord> list2 = this.mActions.get(action);
                if (list2 == null) {
                    list2 = new ArrayList<ReceiverRecord>(1);
                    this.mActions.put(action, list2);
                }
                list2.add(receiverRecord);
            }
        }
    }
    
    public boolean sendBroadcast(final Intent intent) {
        // monitorexit(hashMap)
        while (true) {
            while (true) {
                String action;
                String resolveTypeIfNeeded;
                Uri data;
                String scheme;
                Set categories;
                int n;
                ArrayList<ReceiverRecord> list;
                ArrayList<ReceiverRecord> list2 = null;
                ReceiverRecord receiverRecord;
                int match;
                int n2 = 0;
                String s;
                int i;
                Label_0477_Outer:Block_4_Outer:
                while (true) {
                    Block_11_Outer:Label_0218_Outer:Label_0162_Outer:
                    while (true) {
                        Label_0500: {
                            while (true) {
                                Label_0494: {
                                    Label_0485: {
                                        synchronized (this.mReceivers) {
                                            action = intent.getAction();
                                            resolveTypeIfNeeded = intent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
                                            data = intent.getData();
                                            scheme = intent.getScheme();
                                            categories = intent.getCategories();
                                            if ((0x8 & intent.getFlags()) == 0x0) {
                                                break Label_0500;
                                            }
                                            n = 1;
                                            if (n != 0) {
                                                Log.v("LocalBroadcastManager", "Resolving type " + resolveTypeIfNeeded + " scheme " + scheme + " of intent " + intent);
                                            }
                                            list = this.mActions.get(intent.getAction());
                                            if (list == null) {
                                                break;
                                            }
                                            if (n != 0) {
                                                Log.v("LocalBroadcastManager", "Action list: " + list);
                                            }
                                            break Label_0485;
                                            // iftrue(Label_0303:, n == 0)
                                            // iftrue(Label_0218:, n == 0)
                                            // iftrue(Label_0494:, n == 0)
                                            // iftrue(Label_0339:, match < 0)
                                            // iftrue(Label_0317:, list2 != null)
                                            // iftrue(Label_0242:, !receiverRecord.broadcasting)
                                            // iftrue(Label_0534:, n2 >= list.size())
                                            while (true) {
                                                Block_12:Block_17_Outer:Block_13_Outer:
                                                while (true) {
                                                Block_14:
                                                    while (true) {
                                                        while (true) {
                                                        Label_0317_Outer:
                                                            while (true) {
                                                                while (true) {
                                                                    Block_16: {
                                                                        break Block_16;
                                                                        list2.add(receiverRecord);
                                                                        receiverRecord.broadcasting = true;
                                                                        break Label_0494;
                                                                    }
                                                                    Log.v("LocalBroadcastManager", "  Filter matched!  match=0x" + Integer.toHexString(match));
                                                                    break Label_0317_Outer;
                                                                    receiverRecord = list.get(n2);
                                                                    break Block_12;
                                                                    list2 = new ArrayList<ReceiverRecord>();
                                                                    continue Block_11_Outer;
                                                                }
                                                                break Block_14;
                                                                Label_0242: {
                                                                    match = receiverRecord.filter.match(action, resolveTypeIfNeeded, scheme, data, categories, "LocalBroadcastManager");
                                                                }
                                                                continue Label_0317_Outer;
                                                            }
                                                            continue Block_13_Outer;
                                                        }
                                                        continue Label_0218_Outer;
                                                    }
                                                    Log.v("LocalBroadcastManager", "  Filter's target already added");
                                                    break Label_0494;
                                                    continue Block_17_Outer;
                                                }
                                                Log.v("LocalBroadcastManager", "Matching against filter " + receiverRecord.filter);
                                                continue Label_0162_Outer;
                                            }
                                        }
                                        Label_0339: {
                                            if (n != 0) {
                                                switch (match) {
                                                    default: {
                                                        s = "unknown reason";
                                                        break;
                                                    }
                                                    case -3: {
                                                        s = "action";
                                                        break;
                                                    }
                                                    case -4: {
                                                        s = "category";
                                                        break;
                                                    }
                                                    case -2: {
                                                        s = "data";
                                                        break;
                                                    }
                                                    case -1: {
                                                        s = "type";
                                                        break;
                                                    }
                                                }
                                                Log.v("LocalBroadcastManager", "  Filter did not match: " + s);
                                            }
                                        }
                                        break Label_0494;
                                    }
                                    n2 = 0;
                                    list2 = null;
                                    continue Label_0477_Outer;
                                }
                                ++n2;
                                continue Label_0477_Outer;
                            }
                        }
                        n = 0;
                        continue Label_0477_Outer;
                    }
                    // monitorexit(hashMap)
                    while (true) {
                    Label_0439:
                        while (true) {
                            return true;
                            while (i < list2.size()) {
                                list2.get(i).broadcasting = false;
                                ++i;
                            }
                            break Label_0439;
                            this.mHandler.sendEmptyMessage(1);
                            continue Block_4_Outer;
                        }
                        this.mPendingBroadcasts.add(new BroadcastRecord(intent, list2));
                        continue;
                    }
                }
                // iftrue(Label_0477:, this.mHandler.hasMessages(1))
                return false;
                Label_0534: {
                    if (list2 != null) {
                        i = 0;
                        continue;
                    }
                }
                break;
            }
            continue;
        }
    }
    
    public void sendBroadcastSync(final Intent intent) {
        if (this.sendBroadcast(intent)) {
            this.executePendingBroadcasts();
        }
    }
    
    public void unregisterReceiver(final BroadcastReceiver broadcastReceiver) {
        ArrayList<IntentFilter> list;
        String action;
        int n = 0;
        IntentFilter intentFilter;
        int n2 = 0;
        int n3 = 0;
        ArrayList<ReceiverRecord> list2;
        Label_0053_Outer:Label_0028_Outer:
        while (true) {
        Label_0028:
            while (true) {
                Label_0053:Label_0094_Outer:
                while (true) {
                Label_0094:
                    while (true) {
                        Label_0170: {
                            Label_0164: {
                                synchronized (this.mReceivers) {
                                    list = this.mReceivers.remove(broadcastReceiver);
                                    if (list == null) {
                                        return;
                                    }
                                    break Label_0164;
                                    // iftrue(Label_0182:, n >= intentFilter.countActions())
                                    // iftrue(Label_0176:, list2 == null)
                                    // iftrue(Label_0156:, n3 >= list.size())
                                    // iftrue(Label_0176:, list2.size() > 0)
                                    while (true) {
                                        this.mActions.remove(action);
                                        break Label_0094;
                                        while (true) {
                                            while (true) {
                                                Block_5: {
                                                    break Block_5;
                                                    n2 = 0;
                                                    break Label_0094;
                                                    intentFilter = list.get(n3);
                                                    n = 0;
                                                    continue Label_0053;
                                                }
                                                action = intentFilter.getAction(n);
                                                list2 = this.mActions.get(action);
                                                continue Label_0094_Outer;
                                            }
                                            continue Label_0028_Outer;
                                        }
                                        Label_0135: {
                                            continue Label_0053_Outer;
                                        }
                                    }
                                    // iftrue(Label_0135:, n2 >= list2.size())
                                    while (true) {
                                        Block_7: {
                                            break Block_7;
                                            list2.remove(n2);
                                            --n2;
                                            break Label_0170;
                                            Label_0156: {
                                                return;
                                            }
                                        }
                                        continue;
                                    }
                                }
                                // iftrue(Label_0170:, (ReceiverRecord)list2.get(n2).receiver != broadcastReceiver)
                            }
                            n3 = 0;
                            continue Label_0028;
                        }
                        ++n2;
                        continue Label_0094;
                    }
                    ++n;
                    continue Label_0053;
                }
                Label_0182: {
                    ++n3;
                }
                continue Label_0028;
            }
        }
    }
    
    private static class BroadcastRecord
    {
        final Intent intent;
        final ArrayList<ReceiverRecord> receivers;
        
        BroadcastRecord(final Intent intent, final ArrayList<ReceiverRecord> receivers) {
            super();
            this.intent = intent;
            this.receivers = receivers;
        }
    }
    
    private static class ReceiverRecord
    {
        boolean broadcasting;
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        
        ReceiverRecord(final IntentFilter filter, final BroadcastReceiver receiver) {
            super();
            this.filter = filter;
            this.receiver = receiver;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(128);
            sb.append("Receiver{");
            sb.append(this.receiver);
            sb.append(" filter=");
            sb.append(this.filter);
            sb.append("}");
            return sb.toString();
        }
    }
}
