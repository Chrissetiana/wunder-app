package com.chrissetiana.wunderapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    ListView listDetails;
    DetailAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cars);

        Intent intent = getIntent();

        listDetails = findViewById(R.id.list_details);
        adapter = new DetailAdapter(this, new ArrayList<CarActivity>());
        listDetails.setAdapter(adapter);
    }
}
