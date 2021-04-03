package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseRef;
    EditText regiId, regiPw, regiPwConfirm;
    ProgressBar progressBar;
    Button regiBtn;
    TextView loginLink;

    String name = "", strawResult = "", finalResult = "", votingRip = "", ground = "", comment = "";
    long numPlayer = 0;
    Boolean isReferee = true;

    final String TAG = "SignUp Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        Log.i(TAG, "User: " + auth.getCurrentUser());
        regiId = findViewById(R.id.regiID);
        regiPw = findViewById(R.id.regiPW);
        regiPwConfirm = findViewById(R.id.regiPwConfirm);
        progressBar = findViewById(R.id.progressBar);
        regiBtn = findViewById(R.id.btnRegister);
        loginLink = findViewById(R.id.loginLink);

        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = regiId.getText().toString().trim();
                String password = regiPw.getText().toString().trim();
                String pwconfirm = regiPwConfirm.getText().toString().trim();

                //Validation check
                if(TextUtils.isEmpty(email)){
                    regiId.setError("Email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    regiPw.setError("Password is required.");
                    return;
                }
                if(TextUtils.isEmpty(pwconfirm)){
                    regiPwConfirm.setError("Password confirm field is required.");
                    return;
                }
                if(password.length() < 6 || pwconfirm.length() < 6){
                    regiPw.setError("Password must be longer than 6 characters.");
                    return;
                }
                if(!password.equals(pwconfirm)){
                    regiPw.setError("Password must be matched. Please double check");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                register(email,password);
            }
        });
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void register(final String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser registerUser = auth.getCurrentUser();
                    assert registerUser != null;
                    String userId = registerUser.getUid();
                    databaseRef = FirebaseDatabase.getInstance().getReference("Player");
                    createUserOnDB(userId,email, name,strawResult,finalResult,isReferee,votingRip,ground,comment);
                    Toast.makeText(SignupActivity.this, "User created", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(SignupActivity.this, "Error :"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // store to the database
    private void createUserOnDB(String userId, String email, String name, String strawResult, String finalResult, boolean isReferee, String votingRip, String ground, String comment) {
        DB_Player player = new DB_Player(email, name, strawResult, finalResult, isReferee, votingRip, ground, comment);
        databaseRef.child(userId).setValue(player).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }else {
                    //Toast.makeText(SignupActivity.this, "Error :"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}