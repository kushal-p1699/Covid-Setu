package com.example.covidsetu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    ArrayList<SessionModel> data;
    Context context;

    public MyAdapter(Context context, ArrayList<SessionModel> data) {
        this.data = data;
        this.context = context;
        Log.d("VACCINATION", "data in adapter : "+ data.toString());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.session_card, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d("VACCINATION", "data in adapter : "+ data.toString());
        holder.center_name.setText(data.get(position).getName());
        holder.center_address.setText(data.get(position).getDetailAddress());
        holder.vaccine_name_and_age.setText(data.get(position).getVaccineNameAndAge());
        holder.vaccine_location.setTooltipText("View Center Location");

        SetVaccineCostView(holder, position);

        SetVaccineLocation(holder, position);


    }

    private void SetVaccineLocation(MyViewHolder holder, int position) {
        holder.vaccine_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open google map
                String locationUrl = data.get(position).getLocationUrl();
                Uri uri = Uri.parse(locationUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);

            }
        });
    }

    private void SetVaccineCostView(MyViewHolder holder, int position) {
        // if cost is free, then display it in green color
        if(data.get(position).getFee_type().equalsIgnoreCase("Free")){
            holder.vaccine_cost_img.setImageResource(R.color.green);
            holder.vaccine_cost.setText("FREE");

        }else{
            holder.vaccine_cost_img.setImageResource(R.color.red_paid);
            //holder.vaccine_cost.setText("Rs. "+data.get(position).getFee()+"/-");
            holder.vaccine_cost.setText("PAID");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
