package com.schintha.nytimessearch.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.schintha.nytimessearch.R;
import com.schintha.nytimessearch.adapters.ArticleAdapter;
import com.schintha.nytimessearch.dialogs.FilterDialog;
import com.schintha.nytimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.schintha.nytimessearch.models.ArticleModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = SearchActivity.class.getName();
    private String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    @Bind(R.id.rvResults)
    RecyclerView rvResults;
    public Toolbar toolbar;

    public FragmentManager fm;
    public FilterDialog filterDialog;

    public ArticleAdapter articleAdapter;
    ArrayList<ArticleModel> articles;

    private String query;
    public String sortOrder = new String("");
    public String beginDate = new String("");
    public List<String> newsDesk;

    public AlertDialog networkFailDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Dialog Fragment
        fm = getSupportFragmentManager();
        filterDialog = FilterDialog.newInstance("Filter News Settings");

        newsDesk = new ArrayList<>();

        //Adapter and Data
        articles = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articles);

        //Set RecyclerView
        rvResults.setAdapter(articleAdapter);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        rvResults.setLayoutManager(gridLayoutManager);
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                //Log.i(TAG, "LOADPAGE " + page);
                customLoadDataFromApi(page);
                return true;
            }
        });

        //Network fail dialog
        networkFailDialog = new AlertDialog.Builder(this).setTitle("Network Error").setMessage("Unable to connect to Internet").create();
        if (!(isNetworkAvailable() && isOnline())) {
            networkFailDialog.show();
        }
    }

    public RequestParams getParams(int page) {
        RequestParams params = new RequestParams();
        params.put("api-key", getText(R.string.key));
        params.put("page", page);
        params.put("q", query);
        if (sortOrder.trim().length() > 0) {
            params.put("sort", sortOrder);
        }
        if (beginDate.trim().length() > 0) {
            params.put("begin_date", beginDate);
        }
        String desk = TextUtils.join(" ", newsDesk);
        if (desk.trim().length() > 0) {
            params.put("fq", "news_desk:(" + desk +")");
        }
        Log.i(TAG, params.toString());
        return params;
    }

    public void customLoadDataFromApi(int page) {
        Log.i(TAG, "Get Page " + page);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = getParams(page);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.i(TAG, response.toString());
                JSONArray articleJSONResults = null;
                try {
                    int currSize = articleAdapter.getItemCount();
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(ArticleModel.fromJSONArray(articleJSONResults));
                    articleAdapter.notifyItemRangeInserted(currSize, articles.size() - 1);
                    //Log.i(TAG, "LOADPAGE insert start " + currSize + " end " + articles.size());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                networkFailHandler(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                networkFailHandler(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                networkFailHandler(throwable);
            }
        });
    }

    public void fetchArticles(String query) {
        //Log.i(TAG, "Get Page " + 1);
        this.query = query;
        AsyncHttpClient client = new AsyncHttpClient();
        //Log.i(TAG, "LOADPAGE " + 1);
        RequestParams params = getParams(1);
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.i(TAG, response.toString());
                JSONArray articleJSONResults = null;
                try {
                    articleJSONResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.clear();
                    articles.addAll(ArticleModel.fromJSONArray(articleJSONResults));
                    Log.i(TAG, articles.toString());
                    articleAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                networkFailHandler(throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        //Filter
        MenuItem filterItem = (MenuItem) menu.findItem(R.id.mnuFilter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                filterDialog.show(fm, "Filter News Settings");
                return true;
            }
        });

        //Search View
        MenuItem searchItem = (MenuItem) menu.findItem(R.id.mnuSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.menu_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                fetchArticles(query);
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    private void networkFailHandler(Throwable throwable) {
        try {
            if (isNetworkAvailable() && isOnline()) {
                throwable.printStackTrace();
            }
            else {
                networkFailDialog.show();
            }
        }
        catch (Exception e) {e.printStackTrace();}

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)        { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
