package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class register extends AppCompatActivity {
    int check = 0;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    EditText id;
    EditText name;
    EditText email;
    EditText pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Button regist = findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBlank();
            }
        });

        Button cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void CheckBlank(){
        id = findViewById(R.id.id_field);
        name = findViewById(R.id.name_field);
        email = findViewById(R.id.email_field);
        pwd = findViewById(R.id.pwd_field);

        String GetId = id.getText().toString();
        String GetName = name.getText().toString();
        String GetEmail = email.getText().toString();
        String GetPwd = pwd.getText().toString();
        if(GetEmail.getBytes().length<5)
            check =2;
        if(GetPwd.getBytes().length<6)
            check = 3;
        if(GetId.getBytes().length==0)
            check = 1;
        if(GetName.getBytes().length==0)
            check = 1;
        if(GetEmail.getBytes().length==0)
            check = 1;
        if(GetPwd.getBytes().length==0)
            check = 1;
        if(CheckIsExistEmail(GetEmail))
            check = 4;
        if(CheckIsExistID(GetId))
            check = 5;
        if(check == 1)
            Toast.makeText(this, "정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
        else if(check == 2)
            Toast.makeText(this, "너무 짧은 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
        else if(check==3)
            Toast.makeText(this, "너무 짧은 비밀번호입니다.", Toast.LENGTH_SHORT).show();
        else if(check ==4)
            Toast.makeText(this, "이미 존재하는 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
        else if(check ==5)
            Toast.makeText(this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
        else if (check == 0){
//            CreateUser(GetEmail,GetPwd);
            create(GetId,GetName,GetEmail,GetPwd);
        }
    }

    private void create(String ID, String Name,String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            Log.d("ParsingTest","와이..ㅋ");
                            UploadData1(ID,Name,email,password);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    public void UploadData1(String id,String name, String email,String pwd){
        Log.d("ParsingTest","와이..");
        Toast.makeText(this, "와이...", Toast.LENGTH_SHORT).show();
        SaveRegist saveRegist = new SaveRegist();
        saveRegist.setSavedID(id);
        saveRegist.setSavedName(name);
        saveRegist.setSavedEmail(email);
        saveRegist.setSavedPwd(pwd);
        saveRegist.setNotificationOn(0);
        Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show();
        database.getReference().child("UserInfo").push().setValue(saveRegist);
        finish();

    }

    private boolean CheckIsExistEmail(String email){
        final boolean[] Exist = {false};
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if(value!=null){
                        if(value.getSavedEmail().equals(email)){
                            Exist[0] = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Exist[0];
    }
    private boolean CheckIsExistID(String ID){
        final boolean[] Exist = {false};
        database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    SaveRegist value = snapshot.getValue(SaveRegist.class);
                    if(value!=null){
                        if(value.getSavedID().equals(ID)){
                            Exist[0] = true;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return Exist[0];
    }
}
