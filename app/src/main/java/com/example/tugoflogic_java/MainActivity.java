package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");
    DatabaseReference settingDB = firebaseDatabase.getReference("GameSetting");
//    DatabaseReference boutDB = mDatabase.child("Bout");
    DatabaseReference boutDB = firebaseDatabase.getReference("Bout");
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    TextView tvMainClaim, tvPlayer, tvPlayerStraw, tvRipTitle,
            tvRip, tvNumEstablished, tvNumContested, tvPerGround, tvListGround, tvListDrop,
            tvComments;
    Button btnStartBout, btnEndGame, btnSubRip, btnSubCom, btnChooseRip, btnReRip;
    ToggleButton btnToggleCom, btnShowList;
    EditText editTxtRip;
    LinearLayout linearLayoutPlayer;

    public String txtRipTitle, playerKey, votingRip, votingGround, listGround ="", listDrop="";
    public Integer currentBoutNum = 1, totPlayerNum = 1, numContested = 0, numEstablished = 0, numGround = 0;
    public RadioGroup rg_voteRipe, rg_voteGround;


    //Dialog
    Dialog dialAllList;

    //bar chart
    BarChart barChart;
    int[] colorArray = new int[]{Color.BLUE, Color.RED};
    ArrayList<BarEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerKey = currentPlayer.getUid();

        Intent mIntent = getIntent();
        totPlayerNum = mIntent.getExtras().getInt("totNum");

        //delete voting time here to prevent to back to result page
        settingDB.child("votingTime").removeValue();



        tvMainClaim = findViewById(R.id.mainMC);
        tvPlayer = findViewById(R.id.mainPlayerName);
//        tvPlayerNum = findViewById(R.id.mainPlayerNum);
        tvPlayerStraw = findViewById(R.id.tvPlayerStraw);
        tvRipTitle = findViewById(R.id.ripTitle);
        txtRipTitle = getString(R.string.title_rip);
        tvComments = findViewById(R.id.tvComments);

//        barChart = findViewById(R.id.mainBarChart);
        rg_voteRipe = findViewById(R.id.voteRip);
        editTxtRip = findViewById(R.id.editTxtRip);
        tvRip = findViewById(R.id.txtViewRip);
        tvNumEstablished = findViewById(R.id.tvNumEstablished);
        tvNumContested = findViewById(R.id.tvNumConRip);
        rg_voteGround = findViewById(R.id.voteGround);
        tvPerGround = findViewById(R.id.tvPerSufficient);
        tvListGround = findViewById(R.id.txtViewGround);
        tvListDrop = findViewById(R.id.txtViewDrop);

        btnStartBout = findViewById(R.id.btnStartBout);
        btnEndGame = findViewById(R.id.btnEndGame);
        btnSubRip = findViewById(R.id.btnSubRip);
        btnSubCom = findViewById(R.id.btnSubCom);
        btnShowList = findViewById(R.id.btnShowList);
        btnToggleCom = findViewById(R.id.btnToggleCom);
        btnChooseRip = findViewById(R.id.btnChooseRip);
        btnReRip = findViewById(R.id.btnReRip);

        linearLayoutPlayer = findViewById(R.id.layoutPlayer);

        dialAllList = new Dialog(MainActivity.this);


        getData();

        //toggle button for comment
        btnToggleCom.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            settingDB.child("showComment").setValue(true);
                        } else {
                            settingDB.child("showComment").setValue(false);
                        }
                    }
                }
        );

        //toggle button for list
        btnShowList.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            settingDB.child("showList").setValue(true);
                        } else {
                            settingDB.child("showList").setValue(false);
                        }
                    }
                }
        );


        //submit Rip (student)
        btnSubRip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerRip = editTxtRip.getText().toString();
                playerDB.child(playerKey).child("playerRip").setValue(playerRip);
                btnSubRip.setVisibility(View.INVISIBLE); //after choosing rip they can't
                editTxtRip.setText(""); //reset
            }
        });

        //choose rip (referee)
        btnChooseRip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), RiplistActivity.class);
                mIntent.putExtra("boutNum", currentBoutNum);
                startActivity(new Intent(getApplicationContext(), RiplistActivity.class));
            }
        });

        //rip voting
        rg_voteRipe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtnTureRip){
                    votingRip = "Established";
                } else {
                    votingRip = "Contested";
                }
            }
        });
        //ground voting
        rg_voteGround.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.groundYes){
                    votingGround = "Yes";
                } else {
                    votingGround = "No";
                }
            }
        });

        btnSubCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnSubCom.setVisibility(View.INVISIBLE);

                playerDB.child(playerKey).child("votingRip").setValue(votingRip);
                playerDB.child(playerKey).child("ground").setValue(votingGround);

                if (votingRip == "Established"){
                    numEstablished++;
                } else {
                    numContested++;
                }
                if (votingGround == "Yes"){
                    numGround++;
                }

                boutDB.child(currentBoutNum.toString()).child("numEstablished").setValue(numEstablished);
                boutDB.child(currentBoutNum.toString()).child("numContested").setValue(numContested);
                boutDB.child(currentBoutNum.toString()).child("numGround").setValue(numGround);
            }
        });

        //next bout
        btnStartBout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBoutNum++;

                DB_Bout settingBout = new DB_Bout(currentBoutNum, "", "", 0, 0, 0);
                boutDB.child(String.valueOf(currentBoutNum)).setValue(settingBout);
                settingDB.child("goNextBout").setValue(true);

            }
        });

        btnEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDB.child("endGame").setValue(true);

//                startActivity(new Intent(getApplicationContext(), FinalpollActivity.class));
            }
        });
    }

    //load Data from firebase
    public void getData() {

        //load main claim
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                tvMainClaim.setText(mainClaim.mc);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //game setting
        settingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_GameSetting settingInfo = null;
                settingInfo = snapshot.getValue(DB_GameSetting.class);

                if (settingInfo.endGame){
                    startActivity(new Intent(MainActivity.this, FinalpollActivity.class));
                }
                if (settingInfo.goNextBout){
                    btnSubRip.setVisibility(View.VISIBLE);
                    btnSubCom.setVisibility(View.VISIBLE);
                    playerDB.child(playerKey).child("playerRip").setValue("");
                    playerDB.child(playerKey).child("votingRip").setValue("");
                    //move rip to ground list
//                    if (Integer.parseInt(tvNumEstablished.getText().toString()) >= Integer.parseInt(tvNumContested.getText().toString())){
//                        listGround += tvRip.getText().toString() + "\n";
//                        tvListGround.setText(tvRip.getText().toString());
//                    } else {
//                        listDrop += tvRip.getText().toString() + "\n";
//                        tvListDrop.setText(tvRip.getText().toString());
//                    }
                }
                if (settingInfo.showList){
                    dialAllList.setContentView(R.layout.dialog_all_list);
                    dialAllList.show();
                } else {
                    dialAllList.dismiss();
                }
                //Displaying comments
                if (settingInfo.showComment){
                    tvComments.setVisibility(View.VISIBLE);
                } else {
                    tvComments.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //load player
        playerDB.child(playerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DB_Player playerInfo = null;
                playerInfo = snapshot.getValue(DB_Player.class);
                //Referee
                if (playerInfo.isReferee){
                    tvPlayer.setText("Referee, " + playerInfo.name);
                    btnSubCom.setVisibility(View.INVISIBLE);
                    btnSubRip.setVisibility(View.INVISIBLE);
                } else { //student
                    tvPlayer.setText(playerInfo.name);
                    btnToggleCom.setVisibility(View.INVISIBLE);
                    btnShowList.setVisibility(View.INVISIBLE);
                    btnStartBout.setVisibility(View.INVISIBLE);
                    btnEndGame.setVisibility(View.INVISIBLE);
                    btnChooseRip.setVisibility(View.INVISIBLE);
                    btnReRip.setVisibility(View.INVISIBLE);

                    if (playerInfo.strawResult == "Convinced") {
                        tvPlayerStraw.setText("Straw poll: Convinced");
                    } else {
                        tvPlayerStraw.setText("Straw poll: Not Yet Persuaded");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //load bout
        boutDB.child(String.valueOf(currentBoutNum)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                DB_Bout boutInfo = null;
                boutInfo = snapshot.getValue(DB_Bout.class);

                tvRipTitle.setText(String.format(txtRipTitle, boutInfo.boutNumber));
                tvRip.setText(boutInfo.rip);
                currentBoutNum = boutInfo.boutNumber;

                Float perGround = Float.valueOf((boutInfo.numGround / totPlayerNum) * 100);
                tvPerGround.setText(perGround.toString());
                if (boutInfo.player.length() != 0){
                    playBout(boutInfo.player);
                }

                tvNumEstablished.setText(boutInfo.numEstablished.toString());
                tvNumContested.setText(boutInfo.numContested.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //setting for each bout
    public void playBout(String currentPlayer){
        if(tvPlayer.getText().toString() == currentPlayer){
            btnSubCom.setVisibility(View.INVISIBLE);
        } else {
            btnSubCom.setVisibility(View.VISIBLE);
        }
        btnSubRip.setVisibility(View.INVISIBLE);

    }

}