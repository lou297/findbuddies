package org.capstone.findbuddies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

public class NavigationMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fab;
    String myEmail;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.MemoToolbar);
//        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_main,new MainMapFragment())
                .commit();

        database = FirebaseDatabase.getInstance();
        myEmail = getIntent().getStringExtra("myEmail");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_main,new MemoEditFragment())
                        .commit();
                fab.setVisibility(View.INVISIBLE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.special){
            Intent intent = new Intent(this,ParsingMemo.class);
            EditText titleEdit = findViewById(R.id.title_edit);
            EditText contentEdit = findViewById(R.id.contents_edit);
            intent.putExtra("title",titleEdit.getText().toString());
            intent.putExtra("content",contentEdit.getText().toString());
            startActivity(intent);
            return true;
        }

        if (id == R.id.save) {
//            EditText titleEdit = findViewById(R.id.title_edit);
//            EditText contentEdit = findViewById(R.id.contents_edit);
//            long Now = System.currentTimeMillis();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd hh:mm:ss", Locale.KOREA);
//            String date = simpleDateFormat.format(new Date(Now));
//
//            ImageView imageView = findViewById(R.id.PictureView);
//            SaveMemo saveMemo = new SaveMemo();
//
//            saveMemo.setImageUrl(uri.toString());
//            saveMemo.setUploaderEmail(myEmail);
//            saveMemo.setLastEditDate(date);
//            saveMemo.setTitle(titleEdit.toString());
//            saveMemo.setMemo(contentEdit.toString());
//            saveMemo.setYear(0);
//            saveMemo.setMonth(0);
//            saveMemo.setDay(0);
//
//            database.getReference().child("MemoList").push().setValue(saveMemo);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fab.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("myEmail",myEmail);
        int id = item.getItemId();
        if(id == R.id.map) {
            MainMapFragment mainMapFragment = new MainMapFragment();
            mainMapFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,mainMapFragment)
                    .commit();
        }
        else if (id == R.id.note) {
            MemoList memoList = new MemoList();
            memoList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,memoList)
                    .commit();

        } else if (id == R.id.calendar) {
            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_main,calendarFragment)
                    .commit();
        } else if (id == R.id.group) {
            Intent intent = new Intent(getApplicationContext(),messenger.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("myEmail",myEmail);
            startActivity(intent);
        } else if (id == R.id.account) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
}
