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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    EditText userEmail;
    EditText userPw;
    EditText userName;
    Integer playerNum;
    Button signupBtn;
    Button loginBtn;
    RadioGroup selectUser;
    boolean isStudent = true;
    TextView playerText;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.editTxtID);
        userPw = findViewById(R.id.editTxtPW);
        userName = findViewById(R.id.editTxtName);
        playerNum = 0;
        selectUser = findViewById(R.id.selectUser);
        playerText = findViewById(R.id.txtViewNumber);
        loginBtn = findViewById(R.id.btnSignIn);
        signupBtn = findViewById(R.id.btnSignUp);

        selectUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId){
                    case R.id.btnInstructor :
                        isStudent = false;
                        playerNum = 0;
                        break;
                    case R.id.btnStudent:
                        isStudent = true;
                        playerNum++;
                }
            }
        });

                FirebaseUser currentUser = auth.getCurrentUser();
                assert currentUser != null;
                final String uuid = currentUser.getUid();
                final String femail = currentUser.getEmail();
                final String playerName = userName.getText().toString();

        Log.i("Sangmin", "Current : " + currentUser);


        // sign in
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseUser currentUser = auth.getCurrentUser();
//                assert currentUser != null;
//                final String uuid = currentUser.getUid();
//                final String femail = currentUser.getEmail();
//                final String playerName = userName.getText().toString();

                String email = userEmail.getText().toString().trim();
                String password = userPw.getText().toString().trim();
                String name = userName.getText().toString().trim();

                if(TextUtils.isEmpty(name)){
                    userName.setError("Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    userEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    userPw.setError("Password is Required");
                    return;
                }
                if(password.length() < 6){
                    userPw.setError("Password must be longer than 6 characters");
                    return;
                }
                login(email,password,name);
                //updatePlayer(uuid,playerName,femail);
            }
        });

        // sign up
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
            }
        });

    }

    private void login(final String email, String password, final String name){
        // Auth the user
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() && isStudent){
                    Toast.makeText(LoginActivity.this, "Student Logged in.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), StrawpollActivity.class);
                    intent.putExtra("studentName", userName.getText().toString());

                    FirebaseUser registerUser = auth.getCurrentUser();
                    assert registerUser != null;
                    String userId = registerUser.getUid();
                    updatePlayer(userId,name,email);
                    startActivity(intent);
                    //getIntent().putExtra("playerNumber", txtView)
                }else if (task.isSuccessful() && !isStudent){
                    Toast.makeText(LoginActivity.this, "Instructor Logged in.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), GamesettingActivity.class);
                    intent.putExtra("instructorName",userName.getText().toString());
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginActivity.this, "Log in Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePlayer(String userId, String name, String email){
        String key = playerDB.child(userId).getKey();
        DB_Player player = new DB_Player(name,email);
        Map<String, Object> playerValue = player.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, playerValue);

        playerDB.updateChildren(childUpdates);
    }
}