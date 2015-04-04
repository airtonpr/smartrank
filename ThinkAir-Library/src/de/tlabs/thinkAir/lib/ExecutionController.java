package de.tlabs.thinkAir.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import de.tlabs.thinkAir.lib.db.DatabaseQuery;
import de.tlabs.thinkAir.lib.profilers.DeviceProfiler;
import de.tlabs.thinkAir.lib.profilers.LogRecord;
import de.tlabs.thinkAir.lib.profilers.NetworkProfiler;
import de.tlabs.thinkAir.lib.profilers.Profiler;
import de.tlabs.thinkAir.lib.profilers.ProgramProfiler;

/**
 * The interface to the framework for the client program - controls
 * ExecutionSolver, profilers, communicates with remote server
 * 
 */
public class ExecutionController {
	
	private static final String TAG = "ExecutionController";
	
	public static final int 	REGIME_CLIENT = 1;
	public static final int 	REGIME_SERVER = 2;

	public LogRecord 			lastLogRecord;
	public LogRecord 			lastLocalLogRecord;
	public LogRecord 			lastRemoteLogRecord;

	private Long 				mPureExecutionDuration;

	private static int			mRegime;
	private Clone				clone;

	private String				mAppName;
	private Context				mContext;
	private PackageManager		mPManager;

	private int					nrClones;
	private boolean				onLine;
	private ExecutionSolver 	mSolver;
	private DeviceProfiler		mDevProfiler;
	private NetworkProfiler 	netProfiler;

	private Socket				mSocket;
	private OutputStream		mOutStream;
	private ObjectOutputStream	mObjOutStream;
	private InputStream			mInStream;
	private ObjectInputStream	mObjInStream;

	private DatabaseQuery 		query;

	public static int			myId = -1;
	
	/**
	 * Create ExecutionController which decides where to execute remoteable
	 * methods and handles execution
	 * 
	 * @param clone
	 * 			The clone where to connect
	 * @param pManager
	 *            Package manager for finding apk file of the application
	 */
	public ExecutionController(Clone clone,
			String appName, PackageManager pManager, Context context) {

		Log.d(TAG, "ExecutionController Created");
		mRegime = REGIME_CLIENT;
		
		this.clone		= clone;
		this.mAppName 	= appName;
		this.mPManager 	= pManager;
		this.mContext 	= context;

		mDevProfiler = new DeviceProfiler(context);
		mDevProfiler.trackBatteryLevel();
		netProfiler = new NetworkProfiler(context);
		netProfiler.registerNetworkStateTrackers();

		// Create the database
		query = new DatabaseQuery(context);

		// Close the database
		try {
			query.destroy();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		establishConnection();
	}

	/**
	 * To be used on server side, only local execution
	 */
	public ExecutionController() {
		mRegime = REGIME_SERVER;
		mSolver = new ExecutionSolver(ExecutionSolver.EXECUTION_LOCATION_STATIC_LOCAL);
	}

	/**
	 * Set up streams for the socket connection, perform initial communication
	 * with the server: determine RTT, enquire if it has the required apk and
	 * send it if not.
	 */
	private void establishConnection() {
		try {
			Long sTime = System.nanoTime();

			mSocket = new Socket(clone.getIp(), clone.getPortForPhone());
			// Set up input/output streams for the socket
			mOutStream		= mSocket.getOutputStream();
			mObjOutStream	= new ObjectOutputStream(mOutStream);
			mInStream		= mSocket.getInputStream();
			mObjInStream	= new ObjectInputStream(mInStream);

			synchronized (this) {
				onLine = true;
			}

			Long dur = System.nanoTime() - sTime;
			Log.d("PowerDroid-Profiler", "Socket and streams set-up time - "
					+ dur / 1000000 + "ms");

			// Find rtt to the server
			NetworkProfiler.rttPing(mInStream, mOutStream);

			mSolver = new ExecutionSolver(
					ExecutionSolver.EXECUTION_LOCATION_DYNAMIC);

			Log.d(TAG, "Getting apk data");
			String apkName = mPManager.getApplicationInfo(mAppName, 0).sourceDir;
			Log.d(TAG, "Apk name - " + apkName);

			mOutStream.write(ControlMessages.APK_REGISTER);
			mObjOutStream.writeObject(mAppName);
			int response = mInStream.read();

			if (response == ControlMessages.APK_REQUEST) {
				// Send the APK file if needed
				sendApk(apkName, mObjOutStream);
			}

		} catch (UnknownHostException e) {
			fallBackToLocalExecution("Connection setup to server failed: " + e.getMessage());
		} catch (IOException e) {
			fallBackToLocalExecution("Connection setup to server failed: " + e.getMessage());
		} catch (NameNotFoundException e) {
			fallBackToLocalExecution("Application not found: " + e.getMessage());
		} catch (Exception e) {
			fallBackToLocalExecution("Could not connect: " + e.getMessage());
		}
	}
	
	private void fallBackToLocalExecution(String message) {
		Log.d(TAG, message);
		
		mSolver = new ExecutionSolver(ExecutionSolver.EXECUTION_LOCATION_STATIC_LOCAL);
		
		synchronized (this) {
			onLine = false;
		}
	}

	/**
	 * Wrapper of the execute method with no parameters for the executable
	 * method
	 * 
	 * @param m
	 * @param o
	 * @return
	 * @throws Throwable
	 */
	public Object execute(Method m, Object o) throws Throwable {
		return execute(m, (Object[]) null, o);
	}

	/**
	 * Call ExecutionSolver to decide where to execute the operation, start
	 * profilers, execute (either locally or remotely), collect profiling data
	 * and return execution results.
	 * 
	 * @param m
	 *            method to be executed
	 * @param pValues
	 *            with parameter values
	 * @param o
	 *            on object
	 * @return result of execution, or an exception if it happened
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public Object execute(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException {
		Object result;
		String classMethodName = o.getClass().toString() + m.getName();

		ProgramProfiler progProfiler = new ProgramProfiler(classMethodName);
		DeviceProfiler devProfiler = new DeviceProfiler(mContext);

		try {

			if (mSolver.executeRemotely(mContext, classMethodName)) {

				NetworkProfiler netProfiler = new NetworkProfiler(mContext);

				Profiler profiler = new Profiler(mRegime, mContext, progProfiler, netProfiler,
						devProfiler);

				// Start tracking execution statistics for the method
				profiler.startExecutionInfoTracking();
				result = executeRemotely(m, pValues, o);
				// Collect execution statistics
				profiler
				.stopAndLogExecutionInfoTracking(mPureExecutionDuration);
				lastLogRecord = profiler.lastLogRecord;
				return result;
			} else { // Execute locally
				NetworkProfiler netProfiler1 = null;
				Profiler profiler1 = new Profiler(mRegime, mContext, progProfiler, netProfiler1,
						devProfiler);

				// Start tracking execution statistics for the method
				profiler1.startExecutionInfoTracking();
				result = executeLocally(m, pValues, o);
				// Collect execution statistics
				profiler1
				.stopAndLogExecutionInfoTracking(mPureExecutionDuration);
				lastLogRecord = profiler1.lastLogRecord;
				return result;
			}

		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG, "InvocationTargetException " + e);
			return e;
			// throw e.getTargetException();
		}
	}

	/**
	 * Execute the method locally
	 * 
	 * @param m
	 * @param pValues
	 * @param o
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Object executeLocally(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		// Make sure that the method is accessible
		Object result = null;
		Long startTime = System.nanoTime();
		m.setAccessible(true);
		result = m.invoke(o, pValues); // Access it
		mPureExecutionDuration = System.nanoTime() - startTime;
		Log.d("PowerDroid-Profiler", "LOCAL " + m.getName()
				+ ": Actual Invocation duration - " + mPureExecutionDuration
				/ 1000000 + "ms");
		return result;
	}

	/**
	 * Execute method remotely
	 * 
	 * @param m
	 * @param pValues
	 * @param o
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws SecurityException
	 */
	private Object executeRemotely(Method m, Object[] pValues, Object o)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException,
			ClassNotFoundException, NoSuchMethodException {
		Object result = null;
		try {
			Long startTime = System.nanoTime();
			mOutStream.write(ControlMessages.EXECUTE);
			result = sendAndExecute(m, pValues, o, mObjInStream, mObjOutStream);

			Long duration = System.nanoTime() - startTime;
			Log.d("PowerDroid-Profiler", "REMOTE " + m.getName()
					+ ": Actual Send-Receive duration - " + duration / 1000000
					+ "ms");
		} catch (UnknownHostException e) {
			// No such host exists, execute locally
			Log.e(TAG, "ERROR " + m.getName() + ": " + e);
			result = executeLocally(m, pValues, o);
			ConnectionRepair repair = new ConnectionRepair();
			repair.start();
		} catch (IOException e) {
			// Connection broken, execute locally
			Log.e(TAG, "ERROR " + m.getName() + ": " + e);
			result = executeLocally(m, pValues, o);
			ConnectionRepair repair = new ConnectionRepair();
			repair.start();
		}

		return result;
	}

	/**
	 * Send APK file to the remote server
	 * 
	 * @param apkName
	 *            file name of the APK file (full path)
	 * @param objOut
	 *            ObjectOutputStream to write the file to
	 * @throws IOException
	 */
	private void sendApk(String apkName, ObjectOutputStream objOut)
			throws IOException {
		File apkFile = new File(apkName);
		FileInputStream fin = new FileInputStream(apkFile);
		BufferedInputStream bis = new BufferedInputStream(fin);
		byte[] tempArray = new byte[(int) apkFile.length()];
		bis.read(tempArray, 0, tempArray.length);
		// Send file length first
		Log.d(TAG, "Sending apk length - " + tempArray.length);
		objOut.writeInt(tempArray.length);
		// Send the file
		long startTime = System.nanoTime();
		Log.d(TAG, "Sending apk");
		objOut.write(tempArray);
		objOut.flush();
		long estimatedTime = System.nanoTime() - startTime;
		// The 1000000000 comes from measuring time in nanoseconds
		Double estimatedBandwidth = ((double) tempArray.length / (double) estimatedTime) * 1000000000;
		NetworkProfiler.addNewBandwidthEstimate(estimatedBandwidth);
		Log.d(TAG, tempArray.length + " bytes sent in "
				+ estimatedTime + " ns");
		Log.d(TAG, "Estimated bandwidth - "
				+ NetworkProfiler.bandwidth + " Bps");
		
		bis.close();
	}

	/**
	 * Send the object (along with method and parameters) to the remote server
	 * for execution
	 * 
	 * @param o
	 * @param m
	 * @param pValues
	 * @param objOut
	 * @throws IOException
	 */
	private void sendObject(Object o, Method m, Object[] pValues,
			ObjectOutputStream objOut) throws IOException {
		objOut.reset();
		Log.d(TAG, "Write Object and data");
		Long startSend = System.nanoTime();
		Long startRx = NetworkProfiler.getProcessRxBytes();
		Long startTx = NetworkProfiler.getProcessTxBytes();
		
		// Send the number of clones needed to execute the method
		objOut.writeInt(nrClones);
		
		// Send object for execution
		objOut.writeObject(o);

		// Send the method to be executed
		// Log.d(TAG, "Write Method - " + m.getName());
		objOut.writeObject(m.getName());

		// Log.d(TAG, "Write method parameter types");
		objOut.writeObject(m.getParameterTypes());

		// Log.d(TAG, "Write method parameter values");
		objOut.writeObject(pValues);
		objOut.flush();

		// Estimate the perceived bandwidth
		NetworkProfiler.addNewBandwidthEstimate(NetworkProfiler
				.getProcessRxBytes()
				- startRx + NetworkProfiler.getProcessTxBytes() - startTx,
				System.nanoTime() - startSend);
	}

	/**
	 * Send the object, the method to be executed and parameter values to the
	 * remote server for execution.
	 * 
	 * @param m
	 *            method to be executed
	 * @param pValues
	 *            parameter values of the remoted method
	 * @param o
	 *            the remoted object
	 * @param objIn
	 *            ObjectInputStream which to read results from
	 * @param objOut
	 *            ObjectOutputStream which to write the data to
	 * @return result of the remoted method or an exception that occurs during
	 *         execution
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	private Object sendAndExecute(Method m, Object[] pValues, Object o,
			ObjectInputStream objIn, ObjectOutputStream objOut)
					throws IOException, ClassNotFoundException,
					IllegalArgumentException, SecurityException,
					IllegalAccessException, InvocationTargetException,
					NoSuchMethodException {

		// Send the object itself
		sendObject(o, m, pValues, objOut);

		// Read the results from the server
		Log.d(TAG, "Read Result");
		Long startSend = System.nanoTime();
		Long startRx = NetworkProfiler.getProcessRxBytes();
		Long startTx = NetworkProfiler.getProcessTxBytes();

		Log.d(TAG, "Read Result 1");
		
		Object response = objIn.readObject();

		Log.d(TAG, "Read Result 2");
		
		// Estimate the perceived bandwidth
		NetworkProfiler.addNewBandwidthEstimate(NetworkProfiler
				.getProcessRxBytes()
				- startRx + NetworkProfiler.getProcessTxBytes() - startTx,
				System.nanoTime() - startSend);

		ResultContainer container = (ResultContainer) response;
		Object result;

		Class<?>[] pTypes = { Remoteable.class };
		try {
			// Use the copyState method that must be defined for all Remoteable
			// classes to copy the state of relevant fields to the local object
			o.getClass().getMethod("copyState", pTypes).invoke(o,
					container.objState);
		} catch (NullPointerException e) {
			// Do nothing - exception happened remotely and hence there is
			// no objet state returned.
			// The exception will be returned in the function result anyway.
			Log.d(TAG,
					"Exception received from remote server - "
							+ container.functionResult);
		}

		result = container.functionResult;
		mPureExecutionDuration = container.pureExecutionDuration;

		Log.d(TAG, "Finished execution");
		
		return result;
	}

	/**
	 * Take care of a broken connection - try restarting it when something
	 * breaks down immediately or alternativelylisten to changing network
	 * conditions
	 * 
	 * @author Andrius
	 * 
	 */
	public class ConnectionRepair extends Thread {
		/**
		 * Simple reestablish the connection
		 */
		@Override
		public void run() {
			// Try simply restarting the connection
			Log.d(TAG,
					"Trying to reestablish connection to the server");
			synchronized (this) {
				if (!onLine) {
					establishConnection();
				}
			}

			// If still offline, establish intent listeners that would try to
			// restart the connection when the service comes back up
			synchronized (this) {
				if (!onLine) {
					Log
					.d(TAG,
							"Reestablishing failed - register listeners for reconnecting");
					final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
							.getSystemService(Context.CONNECTIVITY_SERVICE);

					BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
						public void onReceive(Context context, Intent intent) {
							context.unregisterReceiver(this);
							NetworkInfo netInfo = connectivityManager
									.getActiveNetworkInfo();
							if (netInfo != null) {
								Log
								.d(TAG,
										"Network back up, try reestablishing the connection");
								establishConnection();
							}
						}
					};
					IntentFilter networkStateFilter = new IntentFilter(
							ConnectivityManager.CONNECTIVITY_ACTION);
					mContext.registerReceiver(networkStateReceiver,
							networkStateFilter);
				}
			}
		}
	}

	public void setUserChoice(int userChoice) {
		mSolver.setUserChoice(userChoice);
	}

	public void onDestroy() {
		mDevProfiler.onDestroy();
		netProfiler.onDestroy();
	}
	
	public static int getRegime() {
		return mRegime;
	}
	
	public void setNrClones(int nrClones) {
		Log.i(TAG, "Changing nrClones to: " + nrClones);
		this.nrClones = nrClones;
	}
}
