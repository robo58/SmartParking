package com.example.smartsumparking.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartsumparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    Button btnLogin, btnRegister;
    EditText etPassword, etEmail;
    CheckBox checkbox;
    String email, password;

    //Firebase

    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private FirebaseFirestore db;
    private Map<String,Object> reg;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);
        db = FirebaseFirestore.getInstance();
        reg = new HashMap<>();
        etPassword = (EditText)view.findViewById(R.id.etPassword);
        etEmail = (EditText)view.findViewById(R.id.etEmail);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(getActivity());

        btnRegister = (Button)view.findViewById(R.id.btnRegister);
        btnLogin = (Button)view.findViewById(R.id.btnLogin);

        checkbox = (CheckBox) view.findViewById(R.id.checkbox);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    // show password
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = etEmail.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    etEmail.setError("User required");
                }
                if(TextUtils.isEmpty(password)){
                    etPassword.setError("Password required");
                }

                mDialog.setMessage("Proccesing");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Map<String,Boolean> roles = new HashMap<>();
                            roles.put("user",true);
                            roles.put("admin",false);
                            reg.put("email",etEmail.getText().toString());
                            reg.put("roles",roles);
                            db.collection("users")
                                    .add(reg)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(getContext(),"Added user",Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(),"Failed to add user",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                            //Napravi da se prebaci na drugi fragment
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}