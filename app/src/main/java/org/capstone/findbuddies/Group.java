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

public class Group extends Fragment {

    GroupAdapter adapter;
    DatabaseReference database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;
    String MyName;
    ArrayList<GroupItem> groups = new ArrayList<GroupItem>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.group,container,false);

        database = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        if(User!=null){
            MyEmail = User.getEmail();
        }
        getMyName();

        Button addgroup = (Button)rootView.findViewById(R.id.addgroup);

        addgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),GroupNaming.class);
                intent.putExtra("MyEmail",MyEmail);
                intent.putExtra("MyName",MyName);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ListView listview = (ListView) rootView.findViewById(R.id.listView2);

        adapter = new GroupAdapter();


        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int GroupNo = groups.get(position).getGroupNo();
                if(CheckPermission(GroupNo)){
                    int groupsize = groups.get(position).getMemberIdList().size();
                    ArrayList<String> groupIdList= groups.get(position).getMemberIdList();
                    ArrayList<String> groupNameList = groups.get(position).getMemeberNameList();
                    Intent intent = new Intent(getContext(),ShowMap.class);
                    intent.putExtra("no",GroupNo);
                    intent.putExtra("size",groupsize);
                    intent.putStringArrayListExtra("IdList",groupIdList);
                    intent.putStringArrayListExtra("NameList",groupNameList);
                    intent.putExtra("MyEmail",MyEmail);
                    intent.putExtra("MyName",MyName);
                    startActivity(intent);
                }
                else{
                    RestrainMessage(GroupNo);
                }

            }
        });
        return rootView;
    }

    private void getMyName() {
        database.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(MyEmail)) {
                        MyName = value.getSavedName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class GroupAdapter extends BaseAdapter{
        int isMember;
        int GroupNo;
        String GroupName;
        String MemberList;
        private GroupAdapter(){
            database.child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groups.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        GroupNo = 0;
                        GroupName = null;
                        isMember = 0;
                        MemberList="";
                        SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                        if(value!=null){
                            ArrayList<String> members = value.getMembers();
                            for(String memberCheck : members){
                                if(memberCheck.equals(MyName)){
                                    isMember = 1;
                                }
                            }

                            if(isMember==1){
                                for(String member : members){
                                    if(MemberList.length()==0){
                                        MemberList +=member;
                                    }
                                    else {
                                        MemberList = MemberList + ", " + member;
                                    }
                                }
                                GroupNo = value.getGroupNo();
                                GroupName = value.getGroupName();
                                ArrayList<String> memberName = value.getMembers();
                                ArrayList<String> memberID = value.getMembersID();
                                addgroup(new GroupItem(GroupNo,GroupName,MemberList,memberName,memberID,R.drawable.family));
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
            return groups.size();
        }

        public void addgroup(GroupItem group){
            groups.add(group);
        }

        @Override
        public Object getItem(int i) {
            return groups.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GroupItemView itemview = new GroupItemView(getContext());
            GroupItem group = groups.get(i);
            itemview.setName(group.getName());
            itemview.setMember(group.getMember());
            itemview.setImage(group.getResId());

            return itemview;
        }
    }

    public boolean CheckPermission(final int groupNo){
        final boolean[] bool = {true};
        database.child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                    if(value!=null&&value.getGroupNo()==groupNo){
                        for(int i = 0 ; i<value.getMemberPermission().size();i++){
                            if(value.getMemberPermission().get(i)==0){
                                bool[0] = false;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return bool[0];
    }

    public void RestrainMessage(final int groupNo){
        final String[] NotPermMembers = {""};
        database.child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                    if(value!=null&&value.getGroupNo()==groupNo){
                        for(int i = 0 ; i<value.getMemberPermission().size();i++){
                            if(value.getMemberPermission().get(i)==0){
                                NotPermMembers[0] += (value.getMembers().get(i)+" ");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        NotPermMembers[0] += "회원님이 초청을 수락하지 않으셨습니다.";
        new AlertDialog.Builder(getContext())
                .setTitle("Restrained")
                .setMessage(NotPermMembers[0])
                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }


}
