package com.example.smartsumparking.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {

    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG,"Job started");
        db.collection("reservations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document: task.getResult()){
                        String date = (String) document.getData().get("date to");
                        String time_from = (String) document.getData().get("from");
                        String time_to = (String) document.getData().get("to");
                        String email = (String) document.getData().get("email");
                        doBackgroundWork(jobParameters,date,time_from,time_to,email);
                    }
                }
            }
        });
        return true;
    }

    private void doBackgroundWork(final JobParameters jobParameters,final String date,final String time_from,final String time_to,final String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Date c;
                SimpleDateFormat simpleDateFormat;
                SimpleDateFormat simpleTimeFormat;
                String formatedDate;
                String formatedTime;
                do {
                    c = Calendar.getInstance().getTime();
                    simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    simpleTimeFormat = new SimpleDateFormat("HH:mm");
                    formatedDate = simpleDateFormat.format(c);
                    formatedTime = simpleTimeFormat.format(c);
                }while(!(date.equals(formatedDate)) && !(time_to.equals(formatedTime)));
                db.collection("reservations")
                        .whereEqualTo("email",email)
                        .whereEqualTo("date to",date)
                        .whereEqualTo("from",time_from)
                        .whereEqualTo("to",time_to)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document: task.getResult()){
                                document.getReference().delete();
                            }
                        }

                    }
                });
                Log.d(TAG,"Job finished");
                jobFinished(jobParameters,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG,"Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
