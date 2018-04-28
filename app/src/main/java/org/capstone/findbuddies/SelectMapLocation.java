package org.capstone.findbuddies;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectMapLocation extends FragmentActivity implements OnMapReadyCallback {
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 2000;
    Button LocationSearchButton;
    Button SelectedLocationSaveButton;
    TextView SelectedLocationText;
    GoogleMap GoogleMap;
    LatLng getPlace;
    Intent getintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map_location);
        LocationSearchButton = findViewById(R.id.location_search_button);
        SelectedLocationText = findViewById(R.id.selected_location_text);
        SelectedLocationSaveButton = findViewById(R.id.selected_location_save);
        getintent = getIntent();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.select_location_map);
        mapFragment.getMapAsync(this);

        LocationSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(SelectMapLocation.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    //  Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    //  Handle the error.
                }
            }
        });

        SelectedLocationSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getintent.putExtra("latitude",getPlace.latitude);
                getintent.putExtra("longitude",getPlace.longitude);
                setResult(RESULT_OK,getintent);
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap = googleMap;
        getPlace = googleMap.getCameraPosition().target;
        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLng CameraLatLng = googleMap.getCameraPosition().target;
                getLocationAddress(CameraLatLng);
                getPlace = CameraLatLng;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                setPlaceAutocomplete(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    public void getLocationAddress(LatLng latLng){
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
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

        SelectedLocationText.setText(nowAddress);
    }

    public void setPlaceAutocomplete(Place place){
        GoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15));
        SelectedLocationText.setText(place.getAddress());
        getPlace = place.getLatLng();
    }
}
