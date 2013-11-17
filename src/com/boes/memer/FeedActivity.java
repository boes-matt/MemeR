package com.boes.memer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.boes.memer.MemeAdapter.OnMemeClickListener;
import com.boes.memer.models.Meme;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FeedActivity extends Activity implements OnMemeClickListener {

	private ListView lvMemes;
	private MemeAdapter mAdapter;
	
	private static final String GLOBAL_FEED = "globalFeed";
	private String mFeed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		lvMemes = (ListView) findViewById(R.id.lvMemes);
		mAdapter = new MemeAdapter(this, new ArrayList<Meme>());
		lvMemes.setAdapter(mAdapter);
		setFeed(GLOBAL_FEED);
		loadMemes();
	}

	private void loadMemes() {
		mAdapter.clear();
		if (getFeed().equals(GLOBAL_FEED)) {
			loadGlobalMemes();
		} else {
			loadImageMemes();
		}
	}
	
	private void loadGlobalMemes() {
		// Query and add to adapter
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Meme");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects)
						mAdapter.add((Meme) obj);
				}
			}
			
		});
	}
	
	private void loadImageMemes() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Meme");
		query.orderByDescending("createdAt");
		query.whereEqualTo(Meme.BASE_IMAGE_URL, getFeed());
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects)
						mAdapter.add((Meme) obj);
				}
			}
			
		});		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Better to manage different feeds with fragments!
		if (getFeed().equals(GLOBAL_FEED)) {
			super.onBackPressed();			
		} else {
			setFeed(GLOBAL_FEED);
			loadMemes();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}
	
	public void onCreateMeme(MenuItem item) {
		Intent i = new Intent(this, MemeActivity.class);
		startActivityForResult(i, 0);
	}

	public void onRefresh(MenuItem item) {
		refreshMemes();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			refreshMemes();
		}
	}
	
	private void refreshMemes() {
		Toast.makeText(this, "Refreshing Memes", Toast.LENGTH_SHORT).show();
		loadMemes();
	}

	public String getFeed() {
		return mFeed;
	}

	public void setFeed(String feed) {
		mFeed = feed;
	}
	
	@Override
	public void loadMemes(String feed) {
		setFeed(feed);
		loadMemes();
	}

	@Override
	public void shareMeme(Uri bitmapUri) {
		if (bitmapUri != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
			shareIntent.setType("image/*");
			startActivity(Intent.createChooser(shareIntent, "Share Meme"));					
		} else {
			Toast.makeText(this, "Error in sharing meme", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void forkMeme(String memeId) {
		Intent i = new Intent(this, MemeActivity.class);
		i.putExtra(MemeActivity.MEME, memeId);
		startActivityForResult(i, 0);
	}

}
