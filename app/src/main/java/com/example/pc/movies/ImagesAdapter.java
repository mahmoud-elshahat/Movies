package com.example.pc.movies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.RecyclerHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<Movie>movies;
    MovieListener movieListener;
    public void setMovies(ArrayList<Movie>movies)
    {
        this.movies=movies;
    }

    public ImagesAdapter(Context context,MovieListener movieListener) {
        this.context = context;
        inflater=LayoutInflater.from(context);
        this.movieListener=movieListener;
    }


    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view=inflater.inflate(R.layout.rcycler_item,null);
        RecyclerHolder recyclerHolder=new RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerHolder holder, final int position) {

        holder.networkImageView.setVisibility(View.INVISIBLE);
        String IMAGE_URL="http://image.tmdb.org/t/p/w342//" + movies.get(position).poster_path;
        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        holder.networkImageView.setImageUrl(IMAGE_URL, mImageLoader);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    movieListener.setMovie(movies.get(position));
                }catch (NullPointerException n)
                {

                }

            }
        });


        holder.networkImageView.setVisibility(View.VISIBLE);

    }


    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        ProgressBar progressDialog;
        NetworkImageView networkImageView;
        public RecyclerHolder(View itemView) {
            super(itemView);
            networkImageView=(NetworkImageView)itemView.findViewById(R.id.image);

        }
    }

}

