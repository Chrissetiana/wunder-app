package com.chrissetiana.wunderapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<CarActivity>>, CarAdapter.ListItemClickListener {

    private static final String SOURCE = "https://s3-us-west-2.amazonaws.com/wunderbucket/locations.json";
    private static final String KEY = "query";
    private static final int LOADER_ID = 1;
    MapActivity map;
    CarAdapter adapter;
    RecyclerView listCars;
    TextView textEmpty;
    View progressBar;
    List<CarActivity> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listCars = findViewById(R.id.list_cars);
        textEmpty = findViewById(R.id.text_empty);
        progressBar = findViewById(R.id.progress_bar);
        map = new MapActivity();
        adapter = new CarAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listCars.setLayoutManager(layoutManager);
        listCars.setHasFixedSize(false);
        listCars.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(listCars.getContext(), layoutManager.getOrientation());
        listCars.addItemDecoration(divider);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            loadQuery();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText(getString(R.string.no_conn));
        }
    }

    private void loadQuery() {
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
    public Loader<List<CarActivity>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<CarActivity>>(this) {
            @Override
            protected void onStartLoading() {
                if (bundle == null) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public List<CarActivity> loadInBackground() {
                String source = bundle.getString(KEY);

                if (TextUtils.isEmpty(source)) {
                    return null;
                }

                return CarQuery.fetchData(source);
            }
        };
    }

    @SuppressLint("ResourceType")
    @Override
    public void onLoadFinished(Loader<List<CarActivity>> loader, List<CarActivity> data) {
        progressBar.setVisibility(View.INVISIBLE);

        if (data == null) {
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText(getString(R.string.no_data));
            findViewById(R.menu.main).setEnabled(false);
        } else {
            textEmpty.setVisibility(View.INVISIBLE);
            adapter.setData(data);
            adapter.notifyDataSetChanged();
//            map.setData(data);
            cars = data;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CarActivity>> loader) {
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_main:
                Bundle bundle = new Bundle();
                bundle.putSerializable("ArrayList", (Serializable) cars);

                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                intent.putExtra("Bundle", bundle);
                startActivity(intent);

                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
