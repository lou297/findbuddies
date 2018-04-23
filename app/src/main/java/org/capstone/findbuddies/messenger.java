package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class messenger extends AppCompatActivity {
    Buddy buddy;
    Group group;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setTitle("");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);

        mAuth = FirebaseAuth.getInstance();

        buddy = new Buddy();
        group = new Group();

        getSupportFragmentManager().beginTransaction().add(R.id.container,buddy).commit();
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Buddy"));
        tabs.addTab(tabs.newTab().setText("Group"));


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0) {
                    selected = buddy;
                }
                else if(position == 1) {
                    selected = group;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_message,menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.notice_ballon:
                Toast.makeText(this, "버튼 눌림", Toast.LENGTH_SHORT).show();
                Intent notice = new Intent(getApplication(),NoticeList.class);
                startActivity(notice);
                return true;

            case R.id.logout:
                mAuth.signOut();
                finish();
                Intent out = new Intent(getApplication(),MainActivity.class);
                startActivity(out);
                return super.onOptionsItemSelected(item);

            default:
                return true;

        }

    }
}
