package org.capstone.findbuddies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class register extends AppCompatActivity {
    int check = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);


        Button regist = (Button)findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText)findViewById(R.id.name_field);
                EditText id = (EditText)findViewById(R.id.id_field);
                EditText pwd = (EditText)findViewById(R.id.pwd_field);

                String getval1 = name.getText().toString();
                String getval2 = id.getText().toString();
                String getval3 = pwd.getText().toString();


                Toast.makeText(getApplicationContext(),"dfsdf.",Toast.LENGTH_LONG);
                if(getval1.getBytes().length==0){
                    Toast toast = Toast.makeText(getApplicationContext(),"dfsdf1.",Toast.LENGTH_LONG);
                    toast.show();
                    check = 1;
                }
                if(getval2.getBytes().length==0){
                    Toast toast = Toast.makeText(getApplicationContext(),"dfsdf2.",Toast.LENGTH_LONG);
                    toast.show();
                    check = 1;
                }
                if(getval3.getBytes().length==0){
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
                    finish();
                }
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
}
