package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class ParsingMemo extends FragmentActivity implements OnMapReadyCallback {

    EditText contents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_memo);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.parsing_map);
        mapFragment.getMapAsync(this);

        contents= findViewById(R.id.parsing_contents_edit);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
