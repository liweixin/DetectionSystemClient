package com.prp.detectionsystemclient.network;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by lwx on 2016/6/2.
 */
public class StringPostRequest extends StringRequest {
    Map<String, String> params;
    public StringPostRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> params) {
        super(method, url, listener, errorListener);
        this.params = params;
    }
    public StringPostRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> params) {
        super(Method.POST, url, listener, errorListener);
        this.params = params;
    }
    @Override
    protected Map<String, String> getParams(){
        return params;
    }
}
