package com.chrissetiana.wunderapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<String> {

    private static final String SOURCE = "https://s3-us-west-2.amazonaws.com/wunderbucket/locations.json";
    private static final String KEY = "query";
    private static final int LOADER_ID = 1;
    CarAdapter adapter;
    ListView listCars;
    TextView textEmpty;
    View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listCars = findViewById(R.id.list_cars);
        textEmpty = findViewById(R.id.text_empty);
        progressBar = findViewById(R.id.progress_bar);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loadQuery();
        } else {
            listCars.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            textEmpty.setText(getString(R.string.no_conn));
        }
    }

    private void loadQuery() {
        listCars.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString(KEY, SOURCE);

        LoaderManager loaderManager = getLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADER_ID);

        if (loader == null) {
            loaderManager.initLoader(LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, bundle, this);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<String> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (bundle == null) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String source = bundle.getString(KEY);
                if (TextUtils.isEmpty(source)) {
                    return null;
                }

                return CarQuery.fetchData(source);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressBar.setVisibility(View.INVISIBLE);

        if (data == null) {
            listCars.setVisibility(View.INVISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText(getString(R.string.no_data));
        } else {
            listCars.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.INVISIBLE);
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }
}
