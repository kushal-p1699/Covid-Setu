package com.example.covidsetu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.List;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_vaccination, container, false);

        Initilization(view);

        // set the state names to spinner
        SetStateSpinner();

        // get selected state
        GetSelectedState();

        return view;
    }

    private void GetSelectedState() {
        final int[] stateId = new int[1];
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stateId[0] = position;
                SetDistrictSpinner(position+1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void SetDistrictSpinner(int position) {
        String url = baseUrl + "admin/location/districts/"+position;
        Log.d(TAG, url);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<String> district_list = new ArrayList<>();
//                        Log.d(TAG, response.toString());
                        try {
                            JSONArray jsonArray = response.getJSONArray("districts");

                            // store it in sharedPref
                            StoreDistrictsInSharedPrefs(jsonArray);

                            // display the districts names in spinner
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject obj = jsonArray.getJSONObject(i);
                                district_list.add(obj.getString("district_name"));
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