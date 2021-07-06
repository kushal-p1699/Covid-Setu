package com.example.covidsetu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VaccinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VaccinationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String stateNames[];
    private States states;
    private String baseUrl = "https://cdn-api.co-vin.in/api/v2/";
    final private String TAG = "VACCINATION FRAGMENT";

    private Spinner stateSpinner, districtSpinner;
    private EditText dateEntered;
    private Button btnSubmit;

    private Map<String, Integer> district_map;
    private List<String> district_list;
    ArrayList<SessionModel> model_list;

    RecyclerView recyclerView;
    MyAdapter myAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VaccinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VaccinationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VaccinationFragment newInstance(String param1, String param2) {
        VaccinationFragment fragment = new VaccinationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    private void Initilization(View v) {
        states = new States();
        stateNames = states.getStates();

        stateSpinner = (Spinner) v.findViewById(R.id.spinner_states);
        districtSpinner = (Spinner) v.findViewById(R.id.spinner_district);

        btnSubmit = (Button) v.findViewById(R.id.btn_submit);

        dateEntered = (EditText) v.findViewById(R.id.et_date);
        dateEntered.setText(GetTodaysDate());

        // recyclerview
        recyclerView = (RecyclerView) v.findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

     }

    private String GetTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;

        String date = day + "/" + month + "/" + year;
        return date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vaccination, container, false);

        Initilization(view);

        // set the state names to spinner
        SetStateSpinner();

        // get selected state
        GetSelectedState();

        Toast.makeText(getContext(), "Date : " + dateEntered.getText().toString(), Toast.LENGTH_SHORT).show();

        GetVaccineSessionByDistrict();

        return view;
    }

    private void GetVaccineSessionByDistrict() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get district id and date
                int district_id = GetDistrictID();
                String date = dateEntered.getText().toString();
                GetVaccineSessionByDistrict(district_id, date);


                // print session data
                SessionModel sessionModel = new SessionModel();
//                Log.d(TAG, "SESSION : " + sessionModel.getName());
//                Log.d(TAG, "SESSION : " + sessionModel.getSlots().toString());

//                Log.d(TAG, "MODEL LIST: "+model_list.toString());

            }
        });

    }

    private void GetVaccineSessionByDistrict(int districtId, String date) {
        String url = baseUrl + "appointment/sessions/public/findByDistrict?district_id=" + districtId + "&date=" + date;

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

    private int GetDistrictID() {
        SharedPreferences sp = getContext().getSharedPreferences("district_id", Context.MODE_PRIVATE);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int district_id = district_map.get(district_list.get(position));
                sp.edit().putInt("district_id", district_id).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return sp.getInt("district_id", 10);
    }

    private void GetSelectedState() {
        final int[] stateId = new int[1];
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateId[0] = position;
                SetDistrictSpinner(position + 1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetDistrictSpinner(int position) {
        String url = baseUrl + "admin/location/districts/" + position;
        Log.d(TAG, url);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        district_list = new ArrayList<>();
                        district_map = new HashMap<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("districts");

                            // store it in sharedPref
                            StoreDistrictsInSharedPrefs(jsonArray);

                            // display the districts names in spinner
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                String _districtName = obj.getString("district_name");
                                int _districtId = obj.getInt("district_id");
                                district_list.add(_districtName);
                                district_map.put(_districtName, _districtId);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                                getContext(),
                                R.layout.support_simple_spinner_dropdown_item,
                                district_list
                        );
                        districtSpinner.setAdapter(stringArrayAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
            }
        });

        requestQueue.add(request);

    }

    private void StoreDistrictsInSharedPrefs(JSONArray jsonArray) {
        SharedPreferences sp = getContext().getSharedPreferences("districts", Context.MODE_PRIVATE);
        sp.edit().putString("districts", jsonArray.toString()).apply();
    }

    private void SetStateSpinner() {

        List<String> stateList = Arrays.asList(stateNames);

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                getContext(),
                R.layout.support_simple_spinner_dropdown_item,
                stateList
        );

        stateSpinner.setAdapter(stringArrayAdapter);
    }
}