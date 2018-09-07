package com.chrissetiana.wunderapp;

import java.util.ArrayList;

class CarActivity extends ArrayList<CarActivity> {
    private String name;
    private String address;

    CarActivity(String name, String address) {
        setName(name);
        setAddress(address);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    private void setAddress(String address) {
        this.address = address;
    }
}
