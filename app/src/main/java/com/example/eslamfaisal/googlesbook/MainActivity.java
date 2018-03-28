package com.example.eslamfaisal.googlesbook;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {
    private static final int BOOKS_LOADER_ID = 1;
    private static final String STATE_LIST = "stateList";
    private static final String SCROLL_POSITION = "Scroll Position";
    private boolean running = false;
    private String URL_RESPONSE;
    private TextView mEmptyStateTextView;
    private BooksAdapter adapter;
    private String value;
    private List<Books> books_list;
    private View progressBar;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        listView = (ListView) findViewById(R.id.list);
        adapter = new BooksAdapter(this, new ArrayList<Books>());
        listView.setAdapter(adapter);
        mEmptyStateTextView = (TextView) findViewById(R.id.emptyElement);
        listView.setEmptyView(mEmptyStateTextView);
        mEmptyStateTextView.setText(R.string.enter_name);
        progressBar = findViewById(R.id.loading_indicator);
        progressBar.setVisibility(View.GONE);
    }

    public void Search(String value) {

        adapter.clear();
        mEmptyStateTextView.setText("");

            try {
                URL_RESPONSE = "https://www.googleapis.com/books/v1/volumes?q=" + java.net.URLEncoder.encode(value, "UTF-8") + "&maxResults=10";
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

                if (isConnected) {
                    progressBar.setVisibility(View.VISIBLE);
                    LoaderManager loaderManager = getLoaderManager();
                    if (!running) {
                        running = true;
                        loaderManager.initLoader(BOOKS_LOADER_ID, null, this);
                    } else {
                        loaderManager.restartLoader(BOOKS_LOADER_ID, null, this);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_connection);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int i, Bundle bundle) {
        return new BooksLoader(this, URL_RESPONSE);
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> books) {
        books_list = books;
        adapter.clear();
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        } else {
            mEmptyStateTextView.setText(R.string.no_book);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        adapter.clear();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("saveInstance", value);
        outState.putParcelableArrayList(STATE_LIST, (ArrayList<Books>) books_list);
        outState.putInt(SCROLL_POSITION, listView.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        value = savedInstanceState.getString("saveInstance");
        books_list = savedInstanceState.getParcelableArrayList(STATE_LIST);
        int position = savedInstanceState.getInt(SCROLL_POSITION);
        if (books_list != null && !books_list.isEmpty()) {
            adapter.addAll(books_list);
            listView.setSelection(position);
        } else {
            mEmptyStateTextView.setText(R.string.no_book);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater  = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    Search(query);
                    searchView.clearFocus();
                    searchView.setQuery("", false);
                    searchView.setIconified(true);
                    searchItem.collapseActionView();
                    MainActivity.this.setTitle(query);

                    return true;
                }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }
}
