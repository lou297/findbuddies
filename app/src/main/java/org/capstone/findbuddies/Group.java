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

public class Group extends Fragment {

    GroupAdapter adapter;
    DatabaseReference database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;
    String MyName;
    ArrayList<GroupItem> groups = new ArrayList<GroupItem>();
    private List<String> uidLists = new ArrayList<>();

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

        Button addgroup = rootView.findViewById(R.id.addgroup);

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

        ListView listview = rootView.findViewById(R.id.listView2);

        adapter = new GroupAdapter();


        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int GroupNo = groups.get(position).getGroupNo();

            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("AlertDialog Title");
                builder.setMessage("그룹에서 나가시겠습니까?");
                builder.setPositiveButton("나가기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                database.child("GroupList").child(uidLists.get(position)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        groups.remove(position);
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
                    uidLists.clear();
                    groups.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        GroupNo = 0;
                        GroupName = null;
                        isMember = 0;
                        MemberList="";
                        SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                        String uidKey = snapshot.getKey();
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
                                uidLists.add(uidKey);
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

            return itemview;
        }
    }





}
