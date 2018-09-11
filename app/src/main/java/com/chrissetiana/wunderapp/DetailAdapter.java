package com.chrissetiana.wunderapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailAdapter extends ArrayAdapter<CarActivity> {

    DetailAdapter(Activity context, ArrayList<CarActivity> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.activity_detail, parent, false);
        }

        CarActivity current = getItem(position);
        assert current != null;

        TextView name = view.findViewById(R.id.car_name);
        name.setText(current.getName());

        Log.d("DetailAdapter", "name: " + current.getName());

        return view;
    }
}
