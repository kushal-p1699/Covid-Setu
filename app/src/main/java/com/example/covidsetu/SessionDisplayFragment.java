package com.example.covidsetu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionDisplayFragment extends Fragment {
    private static final String TAG = "SESSION FRAGMENT";
    private String baseUrl = "https://cdn-api.co-vin.in/api/v2/";

    private int  district_id;
    private String date;

    ArrayList<SessionModel> model_list;
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_session_display, container, false);

        //initialize
        recyclerView = (RecyclerView) view.findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            district_id = getArguments().getInt("district_id");
            date = getArguments().getString("date");

            GetVaccineSessionByDistrict(district_id, date);
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void GetVaccineSessionByDistrict(int districtId, String date) {

        String url = baseUrl + "appointment/sessions/public/findByDistrict?district_id="+districtId+"&date="+date;
//            String url = "https://cdn-api.co-vin.in/api/v2/appointment/sessions/public/findByDistrict?district_id=512&date=31-03-2021";
        Log.d("SESSION FRAGMENT", districtId +" , "+date+"\n\n"+url);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Response : " + response.toString());
                        SessionModel sessionModel = new SessionModel();
                        MapJsonToSessionModel(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
            }
        });
        requestQueue.add(request);
    }

    private void MapJsonToSessionModel(JSONObject response) {
        SessionModel model;
        model_list = new ArrayList<>();
        try {
            JSONArray jsonArray = response.getJSONArray("sessions");

            for (int i = 0; i < jsonArray.length(); i++) {

                model = new SessionModel();

                JSONObject obj = jsonArray.getJSONObject(i);

                model.setCenter_id(obj.getInt("center_id"));
                model.setName(obj.getString("name"));
                model.setAddress(obj.getString("address"));
                model.setState_name(obj.getString("state_name"));
                model.setDistrict_name(obj.getString("district_name"));
                model.setBlock_name(obj.getString("block_name"));
                model.setPincode(obj.getString("pincode"));
                model.setLat(obj.getDouble("lat"));
                model.setLong(obj.getDouble("long"));
                model.setFrom(obj.getString("from"));
                model.setTo(obj.getString("to"));
                model.setFee_type(obj.getString("fee_type"));
                model.setFee(obj.getString("fee"));
                model.setSession_id(obj.getString("session_id"));
                model.setDate(obj.getString("date"));
                model.setAvailable_capacity(obj.getInt("available_capacity"));
                model.setAvailable_capacity_dose1(obj.getInt("available_capacity_dose1"));
                model.setAvailable_capacity_dose2(obj.getInt("available_capacity_dose2"));
                model.setMin_age_limit(obj.getInt("min_age_limit"));
                model.setVaccine(obj.getString("vaccine"));

                JSONArray slotsArray = obj.getJSONArray("slots");
                model.setSlots(getSlotsLoist(slotsArray));

                model_list.add(model);

            }

            myAdapter = new MyAdapter(model_list);
            recyclerView.setAdapter(myAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSlotsLoist(JSONArray slotsArray) throws JSONException {
        List<String> slotsList = new ArrayList<>();

        for (int j = 0; j < slotsArray.length(); j++) {
            slotsList.add(slotsArray.getString(j));
        }
        return slotsList;
    }
}