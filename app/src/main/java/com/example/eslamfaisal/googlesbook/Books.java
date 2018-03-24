package com.example.eslamfaisal.googlesbook;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Books implements Parcelable {
    Parcelable.Creator CREATOR;
    private String title;
    private String authors;
    private String bookImage;
    private Uri uri;

    public Books(String title, String authors, Uri uri, String bookImage) {
        this.title = title;
        this.authors = authors;
        this.bookImage = bookImage;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getBookImage() {
        return bookImage;
    }

    public Uri getUri() {
        return uri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
