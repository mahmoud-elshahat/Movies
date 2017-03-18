package com.example.pc.movies;

import android.content.ClipData;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by PC on 10/21/2016.
 */

public class ReviewsListAdapter extends BaseAdapter {
    String[] authors;
    String[] content;
    Context context;
    LayoutInflater inflater;

    public ReviewsListAdapter(Context context, String[] authors, String[] content) {
        this.authors = authors;
        this.content = content;
        this.content = content;
        for (int i = 0; i < content.length; i++) {
            Log.v("arrays", content[i]);
            Log.v("arrays", authors[i]);
        }
        if (context != null) {
            inflater = LayoutInflater.from(context);
        }
    }

    @Override
    public int getCount() {
        return content.length;
    }

    @Override
    public Object getItem(int i) {
        return authors[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View root = view;
        if (view == null) {
            root = inflater.inflate(R.layout.review_list_item, null);
        }
        TextView review = (TextView) root.findViewById(R.id.content);
        TextView author = (TextView) root.findViewById(R.id.author);
        review.setText(authors[i]);
        author.setText(content[i]);
        return root;
    }
}
