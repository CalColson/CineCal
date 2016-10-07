package com.example.cal.cinecal;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends ArrayAdapter<MovieInfo> {

    public MovieAdapter(Activity context, List<MovieInfo> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieInfo movie = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie,
                    parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_movie_imageview);
        Picasso.with(getContext()).load(movie.posterPath).into(imageView);

        return convertView;
    }
}
