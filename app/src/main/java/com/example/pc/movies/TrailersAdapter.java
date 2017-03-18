package com.example.pc.movies;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by PC on 10/23/2016.
 */

public class TrailersAdapter extends BaseAdapter {

    ArrayList<String>ImagesPath;
    ArrayList<String>TrailersNames;

    Context context;
    LayoutInflater inflater;

    public TrailersAdapter(Context context,  ArrayList<String>ImagesPath,ArrayList<String>TrailersNames)
    {
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.ImagesPath= ImagesPath;
        this.TrailersNames=TrailersNames;
    }

    @Override
    public int getCount() {
        return ImagesPath.size();
    }

    @Override
    public Object getItem(int i) {
        return ImagesPath.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root=view;
        if(view==null)
        {
            root=inflater.inflate(R.layout.video_list_item,null);
        }
        NetworkImageView networkImageView= (NetworkImageView) root.findViewById(R.id.trailerThumbnail);

        ImageLoader mImageLoader;
        mImageLoader = MySingleton.getInstance(context).getImageLoader();
        networkImageView.setImageUrl(ImagesPath.get(i), mImageLoader);

        TextView textView=(TextView)root.findViewById(R.id.trailerName);
        textView.setText(TrailersNames.get(i));

        return root;
    }
}
