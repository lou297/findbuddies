package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2017-12-02.
 */

public class Group extends Fragment {

    GroupAdapter adapter;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;
    String MyName;
    ArrayList<GroupItem> groups = new ArrayList<GroupItem>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.group,container,false);

        database = FirebaseDatabase.getInstance();

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
//                Intent intent = new Intent(getActivity().getApplicationContext(),ShowMap.class);
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
                int groupsize = groups.get(position).getMemberIdList().size();
                ArrayList<String> groupIdList= groups.get(position).getMemberIdList();
                Intent intent = new Intent(getContext(),ShowMap.class);
                intent.putExtra("no",GroupNo);
                intent.putExtra("size",groupsize);
                intent.putStringArrayListExtra("IdList",groupIdList);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void getMyName() {
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
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
            database.getReference().child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
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
                                ArrayList<String> memberID = value.getMembersID();
                                addgroup(new GroupItem(GroupNo,GroupName,MemberList,memberID,R.drawable.family));
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


}
