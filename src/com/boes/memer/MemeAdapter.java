package com.boes.memer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.boes.memer.models.Meme;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MemeAdapter extends ArrayAdapter<Meme> {

	private static final String TAG = "MemeAdapter";
	private ImageLoader mImageLoader;
	private OnMemeClickListener mListener;
	
	public interface OnMemeClickListener {
		public void loadMemes(String baseImageUrl);
		public void shareMeme(Uri bitmapUri);
		public void forkMeme(String memeId);
	}
	
	public MemeAdapter(Activity activity, ArrayList<Meme> memes) {
		super(activity, 0, memes);
		mImageLoader = ImageLoader.getInstance();
		
		if (activity instanceof OnMemeClickListener) {
			mListener = (OnMemeClickListener) activity;
		} else {
			throw new ClassCastException(activity.toString() + " must implement MemeAdapter.OnMemeClickListener");
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout itemView = (RelativeLayout) convertView;
		if (itemView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			itemView = (RelativeLayout) inflater.inflate(R.layout.item_meme, parent, false);			
		}
				
		final ImageView ivMeme = (ImageView) itemView.findViewById(R.id.ivMeme);
		ivMeme.setImageResource(R.drawable.ic_base_image);
		
		Button btnShare = (Button) itemView.findViewById(R.id.btnShare);
		Button btnFork = (Button) itemView.findViewById(R.id.btnFork);
		
		final Meme meme = getItem(position);
		mImageLoader.displayImage(meme.getComposedImage().getUrl(), ivMeme);
		
		
		ivMeme.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.loadMemes(meme.getBaseImageUrl());
			}
			
		});
		
		btnFork.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.forkMeme(meme.getObjectId());				
			}
			
		});
		
		btnShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bitmap bmp = ((BitmapDrawable) ivMeme.getDrawable()).getBitmap();
				mListener.shareMeme(getBitmapUri(bmp));				
			}
			
			private Uri getBitmapUri(Bitmap bmp) {
				try {
			        File file =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "meme.png");  
			     	FileOutputStream out = new FileOutputStream(file);
			     	bmp.compress(Bitmap.CompressFormat.PNG, 0, out);
			     	out.close();
					return Uri.fromFile(file);	
				} catch (IOException e) {
					Log.e(TAG, "Error retrieving meme file path", e);
					return null;
				}
			}
			
		});
		
		return itemView;
	}

}
