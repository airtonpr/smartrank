package com.example.virusscanningonandroid;

import android.app.*;
import android.os.*;
import android.util.*;
import java.io.*;

public class MainActivity extends Activity
{
    public static boolean START_AS_NEW_PHONE = false;
    private static final String TAG = "MainActivity";
    
    static {
        MainActivity.START_AS_NEW_PHONE = true;
    }
    
    public void onCreate(final Bundle bundle) {
        final ManageFile manageFile = new ManageFile("Log-virus-local-time.txt");
        new Thread(new MyThreadCPUMonit()).start();
        new Thread(new MyThreadMemoryMonit()).start();
        super.onCreate(bundle);
        this.setContentView(2130903063);
        final VirusScanning virusScanning = new VirusScanning();
        final long currentTimeMillis = System.currentTimeMillis();
        Log.d("MainActivity", "Number of viruses found: " + virusScanning.localScanFolder());
        Log.d("MainActivity", "Time: " + (System.currentTimeMillis() - currentTimeMillis));
        while (true) {
            try {
                manageFile.WriteFile(new StringBuilder().append(System.currentTimeMillis() - currentTimeMillis).toString());
                System.out.println();
                System.out.println();
                System.exit(0);
            }
            catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }
}
