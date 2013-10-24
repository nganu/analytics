package com.javatechig.feedreader;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.javatechig.feedreader.asynctaask.ImageDownloaderTask;
import com.javatechig.feedreader.model.FeedItem;
import com.javatechig.feedreader.utility.Constants;

public class FeedDetailsActivity extends Activity {

	private FeedItem feed;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed_details);

		feed = (FeedItem) this.getIntent().getSerializableExtra("feed");

		if (null != feed) {
			ImageView thumb = (ImageView) findViewById(R.id.featuredImg);
			new ImageDownloaderTask(thumb).execute(feed.getAttachmentUrl());

			TextView title = (TextView) findViewById(R.id.title);
			title.setText(feed.getTitle());

			TextView htmlTextView = (TextView) findViewById(R.id.content);
			htmlTextView.setText(Html.fromHtml(feed.getContent(), null, null));

			//flurry
			Map<String, String> articleParams = new HashMap<String, String>();
	        articleParams.put("Title", feed.getTitle()); // Capture author info
	        FlurryAgent.logEvent("Article_Detail");
	        
	        //GA.
	        Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(Constants.ANALYTICS.API_KEY_GA);
			HashMap<String, String> hitParameters = new HashMap<String, String>();
			hitParameters.put(Fields.HIT_TYPE, "appview");
			hitParameters.put(Fields.SCREEN_NAME, "News Detail Screen");
			hitParameters.put(Fields.ITEM_NAME, feed.getTitle());
			tracker.send(hitParameters);
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.menu_share:
	        	shareContent();
	            return true;
	        case R.id.menu_view:
	        	Intent intent = new Intent(FeedDetailsActivity.this, WebViewActivity.class);
				intent.putExtra("url", feed.getUrl());
				startActivity(intent);
				
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	private void shareContent() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, feed.getTitle() + "\n" + feed.getUrl());
		sendIntent.setType("text/plain");
		startActivity(Intent.createChooser(sendIntent, "Share using"));

	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.ANALYTICS.API_KEY_FLURRY);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		// In a function that captures when a user navigates away from article
		FlurryAgent.endTimedEvent("Article_Detail");
		FlurryAgent.onEndSession(this);
	}
	
	
}
