package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddMate extends AppCompatActivity {
    private FirebaseDatabase database;
    int SET = 0;//ON = 1 OFF = 0

    TextView searched_id;
    TextView searched_name;
    TextView SearchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_mate);

        database = FirebaseDatabase.getInstance();
//        SET = 0;
//        OnOffContents(SET);

        searched_id = (TextView)findViewById(R.id.searched_id);
        searched_name = (TextView)findViewById(R.id.searched_name);

        Button search = (Button)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                검색버튼 클릭
                CheckUser();

//                if(database.getReference().child("UserBuddy").child(String.valueOf(SearchId))!=null ){
//                    Toast.makeText(AddMate.this, "있는 존재", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(AddMate.this, "없다네", Toast.LENGTH_SHORT).show();
//                }


            }
        });


    }

    public void OnOffContents(int set,String ID,String Name){
        TextView textview1 = (TextView)findViewById(R.id.textview1);
        TextView textview2 = (TextView)findViewById(R.id.textview2);
        ImageButton add = (ImageButton)findViewById(R.id.Add);
        TextView explain = (TextView)findViewById(R.id.explain);

        if(set==0){
            textview1.setVisibility(View.GONE);
            textview2.setVisibility(View.GONE);
            searched_id.setVisibility(View.GONE);
            searched_name.setVisibility(View.GONE);
            add.setVisibility(View.GONE);
            explain.setVisibility(View.GONE);

        }
        else if(set == 1){
            textview1.setVisibility(View.VISIBLE);
            textview2.setVisibility(View.VISIBLE);
            searched_id.setText(ID);
            searched_name.setText(Name);
            searched_id.setVisibility(View.VISIBLE);
            searched_name.setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);
            explain.setVisibility(View.VISIBLE);
        }
    }

    public void CheckUser(){
        SearchId = (TextView)findViewById(R.id.search_id);

        if(SearchId.getText()!=null){
            database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        SaveRegist value = snapshot.getValue(SaveRegist.class);
                        if( (value.getS_id()) .equals(SearchId.getText().toString())){
                            Toast.makeText(AddMate.this, "있는 존재", Toast.LENGTH_SHORT).show();
                            SET = 1;
                            OnOffContents(SET,value.getS_id(),value.getS_name());
                        }

                    }
                    if(SET==0){
                        Toast.makeText(AddMate.this, "존재하지 않는 ID입니다.", Toast.LENGTH_SHORT).show();
                        OnOffContents(SET,null,null);
                    }
                    SET=0;

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void AddMate(final String MyID, String BuddyID){
        database.getReference().child("UserBuddy").child(MyID).push().setValue(BuddyID);
//        database.getReference().child("UserBuddy").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    String value = snapshot.getValue().toString();
//                    if(value.equals(MyID)){
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }
}
