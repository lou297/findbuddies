package org.capstone.findbuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */


public class Buddy extends Fragment {
    @Nullable
//    @Override
    private static Context context;
    CheckBox addcheck;
    BuddyAdapter adapter;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyID;
    String FriendName;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Buddy.context = getContext();
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.buddy,container,false);

        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        Button addmate = (Button)rootView.findViewById(R.id.addbuddy);
        addcheck = (CheckBox)rootView.findViewById(R.id.checked);
        addmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddMate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,101);
            }
        });

        ListView listview = (ListView) rootView.findViewById(R.id.listView);

        adapter = new BuddyAdapter();
//        MakeFriendsList();
        listview.setAdapter(adapter);

        return rootView;
    }

    public void FindMyID(){
        final String MyEmail = User.getEmail();

        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if( (value.getS_email()).equals(MyEmail) ){
                        MyID = value.getS_id();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void MakeFriendsList(){
        //내 아이디를 찾아서
        FindMyID();

        //데이터 베이스 친구목록에서 내 아이디 해당하는 부분 리스트를 읽어온다.
        database.getReference().child("UserBuddy").child(MyID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String value = snapshot.getValue().toString();
                    adapter.addbuddy(new BuddyItem(FindFriendName(value),value,R.drawable.boy));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public String FindFriendName(final String FriendsID){

        //친구 리스트를 만들기 위해 친구 이름을 찾아온다.

        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if( (value.getS_id()).equals(FriendsID) ){
                        FriendName = value.getS_name();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return FriendName;
    }

    public static Context getAppContext() {
        return Buddy.context;
    }

    static class BuddyAdapter extends BaseAdapter {
        ArrayList<BuddyItem> buddies = new ArrayList<BuddyItem>();


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
            BuddyItemView itemview = new BuddyItemView(Buddy.getAppContext());
            BuddyItem buddy = buddies.get(i);
            itemview.setName(buddy.getName());
            itemview.setMobile(buddy.getMobile());
            itemview.setImage(buddy.getResId());

            return itemview;
        }
    }
}
