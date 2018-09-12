package com.chrissetiana.wunderapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isPlayAvailable()) {
            setContentView(R.layout.activity_map);
            loadMap();
        }
    }

    public boolean isPlayAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int available = api.isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(available)) {
            Dialog dialog = api.getErrorDialog(this, available, 15);
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
        map.getUiSettings().setZoomControlsEnabled(true);

        // goToLocation(53.551086, 9.993682, 15);

        myLocation();
    }

    private void goToLocation(double lat, double lon, float zoom) {
        LatLng loc = new LatLng(lat, lon);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, zoom);
        map.moveCamera(cameraUpdate);
    }

    private void myLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
    }
}
