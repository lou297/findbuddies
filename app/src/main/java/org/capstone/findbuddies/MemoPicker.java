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

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;

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

        Calendar calendar = Calendar.getInstance();
        CalendarDay Day = CalendarDay.today();

        year = getIntent().getIntExtra("year",Day.getYear());
        month = getIntent().getIntExtra("month",Day.getMonth());
        day = getIntent().getIntExtra("day",Day.getDay());

        hour = getIntent().getIntExtra("hour",12);
        minute = getIntent().getIntExtra("minute",0);
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
        String date = year+"년 "+month+"월 "+day+"일";
        String time = AMPM+" "+AMPMhour+"시 "+minute+"분";
        dateTextview.setText(date);
        timeTextview.setText(time);
        datePicker.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int yearofDate, int monthOfYear, int dayOfMonth) {
                String ChangedDate = yearofDate+"년 "+(monthOfYear+1)+"월 "+dayOfMonth+"일";
                dateTextview.setText(ChangedDate);
                year = yearofDate;
                month = monthOfYear+1;
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
                String ChangedTime = AMPM+" "+AMPMhour+"시 "+minutes+"분";
                timeTextview.setText(ChangedTime);
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
                intent.putExtra("READ",1);
                intent.putExtra("UploadedPic",getIntent().getIntExtra("UploadedPic",0));
                intent.putExtra("myEmail",getIntent().getStringExtra("myEmail"));
                intent.putExtra("pictureViewURI",getIntent().getStringExtra("pictureViewURI"));
                intent.putExtra("GroupNo",getIntent().getIntExtra("GroupNo",0));
                intent.putExtra("title",getIntent().getStringExtra("title"));
                intent.putExtra("content",getIntent().getStringExtra("content"));
                intent.putExtra("ReadLatitude",getIntent().getDoubleExtra("ReadLatitude",0));
                intent.putExtra("ReadLongitude",getIntent().getDoubleExtra("ReadLongitude",0));
                intent.putExtra("ReturnDate",1);
                String uidKey = getIntent().getStringExtra("UidKey");
                intent.putExtra("UidKey",uidKey);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }


}
