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
    FirebaseUser User = mAuth.getCurrentUser();;


    String MyEmail;
    int GroupNo;
    int existGroupNo = 0;

    public String GetMyEmail(){
        MyEmail = User.getEmail();
        return MyEmail;
    }

    public String GetMyName(final String Email){
        final String[] MyName = new String[1];
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(Email)) {
                        MyName[0] = value.getSavedName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return MyName[0];
    }

    public String GetMyID(final String Email){
        final String[] MyID = new String[1];
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if (value != null && (value.getSavedEmail()).equals(Email)) {
                        MyID[0] = value.getSavedID();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return MyID[0];
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



    public int getNewGroupNo(){

        database.getReference().child("LastGroupNo").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    if (value != null && (value.getUserEmail()).equals(MyEmail)) {
                        String uidKey = snapshot.getKey();
                        if (value.getGpsList().size() <= 10) {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
