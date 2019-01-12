package com.example.vishalmago.ambulancedriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
/**
 * Created by Vishal Mago on 2/27/2018.
 */

public class SignUp extends BaseActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    final Context context = this;
    private static final String TAG = "EmailPassword";
    private EditText editTextPassword;
    private EditText editTextEmail;
    private EditText editAmbulance;
    private EditText editTextName;
    private Button SignUp;
    private TextView signin;
    private DatabaseReference mDatabase;
    private boolean signup_status=false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editAmbulance=(EditText)findViewById(R.id.editAmbulance);
        editTextName=(EditText)findViewById(R.id.editTextName);

        signin=(TextView)findViewById(R.id.textSignIn);
        SignUp=(Button)findViewById(R.id.buttonRegister);
        SignUp.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonRegister:
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                break;
            case R.id.textSignIn:
                Intent i=new Intent(SignUp.this,Login.class);
                startActivity(i);
                finish();
                break;
        }
    }
    private void createAccount(final String email, final String password) {
        Log.d(TAG, "createAccount:" + email);
        if (editTextEmail.getText().toString().isEmpty()) {
            editTextEmail.setError("Required.");
        }
        else if(editTextPassword.getText().toString().isEmpty()) {
            editTextPassword.setError("Required.");
        }
        else if (editTextName.getText().toString().isEmpty()){
            editTextName.setError("Required.");
        }
        else if (editAmbulance.getText().toString().isEmpty()){
            editAmbulance.setError("Required.");
        } else {
            showProgressDialog();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                writeNewUser(user.getUid(), editTextName.getText().toString(), email, password, editAmbulance.getText().toString());
                                signup_status = true;
                                if (signup_status == true) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                    alertDialogBuilder
                                            .setTitle("Ambulance Driver")
                                            .setIcon(R.drawable.main)
                                            .setMessage("User Account successfully created. Press OK to Login")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false);

                                    // create alert dialog
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show it
                                    alertDialog.show();
                                }
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "User ID already exists",
                                        Toast.LENGTH_SHORT).show();
                            }
                            hideProgressDialog();
                        }
                    });
        }
    }
    private void writeNewUser(String userId, String username, String email, String password, String ambulanceNo) {
        user_model user = new user_model(username, email,password,ambulanceNo);
        mDatabase.child("users").child(userId).setValue(user);
    }
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}


