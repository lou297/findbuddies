package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */


public class Buddy extends Fragment {
    //    private Context context;
    BuddyAdapter adapter;
    DatabaseReference database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.buddy,container,false);

        database = FirebaseDatabase.getInstance().getReference();
//        database.setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        if(User!=null){
            MyEmail = User.getEmail();
        }

        Button addmate = (Button)rootView.findViewById(R.id.addbuddy);
        addmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddMate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ListView listview = (ListView) rootView.findViewById(R.id.listView);

        adapter = new BuddyAdapter();

        listview.setAdapter(adapter);

        return rootView;
    }

//    public void FindMyID(){
//        final String MyEmail = User.getEmail();
//
//        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    SaveRegist value = snapshot.getValue(SaveRegist.class);
//                    if( (value.getS_email()).equals(MyEmail) ){
//                        MyID = value.getS_id();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public void MakeFriendsList(){

        //데이터 베이스 친구목록에서 내 이메일에 해당하는 부분 리스트를 읽어온다.
        database.child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveFriends value = snapshot.getValue(SaveFriends.class);

                    if(value !=null){
                        if( (value.getMyEmail()).equals(MyEmail)){
                            Toast.makeText(getContext(), value.getFriendID(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getContext(), value.getFriendName(), Toast.LENGTH_SHORT).show();
                            adapter.addbuddy(new BuddyItem(value.getFriendName(),value.getFriendID(),R.drawable.boy));
                        }
                    }

                }
                Toast.makeText(getContext(), adapter.getCount(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void ReadData(){
        database.child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(getContext(), "ddd?", Toast.LENGTH_SHORT).show();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveFriends value = snapshot.getValue(SaveFriends.class);

                    if(value !=null){
                        if( (value.getMyEmail()).equals(MyEmail)){
                            adapter.addbuddy(new BuddyItem(value.getFriendName(),value.getFriendID(),R.drawable.boy));
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class BuddyAdapter extends BaseAdapter {

        ArrayList<BuddyItem> buddies = new ArrayList<BuddyItem>();
        private BuddyAdapter() {
            database.child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    buddies.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveFriends value = snapshot.getValue(SaveFriends.class);
                        if(value !=null){
                            if( (value.getMyEmail()).equals(MyEmail)){
                                addbuddy(new BuddyItem(value.getFriendName(),value.getFriendID(),R.drawable.boy));
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

        @Override
        public int getCount() {
            return buddies.size();
        }

        public void addbuddy(BuddyItem buddy){
            buddies.add(buddy);
        }

        @Override
        public Object getItem(int i) {
            return buddies.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BuddyItemView itemview = new BuddyItemView(getContext());
            BuddyItem buddy = buddies.get(i);
            itemview.setName(buddy.getName());
            itemview.setID(buddy.getID());
            itemview.setImage(buddy.getResId());

            return itemview;
        }
    }
}
