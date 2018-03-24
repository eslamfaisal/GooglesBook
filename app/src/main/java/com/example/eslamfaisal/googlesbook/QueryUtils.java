package com.example.eslamfaisal.googlesbook;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public QueryUtils() {
    }

    public static List<Books> getBooksData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem with HTTP request ", e);
        }
        List<Books> books = extractDatafromJson(jsonResponse);

        return books;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
            Log.e(LOG_TAG, url.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Books> extractDatafromJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Books> books = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            if (root == null) {
                return books;
            }
            JSONArray items;
            if (root.has("items")) {
                items = root.getJSONArray("items");
            } else {
                return books;
            }

            for (int i = 0; i < items.length(); i++) {
                JSONObject currentObject = items.getJSONObject(i);
                JSONObject volumeInfo = currentObject.getJSONObject("volumeInfo");
                String title = "no title";
                if (volumeInfo.has("title")) {
                    title = volumeInfo.getString("title");
                }

                String jAuthor = "no authors";
                if (volumeInfo.has("authors")) {
                    JSONArray authorArray = volumeInfo.getJSONArray("authors");
                    jAuthor = authorArray.getString(0);
                }
                String imageUrl = "https://upload.wikimedia.org/wikipedia/commons/6/6c/No_image_3x4.svg";
                if (volumeInfo.has("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    if (imageLinks.has("thumbnail")) {
                        imageUrl = imageLinks.getString("thumbnail");
                    }
                }
                String url = "";
                if (volumeInfo.has("previewLink")) {
                    url = volumeInfo.getString("previewLink");
                }
                Uri bookUri = Uri.parse(url);
                Books books1 = new Books(title, jAuthor, bookUri, imageUrl);
                books.add(books1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }
}
