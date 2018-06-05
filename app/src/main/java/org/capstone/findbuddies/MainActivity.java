package org.capstone.findbuddies;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText Id;
    EditText pwd;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    String myEmail;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        User = mAuth.getCurrentUser();
        Button start = findViewById(R.id.login);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Id = findViewById(R.id.id);
                pwd = findViewById(R.id.pwd);
                if(Id.getText()!=null && pwd.getText()!=null){
                    String email = Id.getText().toString();
                    String password = pwd.getText().toString();
                    LoginCheck(email.trim(),password.trim());
                }
                else {
                    Toast.makeText(MainActivity.this, "zzz", Toast.LENGTH_SHORT).show();
                }

            }
        });

        final Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),register.class);
//                Intent intent = new Intent(getApplicationContext(),StreetView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    myEmail = user.getEmail();
                    CreateChannelAndNotification();
                    // User is signed in
                    Toast.makeText(getApplicationContext(),"zzz",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),NavigationMain.class);
//                    Intent intent = new Intent(getApplicationContext(),SelectMapLocation.class);
                    intent.putExtra("myEmail",user.getEmail());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
//                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Toast.makeText(getApplicationContext(),"dddd",Toast.LENGTH_LONG).show();
                    // User is signed out
//                Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void CreateChannelAndNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage  = new NotificationChannel("ChannelId","ChannelName", NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("channel description");
            channelMessage.enableLights(true);
            channelMessage.setLightColor(Color.GREEN);
            channelMessage.enableVibration(true);
            channelMessage.setVibrationPattern(new long[]{100, 200, 100, 200});
            channelMessage.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channelMessage);
            }
            database.getReference().child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NewApi")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SaveRegist value = snapshot.getValue(SaveRegist.class);
                        if (value != null&&value.getSavedEmail().equals(myEmail)&&value.getNotificationOn()==1) {
                            assert notificationManager != null;
                            if(notificationManager.getActiveNotifications().length==0){
                                startService(new Intent(getApplicationContext(),NotificationService.class));
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


    private void LoginCheck(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Toast.makeText(MainActivity.this, "성공!", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            Toast.makeText(getApplication(),"옳바른 정보를 입력해주세요.",Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
