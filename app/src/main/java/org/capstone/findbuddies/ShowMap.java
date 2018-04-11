package org.capstone.findbuddies;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShowMap extends AppCompatActivity implements OnConnectionFailedListener{

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 101;
    boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    SupportMapFragment mapFragment;
    MapFragment mapFragment;
    GoogleMap map;
    DatabaseReference database;
    String MyEmail;
    MarkerOptions marker;
    MarkerOptions marker2;
    long minTime = 1000;
    float minDistance = 10;
    ArrayList<MarkerOptions> markerList = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    Button but;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        database = FirebaseDatabase.getInstance().getReference();
        MyEmail = getIntent().getStringExtra("MyEmail");

        //////////////////////구글 맵

//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("ShowMap","GoogleMap 준비됨.");

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getApplicationContext(),StreetView.class);
                        intent.putExtra("latitude",marker.getPosition().latitude);
                        intent.putExtra("longitude",marker.getPosition().longitude);

                        startActivity(intent);
                    }
                });

            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        ///////////////// 자동 완성
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//
//            }
//        });
        but = findViewById(R.id.searchButton);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(ShowMap.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


        ///////////////////////사용자에 따른 마커 추가

        int number = getIntent().getIntExtra("size",0);
        for(int i = 0 ; i < number; i ++){
            markerList.add(new MarkerOptions());
        }
        MapsInitializer.initialize(this);
        requestMyLocation();


    }

    @Override
    protected void onPause() {
        super.onPause();

        if(map != null){
//            map.setMyLocationEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(map != null){
//            map.setMyLocationEnabled(true);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
//        updateLocationUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    public void requestMyLocation(){
        Toast.makeText(getApplicationContext(),"requestlocation",Toast.LENGTH_LONG).show();


        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }



        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager != null) {
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            showCurrentLocation(location);
                            SaveLocationData(location);
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    }
            );
        }
    }




    public void showCurrentLocation(Location location) {
        Toast.makeText(getApplicationContext(),"showcurrent",Toast.LENGTH_LONG).show();
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
        showMarker(location);
    }

    public void showMarker(Location location) {

        CustomInfoWindow adapter = new CustomInfoWindow(ShowMap.this);
        map.setInfoWindowAdapter(adapter);


        for(int i = 0 ; i < markerList.size(); i ++){
            markerList.get(i).position(new LatLng(123,123));
            map.addMarker(markerList.get(i));
        }
        if(marker == null ) {


            marker = new MarkerOptions();
            marker.position(new LatLng(location.getLatitude() + 0.005, location.getLongitude() - 0.005));
            marker.title("아들");
            marker.snippet("현재 위치입니다.");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.smallboy));
            map.addMarker(marker).showInfoWindow();
            marker2 = new MarkerOptions();
            marker2.position(new LatLng(location.getLatitude(), location.getLongitude() ) );
            marker2.title("딸");
            marker2.snippet("현재 위치입니다.");
            marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.smallgirl));
            map.addMarker(marker2).showInfoWindow();
        }
        else {

            marker.position(new LatLng(location.getLatitude() + 0.01, location.getLongitude() - 0.005));
            marker2.position(new LatLng(location.getLatitude(), location.getLongitude() ) );
            map.addMarker(marker);
            map.addMarker(marker2);
        }

    }

    public void ShowMarker2(){
        ArrayList<String> memberNameList = getIntent().getStringArrayListExtra("NameList");
        for(int i = 0 ; i<markerList.size();i++){
            (markerList.get(i)).title(memberNameList.get(i));
            (markerList.get(i)).snippet(GetLastDate());
            (markerList.get(i)).position(GetLastLocation());
            map.addMarker(markerList.get(i));
        }
    }

    public LatLng GetLastLocation(){
        final LatLng[] latLng = {null};
        database.child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if(value!=null && (value.getUserEmail()).equals(MyEmail)){
                        if(value.getGpsList()!=null){
                            double latitude = value.getGpsList().get(value.getGpsList().size()-1).getLatitude();
                            double longitude = value.getGpsList().get(value.getGpsList().size()-1).getLongitude();
                            latLng[0] = new LatLng(latitude,longitude);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return latLng[0];
    }

    public String GetLastDate(){
        final String[] date = {""};
        database.child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if(value!=null && (value.getUserEmail()).equals(MyEmail)){
                        if(value.getTimeList()!=null){
                            Date dateInfo = value.getTimeList().get(value.getTimeList().size()-1);

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            date[0] = simpleDateFormat.format(dateInfo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return date[0];
    }

    public void SaveLocationData(final Location location){
        database.child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if (value != null && (value.getUserEmail()).equals(MyEmail)) {
                        String uidKey = snapshot.getKey();
                        if(value.getGpsList()==null){
                            ArrayList<LocationInfo> GpsList = new ArrayList<>();
                            GpsList.add(GetLocationInfo(location));
                            ArrayList<Date> TimeList = new ArrayList<>();
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            TimeList.add(date);

                            database.child("UserGPS").child(uidKey).child("gpsList").setValue(GpsList);
                            database.child("UserGPS").child(uidKey).child("timeList").setValue(TimeList);
                        }
                        else if (value.getGpsList().size() <= 10) {
                            ArrayList<LocationInfo> GpsList = value.getGpsList();
                            GpsList.add(GetLocationInfo(location));

                            ArrayList<Date> TimeList = value.getTimeList();
                            long now = System.currentTimeMillis();
                            Date date = new Date(now);
                            TimeList.add(date);

                            database.child("UserGPS").child(uidKey).child("gpsList").setValue(GpsList);
                            database.child("UserGPS").child(uidKey).child("timeList").setValue(TimeList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LocationInfo GetLocationInfo(Location location){
        float accuracy;
        double latitude;
        double longitude;
        String provider;
        float speed;

        accuracy = location.getAccuracy();
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        provider = location.getProvider();
        speed = location.getSpeed();

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setAccuracy(accuracy);
        locationInfo.setLatitude(latitude);
        locationInfo.setLongitude(longitude);
        locationInfo.setProvider(provider);
        locationInfo.setSpeed(speed);
        return locationInfo;
    }

    public void LoadOnesAllLocation(final String email){
        database.child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if (value != null && (value.getUserEmail()).equals(email)) {
                        ArrayList<MarkerOptions> markers = new ArrayList<>();
                        for(int i = 0 ; i <value.getGpsList().size();i++){
                            markers.add(new MarkerOptions());
                        }
                        for(int i = 0 ; i <value.getGpsList().size();i++){
                            markers.get(i).position(new LatLng(value.getGpsList().get(i).getLatitude(),value.getGpsList().get(i).getLongitude()));
                            markers.get(i).snippet(LoadDate(email,i));
                            map.addMarker(markers.get(i));
                        }

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String LoadDate(final String email, final int position){
        final String[] date = new String[1];
        database.child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if (value != null && (value.getUserEmail()).equals(email)) {
                        Date getDate = value.getTimeList().get(position);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        date[0] = simpleDateFormat.format(getDate);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return date[0];
    }

    public void AddMarker(LatLng latLng){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
