package com.chrissetiana.wunderapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Checkable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

class CarActivity extends ArrayList<CarActivity> implements Parcelable, ClusterItem, Checkable {

    public static final Creator<CarActivity> CREATOR = new Creator<CarActivity>() {
        @Override
        public CarActivity createFromParcel(Parcel parcel) {
            return new CarActivity(parcel);
        }

        @Override
        public CarActivity[] newArray(int size) {
            return new CarActivity[size];
        }
    };
    private String name;
    private String vin;
    private String engine;
    private String fuel;
    private String exterior;
    private String interior;
    private String address;
    private double lat;
    private double lon;
    private LatLng loc;

    CarActivity(String name, String vin, String engine, String fuel, String exterior, String interior, String address, double lat, double lon) {
        setName(name);
        setVin(vin);
        setEngine(engine);
        setFuel(fuel);
        setExterior(exterior);
        setInterior(interior);
        setAddress(address);
        setLat(lat);
        setLon(lon);
    }

    CarActivity(LatLng loc, String title, String snippet) {
        setLoc(loc);
        setName(title);
        setAddress(snippet);
    }

    private CarActivity(Parcel parcel) {
        lat = parcel.readDouble();
        lon = parcel.readDouble();
        name = parcel.readString();
        address = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(name);
        dest.writeString(address);
    }

    @Override
    public LatLng getPosition() {
        return loc;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return address;
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    @Override
    public void setChecked(boolean checked) {

    }

    @Override
    public void toggle() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVin() {
        return vin;
    }

    private void setVin(String vin) {
        this.vin = vin;
    }

    public String getEngine() {
        return engine;
    }

    private void setEngine(String engine) {
        this.engine = engine;
    }

    public String getFuel() {
        return fuel;
    }

    private void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getExterior() {
        return exterior;
    }

    private void setExterior(String exterior) {
        this.exterior = exterior;
    }

    public String getInterior() {
        return interior;
    }

    private void setInterior(String interior) {
        this.interior = interior;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    private void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    private void setLon(double lon) {
        this.lon = lon;
    }

    public LatLng getLoc() {
        return loc;
    }

    private void setLoc(LatLng location) {
        this.loc = location;
    }
}