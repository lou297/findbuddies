package org.capstone.findbuddies;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ParsingMemo extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {
    String myEmail;
    int GroupNo;
    FirebaseDatabase database;
    LinearLayout dateLayout;
    EditText Title;
    EditText Content;
    int year;
    int month;
    int day;

    String AMPM;
    int AMPMhour;
    int hour;
    int minute;

    LatLng latLng;

    int todayMonth;
    int today;

    String Location;

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
        myEmail = getIntent().getStringExtra("myEmail");
        database = FirebaseDatabase.getInstance();
        GroupNo = getIntent().getIntExtra("GroupNo",0);
        Date date = new Date(System.currentTimeMillis());
        today = date.getDay();
        todayMonth =date.getMonth();
        setInitialDate();
        setInitialMemo();
        InitialParsing();

        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MemoPicker.class);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.save_parsing_memo_but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadParsingMemo();
            }
        });
    }

    private void UploadParsingMemo() {
        long Now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
        String date = simpleDateFormat.format(new Date(Now));
        SaveMemo saveMemo = new SaveMemo();
        Title = findViewById(R.id.parsing_title_edit);
        Content = findViewById(R.id.parsing_contents_edit);
        TextView PictureViewURI = findViewById(R.id.PictureViewURI);
        if(PictureViewURI.getText()!=null){
            saveMemo.setImageUrl(PictureViewURI.getText().toString());
        }
        saveMemo.setUploaderEmail(myEmail);
        saveMemo.setLastEditDate(date);
        saveMemo.setEditSystemTime(Now);
        saveMemo.setCheckGroupNo(GroupNo);
        saveMemo.setTitle(Title.getText().toString());
        saveMemo.setMemo(Content.getText().toString());
        saveMemo.setYear(year);
        saveMemo.setMonth(month);
        saveMemo.setDay(day);
        saveMemo.setHour(hour);
        saveMemo.setMinute(minute);
        saveMemo.setLatLng(latLng);

        database.getReference().child("MemoList").push().setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ParsingMemo.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap = googleMap;
        LatLng curPoint = new LatLng(37.234, 126.972);
        latLng = curPoint;
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
        Title = findViewById(R.id.parsing_title_edit);
        Content= findViewById(R.id.parsing_contents_edit);

        Title.setText(getIntent().getStringExtra("title"));
        Content.setText(getIntent().getStringExtra("content"));
    }

    public void setInitialDate(){
        Date Now = new Date(System.currentTimeMillis());
        year = Now.getYear();
        month = Now.getMonth();
        day = Now.getDay();

        hour = Now.getHours();
        minute = Now.getMinutes();



    }

    private void InitialParsing(){
        ParsingText parsingText = new ParsingText();
        List<ParsingText.NameEntity> nameEntities = null;
        try {
            nameEntities = parsingText.new DownloadJson().execute(getIntent().getStringExtra("content")).get();
        } catch (InterruptedException e) {
            Log.d("ParsingTest",e.getMessage());
        } catch (ExecutionException e) {
            Log.d("ParsingTest",e.getMessage());
        }
        if(nameEntities!=null){
            for( ParsingText.NameEntity nameEntity: nameEntities){
                if((nameEntity.type).startsWith("DT_")){
                    Log.d("ParsingTestee","날짜"+nameEntity.text);
                    ParsingDate(nameEntity.text);
                }
                else if(nameEntity.type.startsWith("TI_")){
                    Log.d("ParsingTestee","시간"+nameEntity.text);
                    ParsingTime(nameEntity.text);
                }
                else if(nameEntity.type.startsWith("OG")||nameEntity.type.startsWith("LC")){
                    Log.d("ParsingTestee","장소"+nameEntity.text);
                    Location = nameEntity.text;
                    ParsingLocation(Location);
                }
                else {
                    Log.d("ParsingTestee","그 외"+nameEntity.text);
                }
            }
        }

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
    private void ParsingDate(String Date){
        int This = Date.indexOf("이번");
        int next = Date.indexOf("다음");
        int dayOfWeekIndex = Date.indexOf("요일");
        int monthIndex = Date.indexOf("월");//월 요일일수도 잇으므로 요일이 있으면 월은 생각 x
        int dayIndex = Date.indexOf("일");
        String dayOfWeek = null;
        String month = null;
        String day = null;

        if(dayOfWeekIndex!=-1){
            dayOfWeek = Date.substring(dayOfWeekIndex-1,dayOfWeekIndex);
            if(next!=-1){

            }
        }
        else{
            if(monthIndex!=-1){
                month = Date.substring(monthIndex-1,monthIndex);
                if(dayIndex!=-1){
                    day = Date.substring(dayIndex-1,dayIndex);
                }
                else{

                }
            }
            else{
                if(dayIndex!=-1){

                }
                else{

                }
            }
        }
    }

    private void ParsingTime(String Time){
        String TimeHour = null;
        String TimeMinutes = null;
        int index1 = Time.indexOf("오전");
        int index2 = Time.indexOf("오후");
        int index = -1;
        if(index1!=-1){
            index = index1;
        }
        else if(index2!=-1){
            index = index2;
        }
        int index3 = Time.indexOf("시");
        if(index3!=-1){
            if(index==-1){
                TimeHour = Time.substring(0,index3);
                TimeHour = TimeHour.trim();
            }
            else{
                TimeHour = Time.substring(index+1,index3);
                TimeHour = TimeHour.trim();
            }

        }
        int index4 = Time.indexOf("분");
        if(index3!=-1&&index4!=-1){
            TimeMinutes = Time.substring(index3+1,index4);
            TimeMinutes = TimeMinutes.trim();
        }
        else if(index3==-1&&index4!=-1){
            TimeMinutes = Time.substring(0,index4);
            TimeMinutes = TimeMinutes.trim();
        }

        if(TimeHour!=null){
            if(TimeHour.equals("한")||TimeHour.equals("1")){
                hour = 13;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("두")||TimeHour.equals("2")){
                hour = 14;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("세")||TimeHour.equals("3")){
                hour = 15;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("네")||TimeHour.equals("4")){
                hour = 16;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("다섯")||TimeHour.equals("5")){
                hour = 17;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("여섯")||TimeHour.equals("6")) {
                hour = 18;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("일곱")||TimeHour.equals("7")){
                hour = 19;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("여덞")||TimeHour.equals("8")){
                hour = 20;
                if(index1 !=-1){
                    hour -= 12;
                }
            }
            else if(TimeHour.equals("아홉")||TimeHour.equals("9")){
                hour = 9;
                if(index2 !=-1){
                    hour += 12;
                }
            }
            else if(TimeHour.equals("열")||TimeHour.equals("10")){
                hour = 10;
                if(index2 !=-1){
                    hour += 12;
                }
            }
            else if(TimeHour.equals("열한")||TimeHour.equals("11")){
                hour = 11;
                if(index2 !=-1){
                    hour += 12;
                }
            }
            else if(TimeHour.equals("열두")||TimeHour.equals("12")){
                hour = 12;
                if(index1 !=-1){
                    hour += 12;
                }
            }
        }
        if(TimeMinutes!=null){
            if(TimeMinutes.equals("오")||TimeMinutes.equals("5")){
                minute = 5;
            }
            else if(TimeMinutes.equals("십")||TimeMinutes.equals("10")){
                minute = 10;
            }
            else if(TimeMinutes.equals("십오")||TimeMinutes.equals("15")){
                minute = 15;
            }
            else if(TimeMinutes.equals("이십")||TimeMinutes.equals("20")){
                minute = 20;
            }
            else if(TimeMinutes.equals("이십오")||TimeMinutes.equals("25")){
                minute = 25;
            }
            else if(TimeMinutes.equals("삼십")||TimeMinutes.equals("30")){
                minute = 30;
            }
            else if(TimeMinutes.equals("삼십오")||TimeMinutes.equals("35")){
                minute = 35;
            }
            else if(TimeMinutes.equals("사십")||TimeMinutes.equals("40")){
                minute = 40;
            }
            else if(TimeMinutes.equals("사십오")||TimeMinutes.equals("45")){
                minute = 45;
            }
            else if(TimeMinutes.equals("오십")||TimeMinutes.equals("50")){
                minute = 50;
            }
            else if(TimeMinutes.equals("오십오")||TimeMinutes.equals("55")){
                minute = 55;
            }
        }
    }


    private void ParsingLocation(String Location){
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        List<Address> AddressList = null;
        try {
            AddressList = geocoder.getFromLocationName(
                    Location, // 지역 이름
                    10); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (AddressList != null) {
            if (AddressList.size() == 0) {
                Log.d("ParsingTest","해당되는 주소 정보는 없습니다");
            } else {
                LatLng parsingLatLng = new LatLng(AddressList.get(0).getLatitude(),AddressList.get(0).getLongitude());
                GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parsingLatLng,15));
                latLng = parsingLatLng;
                getLocationAddress(parsingLatLng);
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }
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
                    nowAddress  = address.get(0).getAddressLine(0);

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
                latLng =  curPoint;
                GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
                getLocationAddress(curPoint);
            }

        }
    }
}
