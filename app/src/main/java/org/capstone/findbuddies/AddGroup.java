package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGroup extends AppCompatActivity {

    DatabaseReference database;
    String MyEmail;
    String MyName;
    String MyID;
    int GroupNo;
    int existGroupNo = 0;

    ArrayList<BuddyItem> buddies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);


        buddies = new ArrayList<BuddyItem>();
        database = FirebaseDatabase.getInstance().getReference();

        MyEmail = getIntent().getStringExtra("MyEmail");
        MyName = getIntent().getStringExtra("MyName");
        GetMyID(MyEmail);




        final ListView listView= (ListView)findViewById(R.id.listview);

        Button checkButton = (Button)findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> members = new ArrayList<>();
                ArrayList<String> membersID = new ArrayList<>();
                ArrayList<Integer> memberPermmision = new ArrayList<>();
                if(MyEmail==null){
                    Toast.makeText(AddGroup.this, "여기가 문제", Toast.LENGTH_SHORT).show();
                }
                members.add(MyName);
                membersID.add(MyID);
                memberPermmision.add(1);
                SparseBooleanArray checkedItem = listView.getCheckedItemPositions();
                for(int i = 0 ; i < buddies.size(); i ++){
                    if(checkedItem.get(i)){
                        Toast.makeText(AddGroup.this, buddies.get(i).getName(), Toast.LENGTH_SHORT).show();
                        members.add(buddies.get(i).getName());
                        membersID.add(buddies.get(i).getID());
                        memberPermmision.add(0);
                    }
                }

                if(members.size()>1){
                    String GroupName = getIntent().getStringExtra("name");
                    String GroupPassword = getIntent().getStringExtra("password");
                    AddGroupChat(MyEmail,GroupName,GroupPassword,members,membersID,memberPermmision,getNewGroupNo());
//                    MyFirebaseData.searchMyIdinGPSList(MyFirebaseData.getNewGroupNo());
                }


            }
        });

        AddGroupAdapter adapter = new AddGroupAdapter();
        listView.setAdapter(adapter);
    }

    class AddGroupAdapter extends BaseAdapter {

        public AddGroupAdapter(){
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
            GBuddyItemView itemview = new GBuddyItemView(getApplicationContext());
            BuddyItem buddy = buddies.get(i);
            itemview.setName(buddy.getName());
            itemview.setId(buddy.getID());
            itemview.setImage(buddy.getResId());

            return itemview;
        }
    }

    public void AddGroupChat(String owner, String groupName, String password, ArrayList<String> members,ArrayList<String> membersID, ArrayList<Integer> memberPermission,int groupNo){
        SaveGroupList saveGroupList = new SaveGroupList();
        saveGroupList.setGroupNo(groupNo);
        saveGroupList.setOwner(owner);
        saveGroupList.setGroupName(groupName);
        saveGroupList.setPassword(password);
        saveGroupList.setMembers(members);
        saveGroupList.setMembersID(membersID);
        saveGroupList.setMemberPermission(memberPermission);

        database.child("GroupList").push().setValue(saveGroupList);
        Toast.makeText(this, "그룹 추가 완료", Toast.LENGTH_SHORT).show();
        finish();
        GroupNaming NamingActivity = (GroupNaming) GroupNaming.NamingActivity;
        NamingActivity.finish();
    }

    public void GetMyName(final String Email){
        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(Email)) {
                        MyName = value.getSavedName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void GetMyID(final String Email){
        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(Email)) {
                        MyID = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getNewGroupNo(){

        database.child("LastGroupNo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue()!=null) {
                        //noinspection ConstantConditions
                        GroupNo = snapshot.getValue(int.class);
                        existGroupNo++;
                    }
                }
                if(existGroupNo==0){
                    GroupNo=10000;
                    database.child("LastGroupNo").setValue(GroupNo);
                }
                else {
                    GroupNo++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return GroupNo;
    }



}
