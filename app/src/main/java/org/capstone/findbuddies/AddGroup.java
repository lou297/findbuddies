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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGroup extends AppCompatActivity {

    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;
    String MyName;
    ArrayList<BuddyItem> buddies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        buddies = new ArrayList<BuddyItem>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        if(User!=null){
            MyEmail = User.getEmail();
        }
        GetMyName();

        final ListView listView= (ListView)findViewById(R.id.listview);

        Button checkButton = (Button)findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> members = new ArrayList<>();
                ArrayList<Integer> memberPermmision = new ArrayList<>();
                int memberNumber=1;
                members.add(MyName);
                memberPermmision.add(1);
                SparseBooleanArray checkedItem = listView.getCheckedItemPositions();
                for(int i = 0 ; i < buddies.size(); i ++){
                    if(checkedItem.get(i)){
                        Toast.makeText(AddGroup.this, buddies.get(i).getName(), Toast.LENGTH_SHORT).show();
                        memberNumber++;
                        members.add(buddies.get(i).getName());
                        memberPermmision.add(0);
                    }
                }

                if(members.size()>1){
                    String GroupName = getIntent().getStringExtra("name");
                    String GroupPassword = getIntent().getStringExtra("password");
                    AddGroupChat(MyName,GroupName,GroupPassword,members,memberPermmision);
                }

            }
        });

        AddGroupAdapter adapter = new AddGroupAdapter();
        listView.setAdapter(adapter);
    }

    class AddGroupAdapter extends BaseAdapter {

        public AddGroupAdapter(){
            database.getReference().child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void AddGroupChat(String owner, String groupName, String password, ArrayList<String> members, ArrayList<Integer> memberPermission){
        SaveGroupList saveGroupList = new SaveGroupList();
        saveGroupList.setRoomNo(1000);
        saveGroupList.setOwner(owner);
        saveGroupList.setGroupName(groupName);
        saveGroupList.setPassword(password);
        saveGroupList.setMembers(members);
        saveGroupList.setMemberPermission(memberPermission);

        database.getReference().child("GroupList").push().setValue(saveGroupList);
        Toast.makeText(this, "그룹 추가 완료", Toast.LENGTH_SHORT).show();
        finish();
        GroupNaming NamingActivity = (GroupNaming) GroupNaming.NamingActivity;
        NamingActivity.finish();
    }

    public void GetMyName(){

        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if((value.getSavedEmail()).equals(MyEmail)){
                        MyName = value.getSavedName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
