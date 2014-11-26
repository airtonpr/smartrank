package servers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import utils.FaceDetectionUtils;
import utils.FaceRecognitionUtils;
import utils.RecognitionResult;

public class CapacityEvaluator {

	static TCPServerDetectionAndRecognitionWithSocket detectionAndRecognition = new TCPServerDetectionAndRecognitionWithSocket();
	static FaceRecognitionUtils faceRecognition = new FaceRecognitionUtils();
	

	private static void detectAndRecog(String path) throws FileNotFoundException, IOException {
		changeOutputTo("Log_DetectionAndRecognition.txt");
		List<String> imagesNamesList = new ArrayList<String>();
		
		System.out.println("DETECTION and RECOG PHASE.....\n");
		for (int i = 0; i < 80; i++) {
			long start = System.currentTimeMillis();
			
			
			// DETECT, CROP AND SAVE IMAGES
			imagesNamesList = FaceDetectionUtils.dedectFacesCropAndSave(path);

			// RECOG
			detectionAndRecognition.createFileListOfImagesDetected(imagesNamesList);
			List<RecognitionResult> recognitionResult = faceRecognition.recognizeFileList(detectionAndRecognition.IMAGES_LIST_FOR_RECOGNITION_FILE);

			long end = System.currentTimeMillis();
			System.out.println(end - start);
			
			
			imagesNamesList.clear();
			cleanDirContent();
		}
	}


	private static void cleanDirContent(){
		File folder = new File(TCPServerRecognition.DETECTED_IMAGES_DIR);  
		if (folder.isDirectory()) {  
		    File[] sun = folder.listFiles();  
		    for (File toDelete : sun) {  
		        toDelete.delete();  
		    }  
		}  
	}
	
	
	private static void changeOutputTo(String pathLog) {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(pathLog);
			System.setOut(new PrintStream(fileOutputStream, true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void createListFileWithPicturesNameAndCreateEigenfaces() {
		String rootPath = "E:/WORKSPACES/smartrank_repos/SmartRank-Server/";
		String images_dir = rootPath + "training_images/";
		String fileOfImagesName =  rootPath + "smartrank/facerecognition/pattern_data/training_faces_image_list.txt";
		File arquivo = new File(images_dir);
		File[] file = arquivo.listFiles();

		changeOutputTo(fileOfImagesName);
		
		if (file != null) {
			int length = file.length;

			for (int i = 0; i < length; ++i) {
				File f = file[i];

				if (f.isFile()) {
					System.out.println(i + " " + f.getName() + " " + images_dir + f.getName());
				}
			}
		}
		
		 final FaceRecognitionUtils faceRecognition = new FaceRecognitionUtils();
		 faceRecognition.learn(fileOfImagesName);
	}

	
	public static void main(String[] args) {
		createListFileWithPicturesNameAndCreateEigenfaces();
	}
}
