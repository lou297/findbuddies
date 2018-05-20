package org.capstone.findbuddies;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainMapFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap googleMap;
    private MapView mapView;
    MarkerOptions marker;
    String myEmail;
    String myID;
    Boolean Check;
    FirebaseAuth auth;
    FirebaseDatabase database;
    LatLng latLng;
    ListView listview;
    MapMemoAdaptor adaptor;
    ArrayList<MemoItem> Memos = new ArrayList<>();
    FusedLocationProviderClient mFusedLocationProviderClient;
    int GroupItemSelect = 0;
    int ReadMemo = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_map, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        if(user!=null){
            myEmail = user.getEmail();
            getMyID(myEmail);
        }


        listview = rootView.findViewById(R.id.map_memo_listView);
        adaptor = new MapMemoAdaptor();
        listview.setAdapter(adaptor);

        Locale ko = Locale.KOREA;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapView = rootView.findViewById(R.id.MainMapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();



        //마시멜로 이상이면 권한 요청하기
        if(Build.VERSION.SDK_INT >= 23){
            //권한이 없는 경우
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION} , 1);
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

        return rootView;
    }

    class MapMemoAdaptor extends BaseAdapter{
        private MapMemoAdaptor(){
            database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Memos.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveMemo value = snapshot.getValue(SaveMemo.class);
                        String uidKey = snapshot.getKey();
                        if(value!=null){
                            if(myEmail.equals(value.getUploaderEmail()) || CheckMyEmailInGroup(value.getCheckGroupNo())){
                                MemoItem memoItem;
                                String address = null;
                                if(value.getLatitude()!=0){

                                    memoItem = new MemoItem(value.getEditSystemTime(),value.getCheckGroupNo(),value.getUploaderEmail(),uidKey,value.getYear(),
                                            value.getMonth(),value.getDate(),value.getHour(),value.getMinute(),value.getTitle(),value.getMemo(),
                                            value.getImageUrl(),value.getLatitude(),value.getLongitude(),value.getAddress());


                                    addMemo(memoItem);
                                }
                            }
                        }
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public void addMemo(MemoItem memoItem){
            Memos.add(memoItem);
        }

        @Override
        public int getCount() {
            return Memos.size();
        }

        @Override
        public Object getItem(int position) {
            return Memos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;
            if(view==null) {
                view = getLayoutInflater().inflate(R.layout.map_memo_item, null);
                viewHolder = new ViewHolder();
                viewHolder.memo = view.findViewById(R.id.two_line_memo);
                viewHolder.location = view.findViewById(R.id.two_line_location);
                viewHolder.group = view.findViewById(R.id.group_image);
                viewHolder.date = view.findViewById(R.id.date_image);
                viewHolder.photo = view.findViewById(R.id.photo_image);
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }
            MemoItem Memo = Memos.get(position);
            viewHolder.memo.setText(Memo.getContent());
            viewHolder.location.setText(Memo.getAddress());
            if(Memo.getGroupNo()!=0){
                viewHolder.group.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.group.setVisibility(View.GONE);
            }
            if(Memo.getMonth()!=0){
                viewHolder.date.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.date.setVisibility(View.GONE);
            }
            if(Memo.getPictureURI()!=null){
                viewHolder.photo.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.photo.setVisibility(View.GONE);
            }
            viewHolder.group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GroupItemSelect==0){
                        viewHolder.memo.setText("일");
                    }else if(GroupItemSelect==1){
                        viewHolder.memo.setText("영");
                    }

                    if(GroupItemSelect==0){
                        GroupItemSelect=1;
                    }else if(GroupItemSelect==1){
                        GroupItemSelect=0;
                    }
                }
            });
            return view;
        }
        class ViewHolder{
            TextView memo;
            TextView location;
            ImageView group;
            ImageView date;
            ImageView photo;
        }
    }
    public String getLocationAddress(double latitude,double longitude){
        String nowAddress ="현재 위치를 확인 할 수 없습니다.";
        Geocoder geocoder = new Geocoder(getContext(), Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(latitude, longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    nowAddress  = address.get(0).getAddressLine(0);
                    int nation = nowAddress.indexOf("대한민국");
                    if(nation!=-1){
                        nowAddress = nowAddress.substring(nation+5);
                    }

                }
            }

        } catch (IOException e) {
            Toast.makeText(getContext(), "주소를 가져 올 수 없습니다.", Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }
        return nowAddress;
    }
    private void getMyID(String MyEmail) {
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(MyEmail)) {
                        myID = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private boolean CheckMyEmailInGroup(int GroupNo){
        Check = false;
        database.getReference().child("GruopList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                    if(value!=null){
                        if(value.getGroupNo()==GroupNo){
                            ArrayList<String> membersID = value.getMembersID();
                            for(String memberCheck : membersID){
                                if(memberCheck.equals(myID)){
                                    Check = true;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return Check;
    }

    private void requestMyLocation() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        OnCompleteListener<Location> onCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()&&task.getResult()!=null){
                    Location location = task.getResult();
                    latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    mapView.getMapAsync(MainMapFragment.this);
                }
            }
        };
        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(onCompleteListener);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMapToolbarEnabled(false);
//        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//        marker.position(googleMap.getCameraPosition().target);
//        googleMap.addMarker(marker);
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
                            MarkerOptions marker = new MarkerOptions();
                            marker.position(getLatLng);
                            marker.title(value.getTitle());
                            marker.snippet(value.getMemo());
//                            googleMap.addMarker(new MarkerOptions().position(getLatLng).title(value.getTitle()).snippet(value.getMemo()));
                            googleMap.addMarker(marker);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            //권한받음
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                requestMyLocation();
            }
            //권한못받음
            else{
                Toast.makeText(getContext(), "권한없음", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
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
