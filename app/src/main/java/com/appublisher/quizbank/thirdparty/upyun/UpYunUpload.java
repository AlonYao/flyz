package com.appublisher.quizbank.thirdparty.upyun;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;

public class UpYunUpload extends AsyncTask<String, Integer, String> {

	private ProgressBar bar;
	private TextView tv;
	private UpFinishListener upFinishListener;
	
	private static final String API_KEY = "aYlkcueylVK+O9tbBRTB6rLP0Vc="; //测试使用的表单api验证密钥
	private static final String BUCKET = "edu-1";						//存储空间
	private static final long EXPIRATION = System.currentTimeMillis()/1000 + 1000 * 5 * 10; //过期时间，必须大于当前时间
	
	private final String src;
	private final String target;
	private int count = 0;

    private String url;
	
	public interface UpFinishListener{
		void onUploadFinished(String result, String url);
	}
	
	public UpYunUpload(UpFinishListener upFinishListener, 
			ProgressBar bar, TextView tv, String src, String target) {
		this.upFinishListener = upFinishListener;
		this.bar = bar;
		this.src = src;
		this.target = target;
		this.tv = tv;
	}
	
	@SuppressLint("UseValueOf")
	@Override
	protected String doInBackground(String... params) {
		
		try {
			//取得base64编码后的policy
			String policy = UpYunUtils.makePolicy(target, EXPIRATION, BUCKET);
			
			//根据表单api签名密钥对policy进行签名
			//通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
			String signature = UpYunUtils.signature(policy + "&" + API_KEY);
			
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httppost = new HttpPost("http://v0.api.upyun.com/" + BUCKET + "/");

            url = "http://" + BUCKET + ".b0.upaiyun.com" + target;

			SimpleMultipartEntity sme = new SimpleMultipartEntity(
                    new SimpleMultipartEntity.ProgressListener() {
                @Override
                public void transfer(int num) {
                    publishProgress(num);
                }
            });
			sme.addPart("policy", policy);
			sme.addPart("signature", signature);

			sme.addPart("file", new File(src));
			httppost.setEntity(sme);
			bar.setMax((int)sme.getContentLength());
			
			// 错误调试信息获取
			httpclient.getParams().setParameter("http.socket.sendbuffer", new Integer(1));

			HttpResponse response = httpclient.execute(httppost);

			StatusLine statusLine = response.getStatusLine();

			int code = statusLine.getStatusCode();
			String str = EntityUtils.toString(response.getEntity());
			JSONObject obj = new JSONObject(str);
			String msg = obj.getString("message");

			if (code == HttpStatus.SC_OK && msg.equals("ok")) {
				return "Success";
			} else {
				return "Failed";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed";
		}
	}
	
	@Override
	protected void onPreExecute() {
		tv.setText("loading...");
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		if(isCancelled()) return;
		count += values[0];
		bar.setProgress(count);
		super.onProgressUpdate(values);
	}

    @Override
    protected void onPostExecute(String result) {
		if (this.upFinishListener != null) {
			this.upFinishListener.onUploadFinished(result, url);
		}

		super.onPostExecute(result);
	}
}
