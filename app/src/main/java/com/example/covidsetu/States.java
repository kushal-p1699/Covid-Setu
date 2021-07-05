package com.example.covidsetu;

import java.util.ArrayList;
import java.util.List;

public class States {

    String[] states;

    public States(){
      states = new String[]{
              "Andaman and Nicobar Islands", "Andhra Pradesh", "Arunachal Pradesh", "Assam",
              "Bihar",
              "Chandigarh", "Chhattisgarh",
              "Dadra and Nagar Haveli", "Delhi",
              "Goa", "Gujarat",
              "Haryana", "Himachal Pradesh",
              "Jammu and Kashmir", "Jharkhand",
              "Karnataka", "Kerala",
              "Ladakh", "Lakshadweep",
              "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram",
              "Nagaland", "Odisha",
              "Puducherry", "Punjab",
              "Rajasthan",
              "Sikkim",
              "Tamil Nadu", "Telangana", "Tripura",
              "Uttar Pradesh", "Uttarakhand",
              "West Bengal",
              "Daman and Diu"
      };
    }

    public String[] getStates() {
        return states;
    }
}
