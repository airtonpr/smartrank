package com.example.virusscanningandroid;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.os.Environment;
import android.util.Log;

public class VirusScanning  {

	private static final String 			TAG = "VirusScanning";
	private String[]						signatureDB;
	private static final int 				SIGNATURE_SIZE = 10677;

	public static final String MNT_SDCARD 						= Environment.getExternalStorageDirectory().getAbsolutePath() + "/";// "/mnt/sdcard/";
	public static final String SMARTRANK_FOLDER 				= MNT_SDCARD + "smartrank/";
	public static final String VISUS_SCANNING_FOLDER 			= SMARTRANK_FOLDER + "virusscanning/";
	public static final String VIRUS_DB_PATH					= VISUS_SCANNING_FOLDER + "virusDB/";
	public static final String VIRUS_FOLDER_TO_SCAN				= VISUS_SCANNING_FOLDER + "virusFolderToScan/";
	

	public VirusScanning() {
		// TODO Auto-generated constructor stub
	}
	
	public static void executeShellCommand(String TAG, String cmd, boolean asRoot) {
		Process p = null;
		try {
			if (asRoot) p = Runtime.getRuntime().exec("su " + cmd);
			else        p = Runtime.getRuntime().exec(cmd);

			DataOutputStream outs = new DataOutputStream(p.getOutputStream());

			//          outs.writeBytes(cmd + "\n");
			outs.writeBytes("exit\n");
			outs.close();

			p.waitFor();
			Log.i(TAG, "Executed cmd: " + cmd);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//            destroyProcess(p);
		}
	}

	public int localScanFolder() {
		
		Log.i(TAG, "Scan folder");
		
		Log.i(TAG, "Started signature initialization on folder: " + VIRUS_DB_PATH);
		initSignatureDB(VIRUS_DB_PATH);
		Log.i(TAG, "Finished signature initialization");
		
		int nrVirusesFound = 0;

		Log.i(TAG, "Scanning folder: " + VIRUS_FOLDER_TO_SCAN);
		File 	folderToScan	= new File(VIRUS_FOLDER_TO_SCAN);
		File[] 	filesToScan		= folderToScan.listFiles();
		
		int		howManyFiles				= filesToScan.length;
		
		Log.i(TAG, "Nr signatures: " + signatureDB.length + ". Nr files to scan: " + howManyFiles);
		Log.i(TAG, "Checking files: " + howManyFiles);
		
		for (int i = 0; i < howManyFiles; i ++ ) {
//			Log.i(TAG, "Checking file: " + i);
			if (heavyCheckIfFileVirus(filesToScan[i])) {
//			if (checkIfFileVirus(filesToScan[i])) {
//				Log.i(TAG, "Virus found");
				nrVirusesFound++;
			}
		}

		return nrVirusesFound;
	}
	
	/**
	 * When having more than one clone running the method there will be partial results
	 * which should be combined to get the total result.
	 * This will be done automatically by the main clone by calling
	 * this method.
	 * @param params Array of partial results.
	 * @return The total result.
	 */
	public int localScanFolderReduce(int[] params) {
		int nrViruses = 0;
		for (int i = 0; i < params.length; i++) {
			nrViruses += params[i];
		}
		return nrViruses;
	}

	/**
	 * Compare ONLY the initial bytes with the virus signatures.
	 * @param virus
	 * @return
	 */
	private boolean checkIfFileVirus(File virus) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("SHA-1");
			char[] buffer = new char[SIGNATURE_SIZE];
//			Log.i(TAG, "Checking file " + virus.getName());
			FileReader currentFile = new FileReader(virus);
			int totalRead =	 0;
			int read = 0;
			while ( totalRead != SIGNATURE_SIZE ) {
				read = currentFile.read(buffer, totalRead, buffer.length - totalRead);
				totalRead += read;
			}
			currentFile.close();
			if (totalRead > 0) {
				String signature = byteArrayToHexString(md.digest(new String(buffer).getBytes()));
				return isInVirusDB(signature);
			}
			currentFile.close();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Check all the subsequences of length SIGNATURE_SIZE
	 * for the virus signature.
	 * @param virus
	 * @return
	 */
	private boolean heavyCheckIfFileVirus(File virus) {
		MessageDigest md;

		try {
			md = MessageDigest.getInstance("SHA-1");

			int length = (int) virus.length();

			char[] buffer = new char[length];
//			Log.i(TAG, "Checking file " + virus.getName());
//			Log.i(TAG, "Length of file " + length);
			FileReader currentFile = new FileReader(virus);
			int totalRead =	 0;
			totalRead = currentFile.read(buffer, totalRead, length - totalRead);
			currentFile.close();
			if (totalRead > 0) {
//				for (int i = 0; i < (length - SIGNATURE_SIZE); i++) {
////				for (int i = 0; i < 200; i++) {
					String signature = new String(buffer);
					signature = byteArrayToHexString(md.digest(signature.getBytes()));
					if ( isInVirusDB(signature) )
						return  true;
//				}
			}
			currentFile.close();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isInVirusDB(String signature) {
		for (int i = 0; i < signatureDB.length; i++) {
//			Log.d(TAG, "vir signature " + signature);
//			Log.d(TAG, "signatureDB[i] " + signatureDB[i]);
			if (signature.equals(signatureDB[i]))
				return true;
		}
		return false;
	}
	
	private void initSignatureDB(String pathToSignatures) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			File signatureFolder = new File(pathToSignatures);
			File[] demoViruses = signatureFolder.listFiles();

			signatureDB		= new String[demoViruses.length];
			char[] buffer 	= new char[SIGNATURE_SIZE];

			int i = 0;
			for (File virus : demoViruses) {

				FileReader signatureFile = new FileReader(virus);
				int totalRead =	 0;
				int read = 0;
				read = signatureFile.read(buffer, totalRead, buffer.length - totalRead);
				signatureDB[i++] = byteArrayToHexString(md.digest(new String(buffer).getBytes()));
				signatureFile.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "NoSuchAlgorithmException " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Exception " + e.getMessage());
		}
	}


	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i=0; i < b.length; i++) {
			result +=
					Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		}
		return result;
	}

}
