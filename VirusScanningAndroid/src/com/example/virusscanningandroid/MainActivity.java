package com.example.virusscanningandroid;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity{
	
	public static boolean START_AS_NEW_PHONE = true;
	private static final String TAG = "MainActivity";
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		ManageFile manageFile = new ManageFile("Log-local-virus-scan.txt");
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		VirusScanning virusScanner = new VirusScanning();

		long startTime = System.currentTimeMillis();
		int result = virusScanner.localScanFolder();
		Log.d(TAG, "Number of viruses found: " + result);
		Log.d(TAG, "Time: " + (System.currentTimeMillis() - startTime));
		try {
			manageFile.WriteFile(""+(System.currentTimeMillis() - startTime));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
		System.out.println();
		System.exit(0);
	}
	

}