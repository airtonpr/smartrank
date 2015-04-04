package advisor.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.strobel.decompiler.DecompilerDriver;
import component.advisor.Dex2ApkToJar;
import component.advisor.Zipper;
import component.gr.spinellis.ckjm.MetricsFilter;

public class Main {
	
	static PrintStream originalPrintStrean = System.out;

	public static void main(String[] args) {
		//runMethodAdvisor();

		Main.analyzeAPK("VirusScanningAndroid.apk");
	}


	public static void analyzeAPK(String apkName) {

		// Convert apk into jar
		Dex2ApkToJar.main(apkName);

		// Extract the .class from .jar
		String nameWithoutExtension = apkName
				.substring(0, apkName.length() - 4);
		Zipper.unzip(nameWithoutExtension + ".jar");

		System.out.println("Analysing Code.....");
		
		changeOutput("reportComponent.txt");  

		// Analyze the .class and present the statistics
		List<String> listPathRecursively = Zipper
				.listPathRecursively(nameWithoutExtension +"_class");
		// List<String> listPathRecursively =
		// Zipper.listPathRecursively("E:/WORKSPACES/WORKSPACE_MCC_NEW/Teste/bin");
		MetricsFilter.runComponentAdvisor(listPathRecursively);

		changeOutput("log.txt");  
		
		transformClassesToJava(nameWithoutExtension + ".jar");
		
		changeOutput("reportMethod.txt");
		
		runMethodAdvisor(nameWithoutExtension);
		
		System.setOut(originalPrintStrean); 
		System.out.println("Analysis Finished!");
		System.out.println("Please, see the generated files reportMethod.txt, reportComponent.txt and log.txt");
		
	}


	private static void changeOutput(String file) {
		PrintStream out,log;
		try {
			out = new PrintStream(new FileOutputStream(new File(file)));
			log = new PrintStream(new FileOutputStream(new File("log.txt")));
			System.setOut(out); 
			System.setErr(log);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void runMethodAdvisor(String dirName) {
		List<String> listPathRecursively = Zipper.listPathRecursively(dirName + "_java");
		MethodAdvisor.runMethodAdvisor(listPathRecursively);
	}

	private static void transformClassesToJava(String jarName) {
		String outputPackage = jarName.substring(0, jarName.length() - 4) +"_java";

		String[] s = new String[4];
		s[0] = "-jar";
		s[1] = jarName;
		s[2] = "-o";
		s[3] = outputPackage;
		DecompilerDriver.main(s);
	}

}
