package com.example.smartsumparking.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smartsumparking.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {

    TextView profile_name,profile_email;
    Button profile_signout;
    GoogleSignInClient googleSignInClient = null;
    TextView date_from,date_to,time_from,time_to,park,park_spot;
    FirebaseFirestore db;
    RelativeLayout pres_lay1;
    TextView pres_lay2;
        public ProfileFragment(){

        }
   public  ProfileFragment(GoogleSignInClient googleSignInClient){
            this.googleSignInClient = googleSignInClient;
   }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        db = FirebaseFirestore.getInstance();
        pres_lay1 = view.findViewById(R.id.pres_lay);
        pres_lay2 = view.findViewById(R.id.pres_lay2);
        profile_name = view.findViewById(R.id.profile_name);
        profile_email = view.findViewById(R.id.profile_email);
        profile_signout = view.findViewById(R.id.profile_signout);
        date_from = view.findViewById(R.id.pres_datefromval);
        date_to = view.findViewById(R.id.pres_date2val);
        time_from = view.findViewById(R.id.pres_timefromval);
        time_to = view.findViewById(R.id.pres_time2val);
        park = view.findViewById(R.id.pres_parkval);
        park_spot = view.findViewById(R.id.pres_parkspotval);
        pres_lay1.setVisibility(View.GONE);
        pres_lay2.setVisibility(View.GONE);
        if(account != null) {
            profile_name.setText(account.getGivenName());
            profile_email.setText(account.getEmail());
        }else{
            profile_name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName() != null ? FirebaseAuth.getInstance().getCurrentUser().getDisplayName() : "");
            profile_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }


        db.collection("reservations")
                .whereEqualTo("email",profile_email.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            pres_lay1.setVisibility(View.GONE);
                            pres_lay2.setVisibility(View.VISIBLE);
                        }else{
                            pres_lay1.setVisibility(View.VISIBLE);
                            pres_lay2.setVisibility(View.GONE);
                        }
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                date_from.setText(document.getData().get("date from").toString());
                                date_to.setText(document.getData().get("date to").toString());
                                time_from.setText(document.getData().get("from").toString());
                                time_to.setText(document.getData().get("to").toString());
                                park.setText(document.getData().get("parking_name").toString());
                                park_spot.setText(document.getData().get("parking_spot").toString());
                            }
                        }
                    }
                });



        profile_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                if(googleSignInClient != null) {
                    googleSignInClient.signOut();
                }
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new LoginFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }
}
