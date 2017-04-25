package com.example.brianr.rsslocation;

/**
 * Created by TRidley on 02/06/2017.
 */

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.brianr.rsslocation.RssFeedListAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RSSMainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private Button mFetchFeedButton;
    private SwipeRefreshLayout mSwipeLayout;
    private EditText mSearchKeyword;
    private String searchKeyword;
    public String MapKeySearch;

    private List<RssFeedModel> mFeedModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rssactivity_main);
        try {
            Bundle extras = getIntent().getExtras();
            MapKeySearch = extras.getString("keysearch");
            Log.d("MAPKEYSEARCH",MapKeySearch);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFetchFeedButton = (Button) findViewById(R.id.fetchFeedButton);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSearchKeyword = (EditText) findViewById(R.id.searchKeywordText);
        //mSearchKeyword.setHint();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSearchKeyword.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                //TODO Auto-generated method stub
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO Auto-generated method stub
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //set country search keyword
                searchKeyword = mSearchKeyword.getText().toString().toLowerCase();
            }
        });

        mFetchFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RssFeedListAdapter.urlArticleLink = " ";
                new FetchFeedTask().execute((Void) null);
            }
        });

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });
        if(MapKeySearch != null) {
            searchKeyword = MapKeySearch;
            RssFeedListAdapter.urlArticleLink = " ";
            new FetchFeedTask().execute((Void) null);
        }
    }


    public void positionAction(View view) {
        int position = (int) view.getTag();
        Toast.makeText(view.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();
    }

    public List<RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<RssFeedModel> items = new ArrayList<>();
        items.clear();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            xmlPullParser.nextTag();

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {

                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MainActivity", "Parsing name ==> " + name);
                String result = "";

                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                    xmlPullParser.next();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                    Log.w("app", link);
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null && isItem != false) {
                    //if statement to search for keyword country

                    RssFeedModel item = new RssFeedModel(title, link, description);
                    //items.remove(item);

                    if(title.toString().toLowerCase().contains(searchKeyword) || description.toString().toLowerCase().contains(searchKeyword)){
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;

                }

            }

            return items;

        } finally {
            inputStream.close();
        }
    }



    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            mSwipeLayout.setRefreshing(true);
            urlLink = "http://feeds.bbci.co.uk/news/world/rss.xml";

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                mFeedModelList = parseFeed(inputStream);
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mSwipeLayout.setRefreshing(false);

            if (success) {
                // Fill RecyclerView
                mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));

            }

            else {
                Toast.makeText(RSSMainActivity.this,
                        "Enter a valid Rss feed url",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}