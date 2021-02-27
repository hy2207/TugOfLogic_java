package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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
    DatabaseReference boutDB = mDatabase.child("Bout");
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    TextView tvMainClaim, tvPlayer, tvPlayerStraw, tvRipTitle, tvPlayerNum,
            tvRip, tvNumEstablished, tvNumContested, tvPerGround, tvListGround, tvListDrop;
    Button btnStartBout, btnEndGame, btnSubRip, btnSubCom;
    EditText editTxtRip;

    public String txtRipTitle, playerKey, votingRip, votingGround, listGround ="", listDrop="";
    public Integer currentBoutNum, totPlayerNum = 1, numContested = 0, numEstablished = 0, numGround = 0;
    public RadioGroup rg_voteRipe, rg_voteGround;

    //Dialog
    Dialog groundDialog;

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

        //setting bout when this page is opened
        final DB_Bout settingBout = new DB_Bout(1, "", "", 0, 0, 0);
        boutDB.child("currentBout").setValue(settingBout);

        tvMainClaim = findViewById(R.id.mainMC);
        tvPlayer = findViewById(R.id.mainPlayerName);
        tvPlayerNum = findViewById(R.id.mainPlayerNum);
        tvPlayerStraw = findViewById(R.id.tvPlayerStraw);
        tvRipTitle = findViewById(R.id.ripTitle);
        txtRipTitle = getString(R.string.title_rip);
        btnStartBout = findViewById(R.id.btnStartBout);
        btnEndGame = findViewById(R.id.btnEndGame);
        btnSubRip = findViewById(R.id.btnSubRip);
        btnSubCom = findViewById(R.id.btnSubCom);
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
        groundDialog = new Dialog(MainActivity.this);

        getData();

        //bout result
        btnSubRip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentRip = editTxtRip.getText().toString();
                String currentPlayerName = tvPlayer.getText().toString();
                boutDB.child("currentBout").child("rip").setValue(currentRip);
                boutDB.child("currentBout").child("player").setValue(currentPlayerName);
                boutDB.child("currentBout").child("numEstablished").setValue(0);
                boutDB.child("currentBout").child("numContested").setValue(0);
                boutDB.child("currentBout").child("numGround").setValue(0);

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
                boutDB.child("currentBout").child("numEstablished").setValue(numEstablished);
                boutDB.child("currentBout").child("numContested").setValue(numContested);
                boutDB.child("currentBout").child("numGround").setValue(numGround);
            }
        });

        //next bout
        btnStartBout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBoutNum++;
                boutDB.child("currentBout").child("boutNumber").setValue(currentBoutNum);
                settingDB.child("goNextBout").setValue(true);

            }
        });
        //display all lists of ground  --> still working on
        tvListGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groundDialog.setContentView(R.layout.dialog_ground);
                groundDialog.show();
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
                    //move rip to ground list
                    if (Integer.parseInt(tvNumEstablished.getText().toString()) >= Integer.parseInt(tvNumContested.getText().toString())){
                        listGround += tvRip.getText().toString() + "\n";
                        tvListGround.setText(tvRip.getText().toString());
                    } else {
                        listDrop += tvRip.getText().toString() + "\n";
                        tvListDrop.setText(tvRip.getText().toString());
                    }
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
                //instructor
                if (playerInfo.numPlayer == 0){
                    tvPlayer.setText("Instructor");
                } else { //student
                    tvPlayer.setText(playerInfo.name);
                    tvPlayerNum.setText(String.valueOf(playerInfo.numPlayer));
                    btnStartBout.setVisibility(View.INVISIBLE);
                    btnEndGame.setVisibility(View.INVISIBLE);
                    if (playerInfo.strawResult == "Convinced") {
                        tvPlayerStraw.setText("Straw poll: Convinced");
                    } else {
                        tvPlayerStraw.setText("Straw poll: Not Yet Persuaded");
                    }
                }

                //

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load bout
        boutDB.child("currentBout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numEstablished = 0;
                numContested = 0;
                DB_Bout boutInfo = null;
//                for (DataSnapshot child: snapshot.getChildren()){
                    boutInfo = snapshot.getValue(DB_Bout.class);
//                }
                tvRipTitle.setText(String.format(txtRipTitle, boutInfo.boutNumber));
                tvRip.setText(boutInfo.rip);
                currentBoutNum = boutInfo.boutNumber;
                Float perGround = Float.valueOf((boutInfo.numGround / totPlayerNum) * 100);
                tvPerGround.setText(perGround.toString());
                playBout(currentBoutNum);
                tvNumEstablished.setText(boutInfo.numEstablished.toString());
                tvNumContested.setText(boutInfo.numContested.toString());

//                entries.add(new BarEntry(1.0f, boutInfo.numEstablished ));
//                entries.add(new BarEntry(2.0f, boutInfo.numContested));
//
//                BarDataSet barDataSet = new BarDataSet(entries, "Number of Student");
//                initBarDataSet(barDataSet);
//                BarData data = new BarData(barDataSet);
//                data.setBarWidth(0.3f);
//                barChart.setData(data);
//                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //setting for each bout
    public void playBout(Integer currentbout){
        if (Integer.parseInt(tvPlayerNum.getText().toString()) == 0){
            btnSubCom.setVisibility(View.INVISIBLE);
            btnSubRip.setVisibility(View.INVISIBLE);
        } else {
            if(currentbout % totPlayerNum == Integer.parseInt(tvPlayerNum.getText().toString())){
                btnSubRip.setVisibility(View.VISIBLE);
                btnSubCom.setVisibility(View.INVISIBLE);

            } else {
                btnSubRip.setVisibility(View.INVISIBLE);
                btnSubCom.setVisibility(View.VISIBLE);
            }
        }

    }

    public void initBarDataSet(BarDataSet barDataSet){
        //Changing the color of the bar
        barDataSet.setColor(Color.parseColor("#BF360C"));
        //Setting the size of the form in the legend
        barDataSet.setFormSize(15f);
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false);
        //setting yaxis
        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0f);
        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);
        //hide labels of the axis
        barChart.getXAxis().setDrawLabels(false);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(1000);
    }
}