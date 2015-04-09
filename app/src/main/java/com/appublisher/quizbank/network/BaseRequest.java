package com.appublisher.quizbank.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.appublisher.quizbank.utils.Logger;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

public class BaseRequest {
    protected static final String TAG = "Volley";
    protected static RequestQueue mQueue = null;
    protected RequestCallback listener = null;
    protected String encode = null;
    protected static final int TIMEOUT_S = 15;
    protected static final int RETRY_NUM = 1;
    private MultipartEntity entity = new MultipartEntity();

    /**
     * @return
     * 		instance of the queue
     * @throws
     * 		IllegalStateException if init has not yet been called
     */
    public static RequestQueue getRequestQueue() {
        if (mQueue != null) {
            return mQueue;
        } else {
            throw new IllegalStateException("Not initialized");
        }
    }

    /**
     * 阻塞式请求，异步用于子线程调用
     *
     * @return 返回值
     */
    protected Object syncRequest(String url, String type) {
        if (type.equals("object")) {
            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(), future, future);
            request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S*1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setTag(TAG);
            mQueue.add(request);

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            RequestFuture<JSONArray> future = RequestFuture.newFuture();
            JsonArrayRequest request = new JsonArrayRequest(url, future, future);
            request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S*1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mQueue.add(request);

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    /**
     * 设置监听回调
     *
     * @param callback 回调
     */
    protected void setCallbackListener(RequestCallback callback) {
        this.listener = callback;
    }


    /**
     * 执行异步请求
     *
     * @param url	请求地址
     * @param name	回调的接口标识
     * @param type 	请求的数据类型：array | object | plaintext
     */
    protected void asyncRequest(final String url, final String name, String type) {

        Logger.i(url);

        switch (type) {
            case "object": {
                JsonObjectRequest request = new JsonObjectRequest(url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (listener != null)
                                    listener.requestCompleted(response, name);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                }) {

                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        try {
                            if (encode != null) {
                                String jsonString = new String(response.data, encode);
                                encode = null;
                                return Response.success(new JSONObject(jsonString),
                                        HttpHeaderParser.parseCacheHeaders(response));
                            }
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new VolleyError(e));
                        } catch (JSONException je) {
                            return Response.error(new VolleyError(je));
                        }

                        return super.parseNetworkResponse(response);
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);

                break;
            }
            case "array": {
                JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (listener != null)
                            listener.requestCompleted(response, name);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);

                break;
            }
            default: {
                StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//			        	listener.requestCompleted(response, "");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                });
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);
                break;
            }
        }

    }


    /**
     * 数据提交
     *
     * @param url		请求地址
     * @param params	要提交的数据
     * @param name		回调的接口标识
     * @param type		请求的数据类型：array | object | plaintext
     */
    public void postRequest(final String url, final Map<String, String> params, final String name, String type) {
        switch (type) {
            case "object": {
                buildMultipartEntity(params);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (listener != null)
                                    listener.requestCompleted(response, name);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                }) {

                    @Override
                    public String getBodyContentType() {
                        return entity.getContentType().getValue();
                    }

                    @Override
                    /**
                     * Returns the raw POST or PUT body to be sent.
                     *
                     * @throws com.android.volley.AuthFailureError in the event of auth failure
                     */
                    public byte[] getBody() {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        try {
                            entity.writeTo(bos);
                        } catch (IOException e) {
                            VolleyLog.e("IOException writing to ByteArrayOutputStream");
                        }
                        return bos.toByteArray();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-agent", "DailyLearn");
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);

                break;
            }
            case "array": {
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (listener != null)
                                    listener.requestCompleted(response, name);
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("User-agent", "DailyLearn");
                        return headers;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);

                break;
            }
            default: {
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//			        	listener.requestCompleted(response, name);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (listener != null)
                            listener.requestEndedWithError(error, name);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        headers.put("User-agent", "DailyLearn");
                        return headers;
                    }

                };
                request.setRetryPolicy(new DefaultRetryPolicy(TIMEOUT_S * 1000, RETRY_NUM, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setTag(name.isEmpty() ? TAG : name);
                mQueue.add(request);
                break;
            }
        }
    }


    /**
     * 构造数据上传实体
     *
     * @param params 实体
     */
    private void buildMultipartEntity(Map<String, String>params) {

        entity = new MultipartEntity();

        for (Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                entity.addPart(key, new StringBody(value, Charset.forName("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                VolleyLog.e("UnsupportedEncodingException");
            }
        }
    }


    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag tag
     */
    protected void cancelPendingRequests(Object tag) {
        if (mQueue != null) {
            mQueue.cancelAll(tag);
        }
    }
}
