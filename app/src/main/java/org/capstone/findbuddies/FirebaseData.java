package org.capstone.findbuddies;

import android.location.Location;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 2018-03-15.
 */

public class FirebaseData {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser User = mAuth.getCurrentUser();
    String MyName;
    String MyID;
    String MyEmail;
    int CheckMyGPSList=0;
    int GroupNo;
    int existGroupNo = 0;

    public String getMyEmail(){
        MyEmail = User.getEmail();
        return MyEmail;
    }

    public String GetMyName(){
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

    public String GetMyID(){
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if((value.getSavedEmail()).equals(getMyEmail())){
                        MyID = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return MyID;
    }

//    public void searchMyIdinGPSList(int GroupNo){
//        CheckMyGPSList = 0;
//
//        database.getReference().child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
//                    if((value.getUserName()).equals(GetMyName())){
//                        CheckMyGPSList = 1;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        if(CheckMyGPSList == 0){
////            addNewMyGroupGPSList(GroupNo);
//        }
//        else{
//            addGroupGPSList(GroupNo);
//        }
//    }



//    public void addNewMyGroupGPSList(int GroupNo){
//        SaveUserGPS saveUserGPS = new SaveUserGPS();
//        saveUserGPS.setUserName(GetMyName());
//        ArrayList<Integer> GroupList = new ArrayList<>();
//        GroupList.add(GroupNo);
//        saveUserGPS.setGroupList(GroupList);
//        saveUserGPS.setGpsList(null);
//        saveUserGPS.setTimeList(null);
//
//        database.getReference().child("UserGPS").push().setValue(saveUserGPS);
//    }

//    public void addGroupGPSList(final int GroupNo){
//
//        database.getReference().child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
//                    if((value.getUserName()).equals(GetMyName())){
//                        String uidKey = snapshot.getKey();
////                        ArrayList<Integer> GroupList = new ArrayList<>();
//                        ArrayList<Integer> GroupList = value.getGroupList();
//                        GroupList.add(GroupNo);
//                        database.getReference().child("UserGPS").child(uidKey).child("groupList").setValue(GroupList);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//    }



    public void AddNewOrExistingGPSList(){

    }

    public int getNewGroupNo(){

        database.getReference().child("LastGroupNo").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                  GroupNo = snapshot.getValue(int.class);
                  existGroupNo++;
              }
              if(existGroupNo==0){
                GroupNo=10000;
                database.getReference().child("LastGroupNo").setValue(GroupNo);
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

    public void SaveLocationData(final Location location){
        database.getReference().child("UserGPS").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveUserGPS value = snapshot.getValue(SaveUserGPS.class);
                    if((value.getUserEmail()).equals(MyEmail)){
                        String uidKey = snapshot.getKey();
                        ArrayList<Location> GpsList = value.getGpsList();
                        GpsList.add(location);

                        ArrayList<Date> TimeList = value.getTimeList();
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        TimeList.add(date);

                        database.getReference().child("UserGPS").child(uidKey).child("gpsList").setValue(GpsList);
                        database.getReference().child("UserGPS").child(uidKey).child("timeList").setValue(TimeList);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
