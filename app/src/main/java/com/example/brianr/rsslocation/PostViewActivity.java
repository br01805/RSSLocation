package com.example.brianr.rsslocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by TRidley on 02/06/2017.
 */

public class PostViewActivity extends AppCompatActivity {

    public WebView myWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_post_view);

        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(RssFeedListAdapter.urlArticleLink);

    }

    @Override
    public void onBackPressed() {
        RssFeedListAdapter.urlArticleLink = " ";
        finish();
        return;
    }

}
