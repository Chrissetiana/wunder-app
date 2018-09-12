package com.chrissetiana.wunderapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    LocationManager locator;
    Marker userMarker;
    String name;
    String address;
    Double lat;
    Double lon;

    public MapActivity() {

    }

    public MapActivity(String name, double lat, double lon, String address) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (isPlayAvailable()) {
            setContentView(R.layout.activity_map);
            loadMap();
        }

        myLocation();
    }

    public boolean isPlayAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int available = api.isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(available)) {
            Dialog dialog = api.getErrorDialog(this, available, 10);
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

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setBuildingsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);

        map.addMarker(loadMarker(lat, lon, name, address));

        LatLng loc = new LatLng(53.551086, 9.993682);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(loc, 15);
        map.moveCamera(cameraUpdate);
    }

    private MarkerOptions loadMarker(Double lat, Double lon, String name, String desc) {
        LatLng loc = new LatLng(lat, lon);
        return new MarkerOptions().position(loc).title(name).snippet(desc);
    }

    private void myLocation() {
        locator = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locator.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        double myLat = location.getLatitude();
        double myLon = location.getLongitude();
        LatLng myLoc = new LatLng(myLat, myLon);

        if (userMarker != null) {
            userMarker.remove();
        }

        userMarker = map.addMarker(loadMarker(myLat, myLon, "You are here", "Last known location"));
        // map.animateCamera(CameraUpdateFactory.newLatLng(myLoc), 3000, null);
    }
}
