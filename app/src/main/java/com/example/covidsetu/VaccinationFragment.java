package com.example.covidsetu;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class VaccinationFragment extends Fragment {

    private String stateNames[];
    private States states;
    private String baseUrl = "https://cdn-api.co-vin.in/api/v2/";
    final private String TAG = "VACCINATION FRAGMENT";

    private Spinner stateSpinner, districtSpinner;
    private TextView selectDate;
    private Button btnSubmit;

    private Map<String, Integer> district_map;
    private List<String> district_list;
    int district_id;

    private String selectedDateString;



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

        selectDate = (TextView) v.findViewById(R.id.et_date);

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

        ConstraintLayout expandableCardView = (ConstraintLayout) view.findViewById(R.id.expand_search_by_district_id);
        expandableCardView.setVisibility(View.GONE);

        CardView mainCardView = (CardView) view.findViewById(R.id.search_by_district_id);
        mainCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableCardView.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    expandableCardView.setVisibility(View.VISIBLE);

                }else{
                    TransitionManager.beginDelayedTransition(mainCardView, new AutoTransition());
                    expandableCardView.setVisibility(View.GONE);
                }
            }
        });

        ConstraintLayout expand_search_by_pincode = (ConstraintLayout) view.findViewById(R.id.expand_search_by_pincode);
        expand_search_by_pincode.setVisibility(View.GONE);

        CardView search_by_pincode = (CardView) view.findViewById(R.id.search_by_pincode);
        search_by_pincode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expand_search_by_pincode.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(search_by_pincode, new AutoTransition());
                    expand_search_by_pincode.setVisibility(View.VISIBLE);

                }else{
                    TransitionManager.beginDelayedTransition(search_by_pincode, new AutoTransition());
                    expand_search_by_pincode.setVisibility(View.GONE);
                }
            }
        });



        Initilization(view);

        // set the state names to spinner
        SetStateSpinner();

        // get selected state
        GetSelectedState();

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

        // Calendar Object
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        // today's time
        long todayTime = MaterialDatePicker.todayInUtcMilliseconds();
        calendar.setTimeInMillis(todayTime);

        // start bound in milli sec
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        long startBound = calendar.getTimeInMillis();
        // end bound

        CalendarConstraints.Builder calenderConstraints =  new CalendarConstraints.Builder();
        calenderConstraints.setStart(startBound);


        //date picker builder
        MaterialDatePicker.Builder materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker();
        materialDatePickerBuilder.setTitleText("Select a date");

        materialDatePickerBuilder.setSelection(todayTime);
        materialDatePickerBuilder.setCalendarConstraints(calenderConstraints.build());
        final MaterialDatePicker materialDatePicker = materialDatePickerBuilder.build();

        // testing

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                selectedDateString = materialDatePicker.getHeaderText();
                selectDate.setText(selectedDateString);

            }
        });


        GetVaccineSessionByDistrict();

//        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "logged out", Toast.LENGTH_SHORT).show();
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getContext(), OTPLoginActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });

        return view;
    }

    private void GetVaccineSessionByDistrict() {

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get district id and date
                // TODO add date here
                String date = getSelectedDate();

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

    private String getSelectedDate() {
        String day = "";
        String month = "";
        String year = "";

        String dateArr[] = selectedDateString.split(" ");
        if(dateArr[0].length() == 0){
            day = "0"+dateArr[0];
        }else{
            day = dateArr[0];
        }

        month = getMonthNumber(dateArr[1]);
        year = dateArr[2];

        return day +"-"+month+"-"+year;
    }

    private String getMonthNumber(String month) {
        switch (month){
            case "Jan" : return "01";
            case "Feb" : return "02";
            case "Mar" : return "03";
            case "Apr" : return "04";
            case "May" : return "05";
            case "Jun" : return "06";
            case "Jul" : return "07";
            case "Aug" : return "08";
            case "Sep" : return "09";
            case "Oct" : return "10";
            case "Nov" : return "11";
            case "Dec" : return "12";
        }

        return "-1";
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