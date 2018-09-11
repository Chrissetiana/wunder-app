package com.chrissetiana.wunderapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private final ListItemClickListener listener;
    private List<CarActivity> cars;

    CarAdapter(ListItemClickListener clickListener) {
        cars = new ArrayList<>();
        listener = clickListener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.activity_detail, parent, false);

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

    public List<CarActivity> getData() {
        return cars;
    }

    public void setData(List<CarActivity> data) {
        this.cars = data;
    }

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    class CarViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        TextView name;
        TextView vin;
        TextView engine;
        TextView fuel;
        TextView exterior;
        TextView interior;
        TextView address;
        TextView coordinates;

        CarViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.car_name);
            vin = view.findViewById(R.id.car_vin);
            engine = view.findViewById(R.id.car_engine);
            fuel = view.findViewById(R.id.car_fuel);
            exterior = view.findViewById(R.id.car_exterior);
            interior = view.findViewById(R.id.car_interior);
            address = view.findViewById(R.id.car_address);
            coordinates = view.findViewById(R.id.car_coordinates);
            view.setOnClickListener(this);
        }

        void bind(CarActivity position) {
            name.setText(position.getName());
            vin.setText(position.getVin());
            engine.setText(position.getEngine());
            fuel.setText(position.getFuel());
            exterior.setText(position.getExterior());
            interior.setText(position.getInterior());
            address.setText(position.getAddress());
//            coordinates.setText(position.getCoordinates());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            listener.onListItemClick(position);
        }
    }
}
