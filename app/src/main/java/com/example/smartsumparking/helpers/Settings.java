package com.example.smartsumparking.helpers;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.smartsumparking.R;
import com.example.smartsumparking.activities.GmapActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Settings extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    Button langbutton,contactbtn,aboutusbtn,canclealarm;
    Map<String,Object> res;
    Switch mySwitch;
int hours,min;
String avaliableSpaces;
    Dialog myDialog;
    private Switch myswitch;
    SharedPref sharedpref;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedpref = new SharedPref(getActivity());
        if(sharedpref.loadNightModeState()==true) {
            getActivity().setTheme(R.style.darktheme);
        }
        else  getActivity().setTheme(R.style.AppTheme);


        final View view = inflater.inflate(R.layout.fragment_settings1, container, false);

        myswitch=view.findViewById(R.id.myswitch);
        if (sharedpref.loadNightModeState()==true) {
            myswitch.setChecked(true);
        }
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedpref.setNightModeState(true);
                    restartApp();
                }
                else {
                    sharedpref.setNightModeState(false);
                    restartApp();
                }
            }
        });


        langbutton = (Button)view.findViewById(R.id.changeMyLang);
        langbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }});
        canclealarm=(Button)view.findViewById(R.id.canclealarm);
        canclealarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancleAlarm();
            }
        });
        aboutusbtn = (Button)view.findViewById(R.id.aboutus);
        aboutusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
            }});
        contactbtn = (Button)view.findViewById(R.id.contact);
        contactbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowContactPopup(v);
            }});


        myDialog = new Dialog(getActivity());



        res = new HashMap<>();

        Button button = (Button) view.findViewById(R.id.setnotification);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);

                GmapActivity gmapActivity=(GmapActivity) getActivity();



                TimePickerDialog timePicker = new TimePickerDialog(getContext(), R.style.my_dialog_theme2, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        String timeString = hour + ":" + minute;
                        res.put("from",timeString);

                        hours=hourOfDay;
                        min=minute;

                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.HOUR_OF_DAY, hours);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 1);

                        Intent intent = new Intent(getContext().getApplicationContext(),Notification_receiver.class);
                        intent.setAction("MY_NOTIFICATION_MESSAGE");
//                        intent.putExtra("avaliableSpaces",avaliableSpaces);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                },hour,minute,true);
                timePicker.show();

            }
        });

        return view;
    }

    public void restartApp () {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    public void cancleAlarm(){

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 1);
        Intent intent = new Intent(getContext().getApplicationContext(),Notification_receiver.class);
        intent.setAction("MY_NOTIFICATION_MESSAGE");
//        intent.putExtra("avaliableSpaces",avaliableSpaces);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(getContext(), R.string.Alarmoff,Toast.LENGTH_SHORT).show();
    }

    public void ShowPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void ShowContactPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.contactpopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }



    private void showChangeLanguageDialog() {
        final String[] listItems={getString(R.string.Njemački),getString(R.string.Engleski),getString(R.string.Francuski),getString(R.string.Hrvatski),getString(R.string.Talijanski),getString(R.string.Japanski)};
        AlertDialog.Builder mBuilder=new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Izaberi jezik");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    setLocale("de");
                    getActivity().recreate();

                }
                else if(i==1){
                    setLocale("en");
                    getActivity().recreate();

                }

                else if(i==2){
                    setLocale("fr");
                    getActivity().recreate();

                }
                else if(i==3){
                    setLocale("hr");
                    getActivity().recreate();


                }
                else if(i==4){
                    setLocale("it");
                    getActivity().recreate();

                }

                else if(i==5){
                    setLocale("ja");
                    getActivity().recreate();

                }


                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog=mBuilder.create();
        mDialog.show();

    }



    public void loadLocale(){
        SharedPreferences prefs=getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language=prefs.getString("My_Lang","");
        setLocale(language);

    }
    private void setLocale(String lang){
        Locale locale=new Locale(lang);
        Locale.setDefault(locale);
        Configuration config=new Configuration();
        config.locale=locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor=getActivity().getSharedPreferences("Settings",getActivity().MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();


    }
}
