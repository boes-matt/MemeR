package com.boes.memer.models;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Meme")
public class Meme extends ParseObject implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TAG = "Meme";
	
	public static final String TOP_CAPTION = "topCaption";
	public static final String BOTTOM_CAPTION = "bottomCaption";
	public static final String BASE_IMAGE_URL = "baseImageUrl";
	public static final String COMPOSED_IMAGE = "composedImage";
	
	public Meme() {
		
	}
	
	public String getTopCaption() {
		return getString(TOP_CAPTION);
	}
	
	public void setTopCaption(String topCaption) {
		put(TOP_CAPTION, topCaption);
	}

	public String getBottomCaption() {
		return getString(BOTTOM_CAPTION);
	}
	
	public void setBottomCaption(String bottomCaption) {
		put(BOTTOM_CAPTION, bottomCaption);
	}
	
	public String getBaseImageUrl() {
		return getString(BASE_IMAGE_URL);
	}
	
	public void setBaseImageUrl(String baseImageUrl) {
		put(BASE_IMAGE_URL, baseImageUrl);
	}
	
	public ParseFile getComposedImage() {
		return getParseFile(COMPOSED_IMAGE);
	}
	
	public void setComposedImage(ParseFile composedImage) {
		put(COMPOSED_IMAGE, composedImage);
	}
	
	@Override
	public String toString() {
		return getTopCaption();
	}
	
}
