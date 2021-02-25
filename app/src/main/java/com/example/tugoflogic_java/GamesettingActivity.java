package com.example.tugoflogic_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GamesettingActivity extends AppCompatActivity {


    TextView instructorName, countdownTxt;
    EditText editTxtTime, mainClaim;
    Button btnGoToGameMain;
    Integer votingTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamesetting);

        instructorName = findViewById(R.id.instructorName);
        editTxtTime = findViewById(R.id.editTxtTime);
        btnGoToGameMain = findViewById(R.id.btnGoToGameMain);
        countdownTxt = findViewById(R.id.countDownTxt);
        mainClaim = findViewById(R.id.editTxtMC);

        Intent intent = getIntent();
        String name = intent.getStringExtra("instructorName");
        instructorName.setText("Welcome Instructor : " +"\n"+ name);


        btnGoToGameMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editTxtTime.getText().toString();
                DateFormat format = new SimpleDateFormat("mm:ss");
                try {
                    Date date = format.parse(text);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(!text.equalsIgnoreCase("")) {
                    final int sec = Integer.valueOf(text);
                    CountDownTimer countDownTimer = new CountDownTimer(sec * 1000, 1000) {
                        @Override
                        public void onTick(long millils) {
                            countdownTxt.setText("Minutes : " +  new SimpleDateFormat("mm:ss").format(new Date(millils)));
                        }
                        @Override
                        public void onFinish() {
                            countdownTxt.setText("Finished");
                            startActivity(new Intent(getApplicationContext(), StrawpollResultActivity.class));
                        }
                    }.start();
                }


                Intent intentS = new Intent(getApplicationContext(), StrawpollResultActivity.class);
                intentS.putExtra("mc", mainClaim.getText().toString());
                startActivity(intentS);
            }
        });




    }
}