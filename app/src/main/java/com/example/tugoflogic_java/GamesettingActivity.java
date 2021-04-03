package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GamesettingActivity extends AppCompatActivity {


    TextView instructorName, priorMC;
    EditText editTxtTime, mainClaim;
    Button btnGoToGameMain;
    Integer votingTime;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    DatabaseReference mainClaimDB = mDatabase.child("MainClaim");
    DatabaseReference gameSettingDB = mDatabase.child("GameSetting");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamesetting);

        instructorName = findViewById(R.id.instructorName);
//        editTxtTime = findViewById(R.id.editTxtTime);
        btnGoToGameMain = findViewById(R.id.btnGoToGameMain);
        mainClaim = findViewById(R.id.editTxtMC);
        priorMC = findViewById(R.id.tvPriorMC);

        //get instructor name from login page
        final Intent intent = getIntent();
        final String name = intent.getStringExtra("instructorName");
        instructorName.setText("Welcome Referee, " + name);

        //list of prior mc
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim db_mainClaim = null;
                String listMC = "";
                for (DataSnapshot child : snapshot.getChildren()){
                    db_mainClaim = child.getValue(DB_MainClaim.class);
                    listMC += db_mainClaim.mc + "\n";
                }
                priorMC.setText(listMC);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnGoToGameMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //db update
                DB_MainClaim settingMC = new DB_MainClaim(mainClaim.getText().toString(), 0, 0,0,0);
                mainClaimDB.child("mc").setValue(settingMC);
                //voting time update to db
                gameSettingDB.child("votingTime").setValue(60);
                gameSettingDB.child("endGame").setValue(false);
                gameSettingDB.child("goNextBout").setValue(false);
                gameSettingDB.child("showList").setValue(false);
                gameSettingDB.child("showComment").setValue(false);

                Intent intentS = new Intent(getApplicationContext(), StrawpollActivity.class);
                intentS.putExtra("refereeName", name);
                startActivity(intentS);
            }
        });
    }
}