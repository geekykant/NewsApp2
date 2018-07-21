package com.diyandroid.newsapp.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.diyandroid.newsapp.News;
import com.diyandroid.newsapp.R;
import com.diyandroid.newsapp.adapter.NewsAdapter;
import com.diyandroid.newsapp.settings.SettingsActivity;
import com.diyandroid.newsapp.utill.NewsLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    private NewsAdapter adapter;
    private TextView mEmptyStateTextView;

    private static final String REQUEST_URL = "https://content.guardianapis.com";
    private SharedPreferences prefs;

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean restartBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        ListView newsListView = (ListView) findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        adapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(adapter);

        // opens WebBrowser on ItemClick
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = adapter.getItem(position);
                Uri newseUri = Uri.parse(currentNews.getWebUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newseUri);
                startActivity(websiteIntent);
            }
        });

        loadNews();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                restartBoolean = true;
                loadNews();
            }
        });
    }

    private void loadNews() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        LoaderManager loaderManager = getLoaderManager();

        if (networkInfo != null && networkInfo.isConnected()) {
            int NEWS_LOADER_ID = 1;
            if (!restartBoolean) {
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            } else {
                //restarting the loader!
                getLoaderManager().destroyLoader(NEWS_LOADER_ID);
                loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            }
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        int index = Integer.parseInt(prefs.getString(getString(R.string.key_news_category), null));

        //additional parameters
        if (index != 0) {
            uriBuilder.appendPath(getResources().getStringArray(R.array.pref_category_options)[index].toLowerCase());
        } else {
            uriBuilder.appendPath(getString(R.string.search));
        }
        uriBuilder.appendQueryParameter("api-key", getString(R.string.api_key));
        Log.d("MainActivity", uriBuilder.toString());
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        findViewById(R.id.loading_indicator).setVisibility(View.GONE);

        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
            adapter.notifyDataSetChanged();
        } else {
            mEmptyStateTextView.setText(R.string.no_news_found);
        }

        swipeRefreshLayout.setRefreshing(false);
        restartBoolean = false;
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
