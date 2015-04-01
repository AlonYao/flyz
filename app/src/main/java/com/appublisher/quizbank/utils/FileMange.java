package com.appublisher.quizbank.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileMange {
	
	/**
	 * 创建目录（支持多级目录）
	 * @param dirPath 目录路径
	 */
	public static void mkDir(String dirPath) {
		File fileDirs = new File(dirPath);
		if(!fileDirs.exists()) {
			fileDirs.mkdirs();
		}
	}
	
	/**
	 * 拷贝文件
	 * @param fromPath
	 * @param toPath
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
}
