package com.example.covidsetu;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

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
        holder.center_state.setText(data.get(position).getState_name());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
