package com.boes.memer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.boes.memer.models.Image;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ImagePickerActivity extends Activity {

	private static final String TAG = "ImagePickerActivity";
	public static final String IMAGE_URL = "imageUrl";
	
	private EditText etQuery;
	private String mQuery;
	private GridView gvImages;
	private ImageAdapter mAdapter;
	private int mRequestIndex;
	private boolean isLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_picker);
		
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvImages = (GridView) findViewById(R.id.gvImages);
		mAdapter = new ImageAdapter(this, new ArrayList<Image>());
		gvImages.setAdapter(mAdapter);
		isLoading = false;
		
		setupOnScroll();
	}
	
	public void setupOnScroll() {
		gvImages.setOnScrollListener(new OnScrollListener() {

			private static final int THRESHOLD = 6;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!isLoading && visibleItemCount != 0 && 
					firstVisibleItem + visibleItemCount > totalItemCount - THRESHOLD) {
						String queryUrl = GoogleClient.buildUrl(mQuery, mRequestIndex);
						getImages(queryUrl);				
					}
			}
			
		});
	}
	
	public void onSearch(View v) {
		Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
		mRequestIndex = 0;
		mQuery = etQuery.getText().toString();
		mAdapter.clear();

		// Get a total of 16 images
		String queryUrl = GoogleClient.buildUrl(mQuery, mRequestIndex);
		getImages(queryUrl);
		getImages(queryUrl);
		
		hideSoftKeyboard(v);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_picker, menu);
		return true;
	}
	
	private void getImages(String queryUrl) {
		GoogleClient.getRequest(queryUrl, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONObject object) {
				try {
					JSONArray results = GoogleClient.getResults(object);
					for (int i = 0; i < results.length(); i++) {
						JSONObject result = results.getJSONObject(i);
						Image image = new Image(result);
						mAdapter.add(image);						
					}
				} catch (JSONException e) {
					Log.e(TAG, "Error parsing json response", e);
				}
			}
			
		});
		
		mRequestIndex += GoogleClient.VALUE_PAGE_SIZE;
	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
}
