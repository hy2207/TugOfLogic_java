package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Purpose: straw poll for students (players)
 */
public class StrawpollActivity extends AppCompatActivity {

    private TextView tvMainClaim, tvPlayerName, tvRemainTime;
    private RadioGroup rg_strawPoll;
    private RadioButton rb_convinced, rb_not;
    private Button btnSubmitStraw;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");

    public Integer numConvinced = 0;
    public Integer numNotYet = 0;
    public String strawResult = "Convinced";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strawpoll);

        tvMainClaim = findViewById(R.id.txtViewGetMc);
        tvPlayerName = findViewById(R.id.txtViewPlayerName);

        rg_strawPoll = findViewById(R.id.voteStrawPoll);
        rb_convinced = findViewById(R.id.strawConvinced);
        rb_not = findViewById(R.id.strawNot);

        btnSubmitStraw = findViewById(R.id.btnSubmitStraw);

        //load data from firebase
        getData();

        //voting
        rg_strawPoll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.strawConvinced){
                    strawResult = "Convinced";
                } else {
                    strawResult = "Established";
                }
            }
        });

        btnSubmitStraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerDB.child("id").child("strawResult").setValue(strawResult);
                if (strawResult == "Convinced"){
                    numConvinced++;
                } else {
                    numNotYet++;
                }
                mainClaimDB.child("id").child("numConvinced").setValue(numConvinced);
                mainClaimDB.child("id").child("numNotYet").setValue(numNotYet);
                startActivity(new Intent(StrawpollActivity.this, StrawpollResultActivity.class));
            }
        });
        //get time from setting page
        final String timeForm = getString(R.string.remain_time);
        tvRemainTime = findViewById(R.id.txtViewRemainTime);

        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
//                tvRemainTime.setText(String.format(timeForm, millisUntilFinished, ));
                tvRemainTime.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                tvRemainTime.setText("time is over!");
//                startActivity(new Intent(StrawpollActivity.this, StrawpollResultActivity.class));
            }
        }.start();

    }

    //load Data from firebase
    public void getData(){

        //load main claim
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child: snapshot.getChildren()){
//                    Log.d("Hwayoung", "Main Claim : " + child.getValue());
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                tvMainClaim.setText(mainClaim.mc);
                numConvinced = mainClaim.numStrawCon;
                numNotYet = mainClaim.numStrawNot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load player information
        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Player playerInfo = null;
                for (DataSnapshot child : snapshot.getChildren()){
                    playerInfo = child.getValue(DB_Player.class);
                }
                tvPlayerName.setText("Welcome, " + playerInfo.name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}