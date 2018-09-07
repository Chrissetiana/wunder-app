package com.chrissetiana.wunderapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<CarActivity> cars;

    CarAdapter() {
        cars = new ArrayList<>();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);

        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarActivity car = cars.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return cars.size();
    }

    public void setData(List<CarActivity> data) {
        this.cars = data;
    }

    class CarViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;

        CarViewHolder(View view) {
            super(view);
            name = view.findViewById(android.R.id.text1);
            address = view.findViewById(android.R.id.text2);
        }

        void bind(CarActivity position) {
            name.setText(position.getName());
            address.setText(position.getAddress());
        }
    }
}
