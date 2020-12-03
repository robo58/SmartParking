package com.example.smartsumparking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsumparking.R;
import com.example.smartsumparking.helpers.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManagamentFragment<Interface> extends Fragment {

    private static final String TAG = "UserManagamentFragment";
    private List<String> mEmails  = new ArrayList<>();
    private List<String> mImageUrls  = new ArrayList<>();
    private List<Boolean> mRoles = new ArrayList<>();
    private List<String> mIds = new ArrayList<>();
    String user;
    private FirebaseFirestore db;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_managament, container, false);

        db=FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recycler_view);
        user = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecyclerViewAdapter(mEmails, mImageUrls, mRoles, mIds, getContext());

        recyclerView.setAdapter(adapter);

        readData();

        return view;
    }

    private void readData(){
        db.collection("users").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){

                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                String email  = d.getString("email");
                                if(!(user.equals(email))) {
                                    String imageUrl = d.getString("image");
                                    HashMap role = (HashMap) d.get("roles");
                                    String id = d.getId();
                                    mEmails.add(email);
                                    mIds.add(id);
                                    mImageUrls.add(imageUrl);
                                    mRoles.add((Boolean) role.get("admin"));
                                }
                            }

                            adapter.notifyDataSetChanged();
                        }
                    }
                });

    }

}
