package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.model.Film;

import java.util.List;

class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{
    private Context mContext;
    private List<Film> movieList;

    public MovieAdapter(Context context, List<Film> movieList) {
        mContext = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_card, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        holder.mTitleTextView.setText(movieList.get(position).getOriginalTitle());

        String vote = Double.toString(movieList.get(position).getVoteAverage());
        holder.mUserRatingTextView.setText(vote);

        Glide.with(mContext)
                .load(movieList.get(position).getPosterPath())
                .into(holder.mThumbNail);
    }

    @Override
    public int getItemCount() {
        if(movieList == null)
            return 0;
        return movieList.size();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder{
        private ImageView mThumbNail;
        private TextView mTitleTextView, mUserRatingTextView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title);
            mUserRatingTextView = itemView.findViewById(R.id.userrating);
            mThumbNail = itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Film selectedMovie = null;
                    if(pos!=RecyclerView.NO_POSITION){


                        selectedMovie = movieList.get(pos);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("original_title", selectedMovie.getOriginalTitle());
                        intent.putExtra("poster_path", selectedMovie.getPosterPath());
                        intent.putExtra("overview", selectedMovie.getOverview());
                        intent.putExtra("vote_average", selectedMovie.getVoteAverage());
                        intent.putExtra("release_date", selectedMovie.getReleaseDate());
                        intent.putExtra("id", selectedMovie.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked "+selectedMovie.getOriginalTitle(), Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }
}
