package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Purpose: Final poll for player
 */

public class FinalpollActivity extends AppCompatActivity {

    private TextView tvMainClaim, tvPlayerName;
    private RadioGroup rg_finalPoll;
    private Button btnSubmitFinal;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    DatabaseReference mcDB = mDatabase.child("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");
    //DatabaseReference settingDB = mDatabase.child("GameSetting");

    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    public Integer numFinalCon = 0;
    public Integer numFinalNot = 0;
    public Integer numFinalNew = 0;
    public String finalResult = "Still Convinced";
    public String playerKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpoll);

        playerKey = currentPlayer.getUid();
        tvMainClaim = findViewById(R.id.txtViewFinalGetMc);
        tvPlayerName = findViewById(R.id.txtViewFinalPlayerName);

        rg_finalPoll = findViewById(R.id.voteFinalPoll);
        btnSubmitFinal = findViewById(R.id.btnSubmitFinal);

        // final voting step
        rg_finalPoll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.finalNewConvinced){
                    finalResult = "Still Convinced"; // does not need to show final poll result b/c initial straw poll result is same.
                }if(checkedId == R.id.finalNewConvinced){
                    finalResult = "Newly Convinced";
                }if(checkedId == R.id.finalNotYetPer){
                    finalResult = "No Longer Persuaded";
                }
            }
        });

        btnSubmitFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerDB.child(playerKey).child("finalResult").setValue(finalResult);
                if(finalResult == "Still Convinced"){
                    numFinalCon++;
                }if(finalResult == "Newly Convinced"){
                    numFinalNew++;
                }if(finalResult == "No Longer Persuaded"){
                    numFinalCon++;
                }
                mcDB.child("mc").child("numFinalCon").setValue(numFinalCon);
                mcDB.child("mc").child("numFinalNot").setValue(numFinalNot);
                mcDB.child("mc").child("numFinalNew").setValue(numFinalNew);
            }
        });

        getFinalData();
    }

    public void getFinalData() {
        mcDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DB_MainClaim mc = null;
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    mc = child.getValue(DB_MainClaim.class);
                }
                try {
                    tvMainClaim.setText(mc.mc);
                    numFinalCon = mc.numFinalCon;
                    numFinalNew = mc.numFinalNew;
                    numFinalNot = mc.numFinalNot;
                }catch (Exception e){
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        playerDB.child(playerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DB_Player player = null;
                player = dataSnapshot.getValue(DB_Player.class);
                tvPlayerName.setText("Let's Start Final Poll " + player.name + "\n");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}