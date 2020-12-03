package com.example.smartsumparking.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smartsumparking.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationsFragment extends Fragment {
    Marker[] parkings;
    JSONArray park;
    String rparkingName = null,rspotName = null;
    public ReservationsFragment() {
        this.parkings = null;
        this.park = null;
    }

    public ReservationsFragment(Marker[] parkings, JSONArray park) {
        this.parkings = parkings;
        this.park=park;
    }


    TextView datetext,timetext,timetext2,datetext2;
    Button pickdate,picktime,picktime2,submit,pickdate2;
    EditText name,email;
    Spinner parkSpinner,spotSpinner;
    private FirebaseFirestore db;
    String parkingName,parkingSpot;
    Map<String,Object> res;
    List<String> spinParkings = new ArrayList<>();
    List<String> relatedSpaces = new ArrayList<>();
    ArrayAdapter<String> parkingAdapter;
    ArrayAdapter<String> spotAdapter;

    String defaultPark = "- Pick your parking -";
    String defaultSpace = "- Pick your parking space -";
    public ReservationsFragment(Marker[] parkings, JSONArray park, String rparkingName, String rspotName) {
        this.parkings = parkings;
        this.park=park;
        this.rparkingName = rparkingName;
        this.rspotName = rspotName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        datetext = view.findViewById(R.id.res_datetext);
        timetext = view.findViewById(R.id.res_timetext);
        timetext2 = view.findViewById(R.id.res_timetext2);
        pickdate = view.findViewById(R.id.res_getdate);
        picktime = view.findViewById(R.id.res_getTime);
        picktime2 = view.findViewById(R.id.res_getTime2);
        submit = view.findViewById(R.id.res_submit);
        pickdate2 = view.findViewById(R.id.res_getdate2);
        parkSpinner = view.findViewById(R.id.res_parkspinner);
        spotSpinner = view.findViewById(R.id.res_spotspinner);
        datetext2 = view.findViewById(R.id.res_datetext2);

        res = new HashMap<>();
        pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDateButton();
            }
        });
        pickdate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDate2Button();
            }
        });
        picktime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimeFromButton();
            }
        });
        picktime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTime2Button();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmitButton();
            }
        });
        getParkingData();
        if(rparkingName!=null){
            setParking();
        }
    }

    private void setParking() {
        parkSpinner.setSelection(1);
        int spot = 0;
        for(int i = 0;i<relatedSpaces.size();i++){
            if(relatedSpaces.get(i).equals(rspotName)){
                spot = i;
            }
        }
        spotSpinner.setSelection(spot);
    }

    private void getParkingData(){
    spinParkings.add(defaultPark);
    for(int i = 0;i<parkings.length;i++){
        spinParkings.add(parkings[i].getTitle());
    }
    relatedSpaces.add(defaultSpace);
    for(int i=0;i<park.length();i++){
        try {
            if(park.getJSONObject(i).getInt("occupied") ==1 ){
                //
            }else{
                relatedSpaces.add(park.getJSONObject(i).getString("parking_space_name"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
        parkingAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,spinParkings);
        parkingAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    parkSpinner.setAdapter(parkingAdapter);
        spotAdapter = new ArrayAdapter<>(getContext(),R.layout.spinner_item,relatedSpaces);
        spotAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    spotSpinner.setAdapter(spotAdapter);
        spotSpinner.setEnabled(false);
        parkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            spotSpinner.setEnabled(true);
            parkingName = adapterView.getSelectedItem().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });
        spotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                parkingSpot = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void handleTime2Button() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.my_dialog_theme2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String timeString = hour + ":" + minute;
                timetext2.setText(timeString);
                res.put("to",timeString);
            }
        },hour,minute,true);
        timePickerDialog.show();
    }

    private void handleTimeFromButton() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.my_dialog_theme2, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String timeString = hour + ":" + minute;
                timetext.setText(timeString);
                res.put("from",timeString);
            }
        },hour,minute,true);
        timePickerDialog.show();
    }

    private void handleDateButton() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dateString = day+"/"+month+"/"+year;
                res.put("date from",dateString);
                datetext.setText(dateString);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void handleDate2Button() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dateString = day+"/"+month+"/"+year;
                res.put("date to",dateString);
                datetext2.setText(dateString);
            }
        },year,month,day);
        datePickerDialog.show();
    }

    private void handleSubmitButton(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        res.put("name",user.getDisplayName());
        res.put("email",user.getEmail());
        res.put("parking_name",parkingName);
        res.put("parking_spot",parkingSpot);
        db.collection("reservations")
                .whereEqualTo("email",user.getEmail())
                .whereEqualTo("date to",res.get("date to"))
                .get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                db.collection("reservations")
                        .add(res)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(),"Added reservation",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Failed to add reservation",Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Toast.makeText(getContext(),"User already made a reservation",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
