package org.capstone.findbuddies;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by user on 2018-03-15.
 */

public class FirebaseData {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser User = mAuth.getCurrentUser();
    String MyName;
    String MyEmail;
    int CheckMyGPSList=0;
    int GroupNo = 1000;

    public String getMyEmail(){
        MyEmail = User.getEmail();
        return MyEmail;
    }

    public String getMyName(){
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if((value.getSavedEmail()).equals(getMyEmail())){
                        MyName = value.getSavedName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return MyName;
    }

    public void searchMyIdinGPSList(int GroupNo){
        CheckMyGPSList = 0;

        database.getReference().child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if((value.getUserName()).equals(getMyName())){
                        CheckMyGPSList = 1;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(CheckMyGPSList == 0){
            addNewMyGroupGPSList(GroupNo);
        }
        else{
            addGroupGPSList(GroupNo);
        }
    }

    public void addNewMyGroupGPSList(int GroupNo){
        SaveUserGPS saveUserGPS = new SaveUserGPS();
        saveUserGPS.setUserName(getMyName());
        ArrayList<Integer> GroupList = new ArrayList<>();
        GroupList.add(GroupNo);
        saveUserGPS.setGroupList(GroupList);
        saveUserGPS.setGpsList(null);

        database.getReference().child("UserGPS").push().setValue(saveUserGPS);
    }

    public void addGroupGPSList(final int GroupNo){

        database.getReference().child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if((value.getUserName()).equals(getMyName())){
                        String uidKey = snapshot.getKey();
                        ArrayList<Integer> GroupList = new ArrayList<>();
                        GroupList = value.getGroupList();
                        GroupList.add(GroupNo);
                        database.getReference().child("UserGPS").child(uidKey).child("groupList").push().setValue(GroupList);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public int getNewGroupNo(){
      database.getReference().child("GroupList").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
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
