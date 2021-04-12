package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();

    //recyclerview
    ArrayList<DB_Rip> groundList, dropList;
    RecyclerView rvGround, rvDrop;
    ListAdapter adapterGround, adapterDrop;
    RecyclerView.LayoutManager layoutManager;

    Integer boutNum, yesGround = 0, noGround = 0, totPlayerNum;
    DB_Rip selectRip;
    ArrayList<String> playerList;
    String playerKey;
    Boolean isReferee = false;
    Button btnVoteGround, btnBackMain;
    Double perGround;

    TextView tvPerGround;
    AlertDialog.Builder dlgVoting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //prevent reload
        settingDB.child("goNextBout").setValue(false);
        settingDB.child("isReassign").setValue(false);
        settingDB.child("showList").setValue(false);

        playerKey = currentPlayer.getUid();
        boutNum = getIntent().getIntExtra("boutNum", 1);
        btnVoteGround = findViewById(R.id.btnVoteGround);
        btnBackMain = findViewById(R.id.btnBack);
        tvPerGround = findViewById(R.id.tvPerGround);

        rvGround = findViewById(R.id.rvGround);
        rvDrop = findViewById(R.id.rvDrop);
        layoutManager = new LinearLayoutManager(getBaseContext());
        rvGround.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        rvDrop.setLayoutManager(new LinearLayoutManager(getBaseContext()));
//        rvDrop.setLayoutManager(layoutManager);

        groundList = new ArrayList<>();
        dropList = new ArrayList<>();



        //set dialog
        dlgVoting = new AlertDialog.Builder(ListActivity.this);
        dlgVoting.setTitle("Voting Ground");
        dlgVoting.setMessage("Have these GROUNDS become \n sufficient to prove the MC");
        dlgVoting.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yesGround++;
                mainClaimDB.child("mc").child("numGround").setValue(yesGround);
            }
        });

        dlgVoting.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noGround++;
            }
        });

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
                    totPlayerNum = Integer.parseInt(String.valueOf(snapshot.getChildrenCount())) - 1; //except instructor
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

        playerDB.child(playerKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_Player playerInfo = null;
                playerInfo = snapshot.getValue(DB_Player.class);
                isReferee = playerInfo.isReferee;
                if (isReferee){
                    //call adapter listener (referee)
                    callAdapterListener();
                } else {
                    btnVoteGround.setVisibility(View.INVISIBLE);
                    btnBackMain.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //VOTING GROUND
        btnVoteGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDB.child("voteGround").setValue(true);
            }
        });

        //back to main
        btnBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDB.child("goMain").setValue(true);
                settingDB.child("showList").setValue(false);

            }
        });

        //voting ground & back to main
        settingDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_GameSetting settingInfo = null;
                settingInfo = snapshot.getValue(DB_GameSetting.class);
                if (settingInfo.goMain){
                    Intent mIntent = new Intent(ListActivity.this, MainActivity.class);
                    mIntent.putExtra("perGround", perGround);
                    startActivity(mIntent);
                }
                if (settingInfo.voteGround && !isReferee){
                    dlgVoting.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //get number of voting ground
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child: snapshot.getChildren()){
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                yesGround = mainClaim.numGround;
//                if (totPlayerNum == 0){
//                    perGround = 0.0;
//                } else {
                    perGround = (yesGround * 100.0) / totPlayerNum ;
//                }
                Log.e("Hwayoung", yesGround.toString());
                Log.e("Hwayoung tot", totPlayerNum.toString());
                tvPerGround.setText(String.format("%.1f", perGround));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void callAdapterListener() {
        //choose rip
        adapterGround.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                selectRip = groundList.get(position);

                boutDB.child(boutNum.toString()).child("rip").setValue(selectRip.rip);

                //dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(ListActivity.this);
                dlg.setTitle("Choose player Name");
                final String[] reAssignPlayerName = playerList.toArray(new String[playerList.size()]);

                dlg.setItems(reAssignPlayerName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boutDB.child(boutNum.toString()).child("player").setValue(reAssignPlayerName[which]);
                        finish();
                    }
                });
                dlg.show();

            }
        });

        adapterDrop.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, final int position) {
                selectRip = dropList.get(position);

                boutDB.child(boutNum.toString()).child("rip").setValue(selectRip.rip);

                //dialog
                AlertDialog.Builder dlg = new AlertDialog.Builder(ListActivity.this);
                dlg.setTitle("Choose player Name");
                final String[] reAssignPlayerName = playerList.toArray(new String[playerList.size()]);

                dlg.setItems(reAssignPlayerName, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boutDB.child(boutNum.toString()).child("player").setValue(reAssignPlayerName[which]);
                        finish();
                    }
                });
                dlg.show();

            }
        });
    }
}