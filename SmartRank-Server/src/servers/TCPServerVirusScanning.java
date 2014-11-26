package servers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import utils.Configuration;
import utils.RabbitMQUtils;
import utils.RandomString;
import utils.Utils;
import utils.Zipper;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class TCPServerVirusScanning  implements Runnable {
	
	private RabbitMQUtils rabbitMQUtils = new RabbitMQUtils();

	private String[] signatureDB;
	private String serverName;
	static Configuration config = null;
	private static final int SIGNATURE_SIZE = 1348;

	private static final String SMARTRANK_PATH = "smartrank/";
	private static final String VIRUS_DIR_HOME = SMARTRANK_PATH + "virusscanning/";
	private static final String VIRUS_DB_PATH = VIRUS_DIR_HOME + "virusDB";
	private static final String VIRUS_FOLDER_TO_SCAN = VIRUS_DIR_HOME + "virusFolderToScan/";
	

	public TCPServerVirusScanning(String serverName) {
		
		try {
			 config = Configuration.getConfiguration();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.serverName = serverName + "-scanning";
	}

	@Override
	public void run() {
		try {
			startRPCServer();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TCPServerVirusScanning virusScanner = new TCPServerVirusScanning(null);
		long startTime = System.currentTimeMillis();
		int result = virusScanner.localScanFolder();
		System.out.println("Number of viruses found: " + result);
		System.out.println("Time: " + (System.currentTimeMillis() - startTime));
	}
	
//	public static void main(String[] args) {
//		new Thread(new TCPServerVirusScanning(config.getServerName())).start();
//	}
	
	private void startRPCServer() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		System.out.println("SERVER - Virus Scanning Service - Asynchronous Server started...");
		
		rabbitMQUtils.connect();

		rabbitMQUtils.getChannel().queueDeclare(this.serverName, false, false, false, null);

		rabbitMQUtils.getChannel().basicQos(1);

		QueueingConsumer consumer = new QueueingConsumer(rabbitMQUtils.getChannel());
		rabbitMQUtils.getChannel().basicConsume(this.serverName, false, consumer);
		
		ArrayList<String> files = new ArrayList<String>();
		
		while(true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();

		    BasicProperties props = delivery.getProperties();
		    BasicProperties replyProps = new BasicProperties
		                                     .Builder()
		                                     .correlationId(props.getCorrelationId())
		                                     .build();

		    String zipFileName = new RandomString(5).nextString() + ".zip";
			Utils.writeFileOnDisc(delivery.getBody(), VIRUS_FOLDER_TO_SCAN, zipFileName);

			//Descompactar
			files = Zipper.unzip(VIRUS_FOLDER_TO_SCAN, zipFileName);
			//System.out.println(files);
			//executar varredura
			
			int result = localScanFolder();
			String response = "Number of viruses found: " + result;
			System.out.println(response);
			
			//retornar resposta
			
			rabbitMQUtils.getChannel().basicPublish( "", props.getReplyTo(), replyProps, response.getBytes());

			rabbitMQUtils.getChannel().basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			
			cleanDirContent();
		}
	}
	
	private static void cleanDirContent(){
		File folder = new File(VIRUS_FOLDER_TO_SCAN);  
		if (folder.isDirectory()) {  
		    File[] sun = folder.listFiles();  
		    for (File toDelete : sun) {  
		        toDelete.delete();  
		    }  
		}  
	}

	public int localScanFolder() {

		System.out.println("Scan folder");

		System.out.println("Started signature initialization on folder: "
				+ VIRUS_DB_PATH);
		initSignatureDB(VIRUS_DB_PATH);
		System.out.println("Finished signature initialization");

		int nrVirusesFound = 0;
		int cloneId = -1;

		System.out.println("Scanning folder: " + VIRUS_FOLDER_TO_SCAN);
		File folderToScan = new File(VIRUS_FOLDER_TO_SCAN);
		File[] filesToScan = folderToScan.listFiles();

		// int howManyFiles = (int) ( filesToScan.length / nrClones ); //
		// Integer division, some files may be not considered
		int howManyFiles = (int) (filesToScan.length); // Integer division,
															// some files may be
															// not considered
		int start = (cloneId + 1) * howManyFiles; // Since cloneId starts from
													// -1 (the main clone)
		int end = start + howManyFiles;

		// If this is the clone with the highest id let him take care
		// of the files not considered due to the integer division.

		System.out.println("Nr signatures: " + signatureDB.length
				+ ". Nr files to scan: " + howManyFiles);
		System.out.println("Checking files: " + start + "-" + end);

		for (int i = start; i < end; i++) {
//			 System.out.println( "Checking file: " + i);
			if (heavyCheckIfFileVirus(filesToScan[i])) {
				// if (checkIfFileVirus(filesToScan[i])) {
				// System.out.println( "Virus found");
				nrVirusesFound++;
			}
		}

		return nrVirusesFound;
	}

	/**
	 * When having more than one clone running the method there will be partial
	 * results which should be combined to get the total result. This will be
	 * done automatically by the main clone by calling this method.
	 * 
	 * @param params
	 *            Array of partial results.
	 * @return The total result.
	 */
	public int localScanFolderReduce(int[] params) {
		int nrViruses = 0;
		for (int i = 0; i < params.length; i++) {
			nrViruses += params[i];
		}
		return nrViruses;
	}
	
	private boolean heavyCheckIfFileVirus(File virus) {
	MessageDigest md;

	try {
		md = MessageDigest.getInstance("SHA-1");

		int length = (int) virus.length();

		char[] buffer = new char[length];
//		Log.i(TAG, "Checking file " + virus.getName());
//		Log.i(TAG, "Length of file " + length);
		FileReader currentFile = new FileReader(virus);
		int totalRead =	 0;
		int read = 0;
		do {
			totalRead += read;
			read = currentFile.read(buffer, totalRead, length - totalRead);
		} while ( read > 0 );
		currentFile.close();
		if (totalRead > 0) {
			for (int i = 0; i < (length - SIGNATURE_SIZE); i++) {
				char[] tempBuff = new char[SIGNATURE_SIZE];
				System.arraycopy(buffer, i, tempBuff, 0, SIGNATURE_SIZE);
				String signature = new String(tempBuff);
				signature = byteArrayToHexString(md.digest(signature.getBytes()));
				if ( isInVirusDB(signature) )
					return  true;
			}
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

//	/**
//	 * Check all the subsequences of length SIGNATURE_SIZE for the virus
//	 * signature.
//	 * 
//	 * @param virus
//	 * @return
//	 */
//	private boolean heavyCheckIfFileVirus(File virus) {
//		MessageDigest md;
//
//		try {
//			md = MessageDigest.getInstance("SHA-1");
//
//			int length = (int) virus.length();
//
//			char[] buffer = new char[length];
//			// System.out.println( "Checking file " + virus.getName());
//			// System.out.println( "Length of file " + length);
//			FileReader currentFile = new FileReader(virus);
//			int totalRead = 0;
//			totalRead = currentFile.read(buffer, totalRead, length - totalRead);
//			currentFile.close();
//			if (totalRead > 0) {
//				 for (int i = 0; i < (length - SIGNATURE_SIZE); i++) {
//				// // for (int i = 0; i < 200; i++) {
//				String signature = new String(buffer);
//				signature = byteArrayToHexString(md
//						.digest(signature.getBytes()));
//				if (isInVirusDB(signature))
//					return true;
//			}
//			}
//			currentFile.close();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
//	}


	private boolean isInVirusDB(String signature) {
		for (int i = 0; i < signatureDB.length; i++) {
			// System.out.println("vir signature " + signature);
			// System.out.println("signatureDB[i] " + signatureDB[i]);
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

			signatureDB = new String[demoViruses.length];
			char[] buffer = new char[SIGNATURE_SIZE];

			int i = 0;
			for (File virus : demoViruses) {

				FileReader signatureFile = new FileReader(virus);
				int totalRead = 0;
				int read = signatureFile.read(buffer, totalRead, buffer.length
						- totalRead);
				signatureDB[i++] = byteArrayToHexString(md.digest(new String(
						buffer).getBytes()));
				signatureFile.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception " + e.getMessage());
		}
	}


	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
