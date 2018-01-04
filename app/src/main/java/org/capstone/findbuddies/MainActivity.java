package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button start = (Button)findViewById(R.id.login);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),messenger.class);
                startActivityForResult(intent,101);
            }
        });

        final Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupview = getLayoutInflater().inflate(R.layout.activity_register,null);
                PopupWindow popup = new PopupWindow(popupview, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                popup.setFocusable(true);
                // 외부 영역 선택히 PopUp 종료

                popup.showAtLocation(popupview, Gravity.CENTER, 0, 0);


            }
        });
    }
}
