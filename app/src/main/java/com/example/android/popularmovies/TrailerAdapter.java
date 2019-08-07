package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.model.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder>{
    private Context mContext;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context mContext, List<Trailer> trailerList){
        this.mContext = mContext;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_card, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTitleTextView.setText(trailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTitleTextView;
        ImageView mThumbnailImageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.title);
            mThumbnailImageView = itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){

                        Trailer clickedTrailerItem = trailerList.get(pos);
                        String videoId = trailerList.get(pos).getKey();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+videoId));
                        intent.putExtra("VIDEO_ID", videoId);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked "+clickedTrailerItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
