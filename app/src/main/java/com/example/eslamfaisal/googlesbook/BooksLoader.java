package com.example.eslamfaisal.googlesbook;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;

public class BooksLoader extends AsyncTaskLoader<List<Books>> {
    private String url;

    public BooksLoader(Context context, String mUrl) {
        super(context);
        this.url = mUrl;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Books> loadInBackground() {
        if (url == null) {
            return null;
        }
        List<Books> books = QueryUtils.getBooksData(url);
        return books;
    }
}
