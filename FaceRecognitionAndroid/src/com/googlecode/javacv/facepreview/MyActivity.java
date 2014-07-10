package com.googlecode.javacv.facepreview;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;

public class MyActivity extends Activity {

	private long startTimeProcess;
	private long endTimeProcess;
	private long totalTimeProcess;

	private ManageFile manageFile;

	// MyThread myThread = new MyThread();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_item);
		manageFile = new ManageFile("Log-recog-local.txt");
		System.out.println("Starting ");
		// new Thread(myThread).start();
		ProgressDialog d = new ProgressDialog(MyActivity.this);

		d.setMessage("Recognizing the image...Please wait!!");
		d.setCancelable(true);
		d.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		

		FaceRecognition f = new FaceRecognition();

		startTimeProcess = System.currentTimeMillis();

		f.recognizeOneFace();

		endTimeProcess = System.currentTimeMillis();
		totalTimeProcess = endTimeProcess - startTimeProcess;
		try {
			manageFile.WriteFile(String.valueOf(totalTimeProcess));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);
		return true;
	}

}
