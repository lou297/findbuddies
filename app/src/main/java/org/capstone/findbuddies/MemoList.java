package org.capstone.findbuddies;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MemoList extends Fragment {
    ArrayList<MemoItem> Memos = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseStorage storage;
    String myEmail;
    String myID;
    boolean Check;
    MemoAdapter adapter;
    FloatingActionButton fab;
    private List<String> uidLists = new ArrayList<>();
    private ArrayList<Integer> hour = new ArrayList<>();
    private ArrayList<Integer> minute = new ArrayList<>();
    private ArrayList<String> Uploader = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.memo_list,container,false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        fab = rootView.findViewById(R.id.fab);
        myEmail = getArguments().getString("myEmail");
        getMyID(myEmail);
        ListView listview = rootView.findViewById(R.id.MemoListView);

        adapter = new MemoAdapter();

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadMemo(position);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("AlertDialog Title");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference().child("MemoList").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Memos.remove(position);
                                        uidLists.remove(position);
                                        hour.remove(position);
                                        minute.remove(position);
                                        Uploader.remove(position);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                            }
                        });
                builder.show();
                return false;
            }
        });

        return rootView;
    }


    class MemoAdapter extends BaseAdapter{

        private MemoAdapter() {
            database.getReference().child("MemoList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    uidLists.clear();
                    Memos.clear();
                    hour.clear();
                    minute.clear();
                    Uploader.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveMemo value = snapshot.getValue(SaveMemo.class);
                        String uidKey = snapshot.getKey();
                        if(value!=null){
                            if(myEmail.equals(value.getUploaderEmail()) || CheckMyEmailInGroup(value.getCheckGroupNo())){
                                MemoItem memoItem;
                                String address = null;
                                if(value.getLatitude()!=0){
                                    address = getLocationAddress(value.getLatitude(),value.getLongitude());
                                }
                                if(value.getMonth()==0){
                                    if(value.getLatitude()==0){
                                        memoItem = new MemoItem(value.getEditSystemTime(),null,0,0,0,value.getCheckGroupNo(),
                                                value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null,0,0);
                                    }
                                    else{
                                        memoItem = new MemoItem(value.getEditSystemTime(),null,0,0,0,value.getCheckGroupNo(),
                                                value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address,value.getLatitude(),value.getLongitude());
                                    }

                                }
                                else{
                                    String date_label = value.getMonth()+"월 "+value.getDate()+"일";
                                    if(value.getLatitude()==0){
                                        memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),null,0,0);
                                    }
                                    else {
                                        memoItem = new MemoItem(value.getEditSystemTime(),date_label,value.getYear(),value.getMonth(),value.getDate(),value.getCheckGroupNo(),
                                                value.getTitle(),value.getMemo(),value.getLastEditDate(),value.getImageUrl(),address,value.getLatitude(),value.getLongitude());
                                    }


                                }
                                uidLists.add(uidKey);
                                addMemo(memoItem);
                                hour.add(value.getHour());
                                minute.add(value.getMinute());
                                Uploader.add(value.getUploaderEmail());
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
            if(view==null){
                view = getLayoutInflater().inflate(R.layout.memo_item,null);
                viewHolder = new ViewHolder();
                viewHolder.date_label = view.findViewById(R.id.date_label);
                viewHolder.group_label = view.findViewById(R.id.group_label);
                viewHolder.title = view.findViewById(R.id.title);
                viewHolder.date = view.findViewById(R.id.date);
                viewHolder.content = view.findViewById(R.id.contents);
                viewHolder.picture = view.findViewById(R.id.picture);
                viewHolder.location = view.findViewById(R.id.memo_location);
                viewHolder.content_layout = view.findViewById(R.id.content_layout);

                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }
//            viewHolder.date_label = view.findViewById(R.id.date_label);
//            viewHolder.group_label = view.findViewById(R.id.group_label);
//            viewHolder.title = view.findViewById(R.id.title);
//            viewHolder.date = view.findViewById(R.id.date);
//            viewHolder.content = view.findViewById(R.id.contents);
//            viewHolder.picture = view.findViewById(R.id.picture);
//            viewHolder.location = view.findViewById(R.id.memo_location);
//            viewHolder.content_layout = view.findViewById(R.id.content_layout);
            MemoItem Memo = Memos.get(position);
            viewHolder.date_label.setText(Memo.getDate_label());
            if(Memo.getDate_label()==null) {
                viewHolder.date_label.setVisibility(View.GONE);
            }
            else{
                viewHolder.date_label.setVisibility(View.VISIBLE);
                viewHolder.date_label.setText(Memo.getDate_label());
            }
            if(Memo.getGroup_label()==0){
                viewHolder.group_label.setVisibility(View.GONE);
            }
            else {
                viewHolder.group_label.setVisibility(View.VISIBLE);
            }

//            if(Memo.getDate_label()==null&&Memo.getGroup_label()==0){
//                Resources r = getResources();
//                float pxLeftMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxTopMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxRightMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                float pxBottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//
//                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                params.setMargins(Math.round(pxLeftMargin), Math.round(pxTopMargin), Math.round(pxRightMargin), Math.round(pxBottomMargin));
//                viewHolder.content_layout.setLayoutParams(params);
//            }

            viewHolder.title.setText(Memo.getTitle());
            viewHolder.date.setText(Memo.getDate());
            viewHolder.content.setText(Memo.getContents());
            if(Memo.getPictureURI()!=null){
                viewHolder.picture.setVisibility(View.VISIBLE);
                StorageReference storageReference = storage.getReferenceFromUrl(Memo.getPictureURI());
                Glide.with(getContext())
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(viewHolder.picture);
                viewHolder.picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(getContext(),FullScreenImageView.class);
//                        intent.putExtra("imageUri",viewHolder.picture.getDrawable());
//                        startActivity(intent);
                    }
                });
            }
            else{
                viewHolder.picture.setVisibility(View.GONE);
            }
//
            if(Memo.getLocation()==null){
                viewHolder.location.setVisibility(View.GONE);
            }
            else{
                viewHolder.location.setVisibility(View.VISIBLE);
                viewHolder.location.setText(Memo.getLocation());
            }
            return view;
        }
        class ViewHolder{
            TextView date_label;
            TextView group_label;
            TextView title;
            TextView date;
            TextView content;
            ImageView picture;
            TextView location;
            LinearLayout content_layout;
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

    private void ReadMemo(int position){
        MemoItem memo = Memos.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("myEmail",myEmail);
        bundle.putInt("GroupNo",memo.getGroup_label());
        bundle.putInt("READ",1);
        bundle.putString("ReadTitle",memo.getTitle());
        bundle.putString("ReadContent",memo.getContents());
        bundle.putString("UidKey",uidLists.get(position));
        if(memo.getPictureURI()!=null){
            bundle.putString("ReadPictureURI",memo.getPictureURI());
        }
        if(memo.getLatitude()!=0){
            bundle.putDouble("ReadLatitude",memo.getLatitude());
            bundle.putDouble("ReadLongitude",memo.getLongitude());
        }
        if(memo.getDayOfMonth()!=0){
            bundle.putInt("ReadYear",memo.getYear());
            bundle.putInt("ReadMonth",memo.getMonth());
            bundle.putInt("ReadDay",memo.getDayOfMonth());
            bundle.putInt("ReadHour",hour.get(position));
            bundle.putInt("ReadMinute",minute.get(position));
        }
        bundle.putString("ReadUploader",Uploader.get(position));

        MemoEditFragment memoEditFragment = new MemoEditFragment();
        memoEditFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,memoEditFragment)
                .commit();
//        fab.setVisibility(View.GONE);
    }
}
