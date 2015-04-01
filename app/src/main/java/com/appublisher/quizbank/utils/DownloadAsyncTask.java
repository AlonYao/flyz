package com.appublisher.quizbank.utils;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

	private final ProgressBar bar;
	private int count = 0;
	private FinishListener listener;
	private String filePath, fileUrl;

	public interface FinishListener{
		void onFinished();
	}
	
	/**
	 * 构造函数
	 * @param fileUrl  链接地址
	 * @param filePath  下载文件地址
	 * @param listener  完成监听事件
	 * @param bar  ProgressBar
	 */
	public DownloadAsyncTask(String fileUrl, String filePath, FinishListener listener, ProgressBar bar) {
		super();
		this.fileUrl = fileUrl;
		this.filePath = filePath;
		this.listener = listener;
		this.bar = bar;
	}
	
	@Override
	protected String doInBackground(String... params) {
		
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10*1000);
			
			connection.connect();

            int a = connection.getResponseCode();

			if(connection.getResponseCode() == 200) {
				if(bar != null) {
					bar.setMax(connection.getContentLength());
				}
				
				File image = new File(filePath);
				if(image.exists()) {
                    image.delete();
                }
				image.createNewFile();
				
				InputStream inputStream = connection.getInputStream();
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				
				int len = 0;
				byte[] buffer = new byte[10*1024];
				while(true) {
					if(isCancelled()) return null;
					
					len = inputStream.read(buffer);
					
					if(bar != null){
						publishProgress(len);
					}
					if(len == -1) break;
					
					arrayOutputStream.write(buffer, 0, len);
				}
				
				arrayOutputStream.close();
				inputStream.close();
				
				byte[] data = arrayOutputStream.toByteArray();
				FileOutputStream fileOutputStream = new FileOutputStream(image);
				fileOutputStream.write(data);
				fileOutputStream.close();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return filePath;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(isCancelled()) return;
		
		if(bar != null) {
			count += values[0];
			bar.setProgress(count);
		}
		super.onProgressUpdate(values);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (this.listener!=null)
			this.listener.onFinished();
		
		super.onPostExecute(result);
	}

}
