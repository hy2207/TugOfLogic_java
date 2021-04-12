package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

public class FinalPollGraph extends AppCompatActivity {

    private Button btnGoToComment;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<BarEntry> entries2 = new ArrayList<>();

    BarChart barChartFirst, barChartSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_poll_graph);

        barChartFirst = findViewById(R.id.barChartFirstResult);
        barChartSecond = findViewById(R.id.barChartSecondResult);

        btnGoToComment = findViewById(R.id.btnGoToComment);



        getFirstGraphData();
        getSecondGraphData();

        btnGoToComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FinalpollResultActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getSecondGraphData() {
        //load main claim
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                //entries2.add(new BarEntry(1.0f, mainClaim.numFinalCon));
                entries2.add(new BarEntry(1.0f, mainClaim.numFinalNew));
                entries2.add(new BarEntry(2.0f,mainClaim.numFinalNot));

               // BarDataSet barDataSet = new BarDataSet(entries2, "Convinced (Left)");
                BarDataSet barDataSet1 = new BarDataSet(entries2, "Newly Convinced (Left)");
                BarDataSet barDataSet2 = new BarDataSet(entries2, "No Longer Persuaded (Right)");

                //initBarSecondDataSet(barDataSet);
                initBarSecondDataSet(barDataSet1);
                initBarSecondDataSet(barDataSet2);

                BarData data = new BarData(barDataSet1,barDataSet2);

                data.setBarWidth(0.3f);
                barChartSecond.setData(data);

                float groupSpace = 0.2f;
                float barSpace = 0.01f;
                float barWidth = 0.45f;

                YAxis leftAxis = barChartSecond.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setSpaceTop(30f);
                Description description = new Description();
                description.setEnabled(false);
                barChartSecond.animateY(1000);
                barChartSecond.setDescription(description);
                barChartSecond.getAxisRight().setEnabled(false);
                barChartSecond.getXAxis().setDrawLabels(false);
                barChartSecond.getBarData().setBarWidth(barWidth);
                barChartSecond.groupBars(0.2f, groupSpace, barSpace);
                barChartSecond.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFirstGraphData() {

        //load main claim
        mainClaimDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DB_MainClaim mainClaim = null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    mainClaim = child.getValue(DB_MainClaim.class);
                }
                entries.add(new BarEntry(1.0f, mainClaim.numStrawCon));
                entries.add(new BarEntry(2.0f, mainClaim.numStrawNot));

                BarDataSet barDataSet = new BarDataSet(entries, "Convinced (Left)");
                BarDataSet barDataSet1 = new BarDataSet(entries, "Not Yet Persuaded (Right)");

                initBarFirstDataSet(barDataSet);
                initBarFirstDataSet(barDataSet1);

                BarData data = new BarData(barDataSet,barDataSet1);

                data.setBarWidth(0.3f);
                barChartFirst.setData(data);

                float groupSpace = 0.2f;
                float barSpace = 0.01f;
                float barWidth = 0.45f;

                YAxis leftAxis = barChartFirst.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setSpaceTop(30f);
                Description description = new Description();
                description.setEnabled(false);

                barChartFirst.animateY(1000);
                barChartFirst.setDescription(description);
                barChartFirst.getAxisRight().setEnabled(false);
                barChartFirst.getXAxis().setDrawLabels(false);
                barChartFirst.getBarData().setBarWidth(barWidth);
                barChartFirst.groupBars(0.2f, groupSpace, barSpace);
                barChartFirst.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initBarFirstDataSet(BarDataSet barDataSet){
        //Changing the color of the bar
        barDataSet.setColor(Color.parseColor("#BF360C"));
    }

    public void initBarSecondDataSet(BarDataSet barDataSet){
        //Changing the color of the bar
        barDataSet.setColor(Color.parseColor("#FF8A65"));
    }
}