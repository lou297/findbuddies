package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NoticeList extends AppCompatActivity {
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String MyEmail;
    String MyName;
    ArrayList<NoticeItem> noticeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);

        noticeList = new ArrayList<NoticeItem>();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        if(User!=null){
            MyEmail = User.getEmail();
        }
        GetMyName();

        ListView listView = (ListView)findViewById(R.id.listView);

        NoticeAdapter adapter = new NoticeAdapter();
        listView.setAdapter(adapter);


    }

    class NoticeAdapter extends BaseAdapter{

        NoticeAdapter(){

            database.getReference().child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveGroupList value = snapshot.getValue(SaveGroupList.class);
                        if(value!=null){
                            String uidKey = snapshot.getKey();
                            int InGroup = 0;
                            int position = 0;
                            int NotPermission = 0;
                            ArrayList<String> members = value.getMembers();
                            for(String memberCheck : members){
                                if(memberCheck.equals(MyName)){
                                    InGroup = 1;
                                    break;
                                }
                                position++;
                            }
                            if(InGroup==1){
                                ArrayList<Integer> permission = value.getMemberPermission();
                                if(permission.get(position)==0){
                                    NotPermission=1;
//                                permission.set(position,1);
//                                database.getReference().child("GroupList").child(uidKey).child("memberPermission").setValue(permission);
                                }
                            }
                            if(NotPermission==1){
                                String owner = value.getOwner();
                                String groupName = value.getGroupName();
                                String MemberList="";
                                int i = 0;
                                for(String memberCheck : members){
                                    if(i==0){
                                        MemberList += memberCheck;
                                    }
                                    else {
                                        MemberList = MemberList+" , "+memberCheck;
                                    }
                                    i++;
                                }
                                NoticeItem noticeItem = new NoticeItem(owner,groupName,MemberList);
                                addNotice(noticeItem);
                            }
                            Toast.makeText(NoticeList.this, NotPermission+" "+InGroup, Toast.LENGTH_SHORT).show();
                        }


                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            if(noticeList.size()==0){
                TextView NoNotice = (TextView)findViewById(R.id.NoNotice);
                NoNotice.setVisibility(View.VISIBLE);
            }
        }
        @Override
        public int getCount() {
            return noticeList.size();
        }

        @Override
        public Object getItem(int position) {
            return noticeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NoticeItemView itemView = new NoticeItemView(getApplicationContext());
            NoticeItem noticeItem = noticeList.get(position);
            itemView.setGroupOwner(noticeItem.getOwner());
            itemView.setGroupName(noticeItem.getGroupName());
            itemView.setGroupList(noticeItem.getMemberList());
            return itemView;
        }

        public void addNotice(NoticeItem notice){
            noticeList.add(notice);
        }
    }

    private void GetMyName(){
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
}
