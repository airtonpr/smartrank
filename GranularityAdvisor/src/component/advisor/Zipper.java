package component.advisor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zipper implements Runnable{
	static final int TAMANHO_BUFFER = 4096; // 4kb
	
	public Zipper(String zipFileName){
		this.zipFileName = zipFileName;
	}

	  static int indentLevel = -1;
	    private static List<String> list = new ArrayList<String>(); 
	    
//	    public static void main(String[] args){
//	      System.out.println(listPath(new File("E:/WORKSPACES/WORKSPACE_MCC_NEW/GranularityAdvisor/out"))); 
//	    }
	   
	    public static List<String> listPathRecursively(String pathString){
	        File files[];
	        
	        files = new File(pathString).listFiles();
	        int start = pathString.length();

	        Arrays.sort(files);
	    
	        for(int i = 0, n = files.length; i < n; i++){
	            if(files[i].isDirectory()){
	            	String name = files[i].getName();
					if (name.equals("android")) {
						continue;
					}else{
						listPathRecursively(files[i].getAbsolutePath());
					}
	            }else{
	            	if (files[i].getName().equals("BuildConfig.class") || 
	            			files[i].getName().startsWith("R$") ||
	            			files[i].getName().equals("R.class")) 
						continue;
	            	else
	            		list.add(files[i].getAbsolutePath());
	            }
	        }
	        return list;
	    }
	    
	public static void zipFile(String arqSaida, String arqEntrada)
			throws IOException {
		int cont;
		byte[] dados = new byte[TAMANHO_BUFFER];
		BufferedInputStream origem = null;
		FileInputStream streamDeEntrada = null;
		FileOutputStream destino = null;
		ZipOutputStream saida = null;
		ZipEntry entry = null;
		try {
			destino = new FileOutputStream(new File(arqSaida));
			saida = new ZipOutputStream(new BufferedOutputStream(destino));
			File file = new File(arqEntrada);
			streamDeEntrada = new FileInputStream(file);
			origem = new BufferedInputStream(streamDeEntrada, TAMANHO_BUFFER);
			entry = new ZipEntry(file.getName());
			saida.putNextEntry(entry);
			while ((cont = origem.read(dados, 0, TAMANHO_BUFFER)) != -1) {
				saida.write(dados, 0, cont);
			}
			origem.close();
			saida.close();
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}

	public static ArrayList<String> listFiles(String dir) throws IOException {
		File file = new File(dir);
		ArrayList<String> names = new ArrayList<String>();
		File afile[] = file.listFiles();
		int i = 0;
		for (int j = afile.length; i < j; i++) {
			File arquivos = afile[i];
			names.add(arquivos.getName());
		}
		return names;
	}
	
	public static ArrayList<String> unzip(String zipFileName) {
		System.out.println("\nUnzipping " + zipFileName);
		Enumeration entries;
		ArrayList<String> array = new ArrayList<String>();

		String pacote = zipFileName.substring(0, zipFileName.length() - 4)
				+ "_class/";
		new File(pacote).mkdir();

		try {
			ZipFile zipFile = new ZipFile(zipFileName);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				String fileName = entry.getName();
				if (entry.isDirectory()) {
					//System.err.println("Descompactando diretório: " + fileName);
					(new File(pacote + fileName)).mkdir();
					continue;
				}
				array.add(fileName);
			//	System.out.println("Descompactando arquivo:" + pacote +  fileName);
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(pacote
								+ fileName)));
			}
			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Error to unzip:" + ioe.getMessage());
			return null;
		}
		System.out.println("Done.\n");
		return array;
	}
		
	

		public static void main (String argv[]) {
			zipDir("smartrank/", "virusFolderToScan.zip", "smartrank/virusscanning/virusFolderToScan/");
		}

		
		
		public static void zipDir(String zipDirDestiny, String zipName, String dirToZip){
		      try {
		    	  final int BUFFER = 2048;
		         BufferedInputStream origin = null;
		         FileOutputStream dest = new 
		           FileOutputStream(zipDirDestiny+zipName);
		         ZipOutputStream out = new ZipOutputStream(new 
		           BufferedOutputStream(dest));
		         //out.setMethod(ZipOutputStream.DEFLATED);
		         byte data[] = new byte[BUFFER];
		         // get a list of files from current directory
		         File dir = new File(dirToZip);
		         String files[] = dir.list();

		         for (int i=0; i<files.length; i++) {
		        	 //     System.out.println("Adding: "+files[i]);
		            FileInputStream fi = new 
		              FileInputStream(dirToZip+files[i]);
		            origin = new 
		              BufferedInputStream(fi, BUFFER);
		            ZipEntry entry = new ZipEntry(files[i]);
		            out.putNextEntry(entry);
		            int count;
		            while((count = origin.read(data, 0, 
		              BUFFER)) != -1) {
		               out.write(data, 0, count);
		            }
		            origin.close();
		         }
		         out.close();
		      } catch(Exception e) {
		         e.printStackTrace();
		      }
		}
		
		public static void zipFiles(ArrayList<String> files, String zipDirDestiny, String zipDirOrigin, String zipName){

		      try {
		    	  final int BUFFER = 2048;
		         BufferedInputStream origin = null;
		         FileOutputStream dest = new 
		           FileOutputStream(zipDirDestiny+zipName);
		         ZipOutputStream out = new ZipOutputStream(new 
		           BufferedOutputStream(dest));
		         //out.setMethod(ZipOutputStream.DEFLATED);
		         byte data[] = new byte[BUFFER];
		         // get a list of files from current directory

		         for (String file : files) {
		        	 //    System.out.println("Adding: "+file);
		            FileInputStream fi = new 
		              FileInputStream(zipDirOrigin+file);
		            origin = new 
		              BufferedInputStream(fi, BUFFER);
		            ZipEntry entry = new ZipEntry(file);
		            out.putNextEntry(entry);
		            int count;
		            while((count = origin.read(data, 0, 
		              BUFFER)) != -1) {
		               out.write(data, 0, count);
		            }
		            origin.close();
		         }
		         out.close();
		      } catch(Exception e) {
		         e.printStackTrace();
		      }
		}

	private static void addToZipFile(String fileName, ZipOutputStream zos) throws FileNotFoundException, IOException {

//		System.out.println("Writing '" + fileName + "' to zip file");

		File file = new File(fileName);
		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

	private String zipFileName;

	@Override
	public void run() {
		unzip(zipFileName);
	}
}
