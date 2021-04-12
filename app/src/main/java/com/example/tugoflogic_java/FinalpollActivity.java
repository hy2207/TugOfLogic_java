package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button btnSubmitFinal, btnShowGraph;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = firebaseDatabase.getReference();
    DatabaseReference mcDB = mDatabase.child("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");

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
        btnShowGraph = findViewById(R.id.btnShowFinalGraph);

        // final voting step
        rg_finalPoll.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.finalStillConvinced){
                    finalResult = "Still Convinced"; // does not need to show final poll result b/c initial straw poll result is same.
                }if(checkedId == R.id.finalNewConvinced){
                    finalResult = "Newly Convinced";
                }if(checkedId == R.id.finalNotYetPer){
                    finalResult = "No Longer Persuaded";
                }
            }
        });
        btnShowGraph.setVisibility(View.INVISIBLE);

        // only interested in newly convinced and no longer persuaded so only show graph to two group
        btnSubmitFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerDB.child(playerKey).child("finalResult").setValue(finalResult);
                if(finalResult == "Still Convinced"){
                    numFinalCon++;
                    Toast.makeText(FinalpollActivity.this, "Still Convinced Submitted", Toast.LENGTH_SHORT).show();
                    btnShowGraph.setText("Go To Comment");
                }if(finalResult == "Newly Convinced"){
                    numFinalNew++;
                    Toast.makeText(FinalpollActivity.this, "Newly Convinced Submitted", Toast.LENGTH_SHORT).show();
                    btnShowGraph.setText("Show Graph");
                }if(finalResult == "No Longer Persuaded"){
                    numFinalNot++;
                    Toast.makeText(FinalpollActivity.this, "No Longer Persuaded Submitted", Toast.LENGTH_SHORT).show();
                    btnShowGraph.setText("Show Graph");
                }
                mcDB.child("mc").child("numFinalCon").setValue(numFinalCon);
                mcDB.child("mc").child("numFinalNot").setValue(numFinalNot);
                mcDB.child("mc").child("numFinalNew").setValue(numFinalNew);
                btnSubmitFinal.setVisibility(View.INVISIBLE);
                btnShowGraph.setVisibility(View.VISIBLE);
            }
        });

        btnShowGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalResult == "Still Convinced") {
                    Intent intent = new Intent(getApplicationContext(), FinalpollResultActivity.class);
                    startActivity(intent);
                }
                if(finalResult == "Newly Convinced") {
                    Intent intent = new Intent(getApplicationContext(), FinalPollGraph.class);
                    startActivity(intent);
                }
                if(finalResult == "No Longer Persuaded") {
                    Intent intent = new Intent(getApplicationContext(), FinalPollGraph.class);
                    startActivity(intent);
                }
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
                tvPlayerName.setText("Final Poll, " + player.name + "\n");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}