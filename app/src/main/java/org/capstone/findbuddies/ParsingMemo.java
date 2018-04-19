package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ParsingMemo extends FragmentActivity implements OnMapReadyCallback {
    LinearLayout dateLayout;
    EditText parsingTitle;
    EditText parsingContent;
    int year;
    int month;
    int day;
    String AMPM;
    int AMPMhour;
    int hour;
    int minute;
    TextView parsingDate;
    TextView parsingTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_memo);


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.parsing_map);
        mapFragment.getMapAsync(this);
        setInitialDate();
        setInitialMemo();

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MemoPicker.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng curPoint = new LatLng(37.234, 126.972);
        MarkerOptions marker = new MarkerOptions();
        marker.position(curPoint);
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
    }

    public void setInitialMemo(){
        dateLayout = findViewById(R.id.date_layout);
        parsingTitle = findViewById(R.id.parsing_title_edit);
        parsingContent= findViewById(R.id.parsing_contents_edit);

        parsingTitle.setText(getIntent().getStringExtra("title"));
        parsingContent.setText(getIntent().getStringExtra("content"));
    }

    public void setInitialDate(){
        year = getIntent().getIntExtra("year",2018);
        month = getIntent().getIntExtra("month",4);
        day = getIntent().getIntExtra("day",19);

        hour = getIntent().getIntExtra("hour",13);
        minute = getIntent().getIntExtra("minute",29);
        parsingDate = findViewById(R.id.parsingDate);
        parsingTime = findViewById(R.id.parsingTime);

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
        String date = month+"월 "+day+"일";
        String time = AMPM + " "+ AMPMhour+"시 "+minute+"분";
        parsingDate.setText(date);
        parsingTime.setText(time);


    }

}
