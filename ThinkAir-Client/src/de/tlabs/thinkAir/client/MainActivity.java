package de.tlabs.thinkAir.client;

import de.tlabs.thinkAir.lib.ControlMessages;
import de.tlabs.thinkAir.lib.ExecutionController;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity implements OnItemSelectedListener {
	
	public static boolean START_AS_NEW_PHONE = true;
	private static final String TAG = "MainActivity";
	
	private Integer[] cloneIds = new Integer[ControlMessages.MAX_NUM_CLIENTS + 1];
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		for (int i = 0; i < ControlMessages.MAX_NUM_CLIENTS + 1; i++)
			cloneIds[i] = i - 1;
			
		
		Spinner cloneIdsSpinner = (Spinner) findViewById(R.id.cloneIdSpinner);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
//		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//				R.array.clone_ids_array, android.R.layout.simple_spinner_item);
		
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, cloneIds);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		cloneIdsSpinner.setAdapter(adapter);
		
		cloneIdsSpinner.setOnItemSelectedListener(this);
	}
	
	// Watch for button clicks
	public void startAsNewPhone(View v) {
		START_AS_NEW_PHONE = true;
		Intent intent = new Intent(v.getContext(), StartExecution.class);
		startActivity(intent);
	}
	
	public void startAsOldPhone(View v) {
		START_AS_NEW_PHONE = false;
		Intent intent = new Intent(v.getContext(), StartExecution.class);
		startActivity(intent);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		
		Log.i(TAG, "Selected the item: " + pos + " " + parent.getItemAtPosition(pos));
		ExecutionController.myId = (Integer) parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		Log.i(TAG, "Nothing selected, default to cloneId = -1, new phone connection");
		
	}
}