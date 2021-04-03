package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RiplistActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");
    DatabaseReference boutDB = firebaseDatabase.getReference("Bout");
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    //recyclerview
    ArrayList<DB_Player> playerRipList;
    RecyclerView recyclerView;
    RipAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    String playerKey;
    Integer boutNum;
    DB_Player selectRip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riplist);

        playerKey = currentPlayer.getUid();
        boutNum = getIntent().getIntExtra("boutNum", 1);


        recyclerView = findViewById(R.id.rvRip);
        layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);

        playerRipList = new ArrayList<>();

        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playerRipList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DB_Player playerInfo = null;
                    playerInfo = dataSnapshot.getValue(DB_Player.class);
                    if (playerInfo.playerRip.length() != 0){
                        playerRipList.add(playerInfo);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new RipAdapter(playerRipList, getBaseContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new RipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectRip = playerRipList.get(position);

                boutDB.child(boutNum.toString()).child("player").setValue(selectRip.name);
                boutDB.child(boutNum.toString()).child("rip").setValue(selectRip.playerRip);

                finish();
            }
        });




    }
}