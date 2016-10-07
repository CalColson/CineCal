package com.example.cal.cinecal;

public class MovieInfo {
    String posterPath;
    String title;
    String overview;
    String releaseDate;

    public MovieInfo(String posterPath, String title, String overview, String releaseDate) {
        this.posterPath = "http://image.tmdb.org/t/p/w500" + posterPath;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }
}
