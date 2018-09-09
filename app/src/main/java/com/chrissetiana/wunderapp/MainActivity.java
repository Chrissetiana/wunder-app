package com.chrissetiana.wunderapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<CarActivity>>, CarAdapter.ListItemClickListener, OnMapReadyCallback {

    private static final String SOURCE = "https://s3-us-west-2.amazonaws.com/wunderbucket/locations.json";
    private static final String KEY = "query";
    private static final int LOADER_ID = 1;
    CarAdapter adapter;
    RecyclerView listCars;
    TextView textEmpty;
    View progressBar;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listCars = findViewById(R.id.list_cars);
        textEmpty = findViewById(R.id.text_empty);
        progressBar = findViewById(R.id.progress_bar);
        adapter = new CarAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listCars.setLayoutManager(layoutManager);
        listCars.setHasFixedSize(false);
        listCars.setAdapter(adapter);

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

    @Override
    public void onLoadFinished(Loader<List<CarActivity>> loader, List<CarActivity> data) {
        progressBar.setVisibility(View.INVISIBLE);

        if (data == null) {
            listCars.setVisibility(View.INVISIBLE);
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText(getString(R.string.no_data));
        } else {
            listCars.setVisibility(View.VISIBLE);
            textEmpty.setVisibility(View.INVISIBLE);
            adapter.setData(data);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<CarActivity>> loader) {
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_detail);
        dialog.show();
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
                if (isPlayAvailable()) {
                    setContentView(R.layout.activity_map);
                    loadMap();
                }
                finish();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isPlayAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int available = api.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(available)) {
            Dialog dialog = api.getErrorDialog(this, available, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void loadMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_cars);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        loadLocation(9.1329, 7.3875945, 0); // add the car location here
    }

    private void loadLocation(double lat, double lon, float zoom) {
        LatLng loc = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, zoom);
        map.moveCamera(cameraUpdate);
    }
}
