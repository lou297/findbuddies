package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainMapFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap googleMap;
    private MapView mapView;
    MarkerOptions marker;
    String myEmail;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_map, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            myEmail = user.getEmail();
        }
        database = FirebaseDatabase.getInstance();
//        myEmail = getArguments().getString("myEmail");
        mapView = rootView.findViewById(R.id.MainMapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        Locale ko = Locale.KOREA;

        if(googleMap!=null){

        }

        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        marker = new MarkerOptions();
        LatLng curPoint = new LatLng(37.234, 126.972);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint,15));
        marker.position(googleMap.getCameraPosition().target);
        googleMap.addMarker(marker);
        LoadMemoLocation();

    }

    private void LoadMemoLocation(){
        database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveMemo value = snapshot.getValue(SaveMemo.class);
                    if(value!=null&&value.getUploaderEmail().equals(myEmail)){
                        if(value.getLatitude()>=0){
                            LatLng getLatLng = new LatLng(value.getLatitude(),value.getLongitude());
                            googleMap.addMarker(new MarkerOptions().position(getLatLng));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
