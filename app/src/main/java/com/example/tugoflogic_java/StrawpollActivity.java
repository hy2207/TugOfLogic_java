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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Purpose: straw poll for player
 */
public class StrawpollActivity extends AppCompatActivity {

    private TextView tvMainClaim, tvPlayerName, tvRemainTime;
    private RadioGroup rg_strawPoll;
    private Button btnSubmitStraw, btnSetTime;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    DatabaseReference mainClaimDB = mDatabase.child("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");
    DatabaseReference settingDB = mDatabase.child("GameSetting");

    //get current user
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    public Integer numStrawCon = 0;
    public Integer numStrawNot = 0;
    public String strawResult = "Convinced";
    public String playerKey;
//    CountDownTimer timer = null;
    TimerTask timerTask;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strawpoll);

        playerKey = currentPlayer.getUid();

        tvMainClaim = findViewById(R.id.txtViewGetMc);
        tvPlayerName = findViewById(R.id.txtViewPlayerName);
        tvRemainTime = findViewById(R.id.txtViewRemainTime);

        rg_strawPoll = findViewById(R.id.voteStrawPoll);

        btnSubmitStraw = findViewById(R.id.btnSubmitStraw);
        btnSetTime = findViewById(R.id.btnAddTime);

        //student doesn't need a set time button
        btnSetTime.setVisibility(View.INVISIBLE);

        if (getIntent().hasExtra("refereeName")){
            btnSetTime.setVisibility(View.VISIBLE);
            btnSubmitStraw.setVisibility(View.INVISIBLE);
        }

        //voting
        rg_strawPoll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.strawConvinced){
                    strawResult = "Convinced";
                } else {
                    strawResult = "NotYet";
                }
            }
        });

        btnSubmitStraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerDB.child(playerKey).child("strawResult").setValue(strawResult);
                if (strawResult == "Convinced"){
                    numStrawCon++;
                } else {
                    numStrawNot++;
                }
                mainClaimDB.child("mc").child("numStrawCon").setValue(numStrawCon);
                mainClaimDB.child("mc").child("numStrawNot").setValue(numStrawNot);
                btnSubmitStraw.setVisibility(View.INVISIBLE);
            }
        });

        getData();
    }

    //load Data from firebase
    public void getData(){

        //load main claim
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child: snapshot.getChildren()){
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                try {
                    tvMainClaim.setText(mainClaim.mc);
                    numStrawCon = mainClaim.numStrawCon;
                    numStrawNot = mainClaim.numStrawNot;
                } catch (Exception e){
                    tvMainClaim.setText("**Wait**" + "\n" + "Referee has not set the Main Claim");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //load voting time
        settingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_GameSetting votingMin = null;
                votingMin = snapshot.getValue(DB_GameSetting.class);


                try {
                    //get time from setting page
                    if(timerTask != null){
                        timerTask.cancel();
                        timerTask = null;
                    }

                    //set timer
                    final int[] votingCount = {votingMin.votingTime};

                    timerTask = new TimerTask() {

                        @Override
                        public void run() {
                            votingCount[0] -= 1;
                            tvRemainTime.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvRemainTime.setText("seconds remaining: " + votingCount[0]);
                                }
                            });
                            if(votingCount[0] == 0){
                                timerTask.cancel();
                                startActivity(new Intent(StrawpollActivity.this, StrawpollResultActivity.class));
                                if (!getIntent().hasExtra("refereeName")) {
                                    playerDB.child(playerKey).child("strawResult").setValue(strawResult);
                                }
                            }

                        }
                    };
                    timer.schedule(timerTask, 0, 1000);

                    btnSetTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            settingDB.child("votingTime").setValue(votingCount[0] + 15);
                        }
                    });

//                    timer = new CountDownTimer(votingMin.votingTime * 60 * 1000, 1000) {
//
//                        public void onTick(long millisUntilFinished) {
//                            tvRemainTime.setText("seconds remaining: " + millisUntilFinished / 1000);
//                        }
//
//                        public void onFinish() {
//                            tvRemainTime.setText("time is over!");
//                            startActivity(new Intent(StrawpollActivity.this, StrawpollResultActivity.class));
//                            playerDB.child(playerKey).child("strawResult").setValue(strawResult);
//                        }
//                    }.start();
                } catch (Exception e){
                    tvRemainTime.setText("**Wait**" + "\n" + "Instructor has not set the Time");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load player information
        playerDB.child(playerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Player playerInfo = null;
                    playerInfo = snapshot.getValue(DB_Player.class);
//                }
//                tvPlayerName.setText("Welcome, " + playerInfo.name + "\n" + "Player Number: " + playerInfo.numPlayer);
                tvPlayerName.setText("Welcome, " + playerInfo.name + "\n");

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}