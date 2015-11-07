package com.appublisher.quizbank.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileManager {

	private static final int BUF_SIZE = 1024;

	/**
	 * 创建目录（支持多级目录）
	 * @param dirPath 目录路径
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void mkDir(String dirPath) {
		File fileDirs = new File(dirPath);
		if(!fileDirs.exists()) {
			fileDirs.mkdirs();
		}
	}
	
	/**
	 * 拷贝文件
	 * @param fromPath fromPath
	 * @param toPath toPath
	 */
	public static boolean copyFile(String fromPath, String toPath) {
		try{
			InputStream fos = new FileInputStream(fromPath);
			OutputStream ops = new FileOutputStream(toPath);
			
			byte[] temp = new byte[1024];
			int c;
			while((c = fos.read(temp)) > 0) {
				ops.write(temp, 0, c);
			}
			ops.flush();
			fos.close();
			ops.close();
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 解压缩
	 * @param zip File
	 * @param targetDir File
	 * @throws IOException
	 */
	public static void unzip(File zip, File targetDir) throws IOException {
		InputStream in = new FileInputStream(zip);
		unzip(in, targetDir);
		in.close();
	}

	/**
	 * 解压缩
	 * @param in File
	 * @param targetDir File
	 * @throws IOException
	 */
	private static void unzip(InputStream in, File targetDir) throws IOException {
		final ZipInputStream zipIn = new ZipInputStream(in);
		final byte[] b = new byte[BUF_SIZE];
		ZipEntry zipEntry;
		while ((zipEntry = zipIn.getNextEntry()) != null) {
			String zipEntryName = zipEntry.getName().replace("\\", "");
			final File file = new File(targetDir, zipEntryName);
			if (!zipEntry.isDirectory()) {
				final File parent = file.getParentFile();
				if (!parent.exists()) {
					//noinspection ResultOfMethodCallIgnored
					parent.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(file);
				int r;
				while ((r = zipIn.read(b)) != -1) {
					fos.write(b, 0, r);
				}
				fos.close();
			} else {
				//noinspection ResultOfMethodCallIgnored
				file.mkdirs();
			}
			zipIn.closeEntry();
		}
	}

	/**
	 * 删除指定文件
	 * @param filePath 文件路径
	 */
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void deleteFiles(String filePath) {
		if (filePath == null || filePath.length() == 0) return;

		try {
			File file = new File(filePath);

			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (f.isFile()) {
						f.delete();
					} else if (f.isDirectory()) {
						deleteFiles(f.getAbsolutePath());
					}
				}
			}

		} catch (Exception e) {
			// Empty
		}
	}

}
