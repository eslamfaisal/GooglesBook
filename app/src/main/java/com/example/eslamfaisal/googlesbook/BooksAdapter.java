package com.example.eslamfaisal.googlesbook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BooksAdapter extends ArrayAdapter<Books> {
    public BooksAdapter(Context context, ArrayList<Books> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rootView = convertView;
        if (convertView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }

        final Books currentBook = getItem(position);

        TextView titel = (TextView) rootView.findViewById(R.id.book_title);
        titel.setText(currentBook.getTitle());

        TextView author = (TextView) rootView.findViewById(R.id.book_author);
        author.setText(currentBook.getAuthors());

        ImageView bookImage = (ImageView) rootView.findViewById(R.id.book_image);
        String imgUri = currentBook.getBookImage();
        Picasso.with(getContext()).load(imgUri)
                .into(bookImage);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri bookUri = currentBook.getUri();
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                view.getContext().startActivity(websiteIntent);
            }
        });
        return rootView;
    }
}
