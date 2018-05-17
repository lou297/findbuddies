package org.capstone.findbuddies;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ParsingMemo extends FragmentActivity implements OnMapReadyCallback {
    ProgressDialog asyncDialog;
    String myEmail;
    int GroupNo;
    FirebaseDatabase database;
    FirebaseStorage storage;
    RelativeLayout dateLayout;
    EditText Title;
    EditText Content;
    int year;
    int month;
    int date;

    String AMPM;
    int AMPMhour;
    int hour;
    int minute;
    int decideDate = 0 ;
    int decideTime = 0 ;

    LatLng latLng;

    int todayMonth;
    int today;
    MapFragment mapFragment;
    String ParsingLocationString;
    String PictureViewURI;
    TextView parsingDate;
    TextView parsingTime;
    TextView parsingMapAddress;
    ImageView dateUnable;
    ImageView mapUnable;
    int dateunable = 0;
    int mapunable = 0;
    int READ;
    int CalendarAdd;
    int UploadedPic;
    GoogleMap GoogleMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    int SELECTED_PLACE_REQUEST_CODE = 2001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_memo);
        READ = getIntent().getIntExtra("READ",0);
        CalendarAdd = getIntent().getIntExtra("CalendarAdd",0);
        asyncDialog = new ProgressDialog(
                ParsingMemo.this);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("Parsing..");
//        asyncDialog.setCancelable(false);
        if(READ==0) {
            asyncDialog.show();
        }
        parsingMapAddress = findViewById(R.id.parsing_map_address);
        dateUnable = findViewById(R.id.date_unable);
        mapUnable = findViewById(R.id.map_unable);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //마시멜로 이상이면 권한 요청하기
        if(Build.VERSION.SDK_INT >= 23){
            //권한이 없는 경우
            if(ContextCompat.checkSelfPermission(ParsingMemo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(ParsingMemo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(ParsingMemo.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
            }
            //권한이 있는 경우
            else{
                requestMyLocation();
            }
        }
        //마시멜로 아래
        else{
            requestMyLocation();
        }
        myEmail = getIntent().getStringExtra("myEmail");
        UploadedPic = getIntent().getIntExtra("UploadedPic",0);
        PictureViewURI = getIntent().getStringExtra("pictureViewURI");
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        GroupNo = getIntent().getIntExtra("GroupNo",0);
        Log.d("ParsingTest","no111111: "+GroupNo);
        Date dAte = new Date(System.currentTimeMillis());
        today = dAte.getDay()-1;
        todayMonth =dAte.getMonth();
        setInitialDate();
        setInitialMemo();


        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateunable==0) {
                    Intent intent = new Intent(getApplicationContext(), MemoPicker.class);
                    intent.putExtra("myEmail",myEmail);
                    intent.putExtra("pictureViewURI",PictureViewURI);
                    intent.putExtra("GroupNo",GroupNo);
                    intent.putExtra("UploadedPic",UploadedPic);
                    intent.putExtra("title",getIntent().getStringExtra("title"));
                    intent.putExtra("content",getIntent().getStringExtra("content"));
                    intent.putExtra("year",year);
                    intent.putExtra("month",month);
                    intent.putExtra("day",date);
                    intent.putExtra("hour",hour);
                    intent.putExtra("minute",minute);
                    intent.putExtra("ReadLatitude",getIntent().getDoubleExtra("ReadLatitude",0));
                    intent.putExtra("ReadLongitude",getIntent().getDoubleExtra("ReadLongitude",0));
                    intent.putExtra("UidKey",getIntent().getStringExtra("UidKey"));
                    startActivity(intent);
                }
            }
        });
        dateLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(dateunable==0){
                    dateunable=1;
                    dateUnable.setVisibility(View.VISIBLE);
                }
                else if(dateunable==1){
                    dateunable=0;
                    dateUnable.setVisibility(View.GONE);
                }
                return false;
            }
        });

        Button button = findViewById(R.id.save_parsing_memo_but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImage();
            }
        });
    }
    private void UploadImage(){
        if(!PictureViewURI.equals("") && UploadedPic==0){
            StorageReference storageRef = storage.getReferenceFromUrl("gs://map-api-187214.appspot.com");
            Uri file = Uri.parse(PictureViewURI);
            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Toast.makeText(ParsingMemo.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    UploadParsingMemo(downloadUrl.toString());

                }
            });
        }
        else if(!PictureViewURI.equals("") && UploadedPic==1){
            UploadParsingMemo(PictureViewURI);
        }
        else {
            UploadParsingMemo(null);
        }
    }

    private void UploadParsingMemo(String URI) {
        long Now = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
        String EditDate = simpleDateFormat.format(new Date(Now));
        SaveMemo saveMemo = new SaveMemo();
        Title = findViewById(R.id.parsing_title_edit);
        Content = findViewById(R.id.parsing_contents_edit);
        if(URI!=null){
            saveMemo.setImageUrl(URI);
        }
        saveMemo.setUploaderEmail(myEmail);
        saveMemo.setLastEditDate(EditDate);
        saveMemo.setEditSystemTime(Now);
        saveMemo.setCheckGroupNo(GroupNo);
        saveMemo.setTitle(Title.getText().toString());
        saveMemo.setMemo(Content.getText().toString());
        if(dateunable==0){
            saveMemo.setYear(year);
            saveMemo.setMonth(month);
            saveMemo.setDate(date);
            saveMemo.setHour(hour);
            saveMemo.setMinute(minute);
        }
        if(mapunable==0){
            saveMemo.setLatitude(latLng.latitude);
            saveMemo.setLongitude(latLng.longitude);
        }
        String uidKey = getIntent().getStringExtra("UidKey");
        Log.d("ParsingTest","?? "+uidKey);
        if(uidKey==null) {
            database.getReference().child("MemoList").push().setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                Toast.makeText(ParsingMemo.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        }
        else{
            database.getReference().child("MemoList").child(uidKey).setValue(saveMemo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                Toast.makeText(ParsingMemo.this, "업로드 완료!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap = googleMap;
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        GoogleMap.getUiSettings().setAllGesturesEnabled(false);
        GoogleMap.getUiSettings().setMapToolbarEnabled(false);
        getLocationAddress(latLng);
        if(READ==0) {
            InitialParsing();
        }
        GoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mapunable==0){
                    Intent intent = new Intent(getApplicationContext(),SelectMapLocation.class);
                    intent.putExtra("ReadLatitude",latLng.latitude);
                    intent.putExtra("ReadLongitude",latLng.longitude);
                    startActivityForResult(intent,SELECTED_PLACE_REQUEST_CODE);
                }
            }
        });
        GoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(mapunable==0){
                    mapunable=1;
                    mapUnable.setVisibility(View.VISIBLE);
                }
                else if(mapunable==1){
                    mapunable=0;
                    mapUnable.setVisibility(View.GONE);
                }
            }
        });

    }


    public void requestMyLocation(){
        if(ContextCompat.checkSelfPermission(ParsingMemo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ParsingMemo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        OnCompleteListener<Location> onCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()&&task.getResult()!=null){
                    Location location = task.getResult();
                    if(READ==1){
                        if(getIntent().getDoubleExtra("ReadLatitude",0)!=0){
                            latLng = new LatLng(getIntent().getDoubleExtra("ReadLatitude",0),
                                    getIntent().getDoubleExtra("ReadLongitude",0));
                            mapunable=0;
                            mapUnable.setVisibility(View.GONE);
                        }
                        else {
                            latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            mapunable=1;
                            mapUnable.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                    mapFragment = (MapFragment) getFragmentManager()
                            .findFragmentById(R.id.parsing_map);
                    mapFragment.getMapAsync(ParsingMemo.this);
                }
            }
        };
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(onCompleteListener);

    }




    public void setInitialMemo(){
        dateLayout = findViewById(R.id.date_layout);
        Title = findViewById(R.id.parsing_title_edit);
        Content= findViewById(R.id.parsing_contents_edit);

        Title.setText(getIntent().getStringExtra("title"));
        Content.setText(getIntent().getStringExtra("content"));
    }

    public void setInitialDate(){
        Calendar calendar = Calendar.getInstance();
        CalendarDay day = CalendarDay.today();
        parsingDate = findViewById(R.id.parsingDate);
        parsingTime = findViewById(R.id.parsingTime);
        if(READ==0&&CalendarAdd==0) {
            year = getIntent().getIntExtra("year", day.getYear());
            month = getIntent().getIntExtra("month", day.getMonth() + 1);
            date = getIntent().getIntExtra("day", day.getDay());
            hour = getIntent().getIntExtra("hour", 12);
            minute = getIntent().getIntExtra("minute", 0);
        }
        else if(READ==0&&CalendarAdd==1){
            year = getIntent().getIntExtra("Year", day.getYear());
            month = getIntent().getIntExtra("Month", day.getMonth() + 1);
            date = getIntent().getIntExtra("Date", day.getDay());
            hour = getIntent().getIntExtra("hour", 12);
            minute = getIntent().getIntExtra("minute", 0);
        }
        else if(READ==1){
            if(getIntent().getIntExtra("ReadMonth",0)!=0){
                year = getIntent().getIntExtra("ReadYear",0);
                month = getIntent().getIntExtra("ReadMonth",0);
                date = getIntent().getIntExtra("ReadDay",0);
                hour = getIntent().getIntExtra("ReadHour",0);
                minute = getIntent().getIntExtra("ReadMinute",0);

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
                String ParsingDate = month+"월 "+date+"일";
                String time = AMPM + " "+ AMPMhour+"시 "+minute+"분";
                parsingDate.setText(ParsingDate);
                parsingTime.setText(time);
                dateunable=0;
                dateUnable.setVisibility(View.GONE);
            }
            else {
                year = getIntent().getIntExtra("year", day.getYear());
                month = getIntent().getIntExtra("month", day.getMonth() + 1);
                date = getIntent().getIntExtra("day", day.getDay());
                hour = getIntent().getIntExtra("hour", 12);
                minute = getIntent().getIntExtra("minute", 0);
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
                String ParsingDate = month+"월 "+date+"일";
                String time = AMPM + " "+ AMPMhour+"시 "+minute+"분";
                parsingDate.setText(ParsingDate);
                parsingTime.setText(time);
                if(getIntent().getIntExtra("ReturnDate",0)==0) {
                    dateunable = 1;
                    dateUnable.setVisibility(View.VISIBLE);
                }
            }
        }


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
            int IsDTorTI = 0;
            int IsOGorLC = 0;
            for( ParsingText.NameEntity nameEntity: nameEntities){
                if((nameEntity.type).startsWith("DT_")){
                    Log.d("ParsingTestee","날짜"+nameEntity.text);
                    IsDTorTI = 1;
                    if(decideDate==0) {
                        if(CalendarAdd!=1) {
                            ParsingDate(nameEntity.text);
                        }
                    }
                    decideDate = 1;
                }
                else if(nameEntity.type.startsWith("TI_")){
                    Log.d("ParsingTestee","시간"+nameEntity.text);
                    IsDTorTI = 1;
                    if(decideTime==0) {
                        ParsingTime(nameEntity.text);
                    }
                    decideTime = 1;
                }
                else if(nameEntity.type.startsWith("OG")||nameEntity.type.startsWith("LC")){
                    Log.d("ParsingTestee","장소"+nameEntity.text);
                    IsOGorLC = 1;
                    ParsingLocationString = nameEntity.text;
                    ParsingLocation(ParsingLocationString);
                }
                else {
                    Log.d("ParsingTestee","그 외"+nameEntity.text);
                }
            }
            if(IsDTorTI==0){
                dateunable=1;
                dateUnable.setVisibility(View.VISIBLE);
            }
            if(IsOGorLC==0){
                mapunable=1;
                mapUnable.setVisibility(View.VISIBLE);
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
        String ParsingDate = month+"월 "+date+"일";
        String time = AMPM + " "+ AMPMhour+"시 "+minute+"분";
        parsingDate.setText(ParsingDate);
        parsingTime.setText(time);
        asyncDialog.dismiss();
    }
    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    private void ParsingDate(String ParseDate){
        ParseDate = ParseDate.replace(" ","");
        int This = ParseDate.indexOf("이번");
        int next = ParseDate.indexOf("다음");
        int dayOfWeekIndex = ParseDate.indexOf("요일");
        int monthIndex = ParseDate.indexOf("월");//월 요일일수도 잇으므로 요일이 있으면 월은 생각 x
        int DmonthIndex = ParseDate.indexOf("달");
        int week = ParseDate.indexOf("주");
        int dayIndex = ParseDate.indexOf("일");
        String isCheck = "";
        int isDate = 0 ;
        int comma = ParseDate.indexOf(".");
        int slash = ParseDate.indexOf("/");

        String dayOfWeek = null;
        String month = null;
        String day = null;
        Log.d("ParsingTest","파싱??"+this.month+","+date);
        int subDay = 0;
        if(This!=-1){
            if(ParseDate.substring(This+2,This+3).equals("달")){
                //원래의 month그대로
            }
            else if(ParseDate.substring(This+2,This+3).equals("주")){
                //원래의 week그대로
            }
        }
        else if(next!=-1){
            if(ParseDate.substring(next+2,next+3).equals("달")){
                this.month++;
            }
            else if(ParseDate.substring(next+2,next+3).equals("주")){
                subDay+=7;
            }
        }
        if(monthIndex!=-1){
            if(ParseDate.length()-1>=monthIndex+3){
                isCheck = ParseDate.substring(monthIndex+1,monthIndex+3);
            }
            if(isCheck.equals("요일")){
                //월요일이었다.
            }
            else {
                if(monthIndex-2>-1&&isNumeric(ParseDate.substring(monthIndex-2,monthIndex))){
                    month = ParseDate.substring(monthIndex-2,monthIndex);
                }
                else if(isNumeric(ParseDate.substring(monthIndex-1,monthIndex))){
                    month = ParseDate.substring(monthIndex-1,monthIndex);
                }
            }
        }
        if(month!=null){
            if(month.equals("1")||month.equals("일")){
                this.month=1;
            } else if(month.equals("2")||month.equals("이")){
                this.month=2;
            } else if(month.equals("3")||month.equals("삼")){
                this.month=3;
            } else if(month.equals("4")||month.equals("사")){
                this.month=4;
            } else if(month.equals("5")||month.equals("오")){
                this.month=5;
            } else if(month.equals("6")||month.equals("육")||month.equals("유")){
                this.month=6;
            } else if(month.equals("7")||month.equals("칠")){
                this.month=7;
            } else if(month.equals("8")||month.equals("팔")){
                this.month=8;
            } else if(month.equals("9")||month.equals("구")){
                this.month=9;
            } else if(month.equals("10")||month.equals("십")||month.equals("시")){
                this.month=10;
            } else if(month.equals("11")||month.equals("십일")){
                this.month=11;
            } else if(month.equals("12")||month.equals("십이")){
                this.month=12;
            }

        }

        if(dayIndex!=-1){
            if(((ParseDate.length()-1>=dayIndex+1)&&(ParseDate.substring(dayIndex+1,dayIndex+2).equals("요")))||ParseDate.substring(dayIndex-1,dayIndex).equals("요")){
                //일요일이었다.
            }
            else {
                isDate = 1;
                if(dayIndex-2>-1&&isNumeric(ParseDate.substring(dayIndex-2,dayIndex))){
                    day = ParseDate.substring(dayIndex-2,dayIndex);
                    date = Integer.parseInt(ParseDate.substring(dayIndex-2,dayIndex));
                    Log.d("ParsingTest",ParseDate.substring(dayIndex-2,dayIndex)+","+date);
                }
                else if(isNumeric(ParseDate.substring(dayIndex-1,dayIndex))){
                    day = ParseDate.substring(dayIndex-1,dayIndex);
                    date = Integer.parseInt(ParseDate.substring(dayIndex-1,dayIndex));
                }
            }
        }
        if(comma!=-1){
            isDate = 1;
            String Tyear;
            String Tmonth;
            String Tdate;
            String first;
            String second;
            int secondComma=ParseDate.indexOf(".",comma+1);
            if(secondComma!=-1){
                Tyear = ParseDate.substring(0,comma);
                Tyear = Tyear.trim();
                year = Integer.parseInt(Tyear);
                Tmonth = ParseDate.substring(comma+1,secondComma);
                Tmonth = Tmonth.trim();
                this.month = Integer.parseInt(Tmonth);
                if(isNumeric(ParseDate.substring(secondComma+1,secondComma+3))){
                    date = Integer.parseInt(ParseDate.substring(secondComma+1,secondComma+3));
                }
                else{
                    date = Integer.parseInt(ParseDate.substring(secondComma+1,secondComma+2));
                }
            }
            else {
                first = ParseDate.substring(0,comma);
                first = first.trim();
                if(Integer.parseInt(first)>12){
                    year = Integer.parseInt(first);
                    if(isNumeric(ParseDate.substring(comma+1,comma+3))){
                        this.month = Integer.parseInt(ParseDate.substring(comma+1,comma+3));
                    }
                    else{
                        this.month = Integer.parseInt(ParseDate.substring(comma+1,comma+2));
                    }
                }
                else{
                    this.month = Integer.parseInt(first);
                    if(isNumeric(ParseDate.substring(comma+1,comma+3))){
                        date = Integer.parseInt(ParseDate.substring(comma+1,comma+3));
                    }
                    else{
                        date = Integer.parseInt(ParseDate.substring(comma+1,comma+2));
                    }
                }
            }
        }
        if(slash!=-1) {
            isDate = 1;
            String Tyear;
            String Tmonth;
            String Tdate;
            String first;
            String second;
            int secondSlash=ParseDate.indexOf(".",slash+1);
            if(secondSlash!=-1){
                Tyear = ParseDate.substring(0,slash);
                Tyear = Tyear.trim();
                year = Integer.parseInt(Tyear);
                Tmonth = ParseDate.substring(slash+1,secondSlash);
                Tmonth = Tmonth.trim();
                this.month = Integer.parseInt(Tmonth);
                if(isNumeric(ParseDate.substring(secondSlash+1,secondSlash+3))){
                    date = Integer.parseInt(ParseDate.substring(secondSlash+1,secondSlash+3));
                }
                else{
                    date = Integer.parseInt(ParseDate.substring(secondSlash+1,secondSlash+2));
                }
            }
            else {
                first = ParseDate.substring(0,slash);
                first = first.trim();
                if(Integer.parseInt(first)>12){
                    year = Integer.parseInt(first);
                    if(isNumeric(ParseDate.substring(slash+1,slash+3))){
                        this.month = Integer.parseInt(ParseDate.substring(slash+1,slash+3));
                    }
                    else{
                        this.month = Integer.parseInt(ParseDate.substring(slash+1,slash+2));
                    }
                }
                else{
                    this.month = Integer.parseInt(first);
                    if(isNumeric(ParseDate.substring(slash+1,slash+3))){
                        date = Integer.parseInt(ParseDate.substring(slash+1,slash+3));
                    }
                    else{
                        date = Integer.parseInt(ParseDate.substring(slash+1,slash+2));
                    }
                }
            }

        }

        if(dayOfWeekIndex!=-1){
            if(isDate==0){
                dayOfWeek = ParseDate.substring(dayOfWeekIndex-1,dayOfWeekIndex);
                if(dayOfWeek.equals("일")){
                    subDay += today;
                } else if(dayOfWeek.equals("월")){
                    subDay += today-1;
                } else if(dayOfWeek.equals("화")){
                    subDay += today-2;
                } else if(dayOfWeek.equals("수")){
                    subDay += today-3;
                } else if(dayOfWeek.equals("목")){
                    subDay += today-4;
                } else if(dayOfWeek.equals("금")){
                    subDay += today-5;
                } else if(dayOfWeek.equals("토")){
                    subDay += today-6;
                }
            }

        }
        Log.d("ParsingTest","date = "+date+", today = "+today+", subday = "+subDay);
        if(dayOfWeekIndex!=-1) {
            if (isDate == 0) {
                date +=subDay;
            }
        }
        int maxDate =0;
        switch (this.month){
            case 2:
                maxDate = 28;
                break;
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                maxDate = 31;
                break;

                default:
                    maxDate = 30;
                    break;
        }
        if(date > maxDate){
            this.month++;
            date -= maxDate;
        }
        Log.d("ParsingTest","파싱 날짜"+year+"."+this.month+"."+date);
    }

    private void ParsingTime(String Time){
        Time = Time.replace(" ","");
        String TimeHour = null;
        String TimeMinutes = null;
        int colon = Time.indexOf(":");
        int index1 = Time.indexOf("오전");
        int index2 = Time.indexOf("오후");
        int index = -1;
        if(index1!=-1){
            index = index1;
        }
        else if(index2!=-1){
            index = index2;
        }
        if(colon!=-1){
            if(index==-1){
                TimeHour = Time.substring(0,colon);
            }
            else{
                TimeHour = Time.substring(index+2,colon);
            }
            if(isNumeric(Time.substring(colon+1,colon+3))){
                TimeMinutes = Time.substring(colon+1,colon+3);
            }
            else{
                TimeMinutes = Time.substring(colon+1,colon+2);
            }
        }
        int index3 = Time.indexOf("시");
        if(index3!=-1){
            if(index==-1){
                TimeHour = Time.substring(0,index3);
            }
            else{
                TimeHour = Time.substring(index+2,index3);
            }
            minute =0;
        }
        int index4 = Time.indexOf("분");
        if(index3!=-1&&index4!=-1){
            TimeMinutes = Time.substring(index3+1,index4);
        }
        else if(index3==-1&&index4!=-1){
            TimeMinutes = Time.substring(0,index4);
        }

        if(TimeHour!=null){
            if(TimeHour.equals("열한")||TimeHour.equals("11")){
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
            else if(TimeHour.equals("한")||TimeHour.equals("1")){
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
                Log.d("ParsingTest","123hour: "+hour+"minute: "+minute);
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
        Log.d("ParsingTest","hour: "+hour+"minute: "+minute);
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
            Log.e("ParsingTest","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (AddressList != null) {
            if (AddressList.size() == 0) {
                Log.d("ParsingTest","해당되는 주소 정보는 없습니다");
            } else {
                Log.d("ParsingTest",AddressList.get(0).getLatitude()+"");
                Log.d("ParsingTest",AddressList.get(0).getLongitude()+"");
                LatLng parsingLatLng = new LatLng(AddressList.get(0).getLatitude(),AddressList.get(0).getLongitude());
                if(GoogleMap==null){
                    Log.d("ParsingTest","여기가 문제");
                }
                GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parsingLatLng,15));
                latLng = parsingLatLng;
                getLocationAddress(latLng);
                //          list.get(0).getCountryName();  // 국가명
                //          list.get(0).getLatitude();        // 위도
                //          list.get(0).getLongitude();    // 경도
            }
        }
    }

    public void getLocationAddress(LatLng latLng){
        Log.d("ParsingTest","222");
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
        Log.d("ParsingTest","333");

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            //권한받음
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                requestMyLocation();
            }
            //권한못받음
            else{
                Toast.makeText(this, "권한없음", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
