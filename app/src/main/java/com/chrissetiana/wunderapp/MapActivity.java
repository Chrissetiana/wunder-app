package com.chrissetiana.wunderapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
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
import android.util.DisplayMetrics;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ArrayList<CarActivity> cars;
    private ArrayList<Marker> marks = new ArrayList<>();
    private Marker locationMarker;
    private Marker selectedMarker = null;
    private GoogleMap map;
    private ClusterManager<CarActivity> clusterManager;
    private CarRenderer carRenderer;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private FusedLocationProviderClient fusedProviderClient;
    private LocationCallback locationCallback = new LocationCallback() {
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
            initLocation();
            initCluster();

            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setAllGesturesEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(false);
            map.setOnMapClickListener(latLng -> setItemChecked(null));
            /*map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    selectedMarker = null;
                }
            });*/
            map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    double lat = lastLocation.getLatitude();
                    double lon = lastLocation.getLongitude();

                    if (locationMarker != null) {
                        locationMarker.remove();
                    }

                    LatLng loc = new LatLng(lat, lon);
                    locationMarker = map.addMarker(new MarkerOptions()
                            .position(loc)
                            .title("Current Location")
                            .snippet("You are here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 5));
                    Log.d("MapActivity", "carMarker created for current " + locationMarker.getPosition());
                    return true;
                }
            });
        }
    }

    private void setItemChecked(CarActivity carActivity) {
        CarRenderer carRenderer = (CarRenderer) clusterManager.getRenderer();
        for (CarActivity car : clusterManager.getAlgorithm().getItems()) {
            car.setChecked(false);

            if (carActivity != null && car.equals(carActivity)) {
                carActivity.setChecked(true);
            }
            carRenderer.setUpdateMarker(car);
        }
    }

    private void initLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            map.setMyLocationEnabled(true);
            fusedProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }
    }

    private void initCluster() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        clusterManager = new ClusterManager<>(this, map);
        clusterManager.setAlgorithm(new NonHierarchicalDistanceBasedAlgorithm<>());

        carRenderer = new CarRenderer(this, map, clusterManager);
        clusterManager.setRenderer(carRenderer);

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(carRenderer);
        clusterManager.setOnClusterInfoWindowClickListener(carRenderer);
        clusterManager.setOnClusterItemClickListener(carRenderer);
        clusterManager.setOnClusterItemInfoWindowClickListener(carRenderer);

        clusterManager.getMarkerCollection().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d("MapActivity", marker.getTitle() + " marker clicked from cluster section");
                if (marker.equals(selectedMarker)) {
                    selectedMarker = null;

                    for (Marker m : marks) {
                        m.setVisible(true);
                    }

                    return true;
                } else {
                    selectedMarker = marker;

                    for (Marker m : marks) {
                        if (!m.equals(selectedMarker)) {
                            m.setVisible(false);
                        }
                    }

                    return false;
                }
            }
        });
        clusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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

//        clusterManager.clearItems();
        addItems();
        clusterManager.cluster();
    }

    private void addItems() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (CarActivity car : cars) {
            double lat = car.getLat();
            double lon = car.getLon();
            LatLng loc = new LatLng(lat, lon);
            String name = car.getName();
            String address = car.getAddress();

            CarActivity items = new CarActivity(loc, name, address);
            Log.d("MapActivity", "car: " + items.size());
//            clusterManager.removeItem(item);
            clusterManager.addItem(items);
            builder.include(loc);
        }

        clusterManager.cluster();

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.40);

        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
//        map.moveCamera((CameraUpdateFactory.newLatLngBounds(bounds, 1)));
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

    class CarRenderer extends DefaultClusterRenderer<CarActivity> implements
            ClusterManager.OnClusterClickListener<CarActivity>,
            ClusterManager.OnClusterInfoWindowClickListener<CarActivity>,
            ClusterManager.OnClusterItemClickListener<CarActivity>,
            ClusterManager.OnClusterItemInfoWindowClickListener<CarActivity> {

        private CarRenderer(Context context, GoogleMap map, ClusterManager<CarActivity> clusterManager) {
            super(context, map, clusterManager);
            setOnClusterClickListener(this);
            setOnClusterInfoWindowClickListener(this);
            setOnClusterItemClickListener(this);
            setOnClusterItemInfoWindowClickListener(this);
        }

        void setUpdateMarker(CarActivity carActivity) {
            Marker marker = getMarker(carActivity);

            if (marker != null) {
                Log.d("MapActivity", "marker is null!");
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<CarActivity> cluster) {
            boolean isit = cluster.getSize() > 1;
            Log.d("MapActivity", "Should render as cluster: " + isit + ", cluster size: " + cluster.getSize());
            return cluster.getSize() > 1;
        }

        @Override
        protected void onBeforeClusterItemRendered(CarActivity item, MarkerOptions markerOptions) {
            // for each marker
            markerOptions.title(item.getTitle());
            markerOptions.snippet(item.getSnippet());

            // copy map code for marker click here

            super.onBeforeClusterItemRendered(item, markerOptions);
            Log.d("MapActivity", "Before cluster item rendered");
        }

        @Override
        protected void onClusterItemRendered(CarActivity car, Marker marker) {
            // get map.onMarkerClick code and place it here
            super.onClusterItemRendered(car, marker);
            Log.d("MapActivity", "Cluster item rendered " + marker.getTitle());
        }

        @Override
        protected void onClusterRendered(Cluster<CarActivity> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
            Log.d("MapActivity", "Cluster rendered " + marker.getTitle());
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<CarActivity> cluster, MarkerOptions options) {
            // for each cluster
            super.onBeforeClusterRendered(cluster, options);
            Log.d("MapActivity", "Before cluster rendered; size " + cluster.getSize());
        }

        @Override
        public boolean onClusterClick(Cluster<CarActivity> cluster) {
            Log.d("MapActivity", " cluster click");

            LatLngBounds.Builder builder = LatLngBounds.builder();

            for (ClusterItem item : cluster.getItems()) {
                builder.include(item.getPosition());
            }

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);

            // map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1));
            return true;
        }

        @Override
        public void onClusterInfoWindowClick(Cluster<CarActivity> cluster) {
            Log.d("MapActivity", " cluster info window click");
        }

        @Override
        public boolean onClusterItemClick(CarActivity carActivity) {
            // this will not work because of the marker click meth inside cluster
            // when you click a marker
            Log.d("MapActivity", carActivity.getTitle() + " cluster item click");
            setItemChecked(carActivity);
            return true;
        }

        @Override
        public void onClusterItemInfoWindowClick(CarActivity carActivity) {
            // when you click a marker's info window
            Log.d("MapActivity", carActivity.getName() + " cluster item info window click");
        }
    }
}
