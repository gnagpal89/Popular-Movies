package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.api.Client;
import com.example.android.popularmovies.api.Service;
import com.example.android.popularmovies.model.Trailer;
import com.example.android.popularmovies.model.TrailerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity{
    TextView mMovieName, mSynopsis, mUserRating, mReleaseDate;
    ImageView mPosterImageView;

    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();

        mPosterImageView = findViewById(R.id.thumbnail_image_header);
        mMovieName = findViewById(R.id.title);
        mSynopsis = findViewById(R.id.plotsynopsis);
        mUserRating = findViewById(R.id.userrating);
        mReleaseDate = findViewById(R.id.releasedate);

        Intent intent = getIntent();
        if(intent.hasExtra("original_title")){
            String thumbnail = intent.getExtras().getString("poster_path");
            String movieName = intent.getExtras().getString("original_title");
            String synopsis = intent.getExtras().getString("overview");
            String rating = intent.getExtras().getString("vote_average");
            String dateOfRelease = intent.getExtras().getString("release_date");

            Glide.with(this)
                    .load(thumbnail)
                    .into(mPosterImageView);

            mMovieName.setText(movieName);
            mSynopsis.setText(synopsis);
            mUserRating.setText(rating);
            mReleaseDate.setText(dateOfRelease);

        } else {
            Toast.makeText(this, "No API data", Toast.LENGTH_SHORT);
        }

        initViews();
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange+ verticalOffset==0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                } else if (isShow){
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initViews(){
        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailerList);

        mRecyclerView = findViewById(R.id.recycler_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(trailerAdapter);
        trailerAdapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON(){
        int movie_id = getIntent().getExtras().getInt("id");
        try{
            if(BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please get the API key", Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service apiService = client.getClient().create(Service.class);

            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailers = response.body().getResults();
                    mRecyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailers));
                    mRecyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(DetailActivity.this, "Error fetching trailer data", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}
