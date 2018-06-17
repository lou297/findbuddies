package org.capstone.findbuddies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MemoList extends Fragment {
    ArrayList<MemoItem> Memos = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseStorage storage;
    String myEmail;
    String myID;
    boolean Check;
    MemoAdapter adapter;
    FloatingActionButton fab;
    private static final int GROUP_MEMO_NO = 3000;
    int GroupNo;
    String GroupName=null;
    String UploaderName=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.memo_list,container,false);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        myEmail = getArguments().getString("myEmail");
        getMyID(myEmail);
        fab = rootView.findViewById(R.id.fab);
        ListView listview = rootView.findViewById(R.id.MemoListView);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReadMemo(position);
            }
        });

        adapter = new MemoAdapter();

        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("AlertDialog Title");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.getReference().child("MemoList").child(Memos.get(position).getUidKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Memos.remove(position);
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("myEmail",myEmail);
                GroupNo = 0 ;
                bundle.putInt("GroupNo",GroupNo);
                MemoEditFragment memoEditFragment = new MemoEditFragment();
                memoEditFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,memoEditFragment)
                        .commit();
                ((NavigationMain)getActivity()).isEdit =1;
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getContext(),AddGroupMemo.class);
                intent.putExtra("MyEmail",myEmail);
                startActivityForResult(intent,GROUP_MEMO_NO);
                return true;
            }
        });

        return rootView;
    }


    class MemoAdapter extends BaseAdapter{

        private MemoAdapter() {
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

                                if(value.getLatitude()==0){
                                    memoItem = new MemoItem(value.getEditSystemTime(), value.getCheckGroupNo(), value.getUploaderEmail(), uidKey,value.getYear(), value.getMonth(),
                                            value.getDate(), value.getHour(), value.getMinute(),value.getTitle(),value.getMemo(),value.getImageUrl(),
                                            0, 0, null);
                                }
                                else{
                                    memoItem = new MemoItem(value.getEditSystemTime(), value.getCheckGroupNo(), value.getUploaderEmail(), uidKey,value.getYear(), value.getMonth(),
                                            value.getDate(), value.getHour(), value.getMinute(),value.getTitle(),value.getMemo(),value.getImageUrl(),
                                            value.getLatitude(), value.getLongitude(), value.getAddress());
                                }
                                addMemo(memoItem);
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
                view = getLayoutInflater().inflate(R.layout.memo_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.DtContainer = view.findViewById(R.id.dt_container);
                viewHolder.DtLoad = view.findViewById(R.id.dt_load);
                viewHolder.TmLoad = view.findViewById(R.id.tm_load);
                viewHolder.TitleLoad = view.findViewById(R.id.title_load);
                viewHolder.ContentLoad = view.findViewById(R.id.content_load);
                viewHolder.ShowMapBut = view.findViewById(R.id.show_map_but);
                viewHolder.ShowPicBut = view.findViewById(R.id.show_pic_but);
                viewHolder.ShowGroup = view.findViewById(R.id.show_group);
                viewHolder.ContentLoad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if(viewHolder.ContentLoad.getMaxLines()==2){
                                viewHolder.ContentLoad.setMaxLines(20);
                            }
                            else {
                                viewHolder.ContentLoad.setMaxLines(2);
                            }
                        }
                    }
                });


                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)view.getTag();
            }
            MemoItem Memo = Memos.get(position);
            viewHolder.ContentLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReadMemo(position);
                }
            });
            viewHolder.ShowMapBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewHolder.ContentLoad.getText().toString().equals(Memos.get(position).getContent())){
                        viewHolder.ContentLoad.setText(Memos.get(position).getAddress());
                        Log.d("ParsingTest",Memos.get(position).getAddress());
                        Log.d("ParsingTest","position: "+position);
                    } else {
                        viewHolder.ContentLoad.setText(Memos.get(position).getContent());
                        Log.d("ParsingTest",Memos.get(position).getContent());
                        Log.d("ParsingTest","position: "+position);
                    }
                }
            });
            if(Memo.getMonth()==0){
                viewHolder.DtLoad.setVisibility(View.INVISIBLE);
                viewHolder.TmLoad.setVisibility(View.INVISIBLE);
            }
            else{
                viewHolder.DtLoad.setVisibility(View.VISIBLE);
                viewHolder.TmLoad.setVisibility(View.VISIBLE);
                String Dt = Memo.getMonth()+"월 "+Memo.getDate()+"일";
                viewHolder.DtLoad.setText(Dt);
                String TM = Memo.getHour()+":"+Memo.getMinute();
                viewHolder.TmLoad.setText(TM);
            }
            if(Memo.getLatitude()==0){
                viewHolder.ShowMapBut.setVisibility(View.GONE);
            }
            else{
                viewHolder.ShowMapBut.setVisibility(View.VISIBLE);
            }
            if(Memo.getTitle()==null||Memo.getTitle().trim().length()==0){
                viewHolder.TitleLoad.setVisibility(View.INVISIBLE);
            }
            else{
                viewHolder.TitleLoad.setVisibility(View.VISIBLE);
            }
            viewHolder.TitleLoad.setText(Memo.getTitle());
            viewHolder.ContentLoad.setText(Memo.getContent());
            if(Memo.getPictureURI()!=null){
                viewHolder.ShowPicBut.setVisibility(View.VISIBLE);
                viewHolder.ShowPicBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),FullScreenImageView.class);
                        intent.putExtra("imageUri",Memo.getPictureURI());
                        intent.putExtra("Uploaded",1);
                        startActivity(intent);
                    }
                });
            }
            else{
                viewHolder.ShowPicBut.setVisibility(View.GONE);
            }
            if(Memo.getGroupNo()!=0){
                database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                            SaveRegist value = snapshot.getValue(SaveRegist.class);
                            if (value != null && (value.getSavedEmail()).equals(Memo.getUploader())) {
                                UploaderName = value.getSavedName();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                database.getReference().child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                            SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                            if (value != null && (value.getGroupNo()==Memo.getGroupNo())) {
                                GroupName = value.getGroupName();
                            }
                        }
                        String GroupInfo = GroupName+"("+UploaderName+")";
                        viewHolder.ShowGroup.setText(GroupInfo);
                        viewHolder.ShowGroup.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else{
                viewHolder.ShowGroup.setVisibility(View.GONE);
            }
            return view;
        }
        class ViewHolder{
            LinearLayout DtContainer;
            TextView DtLoad;
            TextView TmLoad;
            TextView TitleLoad;
            TextView ContentLoad;
            ImageView ShowMapBut;
            ImageView ShowPicBut;
            TextView ShowGroup;
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
        bundle.putInt("GroupNo",memo.getGroupNo());
        bundle.putInt("READ",1);
        bundle.putString("ReadTitle",memo.getTitle());
        bundle.putString("ReadContent",memo.getContent());
        bundle.putString("UidKey",memo.getUidKey());
        if(memo.getPictureURI()!=null){
            bundle.putString("ReadPictureURI",memo.getPictureURI());
        }
        if(memo.getLatitude()!=0){
            bundle.putDouble("ReadLatitude",memo.getLatitude());
            bundle.putDouble("ReadLongitude",memo.getLongitude());
            bundle.putString("ReadAddress",memo.getAddress());
        }
        if(memo.getMonth()!=0){
            bundle.putInt("ReadYear",memo.getYear());
            bundle.putInt("ReadMonth",memo.getMonth());
            bundle.putInt("ReadDay",memo.getDate());
            bundle.putInt("ReadHour",memo.getHour());
            bundle.putInt("ReadMinute",memo.getMinute());
        }
        bundle.putString("ReadUploader",memo.getUploader());

        MemoEditFragment memoEditFragment = new MemoEditFragment();
        memoEditFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,memoEditFragment)
                .commit();
        ((NavigationMain)getActivity()).isEdit =1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GROUP_MEMO_NO){
            if(resultCode==RESULT_OK){
                GroupNo = data.getIntExtra("GroupNo",0);
                MemoEditFragment memoEditFragment = new MemoEditFragment();
                Bundle bundle = new Bundle();
                bundle.putString("myEmail",myEmail);
                Log.d("ParsingTest","no: "+GroupNo);
                bundle.putInt("GroupNo",GroupNo);
                memoEditFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,memoEditFragment)
                        .commit();
                ((NavigationMain)getActivity()).isEdit =1;
            }
        }
    }
}
