package com.appublisher.quizbank.thirdparty.upyun;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.appublisher.quizbank.thirdparty.upyun.SimpleMultipartEntity.ProgressListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;

public class UpYunTask extends AsyncTask<String, Integer, String> {
	private final Activity activity;
	private final ProgressBar bar;
	
	private static final String API_KEY = "aYlkcueylVK+O9tbBRTB6rLP0Vc="; //测试使用的表单api验证密钥
	private static final String BUCKET = "edu-1";						//存储空间
	private static final long EXPIRATION = System.currentTimeMillis()/1000 + 1000 * 5 * 10; //过期时间，必须大于当前时间
	
	private final String src;
	private final String target;
	private int count = 0;
	
	public UpYunTask(Activity activity, ProgressBar bar, String src, String target) {
		this.bar = bar;
		this.src = src;
		this.target = target;
		this.activity = activity;
	}
	
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

			SimpleMultipartEntity sme = new SimpleMultipartEntity(new ProgressListener() {
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
			
//			httpclient.getParams().setParameter("http.socket.sendbuffer", new Integer(1));

			HttpResponse response = httpclient.execute(httppost);

			StatusLine statusLine = response.getStatusLine();

			int code = statusLine.getStatusCode();
			String str = EntityUtils.toString(response.getEntity());

			if (code != HttpStatus.SC_OK) {
				JSONObject obj = new JSONObject(str);
				@SuppressWarnings("unused")
				String msg = obj.getString("message");

			} else {
				@SuppressWarnings("unused")
				JSONObject obj = new JSONObject(str);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	
		activity.setResult(Activity.RESULT_OK);
		activity.finish();
		super.onPostExecute(result);
	}

}
