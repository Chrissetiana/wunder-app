package com.chrissetiana.wunderapp;

import java.util.ArrayList;

class CarActivity extends ArrayList<CarActivity> {

    private String name;
    private String vin;
    private String engine;
    private String fuel;
    private String exterior;
    private String interior;
    private String address;
    private double lat;
    private double lon;

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
}