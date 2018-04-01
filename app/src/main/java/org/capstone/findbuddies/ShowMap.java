package org.capstone.findbuddies;

import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ShowMap extends AppCompatActivity {

    boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    SupportMapFragment mapFragment;
    MapFragment mapFragment;
    GoogleMap map;
    FirebaseData firebaseData;
    MarkerOptions marker;
    MarkerOptions marker2;
    long minTime = 1000;
    float minDistance = 0;
    ArrayList<MarkerOptions> markerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        firebaseData = new FirebaseData();
//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d("ShowMap","GoogleMap 준비됨.");

                map = googleMap;
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        int number = getIntent().getIntExtra("number",1);
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
                            firebaseData.SaveLocationData(location);
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
            map.addMarker(marker);
            marker2 = new MarkerOptions();
            marker2.position(new LatLng(location.getLatitude(), location.getLongitude() ) );
            marker2.title("딸");
            marker2.snippet("현재 위치입니다.");
            marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.smallgirl));
            map.addMarker(marker2);
        }
        else {

            marker.position(new LatLng(location.getLatitude() + 0.01, location.getLongitude() - 0.005));
            marker2.position(new LatLng(location.getLatitude(), location.getLongitude() ) );
            map.addMarker(marker);
            map.addMarker(marker2);
        }

    }


}
