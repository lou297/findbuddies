package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

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

        Button regist = (Button)findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBlank();
            }
        });

        Button cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void CheckBlank(){
        id = findViewById(R.id.id_field);
        name = (EditText)findViewById(R.id.name_field);
        email = (EditText)findViewById(R.id.email_field);
        pwd = (EditText)findViewById(R.id.pwd_field);

        String GetId = id.getText().toString();
        String GetName = name.getText().toString();
        String GetEmail = email.getText().toString();
        String GetPwd = pwd.getText().toString();

        Toast.makeText(getApplicationContext(),"dfsdf.",Toast.LENGTH_LONG);
        if(GetId.getBytes().length==0){
            Toast toast = Toast.makeText(getApplicationContext(),"dfsdf3.",Toast.LENGTH_LONG);
            toast.show();
            check = 1;
        }
        if(GetName.getBytes().length==0){
            Toast toast = Toast.makeText(getApplicationContext(),"dfsdf1.",Toast.LENGTH_LONG);
            toast.show();
            check = 1;
        }
        if(GetEmail.getBytes().length==0){
            Toast toast = Toast.makeText(getApplicationContext(),"dfsdf2.",Toast.LENGTH_LONG);
            toast.show();
            check = 1;
        }
        if(GetPwd.getBytes().length==0){
            Toast toast = Toast.makeText(getApplicationContext(),"dfsdf3.",Toast.LENGTH_LONG);
            toast.show();
            check = 1;
        }

        if(check == 1){
            Toast toast = Toast.makeText(getApplicationContext(),"정보를 입력해주세요.",Toast.LENGTH_LONG);
            toast.show();
        }
        else if (check == 0){
            Toast toast = Toast.makeText(getApplicationContext(),"정보 입력됨.",Toast.LENGTH_LONG);
            toast.show();
            CreateUser(GetEmail,GetPwd);
            UploadData(GetId,GetName,GetEmail,GetPwd);
            finish();
        }
    }

    private void CreateUser(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void UploadData(String id,String name, String email,String pwd){
        SaveRegist saveRegist = new SaveRegist();
        saveRegist.setSavedID(id);
        saveRegist.setSavedName(name);
        saveRegist.setSavedEmail(email);
        saveRegist.setSavedPwd(pwd);
//        SaveRegist.S_id = id;
//        SaveRegist.S_name = name;
//        SaveRegist.S_email = email;
//        SaveRegist.S_pwd = pwd;
        database.getReference().child("UserInfo").push().setValue(saveRegist);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }
}
