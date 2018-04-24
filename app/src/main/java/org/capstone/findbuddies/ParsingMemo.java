package org.capstone.findbuddies;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ParsingMemo extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {
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
    TextView parsingMapAddress;
    GoogleMap GoogleMap;
    private GoogleApiClient mGoogleApiClient;
    int SELECTED_PLACE_REQUEST_CODE = 2001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_memo);
        parsingMapAddress = findViewById(R.id.parsing_map_address);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

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
        GoogleMap = googleMap;
        LatLng curPoint = new LatLng(37.234, 126.972);
        MarkerOptions marker = new MarkerOptions();
        marker.position(curPoint);
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        getLocationAddress(curPoint);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(getApplicationContext(),SelectMapLocation.class);
                startActivityForResult(intent,SELECTED_PLACE_REQUEST_CODE);
            }
        });

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

    public void getLocationAddress(LatLng latLng){
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    nowAddress  = currentLocationAddress;

                }
            }

        } catch (IOException e) {
            Toast.makeText(this, "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }

        parsingMapAddress.setText(nowAddress);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SELECTED_PLACE_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                Double getLatitude = data.getDoubleExtra("latitude",0);
                Double getLongitude = data.getDoubleExtra("longitude",0);
                LatLng curPoint = new LatLng(getLatitude,getLongitude);
                GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
                getLocationAddress(curPoint);
            }

        }
    }
}
