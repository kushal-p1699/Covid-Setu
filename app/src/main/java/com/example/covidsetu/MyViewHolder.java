package com.example.covidsetu;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView center_name, center_address, vaccine_name_and_age, vaccine_cost;
    ShapeableImageView vaccine_cost_img;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        center_name = (TextView) itemView.findViewById(R.id.center_name);
        center_address = (TextView) itemView.findViewById(R.id.center_address);
        vaccine_name_and_age = (TextView) itemView.findViewById(R.id.tv_vaccine_name_and_age);
        vaccine_cost = (TextView) itemView.findViewById(R.id.tv_vaccine_cost);

        vaccine_cost_img = (ShapeableImageView) itemView.findViewById(R.id.vaccine_cost);

    }
}
