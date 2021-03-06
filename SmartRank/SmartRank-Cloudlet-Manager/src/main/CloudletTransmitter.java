package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipEntry;

import recognition.TransmissionClient;
import utils.CloudletUtils;
import utils.Configuration;
import utils.FaceDetectionUtils;
import utils.VirtualMachineServer;
import utils.RabbitMQUtils;
import utils.Zipper;

import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Main class of the cloudlet, here the cloudlet server is started. 
 * The cloudlet works as a load balancer forwarding requests 
 * to servers capable to perform detection/recognition.
 * @author airton
 *
 */
public class CloudletTransmitter {
	public static final String RECOG_TYPE = "recognition";
	public static final String SCAN_TYPE = "scanning";
	public Socket connectionSocket;
	public ServerSocket welcomeSocket;
	public static final String SMARTRANK_PATH = "smartrank/";
	public static String PATTERN_DATA_PATH = SMARTRANK_PATH+"pattern_data/";
	public static String DETECTED_IMAGES_DIR = SMARTRANK_PATH+"detected_images/";
	public static String RECEIVED_IMAGES_DIR = SMARTRANK_PATH+"received_images/";
	public static String TRAINING_DATABASE_IMAGE_LIST = PATTERN_DATA_PATH + "facedata.xml";
	
	
	public static final String VIRUS_SCANNING_PATH = SMARTRANK_PATH + "virusscanning/";
	private static final String VIRUS_DB_PATH = VIRUS_SCANNING_PATH + "virusDB/";
	private static final String VIRUS_FOLDER_TO_SCAN = VIRUS_SCANNING_PATH + "virusFolderToScan/";
	
	
	protected  List<String> detectedImageNames = new ArrayList<String>();
	protected  List<byte[]> detectedImagesBytes = new ArrayList<byte[]>();
	protected List<VirtualMachineServer> firstServerGroupList  = new ArrayList<VirtualMachineServer>();
	protected List<VirtualMachineServer> secondServerGroupList  = new ArrayList<VirtualMachineServer>();
	SortedMap<Long, String> ipServersAux;
	String resultFromServerRecog;
	public String bestIpServer = null;
	private RabbitMQUtils rabbitMQUtils = new RabbitMQUtils();
	
	
	/*Servers ports*/
	public static int PORT_CLOUDLET_SERVER = 6790;
	public static int PORT_SERVER_RECOG = 6791;
	public static int PORT_SERVER_DETECT = 6792;
	public static int PING_PORT_SERVER = 6793;
	
	public static int IMG_WIDTH = 260;
	public static int IMG_HEIGHT = 360;
	
	CloudletUtils tcpClientCloudlet;
	private  int counter; //represent how many faces where already returned over the recognition phase
	
	protected String originalImageName;
	private static final int SCALE = 2;
	public List<CvRect> cvRectList;
	static Configuration config;
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		CloudletTransmitter cloudetTransmitter = new CloudletTransmitter();
		
		try {
			cloudetTransmitter.startCloudlet();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
			
	public CloudletTransmitter() {
		System.out.println("Starting Cloudlet...");
		try {
			welcomeSocket = new ServerSocket(PORT_CLOUDLET_SERVER);
			this.ipServersAux = new TreeMap<Long, String>();
			tcpClientCloudlet = new CloudletUtils();
			config = Configuration.getConfiguration();
			
			for (String name : config.getFirstServersGroupNames()) {
				firstServerGroupList.add(new VirtualMachineServer(name));
			}
			for (String name : config.getSecondServersGroupNames()) {
				secondServerGroupList.add(new VirtualMachineServer(name));
			}
			// startCloudlet();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public void startCloudlet() throws IOException, FileNotFoundException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		byte[] bytesFromClient;
		long startTime,endTime;
		
		clearFile();
		int i = 1;
		while(true){
			
			// RECEIVE FROM CLIENT
//				connectionSocket = welcomeSocket.accept();
//				System.out.println("CLOUDLET - Connection accepted... CLIENT-CLOUDLET");
//					bytesFromClient = readBytesFromClient();
//					System.out.println("CLOUDLET - Bytes received: " + bytesFromClient);
//			
//					System.err.println("Count: "+ (i++));
			
			//	String rootPath = new File("").getAbsolutePath().replace('\\', '/');
			//	bytesFromClient = CloudletUtils.fileToBytes(rootPath+"/picture15.png");
			
			//	detectAndDistributeForRecognition(bytesFromClient);
			
			//Este método está pegando os arquivos localmente e não recebendo do cliente mobile, os arquivos a serem scaneados serão zipados e enviados ao server para processamento
			detectAndDistributeForScanning(null, true);
		}
		
	}


	private void detectAndDistributeForRecognition(byte[] bytesFromClient)
			throws IOException, InterruptedException {
		long startTime,endTime;
		//DETECT FACES
		String originalImage = CloudletUtils.writeFileOnDisc(RECEIVED_IMAGES_DIR, bytesFromClient, null, ".jpg");
		
		startTime = System.currentTimeMillis();
		List<String> imagesNamesList = FaceDetectionUtils.dedectFacesCropAndSave(RECEIVED_IMAGES_DIR + originalImage);
		endTime = System.currentTimeMillis();
	
		//RECOGNITION
		/*To each face it calculates the best server and sends it for recognition*/
		
		distributeAndReturnResult(imagesNamesList, RECOG_TYPE);
	}
	
	private void detectAndDistributeForScanning(byte[] bytesFromClient, boolean pickUpLocalZipInsteadAndroidZip)
			throws IOException, InterruptedException {
		long startTime,endTime;
		
		startTime = System.currentTimeMillis();
		
		if (!pickUpLocalZipInsteadAndroidZip) {
			//UNZIP FILE
			String zipFile = CloudletUtils.writeFileOnDisc(VIRUS_FOLDER_TO_SCAN, bytesFromClient, "virusFolderToScan", ".zip");
			System.out.println("Unzipping...file " + zipFile);
			Zipper.unzip(VIRUS_FOLDER_TO_SCAN, zipFile);
			System.out.println("Deleting...file " + zipFile);
			deleteFile(VIRUS_FOLDER_TO_SCAN + zipFile);
		}
		
		
		
	//	deleteFile(VIRUS_FOLDER_TO_SCAN + zipFile);
		
		//LISTAR NOMES DOS ARQUIVOS
		
		File file = new File(VIRUS_FOLDER_TO_SCAN);
		List<String> filesToScan = Arrays.asList(file.list());
		System.out.println("Files unzipped: " + filesToScan);
		endTime = System.currentTimeMillis();
	
		//RECOGNITION
		/*To each face it calculates the best server and sends it for recognition*/
		
		distributeAndReturnResult(filesToScan, SCAN_TYPE);
		//writeFile("ZIP/UNZIP TIME "+ (endTime - startTime));
	}
	
	
	private void distributeAndReturnResult(List<String> itensListToSend,  String type) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		String result = "";
		
		CountDownLatch signalToAllProcess = new CountDownLatch(1);
		CountDownLatch signalToServersNumber = null;
		
		boolean useReplicatedStrategy = config.isReplicatedStrategy(); // In case of using replicated strategy, the second group of servers will receive the
	
		List<TransmissionClient> itensToProcessList = null;
		
		if (useReplicatedStrategy) {
			System.out.println("Replicated Strategy: Enabled");
			signalToServersNumber = new CountDownLatch(
					firstServerGroupList.size() + firstServerGroupList.size());

			itensToProcessList = distributeByGroupServers(
					itensListToSend, firstServerGroupList, type,
					signalToAllProcess, signalToServersNumber);

			itensToProcessList.addAll(distributeByGroupServers(itensListToSend,
					secondServerGroupList, type, signalToAllProcess,
					signalToServersNumber));
		} else {
			System.out.println("Replicated Strategy: Disabled");
			signalToServersNumber = new CountDownLatch(
					firstServerGroupList.size());

			itensToProcessList = distributeByGroupServers(
					itensListToSend, firstServerGroupList, type,
					signalToAllProcess, signalToServersNumber);

		}
		
		signalToAllProcess.countDown();
		
		System.out.println("Waiting for threads to finish");
		try {
			signalToServersNumber.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(TransmissionClient client: itensToProcessList){
			result += client.getResult(); 
		}

		// RETURN TO CLIENT
		System.out.println("CLOUDLET - Returning result to mobile device...");
//		result = observer.getResult();
		returnResultToClient(result);
		
	}


	private List<TransmissionClient> distributeByGroupServers(
			List<String> itensListToSend,
			List<VirtualMachineServer> serversList, String type,
			CountDownLatch signalToAllProcess,
			CountDownLatch signalToServersNumber) throws IOException,
			InterruptedException {
		
		if (config.isSmartStrategy()) {
			System.out.println("Smart Strategy: Enabled");
			this.rankServers();
			fillServersItensNumberByTotalCost(itensListToSend.size(), serversList);
		}else{
			System.out.println("Smart Strategy: Disabled");
			fillServersItensNumberEqualy(itensListToSend.size(), serversList);
		}               
	
		List<TransmissionClient> itensToProcessList = new ArrayList<TransmissionClient>();
		int index = 0;
		int threadIndex = 0;
		
		ArrayList<String> nameItensPerServer = null;
		String zipFileName = null;
		for (VirtualMachineServer server : serversList) {
			nameItensPerServer = new ArrayList<String>();
			for (int i = index; i < (index + server.getTotalItensToProcess()); i++) {
				//criando um array de itens para cada server
				nameItensPerServer.add(itensListToSend.get(i));
			}
			
			if (type == SCAN_TYPE) {
				zipFileName = Zipper.zipFiles(nameItensPerServer, VIRUS_FOLDER_TO_SCAN, VIRUS_FOLDER_TO_SCAN);
				System.out.println("ZIPANDO "+zipFileName + " PARA MANDAR PARA O SERVER");
			}else if (type == RECOG_TYPE){
				zipFileName = Zipper.zipFiles(nameItensPerServer, RECEIVED_IMAGES_DIR,RECEIVED_IMAGES_DIR);
			}
			
			index = index + server.getTotalItensToProcess();
			itensToProcessList.add(new TransmissionClient(zipFileName, server.getName(), this, signalToAllProcess, signalToServersNumber, type));
			new Thread(itensToProcessList.get(threadIndex)).start();
			System.out.println("## The server "+ server.getName() + " will receive " + server.getTotalItensToProcess() + " itens."); 
			threadIndex++;
		}
		return itensToProcessList;
	}
		

	public boolean writeFile(String text){
        try {
            File file = new File("Log.txt");
            FileOutputStream out = new FileOutputStream(file, true);
            out.write("\n".getBytes());
            out.write(text.getBytes());
            out.flush();
            out.close();    
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
	public static void deleteFile(String fileName){
		try{
			 
    		File file = new File(fileName);
 
    		if(file.delete()){
    			System.out.println(file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
 
    	}catch(Exception e){
 
    		e.printStackTrace();
 
    	}
	}
	
	
    public boolean clearFile(){
    	File file = new File("Log.txt");
    	try {
			RandomAccessFile r = new RandomAccessFile(file, "rws");
			r.setLength(0);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
	private static void cleanDirContent(String dirName){
		File folder = new File(dirName);  
		if (folder.isDirectory()) {  
		    File[] sun = folder.listFiles();  
		    for (File toDelete : sun) {  
		        toDelete.delete();  
		    }  
		}  
	}

	
	private void fillCVRectList(String text) {
		cvRectList = new ArrayList<CvRect>();
		String[] tokens = text.split(";");
		String[] tokens2 = null;
		
		for (String string : tokens) {
			tokens2 = string.split(",");
			cvRectList.add(new CvRect(Integer.parseInt(tokens2[0]),Integer.parseInt(tokens2[1]),Integer.parseInt(tokens2[2]),Integer.parseInt(tokens2[3])));
		}
		
	}
	
	public void addResult(String result) {
		this.resultFromServerRecog += result;
		this.counter++;
	}
	
	public void returnResultToClient(String text) {
	    try {
	        OutputStream socketStream = connectionSocket.getOutputStream();
	        ObjectOutputStream objectOutput = new ObjectOutputStream(socketStream);
	        objectOutput.writeObject(text);

	        objectOutput.close();
	        socketStream.close();
	        
	    	resultFromServerRecog = "";
			detectedImagesBytes.clear();
			counter = 0;
			
			cleanDirContent(DETECTED_IMAGES_DIR);
			cleanDirContent(RECEIVED_IMAGES_DIR);
			cleanDirContent(VIRUS_FOLDER_TO_SCAN);

	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}

	public byte[] readBytesFromClient() throws IOException {
		InputStream in = connectionSocket.getInputStream();
		DataInputStream dis = new DataInputStream(in);

		int len = dis.readInt();
		byte[] dataToStoreTheImage = new byte[len];

		if (len > 0) {
			dis.readFully(dataToStoreTheImage);
		}

		return dataToStoreTheImage;
	}
	

	/**
	 * This method ping the list of registered servers and rank the round by delay and load percentage of them to get the fast server and
	 * then return its IP.
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws ConsumerCancelledException 
	 * @throws ShutdownSignalException 
	 */
	public void rankServers() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String returnedStatistics = "";
		double maxLoadPercentage = -1, minLoadPercentage = 10000000;
		double maxRtt = -1, minRtt = 10000000;
		
		int latency;
		double capacity;
		
		//CAPTURE THE STATISTICS LOAD AND DELAY TO FILL THE PINGRESULTS
		for (VirtualMachineServer pingResult: this.firstServerGroupList) {
			returnedStatistics = getReturnTimeAndLoadPercentage(pingResult.getName());
			String[] tokens = returnedStatistics.split(";");
			
			double loadPercentage = Double.parseDouble(tokens[0]);
			latency = Integer.parseInt(tokens[1]);
			capacity = Double.parseDouble(tokens[2]);
			
			pingResult.setLoadPercentage(loadPercentage);
			
			
			//Ação temporária para fixar a latencia, procurando atingir distribuição normal no experimento.
			pingResult.setLatency(3);
			pingResult.setCapacity(capacity);
			
			System.out.println("CLOUDLET - Server: " + pingResult.getName() +
					" has a latency: " +latency + 
					"Ms and load percentage: " + loadPercentage +
					" and capacity: " +capacity);

			if (pingResult.getLoadPercentage() < minLoadPercentage) {
				minLoadPercentage = pingResult.getLoadPercentage() == 0 ? 1 : pingResult.getLoadPercentage();
			}
			if (pingResult.getRtt() < minRtt) {
				minRtt = pingResult.getRtt() == 0 ? 1 : pingResult.getRtt();
			}
			
			if (pingResult.getLoadPercentage() > maxLoadPercentage){
				maxLoadPercentage = pingResult.getLoadPercentage() == 0 ? 1 : pingResult.getLoadPercentage();
			}
			if (pingResult.getRtt() > maxRtt){
				maxRtt = pingResult.getRtt() == 0 ? 1 : pingResult.getRtt();
			}
			
		}
		
		//FILL THE FIELD FITNESS 
		for (VirtualMachineServer pingResult: this.firstServerGroupList) {
			double cost = CloudletUtils.calculateCost(
					pingResult,
					minLoadPercentage,
					maxLoadPercentage,
					minRtt,
					maxRtt);
			pingResult.setCost(cost);
		}
		
		//SORT THE LIST
		Collections.sort(this.firstServerGroupList);
	}

	/**
	 * Return time + Load Percentage 
	 * Example1: 43434;34
	 * Example2: 434;99
	 * @param nameServer
	 * @param serverType
	 * @return
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws ConsumerCancelledException 
	 * @throws ShutdownSignalException 
	 */
	public String getReturnTimeAndLoadPercentage(String nameServer) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		rabbitMQUtils.connect();
	    
	    String replyQueueName = rabbitMQUtils.getChannel().queueDeclare().getQueue(); 
	    QueueingConsumer consumer = new QueueingConsumer(rabbitMQUtils.getChannel());
	    rabbitMQUtils.getChannel().basicConsume(replyQueueName, true, consumer);
	    
		String response = null;
	    String corrId = java.util.UUID.randomUUID().toString();

	    String requestQueueName = nameServer + "-ping";
	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .correlationId(corrId)
	                                .replyTo(replyQueueName)
	                                .build();

		long startTime = System.currentTimeMillis();

		rabbitMQUtils.getChannel().basicPublish("", requestQueueName, props, ".".getBytes());

	    while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            response = new String(delivery.getBody());
	            break;
	        }
	    }

		System.out.println("CLOUDLET - Pinging server " + nameServer + "...");
		long endTime = System.currentTimeMillis();

		if (response.contains("pong")) {
			String[] tokens = response.split(";");
			String cpuUsagePercentage = tokens[1];
			return cpuUsagePercentage +";"+String.valueOf(endTime - startTime) + ";" + Double.parseDouble(tokens[2]);
		}

		return "Returned with errors.";
	}
	
	public void fillServersItensNumberByTotalCost(int numberOfItensToProcess, List<VirtualMachineServer> serversList) {
		double inverseCostSum = 0;
		int totalItensByName = 0;
		int totalRemaining = numberOfItensToProcess;
		Map<String, Integer> result = new HashMap<String, Integer>();
		for(VirtualMachineServer pingResult : serversList){
			inverseCostSum = inverseCostSum + (1 - pingResult.getCost());
		}
		System.out.println("Total itens: " + numberOfItensToProcess);
		for(VirtualMachineServer vm : serversList){
			totalItensByName = (int) Math.round(((1 - vm.getCost()) * numberOfItensToProcess) / inverseCostSum);
			if(totalItensByName > totalRemaining) {
				totalItensByName = totalRemaining;
			}
			vm.setTotalItensToProcess(totalItensByName);
			totalRemaining = totalRemaining - totalItensByName;
			System.out.println("Total itens for: " + vm.getName() + ": " + totalItensByName);
		}
		if(totalRemaining > 0){
			VirtualMachineServer firstServer = serversList.get(0);
			int currentPictures = result.get(firstServer.getName());
			int adjustmentPictures = currentPictures + totalRemaining;
			firstServer.setTotalItensToProcess(totalItensByName);
			System.out.println("Adjustment for " + firstServer.getName() + ": " + adjustmentPictures);
		}
	}
	
	
	public void fillServersItensNumberEqualy(int itensNumber, List<VirtualMachineServer> serversList) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		int vMsNumber = serversList.size();
		int itensPerVM = itensNumber/vMsNumber;
		int rest = itensNumber%vMsNumber;
		for(VirtualMachineServer pingResult : serversList){
			pingResult.setTotalItensToProcess(itensPerVM);
		}
		
		//Allocating the remaining faces for the first server of the list.
		serversList.get(0).setTotalItensToProcess(itensPerVM + rest);
	}
	
	public List<VirtualMachineServer> getReturnedPingResultList() {
		return firstServerGroupList;
	}

	public void setReturnedPingResultList(List<VirtualMachineServer> returnedPingResultList) {
		this.firstServerGroupList = returnedPingResultList;
	}
	
	
}
