package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.api.Client;
import com.example.android.popularmovies.api.Service;
import com.example.android.popularmovies.model.MoviesResponse;
import com.example.android.popularmovies.model.Film;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private RecyclerView mRecyclerView;

    private MovieAdapter adapter;

    private List<Film> movieList;

    private SwipeRefreshLayout swipeContainer;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private static int columnWidth = 120;

    public static final String LOG_TAG = MovieAdapter.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        swipeContainer = findViewById(R.id.main_content);

        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this, "movies refreshed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        mRecyclerView = findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        checkSortOrder();
    }

    private void loadJSON() {
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(getApplicationContext(), "Get API key from themoviedb.org", Toast.LENGTH_SHORT);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                return;
            }

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);

            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Film> movies = response.body().getResults();
                    mRecyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    mRecyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void loadJSON1() {
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(getApplicationContext(), "Get API key from themoviedb.org", Toast.LENGTH_SHORT);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                return;
            }

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);

            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Film> movies = response.body().getResults();
                    mRecyclerView.setAdapter(new MovieAdapter(getApplicationContext(), movies));
                    mRecyclerView.smoothScrollToPosition(0);
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private Activity getActivity() {
        Context context = this;
        while(context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(LOG_TAG, "Preferences updated");
        checkSortOrder();
    }

    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );

        if(sortOrder.equals(this.getString(R.string.pref_most_popular))){
            Log.d(LOG_TAG, "Sorting by most popular");
            loadJSON();
        } else {
            Log.d(LOG_TAG, "Sorting by rating");
            loadJSON1();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(movieList.isEmpty()){
            checkSortOrder();
        }
    }

    //    private int spanCount() {
//        return (int) Math.floor(mRecyclerView.getWidth()/convertDPToPixel(columnWidth));
//    }
//
//    private float convertDPToPixel(int dp) {
//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        float logicalDensity = metrics.density;
//        return (int) (dp * logicalDensity);
//    }
//

}
