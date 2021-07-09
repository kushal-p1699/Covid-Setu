package com.example.covidsetu;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    ArrayList<SessionModel> data;

    public MyAdapter(ArrayList<SessionModel> data) {
        this.data = data;
        Log.d("VACCINATION", "data in adapter : "+ data.toString());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.session_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d("VACCINATION", "data in adapter : "+ data.toString());
        holder.center_name.setText(data.get(position).getName());
        holder.center_address.setText(data.get(position).getDetailAddress());
        holder.vaccine_name_and_age.setText(data.get(position).getVaccineNameAndAge());

        // if cost is free, then display it in green color
        if(data.get(position).getFee_type().equalsIgnoreCase("Free")){
            holder.vaccine_cost_img.setImageResource(R.color.green);
            holder.vaccine_cost.setText("FREE");

        }else{
            holder.vaccine_cost_img.setImageResource(R.color.red_paid);
//            holder.vaccine_cost.setText("Rs. "+data.get(position).getFee()+"/-");
            holder.vaccine_cost.setText("PAID");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
