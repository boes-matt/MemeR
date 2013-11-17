package com.boes.memer.models;

import org.json.JSONException;
import org.json.JSONObject;

import com.boes.memer.GoogleClient;

public class Image {
	
	private String thumbUrl;
	private String fullUrl;
	
	public Image(JSONObject object) throws JSONException {
		thumbUrl = object.getString(GoogleClient.KEY_TB_URL);
		fullUrl = object.getString(GoogleClient.KEY_URL);
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getFullUrl() {
		return fullUrl;
	}

	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}

}
