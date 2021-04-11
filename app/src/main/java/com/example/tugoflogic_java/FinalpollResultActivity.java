package com.example.tugoflogic_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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
    DatabaseReference mainClaimDB = firebaseDatabase.getReference("MainClaim");
    DatabaseReference playerDB = firebaseDatabase.getReference("Player");


    ArrayList<BarEntry> entries = new ArrayList<>();
    BarChart barChart;
    Integer totPlayerNum;
    ArrayList barEntries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalpoll_result);

        barChart = findViewById(R.id.barChartFinalResult);
        btnSummitCom = findViewById(R.id.btnSummitFinalCom);

        getData();

        playerDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totPlayerNum = Integer.parseInt(String.valueOf(snapshot.getChildrenCount())) - 1; //except instructor
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSummitCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //db update player comment
               // playerDB.child("comment").setValue()
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
                entries.add(new BarEntry(1.0f, mainClaim.numStrawCon));
                entries.add(new BarEntry(2.0f, mainClaim.numStrawNot));

                BarDataSet barDataSet = new BarDataSet(getBarEntriesZero(), "Number of Student");
                BarDataSet barDataSet1 = new BarDataSet(getBarEntriesOne(), "Newly Convinced");
                BarDataSet barDataSet2 = new BarDataSet(getBarEntriesTwo(), "No Longer Persuaded");

                initBarDataSet(barDataSet);
                barDataSet.setColor(Color.parseColor("#BF360C"));
                initBarDataSet(barDataSet1);
                barDataSet1.setColor(Color.rgb(104,241,175));
                initBarDataSet(barDataSet2);
                barDataSet2.setColor(Color.rgb(164,221,251));

                BarData data = new BarData(barDataSet,barDataSet1,barDataSet2);
                data.setBarWidth(0.3f);
                barChart.setData(data);
                float groupSpace = 1.3f;
                float barSpace = 0.05f;
                float barWidth = 0.2f;


                YAxis leftAxis = barChart.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setSpaceTop(30f);
                barChart.getAxisRight().setEnabled(false);
                barChart.getBarData().setBarWidth(barWidth);
                barChart.groupBars(0, groupSpace, barSpace);
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
        barDataSet.setFormSize(8f);
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

    // array list for zero set.
    private ArrayList<BarEntry> getBarEntriesZero() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, 8));
        return barEntries;
    }

    // array list for first set
    private ArrayList<BarEntry> getBarEntriesOne() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, 5));
        return barEntries;
    }

    // array list for second set.
    private ArrayList<BarEntry> getBarEntriesTwo() {

        // creating a new array list
        barEntries = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(new BarEntry(1f, 3));
        return barEntries;
    }
}