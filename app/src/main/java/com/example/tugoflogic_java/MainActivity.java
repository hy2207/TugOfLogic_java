package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");
    DatabaseReference boutDB = firebaseDatabase.getReference("Bout");

    TextView tvMainClaim, tvPlayer, tvPlayerStraw, tvRipTitle;
    String txtRipTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMainClaim = findViewById(R.id.mainMC);
        tvPlayer = findViewById(R.id.mainPlayerName);
        tvPlayerStraw = findViewById(R.id.tvPlayerStraw);
        tvRipTitle = findViewById(R.id.ripTitle);
        txtRipTitle = getString(R.string.title_rip);

        getData();
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
        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Player playerInfo = null;
                for (DataSnapshot child: snapshot.getChildren()){
                    playerInfo = child.getValue(DB_Player.class);
                }
                tvPlayer.setText(playerInfo.name);
                if (playerInfo.strawResult == "Convinced"){
                    tvPlayerStraw.setText("Straw poll: Convinced" );
                } else {
                    tvPlayerStraw.setText("Straw poll: Not Yet Persuaded");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //load bout
        boutDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Bout boutInfo = null;
                for (DataSnapshot child: snapshot.getChildren()){
                    boutInfo = child.getValue(DB_Bout.class);
                }
                tvRipTitle.setText(String.format(txtRipTitle, boutInfo.boutNumber));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}