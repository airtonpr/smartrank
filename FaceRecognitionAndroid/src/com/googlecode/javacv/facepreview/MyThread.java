package com.googlecode.javacv.facepreview;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MyThread implements Runnable {

	private ManageFile manageFile;
	
	@Override
	public void run() {
		manageFile = new ManageFile("Log-recog-local.txt");
//		manageFile.clearFile();
//		manageFile.WriteFile(String.valueOf("TOTAL MEMORY HEAP; USED MEMORY HEAP; PERCENT USED HEAP"));
		//	while (true) {
		//		 manageFile.WriteFile(String.valueOf(getUsedCPU()));
		//	}
	}
	
	public float getUsedMemorySize() throws IOException {
		float freeSize = 0L;
		float totalSize = 0L;
	    float usedSize = -1L;
	    float percent = -1L;
	    try {
	        Runtime info = Runtime.getRuntime();
	        freeSize = info.freeMemory();
	        totalSize = info.totalMemory();
	        usedSize = totalSize - freeSize;
	        percent = usedSize/totalSize;
	        Thread.sleep(2000);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    manageFile.WriteFile(String.valueOf(totalSize + ";" + usedSize + ";" + percent));
	    return percent;

	}
	
	
	private float getUsedCPU() {
		try {
			
			
			RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
			String load = reader.readLine();

			String[] toks = load.split(" ");

			long idle1 = Long.parseLong(toks[5]);
			long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}

			reader.seek(0);
			load = reader.readLine();
			reader.close();

			toks = load.split(" ");

			long idle2 = Long.parseLong(toks[5]);
			long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
					+ Long.parseLong(toks[4]) + Long.parseLong(toks[6])
					+ Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

			return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return 0;
	}

}
