package com.example.covidsetu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccinationFragment extends Fragment {

    private String stateNames[];
    private States states;
    private String baseUrl = "https://cdn-api.co-vin.in/api/v2/";
    final private String TAG = "VACCINATION FRAGMENT";

    private Spinner stateSpinner, districtSpinner;
    private EditText dateEntered;
    private Button btnSubmit;

    private Map<String, Integer> district_map;
    private List<String> district_list;
    int district_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void Initilization(View v) {
        states = new States();
        stateNames = states.getStates();

        stateSpinner = (Spinner) v.findViewById(R.id.spinner_states);
        districtSpinner = (Spinner) v.findViewById(R.id.spinner_district);

        btnSubmit = (Button) v.findViewById(R.id.btn_submit);

        dateEntered = (EditText) v.findViewById(R.id.et_date);
        dateEntered.setText(GetTodaysDate());

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

        // read district id
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                district_id = district_map.get(district_list.get(position));
                Log.d("SESSION FRAGMENT select", "" + district_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Log.d("SESSION FRAGMENT OUT",""+district_id);


        GetVaccineSessionByDistrict();

        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "logged out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), OTPLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }

    private void GetVaccineSessionByDistrict() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get district id and date
                String date = dateEntered.getText().toString().trim();

                // send data to session fragment using bundel
                Log.d("SESSION FRAGMENT1", district_id + " , " + date);
                Bundle bundle = new Bundle();
                bundle.putInt("district_id", district_id);
                bundle.putString("date", date);

                Fragment sessionDisplayFragment = new SessionDisplayFragment();
                sessionDisplayFragment.setArguments(bundle);


                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                getFragmentManager().popBackStack();
                ;

                fragmentTransaction.replace(R.id.id_vaccination_fragment, sessionDisplayFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

    }

    private void GetDistrictID() {

//        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                district_id = district_map.get(district_list.get(position));
//                Log.d("SESSION FRAGMENT select",""+district_id);
//                SharedPreferences sp = getContext().getSharedPreferences("district_id", Context.MODE_PRIVATE);
//                sp.edit().putInt("district_id", district_id).apply();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
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