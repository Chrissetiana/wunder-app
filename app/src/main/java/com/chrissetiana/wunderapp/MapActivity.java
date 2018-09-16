package com.chrissetiana.wunderapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "MapActivity";
    ArrayList<CarActivity> cars;
    ArrayList<Marker> marks = new ArrayList<>(); // arraylist of car markers
    Marker locationMarker; // location locationMarker
    Marker carMarker; // car locationMarker
    GoogleMap map;
    LocationRequest locationRequest;
    Location lastLocation;
    FusedLocationProviderClient fusedProviderClient;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> list = locationResult.getLocations();

            if (list.size() > 0) {
                lastLocation = list.get(list.size() - 1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        cars = intent.getExtras().getParcelableArrayList("Cars");

        if (isPlayAvailable()) {
            setContentView(R.layout.activity_map);
            fusedProviderClient = LocationServices.getFusedLocationProviderClient(this);
            MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_cars);
            mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (map != null) {
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    double lat = lastLocation.getLatitude();
                    double lon = lastLocation.getLongitude();

                    if (locationMarker != null) {
                        locationMarker.remove();
                    }

                    LatLng loc = new LatLng(lat, lon);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 10));

                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon)) // 9.1329843,7.3530768, Abuja
                            .title("Current Location")
                            .snippet("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                    Log.d("MapActivity", "carMarker created for current " + locationMarker.getPosition());

                    return true;
                }
            });
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    return false;
                }
            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View view = getLayoutInflater().inflate(R.layout.activity_info, null);

                    TextView carName = view.findViewById(R.id.info_name);
                    carName.setText(marker.getTitle());

                    TextView carAddress = view.findViewById(R.id.info_address);
                    carAddress.setText(marker.getSnippet());

                    LatLng loc = marker.getPosition();
                    TextView carCoordinates = view.findViewById(R.id.info_coordinates);
                    String location = loc.latitude + ", " + loc.longitude;
                    carCoordinates.setText(location);

                    return view;
                }
            });

            locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
                return;
            } else {
                map.setMyLocationEnabled(true);
                fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }

        loadCarMarkers();
    }

    private void loadCarMarkers() {
        for (CarActivity car : cars) {
            carMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(car.getLat(), car.getLon()))
                    .title(car.getName())
                    .snippet(car.getAddress()));
            marks.add(carMarker);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker m : marks) {
            // LatLng loc = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
            map.addMarker(new MarkerOptions()
                    .position(m.getPosition())
                    .title(m.getTitle())
                    .snippet(m.getSnippet()));
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        Log.d("MapActivity", "Created " + marks.size() + " marks.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.map_type_none:
                map.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.map_type_normal:
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.map_type_satellite:
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.map_type_hybrid:
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.map_type_terrain:
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        map.setMyLocationEnabled(true);
                        fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (fusedProviderClient != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                checkLocationPermission();
            } else {
                fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (fusedProviderClient != null) {
            fusedProviderClient.removeLocationUpdates(locationCallback);
        }
    }
}
