package com.boes.memer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class GoogleClient {

	private static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images";

	private static final String KEY_VERSION = "v";
	private static final String VALUE_VERSION = "1.0";

	private static final String KEY_QUERY = "q";

	private static final String KEY_REFERER = "Referer";
	private static final String VALUE_REFERER = "com.boes.memer";

	private static AsyncHttpClient client = new AsyncHttpClient();
	
	private static final String KEY_STATUS = "responseStatus";
	private static final int VALUE_OK = 200;
	
	private static final String KEY_DATA = "responseData";           
	private static final String KEY_DATA_RESULTS = "results";
	
	private static final String KEY_PAGE_SIZE = "rsz";
	public static final int VALUE_PAGE_SIZE = 8;
	private static final String KEY_START = "start";
	private static final int MAX_RESULTS = 64;
	
	public static final String KEY_TB_URL = "tbUrl";
	public static final String KEY_URL = "url";
	
	public static String buildUrl(String query, int start) {
		return Uri.parse(BASE_URL).buildUpon()
				.appendQueryParameter(KEY_VERSION, VALUE_VERSION)
				.appendQueryParameter(KEY_QUERY, query)
				.appendQueryParameter(KEY_PAGE_SIZE, String.valueOf(VALUE_PAGE_SIZE))
				.appendQueryParameter(KEY_START, String.valueOf(start)).build().toString();
	}
	
	public static void getRequest(String url, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put(KEY_REFERER, VALUE_REFERER);
		client.get(url, params, responseHandler);
	}
	
	public static JSONArray getResults(JSONObject json) throws JSONException {
		JSONArray results = new JSONArray();
		
		if (json.getInt(KEY_STATUS) == VALUE_OK)
			results = json.getJSONObject(KEY_DATA).getJSONArray(KEY_DATA_RESULTS);
		
		return results;
	}
	
}
