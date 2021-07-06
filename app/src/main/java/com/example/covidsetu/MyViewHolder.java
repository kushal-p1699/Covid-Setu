package com.example.covidsetu;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView center_name, center_state;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        center_name = (TextView) itemView.findViewById(R.id.center_name);
        center_state = (TextView)itemView.findViewById(R.id.center_state);
    }
}
