package com.appublisher.quizbank.network;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

public interface RequestCallback {
	
	void requestCompleted(JSONObject response, String apiName);
	
	void requestCompleted(JSONArray response, String apiName);
	
	void requestEndedWithError(VolleyError error, String apiName);
}
