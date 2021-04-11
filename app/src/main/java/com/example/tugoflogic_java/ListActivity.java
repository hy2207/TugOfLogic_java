package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference boutDB = firebaseDatabase.getReference("Bout");
    DatabaseReference ripDB = firebaseDatabase.getReference("Rip");
    DatabaseReference settingDB = firebaseDatabase.getReference("GameSetting");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");

    //recyclerview
    ArrayList<DB_Rip> groundList, dropList;
    RecyclerView rvGround, rvDrop;
    ListAdapter adapterGround, adapterDrop;
    RecyclerView.LayoutManager layoutManager;

    Integer boutNum;
    DB_Rip selectRip;
    ArrayList<String> playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        boutNum = getIntent().getIntExtra("boutNum", 1);

        rvGround = findViewById(R.id.rvGround);
        rvDrop = findViewById(R.id.rvDrop);
        layoutManager = new LinearLayoutManager(getBaseContext());
        rvGround.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvDrop.setLayoutManager(new LinearLayoutManager(getBaseContext()));
//        rvDrop.setLayoutManager(layoutManager);

        groundList = new ArrayList<>();
        dropList = new ArrayList<>();

        ripDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groundList.clear();
                dropList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DB_Rip ripInfo = null;
                    ripInfo = dataSnapshot.getValue(DB_Rip.class);
                    if (ripInfo.playerName.length() != 0 && ripInfo.isGround){
                        //ground list
                        groundList.add(ripInfo);
                    } else if (ripInfo.playerName.length() != 0) {
                        dropList.add(ripInfo);
                    }
                }
                adapterGround.notifyDataSetChanged();
                adapterDrop.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //get player list
        playerList = new ArrayList();
        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DB_Player playerInfo = null;
                    playerInfo = dataSnapshot.getValue(DB_Player.class);
                    if (!playerInfo.isReferee){
                        playerList.add(playerInfo.name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapterGround = new ListAdapter(groundList, getBaseContext());
        rvGround.setAdapter(adapterGround);
        adapterDrop = new ListAdapter(dropList, getBaseContext());
        rvDrop.setAdapter(adapterDrop);

        //choose rip
        adapterGround.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                selectRip = groundList.get(position);

                boutDB.child(boutNum.toString()).child("rip").setValue(selectRip.rip);

                settingDB.child("goNextBout").setValue(false);

                //dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(ListActivity.this);
                dlg.setTitle("Choose player Name");
                final String[] reAssignPlayerName = playerList.toArray(new String[playerList.size()]);

                dlg.setItems(reAssignPlayerName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boutDB.child(boutNum.toString()).child("player").setValue(reAssignPlayerName[which]);
                    }
                });
                dlg.show();
            }
        });

    }
}