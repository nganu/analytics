package com.javatechig.feedreader;

import com.flurry.android.FlurryAgent;
import com.javatechig.feedreader.utility.Constants;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new MyWebViewClient());

		Bundle bundle = this.getIntent().getExtras();
		String url = bundle.getString("url");

		if (null != url) {
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
			FlurryAgent.logEvent("Article_Detail_Webview");
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
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
		FlurryAgent.onEndSession(this);
	}
}
