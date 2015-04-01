package com.appublisher.quizbank.network;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract interface RequestCallback {
	
	public void requestCompleted(JSONObject response, String apiName);
	
	public void requestCompleted(JSONArray response, String apiName);
	
	public void requestEndedWithError(VolleyError error, String apiName);
}
