package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class MemoPicker extends AppCompatActivity {
    DatePicker datePicker;
    TimePicker timePicker;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int AMPMhour;
    String AMPM;
    TextView dateTextview;
    TextView timeTextview;
    Button modifyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_picker);

        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        dateTextview = findViewById(R.id.dateTextView);
        timeTextview = findViewById(R.id.timeTextView);

        year = 2013;
        month = 10;
        day = 20;

        hour = 10;
        minute = 11;
        if(hour>=12){
            AMPM = "오후";
            if(hour==12){
                AMPMhour = 12;
            }
            else{
                AMPMhour = hour-12;
            }

        }
        else {
            AMPM = "오전";
            AMPMhour = hour;
        }
        dateTextview.setText(year+"년 "+month+"월 "+day+"일");
        timeTextview.setText(AMPM+" "+AMPMhour+"시 "+minute+"분");
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int yearofDate, int monthOfYear, int dayOfMonth) {
                dateTextview.setText(yearofDate+"년 "+monthOfYear+"월 "+dayOfMonth+"일");
                year = yearofDate;
                month = monthOfYear;
                day = dayOfMonth;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        }

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minutes) {
                if(hourOfDay>=12){
                    AMPM = "오후";
                    if(hourOfDay==12){
                        AMPMhour = 12;
                    }
                    else{
                        AMPMhour = hourOfDay-12;
                    }

                }
                else {
                    AMPM = "오전";
                    AMPMhour = hourOfDay;
                }
                timeTextview.setText(AMPM+" "+AMPMhour+"시 "+minutes+"분");
                hour = hourOfDay;
                minute = minutes;
            }
        });


        modifyDate = findViewById(R.id.modifyDate);
        modifyDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ParsingMemo.class);
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("day",day);
                intent.putExtra("hour",hour);
                intent.putExtra("minute",minute);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
}
