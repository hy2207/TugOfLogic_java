package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
    DatabaseReference boutDB = mDatabase.child("Bout");
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    TextView tvMainClaim, tvPlayer, tvPlayerStraw, tvRipTitle, tvPlayerNum, tvRip;
    String txtRipTitle;
    Button btnStartBout, btnEndGame, btnSubRip, btnSubCom;
    EditText editTxtRip;

    public String playerKey, votingRip;
    public Integer currentBoutNum, totPlayerNum = 1, numContested = 0, numEstablished = 0;
    public RadioGroup rg_voteRipe;

    //pie chart
    PieChart pieChart;
    int[] colorArray = new int[]{Color.BLUE, Color.RED};
    ArrayList<PieEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerKey = currentPlayer.getUid();

        Intent mIntent = getIntent();
        totPlayerNum = mIntent.getExtras().getInt("totNum");

        //setting bout when this page is opened
        DB_Bout settingBout = new DB_Bout(1, "", "", 0, 0);
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
        pieChart = findViewById(R.id.pieChartResult);
        rg_voteRipe = findViewById(R.id.voteRip);
        editTxtRip = findViewById(R.id.editTxtRip);
        tvRip = findViewById(R.id.txtViewRip);

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
            }
        });
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

        btnSubCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerDB.child(playerKey).child("votingRip").setValue(votingRip);
                if (votingRip == "Established"){
                    numEstablished++;
                } else {
                    numContested++;
                }
                boutDB.child("currentBout").child("numEstablished").setValue(numEstablished);
                boutDB.child("currentBout").child("numContested").setValue(numContested);
            }
        });

        //next bout
        btnStartBout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentBoutNum++;
                boutDB.child("currentBout").child("boutNumber").setValue(currentBoutNum);
            }
        });
        btnEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FinalpollActivity.class));
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load bout
        boutDB.child("currentBout").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Bout boutInfo = null;
//                for (DataSnapshot child: snapshot.getChildren()){
                    boutInfo = snapshot.getValue(DB_Bout.class);
//                }
                tvRipTitle.setText(String.format(txtRipTitle, boutInfo.boutNumber));
                tvRip.setText(boutInfo.rip);
                currentBoutNum = boutInfo.boutNumber;
                playBout(currentBoutNum);

                entries.add(new PieEntry(boutInfo.numEstablished, "Established"));
                entries.add(new PieEntry(boutInfo.numContested, "Contested"));

                PieDataSet pieDataSet = new PieDataSet(entries, "Number of Student");
                initPieDataSet(pieDataSet);
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.invalidate();;
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

    public void initPieDataSet(PieDataSet pieDataSet){
        pieDataSet.setColors(colorArray);
    }
}