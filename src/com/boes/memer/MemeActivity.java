package com.boes.memer;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.boes.memer.models.Meme;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MemeActivity extends Activity {

	public static final String TAG = "MemeActivity";
	public static final String MEME = "meme";
	
	private Meme mMeme;
	
	private EditText etTopCaption;
	private EditText etBottomCaption;
	private ImageButton ivBaseImage;
	private ImageLoader mImageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meme);
		
		etTopCaption = (EditText) findViewById(R.id.etTopCaption);
		etBottomCaption = (EditText) findViewById(R.id.etBottomCaption);
		ivBaseImage = (ImageButton) findViewById(R.id.ivBaseImage);
		mImageLoader = ImageLoader.getInstance();
		
		String memeId = getIntent().getStringExtra(MEME);
		
		if (memeId != null) {
			Log.d(TAG, "Meme id is" + memeId);
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Meme");
			query.getInBackground(memeId, new GetCallback<ParseObject>() {
				
				@Override
				public void done(ParseObject object, ParseException e) {
					// Fork meme
					if (e == null) {
						Meme oldMeme = (Meme) object;
						Log.d(TAG, "Forked meme: " + oldMeme);
						
						etTopCaption.setText(oldMeme.getTopCaption());
						etBottomCaption.setText(oldMeme.getBottomCaption());
						mImageLoader.displayImage(oldMeme.getBaseImageUrl(), ivBaseImage);						
						
						mMeme = new Meme();
						mMeme.setBaseImageUrl(oldMeme.getBaseImageUrl());
					}
				}
				
			});
			ivBaseImage.setImageResource(android.R.color.transparent);
			
		} else {
			mMeme = new Meme();
		}
		
		ivBaseImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MemeActivity.this, ImagePickerActivity.class);
				startActivityForResult(i, 0);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.meme, menu);
		return true;
	}
	
	public void onPublish(View v) {
		String topCaption = etTopCaption.getText().toString();
		String bottomCaption = etBottomCaption.getText().toString();
		
		if (!topCaption.equals("") && !bottomCaption.equals("") && mMeme.getBaseImageUrl() != null) {
			mMeme.setTopCaption(topCaption);
			mMeme.setBottomCaption(bottomCaption);
			saveMeme();
			setResult(Activity.RESULT_OK);
			finish();			
		} else {
			Toast.makeText(this, "Unable to publish unfinished meme", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void saveMeme() {
		final String baseImageUrl = mMeme.getBaseImageUrl();
		mImageLoader.loadImage(baseImageUrl, new SimpleImageLoadingListener() {
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				float WIDTH = 512;
				float scaleByWidth = WIDTH / loadedImage.getWidth();
				int scaledW = (int) WIDTH;
				int scaledH = (int) (loadedImage.getHeight() * scaleByWidth);
				
				Bitmap cropped = Bitmap.createScaledBitmap(loadedImage, scaledW, scaledH, true);		
				
				Canvas canvas = new Canvas(cropped);
				Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				textPaint.setColor(Color.BLACK);
				textPaint.setStyle(Style.STROKE);
				textPaint.setStrokeWidth(5);
				textPaint.setTextAlign(Align.CENTER);
				textPaint.setTextSize(scaledH * 0.06f);
				
				FontMetrics fm = textPaint.getFontMetrics();
				float xText = scaledW / 2;
				
				float Y_MARGIN = 15f;
				float yTopText = -fm.ascent + fm.descent + Y_MARGIN;
				float yBottomText = scaledH - yTopText;
				
				Log.d(TAG, "Dimensions: " + "fmDescent = " + fm.descent 
						+ ", fmAscent = " + fm.ascent
						+ ", scaledH = " + scaledH
						+ ", yTopText = " + yTopText
						+ ", yBottomText = " + yBottomText);
								
				canvas.drawText(mMeme.getTopCaption(), xText, yTopText, textPaint);
				canvas.drawText(mMeme.getBottomCaption(), xText, yBottomText, textPaint);
				
				// Draw white fill
				textPaint.setStyle(Style.FILL);
				textPaint.setColor(Color.WHITE);
				canvas.drawText(mMeme.getTopCaption(), xText, yTopText, textPaint);
				canvas.drawText(mMeme.getBottomCaption(), xText, yBottomText, textPaint);
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				cropped.compress(CompressFormat.PNG, 0, bos);
				byte[] bytes = bos.toByteArray();
				
				final ParseFile imageFile = new ParseFile("image.png", bytes);
				imageFile.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
						if (e == null) {
							mMeme.setComposedImage(imageFile);
							mMeme.saveInBackground();
						} else {
							Log.e(TAG, "Error saving cropped image", e);
						}
					}
					
				});
			}

		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String imageUrl = data.getStringExtra(ImagePickerActivity.IMAGE_URL);
			mImageLoader.displayImage(imageUrl, ivBaseImage);
			mMeme.setBaseImageUrl(imageUrl);
			Toast.makeText(this, "Loading full size image. Patience please.", Toast.LENGTH_SHORT).show();
		}
	}

}
