package com.utils;

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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zipper {
	static final int TAMANHO_BUFFER = 4096; // 4kb

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
	
	public static void main(String[] args) throws IOException {
		System.out.print(Zipper.listFiles("E:/WORKSPACES/WORKSPACE_MCC_NEW/private_mcc_vision/Cloudlet/src/main"));
	}

	public static void unzip(String dir, String zipFileName) {
		Enumeration entries;

		try {
			ZipFile zipFile = new ZipFile(dir+zipFileName);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				if (entry.isDirectory()) {
//					System.err.println("Descompactando diretório: "
//							+ entry.getName());
					(new File(entry.getName())).mkdir();
					continue;
				}
//				System.out.println("Descompactando arquivo:" + entry.getName());
				copyInputStream(
						zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(dir+entry
								.getName())));
			}
			zipFile.close();
		} catch (IOException ioe) {
//			System.err.println("Erro ao descompactar:" + ioe.getMessage());
			return;
		}
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

	public static String zipFiles(ArrayList<String> files, String dirDestiny, String zipName) {
		try {
			FileOutputStream fos = new FileOutputStream(dirDestiny + zipName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (String file : files) {
				addToZipFile(dirDestiny + file, zos);
			}

			zos.close();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dirDestiny + zipName;
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
}
