package org.capstone.findbuddies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    String myID;
    String FriendName;
    String FriendID;
    ArrayList<BuddyItem> buddies = new ArrayList<BuddyItem>();
    private List<String> uidLists = new ArrayList<>();


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
            getMyID(MyEmail);
        }

        Button addmate = rootView.findViewById(R.id.addbuddy);
        addmate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),AddMate.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ListView listview = rootView.findViewById(R.id.listView);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("AlertDialog Title");
                builder.setMessage("친구 목록에서 제거하시겠습니까?");
                builder.setPositiveButton("제거",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.child("UserBuddy").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        buddies.remove(position);
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

        adapter = new BuddyAdapter();

        listview.setAdapter(adapter);

        return rootView;
    }

    private void getMyID(String MyEmail) {
        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
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

//    private String getFriendID(String Email){
//        final String[] ID = new String[1];
//        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
//                    SaveRegist value = snapshot.getValue(SaveRegist.class);
//                    if (value != null && (value.getSavedEmail()).equals(Email)) {
//                        ID[0] = value.getSavedID();
//                        Log.d("ParsingTest","친구 아이디1: "+ID[0]);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        Log.d("ParsingTest","친구 아이디: "+ID[0]);
//        return ID[0];
//    }
//
    private void getFriendInfo(String Email){
        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(Email)) {
                        FriendName = value.getSavedName();
                        FriendID = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class BuddyAdapter extends BaseAdapter {
        private BuddyAdapter() {
            database.child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    uidLists.clear();
                    buddies.clear();
                    FriendID= null;
                    FriendName = null;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String uidKey = snapshot.getKey();
                        SaveFriends value = snapshot.getValue(SaveFriends.class);
                        if(value !=null){
                            if( (value.getMyEmail()).equals(MyEmail) || value.getFriendID().equals(myID)){
                                uidLists.add(uidKey);
                                if(value.getFriendID().equals(myID)){
                                    addbuddy(new BuddyItem(value.getMyName(),value.getMyID(),R.drawable.boy));
                                }
                                else{
                                    addbuddy(new BuddyItem(value.getFriendName(),value.getFriendID(),R.drawable.boy));
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
