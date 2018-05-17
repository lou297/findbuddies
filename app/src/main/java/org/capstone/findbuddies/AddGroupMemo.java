package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGroupMemo extends AppCompatActivity {
    FirebaseDatabase database;
    String MyEmail;
    String MyID;
    ListView GrouplistView;
    Button AddGroupMemoButton;
    int GroupNo;
    Intent getIntent;

    ArrayList<GroupItem> Groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_memo);

        database = FirebaseDatabase.getInstance();
        MyEmail = getIntent().getStringExtra("MyEmail");
        GetMyID(MyEmail);
        GrouplistView = findViewById(R.id.group_memo_listView);
        AddGroupMemoButton = findViewById(R.id.add_group_memo_button);
        getIntent = getIntent();
        Groups = new ArrayList<>();

        AddGroupMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupNo = 0;
                SparseBooleanArray checkedItem = GrouplistView.getCheckedItemPositions();
                int checkposition = GrouplistView.getCheckedItemPosition();
//
//                if(GrouplistView==null){
//                    Toast.makeText(AddGroupMemo.this, "리스트뷰가 없음.", Toast.LENGTH_SHORT).show();
//                }
//                if(checkedItem==null){
//                    Toast.makeText(AddGroupMemo.this, "체크 아이템이 없음.", Toast.LENGTH_SHORT).show();
//                }
//                else if(checkedItem!=null){
//                    Toast.makeText(AddGroupMemo.this, ""+checkedItem.size(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(AddGroupMemo.this, ""+Groups.size(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(AddGroupMemo.this, ""+checkposition, Toast.LENGTH_SHORT).show();
//                }
//                Toast.makeText(, Groups.size(), Toast.LENGTH_SHORT).show();
                for(int i = 0 ; i < Groups.size();i++){
                    if(checkedItem.get(i)){
                        GroupNo = Groups.get(i).getGroupNo();
                    }
//                    Toast.makeText(AddGroupMemo.this, ""+checkedItem.get(i), Toast.LENGTH_SHORT).show();
                }

                getIntent.putExtra("GroupNo",GroupNo);
                Log.d("ParsingTest","no??: "+GroupNo);
                setResult(RESULT_OK,getIntent);
                finish();
            }
        });


        AddGroupMemoAdapter adapter = new AddGroupMemoAdapter();
        GrouplistView.setAdapter(adapter);
    }

    class AddGroupMemoAdapter extends BaseAdapter{
        public AddGroupMemoAdapter() {
            database.getReference().child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Groups.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                        if(value !=null){
                            ArrayList<String> membersID;
                            ArrayList<String> members;
                            membersID = value.getMembersID();
                            members = value.getMembers();
                            for(String ID : membersID ){
                                if(ID.equals(MyID)){
                                    String MemberList = "";
                                    for(String member : members){
                                        if(MemberList.length()==0){
                                            MemberList +=member;
                                        }
                                        else {
                                            MemberList = MemberList + ", " + member;
                                        }
                                    }

                                    addGroup(new GroupItem(value.getGroupNo(),value.getGroupName(),MemberList,value.getMembers(),value.getMembersID(),R.drawable.boy));
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

        public void addGroup(GroupItem groupItem){
            Groups.add(groupItem);
        }

        @Override
        public int getCount() {
            return Groups.size();
        }

        @Override
        public Object getItem(int position) {
            return Groups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GGroupItemView itemview = new GGroupItemView(getApplicationContext());
            GroupItem group = Groups.get(position);
            itemview.setGroup(group.getName());
            itemview.setMember(group.getMember());
            itemview.setImage(group.getResId());

            return itemview;
        }
    }

    public void GetMyID(final String Email){
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
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
}
