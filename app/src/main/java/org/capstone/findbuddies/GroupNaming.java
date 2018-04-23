package org.capstone.findbuddies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupNaming extends AppCompatActivity {
    public static Activity NamingActivity;
    String MyEmail;
    String MyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_naming);
        NamingActivity = GroupNaming.this;
        MyEmail = getIntent().getStringExtra("MyEmail");
        MyName = getIntent().getStringExtra("MyName");
        
        Button create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassData();
            }
        });
    }

    public String GetGroupName(){
        EditText GroupName = findViewById(R.id.groupName);
        String GetName = GroupName.getText().toString();
        return GetName;
    }
    public String GetGroupPassword(){
        EditText GroupPassword = findViewById(R.id.groupPassword);
        String GetPassword = GroupPassword.getText().toString();
        return GetPassword;
    }
    
    public void PassData(){
        if(GetGroupName().getBytes().length==0){
            Toast.makeText(this, "그룹 명을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else {
            String password;
            if(GetGroupPassword().getBytes().length==0){
                password = "-";
            }
            else {
                password = GetGroupPassword();
            }

            Intent intent = new Intent(getApplicationContext(),AddGroup.class);
            intent.putExtra("name",GetGroupName());
            intent.putExtra("password",password);
            intent.putExtra("MyEmail",MyEmail);
            intent.putExtra("MyName",MyName);
            startActivity(intent);

        }
    }
}
