package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StrawpollResultActivity extends AppCompatActivity {

    private TextView tvMainClaim;
    private Button btnStartGame;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");

    //for bar chart
    ArrayList<BarEntry> entries = new ArrayList<>();

    BarChart barChart;
    Integer totPlayerNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strawpoll_result);

        tvMainClaim = findViewById(R.id.txtViewPollResultMC);

        barChart = findViewById(R.id.barChartResult);
        btnStartGame = findViewById(R.id.btnStartGame);

        getData();
        //load total number of player
        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totPlayerNum = Integer.parseInt(String.valueOf(snapshot.getChildrenCount())) - 1; //except instructor
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), MainActivity.class);
                mIntent.putExtra("totNum", totPlayerNum);
                startActivity(mIntent);
//                startActivity(new Intent(StrawpollResultActivity.this, MainActivity.class));
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
                entries.add(new BarEntry(1.0f, mainClaim.numStrawCon));
                entries.add(new BarEntry(2.0f, mainClaim.numStrawNot));

                BarDataSet barDataSet = new BarDataSet(entries, "Number of Student");
                initBarDataSet(barDataSet);
                BarData data = new BarData(barDataSet);
                data.setBarWidth(0.3f);
                barChart.setData(data);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initBarDataSet(BarDataSet barDataSet){
        //Changing the color of the bar
        barDataSet.setColor(Color.parseColor("#BF360C"));
        //Setting the size of the form in the legend
        barDataSet.setFormSize(15f);
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false);
        //setting the text size of the value of the bar
//        barDataSet.setValueTextSize(15f);
        //remove the description label text located at the lower right corner

        //setting yaxis
        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0f);
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);
        //hide labels of the axis
        barChart.getXAxis().setDrawLabels(false);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(1000);


    }
}