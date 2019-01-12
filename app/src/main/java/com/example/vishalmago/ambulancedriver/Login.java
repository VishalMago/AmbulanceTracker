package com.example.vishalmago.ambulancedriver;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;


/**
 * Created by Hp on 9/27/2016.
 */
public class Login extends Activity {
    private Button button;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static final String TAG = "EmailPassword";
    private TextView signup,fpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(isRegistered()){
            Intent i=new Intent(getApplicationContext(),HomePage.class);
            startActivity(i);
            finish();
        }
        button = (Button) findViewById(R.id.buttonLogin);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText) findViewById(R.id.editTextPassword);
        signup=(TextView)findViewById(R.id.csignup);
        fpass=(TextView)findViewById(R.id.fpassword);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Login.this,SignUp.class);
                startActivity(i);
                finish();
            }
        });

        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i=new Intent(Login.this,ForgotPassword.class);
                startActivity(i);*/
            }
        });

        //Attaching an onclicklistener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (!isRegistered()) {
                    if(editTextEmail.getText().toString().isEmpty()) {
                        editTextEmail.setError("Required.");
                    }
                    else if (editTextPassword.getText().toString().isEmpty()){
                        editTextPassword.setError("Required.");
                    }
                    else {
                        if(pattern.matcher(editTextEmail.getText().toString()).matches()){
                            signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());
                        }
                        else {
                            Toast.makeText(Login.this,"Invalid Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //registering the device
                } else {
                    //if the device is already registered
                    //displaying a toast
                    Toast.makeText(Login.this, "Already registered...", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private boolean isRegistered() {
        //Getting shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        //Getting the value from shared preferences
        //The second parameter is the default value
        //if there is no value in sharedpreference then it will return false
        //that means the device is not registered
        return sharedPreferences.getBoolean(Constants.REGISTERED, false);
    }

    private void signIn(String email, String password){
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Authenticating");
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                FirebaseUser user = mAuth.getCurrentUser();
                                mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Opening shared preference
                                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

                                        //Opening the shared preferences editor to save values
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        //Saving the boolean as true i.e. the device is registered
                                        editor.putBoolean(Constants.REGISTERED, true);
                                        editor.putString("Email", editTextEmail.getText().toString());
                                        editor.putString("Name",dataSnapshot.child("username").getValue().toString());
                                        editor.putString("Ambulanceno",dataSnapshot.child("ambulanceNo").getValue().toString());

                                        //Applying the changes on sharedpreferences
                                        editor.apply();

                                        Intent i = new Intent(Login.this, HomePage.class);
                                        i.putExtra("IS_FROM_LOGIN", true);
                                        startActivity(i);
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Log.w(TAG, "Failed to read value.", error.toException());
                                    }
                                });

                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                progressDialog.dismiss();
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                            if (!task.isSuccessful()) {
                                progressDialog.dismiss();
                                Log.w(TAG, "Task Not Sucessful");
                            }
                        }
                });

    }
}
