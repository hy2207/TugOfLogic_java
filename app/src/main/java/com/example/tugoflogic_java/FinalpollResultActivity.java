package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FinalpollResultActivity extends AppCompatActivity {


    private Button btnSummitCom;
    private EditText finalComment;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");

    String playerKey;

    ArrayList<DB_Player> playerRipList;
    RecyclerView recyclerView;
    RipAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    DB_Player selectRip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpoll_result);

        //barChart = findViewById(R.id.barChartFinalResult);
        btnSummitCom = findViewById(R.id.btnSummitFinalCom);
        finalComment = findViewById(R.id.editTxtFinalComment);

        FirebaseUser currentPlayer = FirebaseAuth.getInstance().getCurrentUser();
        playerKey = currentPlayer.getUid();


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

        //show all rip
        adapter.setOnItemClickListener(new RipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                selectRip = playerRipList.get(position);
            }
        });


        // comment to player db
        btnSummitCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db update player comment
               String comment = finalComment.getText().toString();
               playerDB.child(playerKey).child("comment").setValue(comment);
                Toast.makeText(FinalpollResultActivity.this, "Comment Submitted", Toast.LENGTH_SHORT).show();
                finalComment.setText("");
            }
        });
    }
}