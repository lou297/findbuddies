package org.capstone.findbuddies;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    EditText Id;
    EditText pwd;
    private FirebaseAuth mAuth;
    FirebaseUser User;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        User = mAuth.getCurrentUser();
        Button start = (Button)findViewById(R.id.login);
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

        final Button register = (Button)findViewById(R.id.register);
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

                    // User is signed in
                    Toast.makeText(getApplicationContext(),"zzz",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(),NavigationMain.class);
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
