package com.boes.memer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.boes.memer.models.Image;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageAdapter extends ArrayAdapter<Image> {
	
	private static final String TAG = "ImageAdapter";
	private ImageLoader mImageLoader;
	private Context mContext;

	public ImageAdapter(Context context, ArrayList<Image> images) {
		super(context, 0, images);
		mImageLoader = ImageLoader.getInstance();
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Image image = getItem(position);		
		ImageView view = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
		view.setImageResource(android.R.color.transparent);
		mImageLoader.displayImage(image.getThumbUrl(), view);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra(ImagePickerActivity.IMAGE_URL, image.getFullUrl());
				Activity activity = (Activity) mContext;
				activity.setResult(Activity.RESULT_OK, data);
				activity.finish();
			}
			
		});
		
		return view;
	}
	
}
