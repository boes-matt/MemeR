package com.boes.memer;

import android.app.Application;

import com.boes.memer.models.Meme;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class MemerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(Meme.class);
		Parse.initialize(this, "2qYPX6DJolxvMZ4E6IiBZ7aKFQsLKYOf4Z9sbIAO", 
				"9NuM7uvFzkDqBpzFbFxnTdGz5mintIZPax4Z9y1T");

		/*
		 * This app lets an anonymous user create and save data.
		 * An anonymous user is a user that can be created
		 * without a username and password but still has all of the same
		 * capabilities as any other ParseUser.
		 * 
		 * After logging out, an anonymous user is abandoned, and its data is no
		 * longer accessible. In your own app, you can convert anonymous users
		 * to regular users so that data persists.
		 * 
		 * Learn more about the ParseUser class:
		 * https://www.parse.com/docs/android_guide#users
		 */
		ParseUser.enableAutomaticUser();

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
		ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);
		
		
        // Create global configuration and initialize ImageLoader with this configuration
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
        		cacheInMemory().cacheOnDisc().build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);		
	}

}