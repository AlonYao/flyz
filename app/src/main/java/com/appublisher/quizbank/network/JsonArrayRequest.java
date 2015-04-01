package com.appublisher.quizbank.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class JsonArrayRequest extends JsonRequest<JSONArray> {
	/**
	 * Creates a new request.
	 * @param url URL to fetch the JSON from
	 * @param listener Listener to receive the JSON response
	 * @param errorListener Error listener, or null to ignore errors.
	 */
	public JsonArrayRequest(String url, Listener<JSONArray> listener, ErrorListener errorListener) {
	    super(Method.GET, url, null, listener, errorListener);
	}
	
	public JsonArrayRequest(int method, String url, Listener<JSONArray> listener, ErrorListener errorListener) {
	    super(method, url, null, listener, errorListener);
	}

	@Override
	protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
		// TODO Auto-generated method stub
		try {
	        String jsonString =
	                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
	        return Response.success(new JSONArray(jsonString),
	                HttpHeaderParser.parseCacheHeaders(response));
	    } catch (UnsupportedEncodingException e) {
	        return Response.error(new ParseError(e));
	    } catch (JSONException je) {
	        return Response.error(new ParseError(je));
	    }
	}
}
