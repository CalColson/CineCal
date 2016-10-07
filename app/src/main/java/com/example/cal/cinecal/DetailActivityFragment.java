package com.example.cal.cinecal;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String TITLE_KEY = "title";
        final String OVERVIEW_KEY = "overview";
        final String RELEASE_DATE_KEY = "date";
        final String POSTER_KEY = "poster";

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(TITLE_KEY);
            String overview = intent.getStringExtra(OVERVIEW_KEY);
            String date = intent.getStringExtra(RELEASE_DATE_KEY);
            String posterPath = intent.getStringExtra(POSTER_KEY);

            TextView titleText = (TextView) rootView.findViewById(R.id.title_text);
            titleText.setText(title);
            TextView overviewText = (TextView) rootView.findViewById(R.id.overview_text);
            overviewText.setText(overview);
            TextView dateText = (TextView) rootView.findViewById(R.id.date_text);
            dateText.setText(date);

            ImageView posterImage = (ImageView) rootView.findViewById(R.id.poster_image);
            Picasso.with(getContext()).load(posterPath).into(posterImage);
        }

        return rootView;
    }
}
