package com.chrissetiana.wunderapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CarAdapter extends ArrayAdapter<CarActivity> {

    CarAdapter(Activity context, ArrayList<CarActivity> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        CarActivity current = getItem(position);
        assert current != null;

        TextView name = view.findViewById(android.R.id.text1);
        name.setText(current.getName());

        TextView address = view.findViewById(android.R.id.text2);
        address.setText(current.getAddress());

        return view;
    }
}
